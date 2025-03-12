package com.lerchenflo.t10elementekatalog;
import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class uebungscreator extends AppCompatActivity {

    private RecyclerView elementList;
    private LinearLayout dropZone;
    private Spinner categorySpinner;
    private ElementAdapter adapter;
    private List<String> elements;
    private HashMap<String, String[][]> elementData = new HashMap<>();
    private String selectedCategory = "Boden"; // Default category

    private Set<String> addedGroups = new HashSet<>(); // To track groups already added to the drop zone

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uebungscreator);

        elementList = findViewById(R.id.recyclerView);
        dropZone = findViewById(R.id.dropZone);
        categorySpinner = findViewById(R.id.categorySpinner);
        View trashcan = findViewById(R.id.trashcan); // Add trashcan view

        // Initialize category data
        elementData.put("Boden", constants.Boden);
        elementData.put("Barren", constants.Barren);
        elementData.put("Balken", constants.Balken);
        elementData.put("Pferd", constants.Pferd);
        elementData.put("Sufenbarren", constants.Stufenbarren);
        elementData.put("Hochreck", constants.Hochreck);
        elementData.put("Tiefreck", constants.Tiefreck);
        elementData.put("Ringe", constants.Ringe);

        setupCategorySpinner();
        loadElements(selectedCategory);

        adapter = new ElementAdapter(elements, this);
        elementList.setLayoutManager(new LinearLayoutManager(this));
        elementList.setAdapter(adapter);

        elementList.addItemDecoration(new RightDividerItemDecoration(
                this,
                getResources().getColor(android.R.color.darker_gray),
                2, 20
        ));

        setupDragAndDrop();
        setupTrashcan(trashcan); // Setup the trashcan drop behavior

        findViewById(R.id.backbutton_uebungscreator).setOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>(elementData.keySet());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
                loadElements(selectedCategory);

                // Clear the right panel before updating
                clearRightPanel();

                // Update the RecyclerView with the new elements
                adapter.updateElements(elements);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadElements(String category) {
        elements = new ArrayList<>();
        if (elementData.containsKey(category)) {
            for (String[] group : elementData.get(category)) {
                for (String item : group) {
                    elements.add(item);
                }
            }
        }
    }

    private void setupDragAndDrop() {
        dropZone.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    View draggedView = (View) event.getLocalState();
                    String element = ((TextView) draggedView).getText().toString();

                    // Check if the element's group is already added to the drop zone
                    String group = getGroupForElement(element);
                    if (!addedGroups.contains(group) && !isElementInDropZone(element)) {
                        addElementToDropZone(element, group);
                        disableAlternativeElements(element);
                    }
                    break;
            }
            return true;
        });
    }

    private String getGroupForElement(String element) {
        // Loop through all groups to find the group that contains the element
        for (String[][] groupArray : elementData.values()) {
            for (String[] group : groupArray) {
                for (String item : group) {
                    if (item.equals(element)) {
                        return String.join(", ", group); // Return the entire group as a string identifier
                    }
                }
            }
        }
        return ""; // Default case, shouldn't reach here
    }

    private boolean isElementInDropZone(String element) {
        for (int i = 0; i < dropZone.getChildCount(); i++) {
            View child = dropZone.getChildAt(i);
            if (child instanceof TextView && ((TextView) child).getText().toString().equals(element)) {
                return true;
            }
        }
        return false;
    }

    private void addElementToDropZone(String element, String group) {
        TextView textView = new TextView(this);
        textView.setText(element);
        textView.setPadding(10, 10, 10, 10);

        // Set the drag listener for the TextView (element in the drop zone)
        textView.setOnLongClickListener(v -> {
            // Start the drag operation
            ClipData data = ClipData.newPlainText("element", element);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });

        View separator = new View(this);
        separator.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2
        ));
        separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        dropZone.addView(textView);
        dropZone.addView(separator);

        // Mark the group as added
        addedGroups.add(group);
    }

    private void disableAlternativeElements(String selectedElement) {
        // Get the groups for the current category
        String[][] groups = elementData.get(selectedCategory);
        if (groups != null) {
            // Find the group that contains the selectedElement
            List<String> elementsToDisable = new ArrayList<>();
            for (String[] group : groups) {
                for (String item : group) {
                    if (item.equals(selectedElement)) {
                        // Found the group, add all elements in this group
                        for (String element : group) {
                            elementsToDisable.add(element);
                        }
                        break;
                    }
                }
                if (!elementsToDisable.isEmpty()) {
                    break; // Exit loop once the group is found
                }
            }
            // Disable all elements in the found group
            adapter.disableElements(elementsToDisable.toArray(new String[0]));
        }
    }

    private void clearRightPanel() {
        dropZone.removeAllViews(); // Remove all child views from the dropZone (right panel)
        addedGroups.clear(); // Clear the list of added groups
    }
    private void setupTrashcan(View trashcan) {
        trashcan.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    View draggedView = (View) event.getLocalState();
                    String element = ((TextView) draggedView).getText().toString();

                    // Only remove elements from the drop zone (right panel)
                    if (draggedView.getParent() == dropZone) {
                        removeElementFromDropZone(element);
                    }
                    break;
            }
            return true;
        });
    }


    private void removeElementFromDropZone(String element) {
        // Remove the element and its separator from the drop zone
        for (int i = 0; i < dropZone.getChildCount(); i++) {
            View child = dropZone.getChildAt(i);
            if (child instanceof TextView && ((TextView) child).getText().toString().equals(element)) {
                dropZone.removeViewAt(i); // Remove element
                dropZone.removeViewAt(i); // Remove separator
                break;
            }
        }

        // Remove the group identifier from addedGroups
        String groupIdentifier = getGroupForElement(element);
        addedGroups.remove(groupIdentifier);

        // Re-enable all elements in this group
        enableElementsInGroup(element); // <-- New method
    }
    private void enableElementsInGroup(String element) {
        // Get groups for the current category
        String[][] groups = elementData.get(selectedCategory);
        if (groups != null) {
            for (String[] group : groups) {
                for (String item : group) {
                    if (item.equals(element)) {
                        // Found the group: Enable all elements in it
                        adapter.enableElements(group);
                        break;
                    }
                }
            }
        }
    }
}
