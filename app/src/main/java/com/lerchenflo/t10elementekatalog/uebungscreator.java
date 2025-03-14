package com.lerchenflo.t10elementekatalog;
import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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
    private uebungscreator_ElementAdapter adapter;
    private List<String> elements;
    private HashMap<String, String[][]> elementData = new HashMap<>();
    private String selectedCategory = "Boden"; // Default category
    private int draggedIndex = -1; // Track the index of the dragged element

    private Set<String> addedGroups = new HashSet<>(); // To track groups already added to the drop zone
    private Spinner uebungSpinner;
    private ArrayAdapter<String> uebungAdapter;
    private List<String> uebungenList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_uebungscreator);

        // Initialize new views
        uebungSpinner = findViewById(R.id.uebungSpinner);
        ImageButton deleteUebungButton = findViewById(R.id.deleteUebungButton);

        // Setup Übung Spinner
        uebungenList.add("Übung 1");
        uebungenList.add("Übung hinzufügen");

        //uebungAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, uebungenList);
        uebungAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, uebungenList);

        uebungAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uebungSpinner.setAdapter(uebungAdapter);
        Log.d("DEBUG", "uebungenList size: " + uebungenList.size());
        Log.d("SpinnerDebug", "uebungenList contents: " + uebungenList.toString());


        // Übung Spinner selection listener
        uebungSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = uebungenList.get(position);
                if (selected.equals("Übung hinzufügen")) {
                    showAddUebungDialog();
                    // Reset selection to previous item
                    uebungSpinner.setSelection(0);
                } else {
                    clearRightPanel();
                    adapter.resetDisabledElements();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Delete Übung Button
        deleteUebungButton.setOnClickListener(v -> deleteCurrentUebung());


        findViewById(R.id.clear).setOnClickListener(v -> {
            clearRightPanel();
            adapter.resetDisabledElements();
        });
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

        adapter = new uebungscreator_ElementAdapter(elements, this);
        elementList.setLayoutManager(new LinearLayoutManager(this));
        elementList.setAdapter(adapter);

        elementList.addItemDecoration(new uebungscreator_RightDividerItemDecoration(
                this,
                getResources().getColor(android.R.color.darker_gray),
                2, 20
        ));

        setupDragAndDrop();
        setupTrashcan(trashcan); // Setup the trashcan drop behavior

        findViewById(R.id.backbutton_uebungscreator).setOnClickListener(v -> finish());
    }
    private void showAddUebungDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Übung hinzufügen");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Hinzufügen", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                // Insert new Übung before "Übung hinzufügen"
                uebungenList.add(uebungenList.size() - 1, newName);
                uebungAdapter.notifyDataSetChanged();
                clearRightPanel();
                adapter.resetDisabledElements();
                uebungSpinner.setSelection(uebungenList.indexOf(newName));
            }
        });
        builder.setNegativeButton("Abbrechen", null);
        builder.show();
    }

    private void deleteCurrentUebung() {
        int position = uebungSpinner.getSelectedItemPosition();
        String current = uebungenList.get(position);

        if (position == 0 || current.equals("Übung hinzufügen")) {
            return; // Can't delete default or add button
        }

        uebungenList.remove(position);
        uebungAdapter.notifyDataSetChanged();
        uebungSpinner.setSelection(position > 0 ? position - 1 : 0);
        clearRightPanel();
        adapter.resetDisabledElements();
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
                adapter.resetDisabledElements();

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
        // Handle drops in the drop zone
        dropZone.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Store the original index when dragging starts
                    View draggedView = (View) event.getLocalState();
                    if (draggedView.getParent() == dropZone) {
                        draggedIndex = dropZone.indexOfChild(draggedView);
                    }
                    return true;

                case DragEvent.ACTION_DROP:
                    draggedView = (View) event.getLocalState();
                    String element = ((TextView) draggedView).getText().toString();

                    if (draggedView.getParent() == dropZone) {
                        // REORDERING: Move existing element within drop zone
                        int insertIndex = findInsertIndex(event.getY());

                        // Remove the dragged element and its separator
                        dropZone.removeViewAt(draggedIndex);
                        dropZone.removeViewAt(draggedIndex); // Remove separator

                        // Adjust insert index if moving downward
                        if (draggedIndex < insertIndex) insertIndex -= 2;

                        // Insert at new position
                        dropZone.addView(draggedView, insertIndex);
                        View newSeparator = createSeparator();
                        dropZone.addView(newSeparator, insertIndex + 1);
                    } else {
                        // ADDING: New element from the left panel
                        String group = getGroupForElement(element);
                        if (!addedGroups.contains(group) && !isElementInDropZone(element)) {
                            addElementToDropZone(element, group);
                            disableAlternativeElements(element);
                        }
                    }
                    return true;
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

    private View createSeparator() {
        View separator = new View(this);
        separator.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2
        ));
        separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        return separator;
    }

    private void addElementToDropZone(String element, String group) {
        TextView textView = new TextView(this);
        textView.setText(element);
        textView.setPadding(10, 10, 10, 10);

        // Enable drag for reordering
        textView.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("element", element);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });

        dropZone.addView(textView);
        dropZone.addView(createSeparator());
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
        for (int i = 0; i < dropZone.getChildCount(); i++) {
            View child = dropZone.getChildAt(i);
            if (child instanceof TextView && ((TextView) child).getText().toString().equals(element)) {
                // Remove element and separator
                dropZone.removeViewAt(i);
                dropZone.removeViewAt(i);
                break;
            }
        }

        String groupIdentifier = getGroupForElement(element);
        addedGroups.remove(groupIdentifier);
        enableElementsInGroup(element);
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
    // Helper method to find the index where to insert the dragged element
    private int findInsertIndex(float y) {
        for (int i = 0; i < dropZone.getChildCount(); i++) {
            View child = dropZone.getChildAt(i);
            if (child instanceof TextView && y < child.getY() + child.getHeight() / 2) {
                return i; // Insert above this element
            }
        }
        return dropZone.getChildCount(); // Insert at the end
    }
}
