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

import java.io.File;
import java.io.IOException;
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
    private SaveFileManager saveFileManager = new SaveFileManager();
    private Kind currentKind = new Kind();
    private String currentUebungName = "Übung 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uebungscreator);

        // Initialize new views
        uebungSpinner = findViewById(R.id.uebungSpinner);
        dropZone = findViewById(R.id.dropZone);
        ImageButton deleteUebungButton = findViewById(R.id.deleteUebungButton);

        // Setup Übung Spinner
        uebungAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, uebungenList);
        uebungAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uebungSpinner.setAdapter(uebungAdapter);

        // Load saved exercises FIRST
        loadSavedUebungen();
        //uebungAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, uebungenList);
        Log.d("DEBUG", "uebungenList size: " + uebungenList.size());
        Log.d("SpinnerDebug", "uebungenList contents: " + uebungenList.toString());
        // Initialize spinner with existing saved exercises

        // Rest of onCreate remains the same until the end...
        findViewById(R.id.backbutton_uebungscreator).setOnClickListener(v -> finish());

        // Load initial exercise
        loadUebung(currentUebungName);


        // Übung Spinner selection listener
        uebungSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = uebungenList.get(position);
                if (selected.equals("Übung hinzufügen")) {
                    showAddUebungDialog();
                    uebungSpinner.setSelection(0);
                } else {
                    if (!selected.equals(currentUebungName)) {
                        currentUebungName = selected;
                        loadUebung(currentUebungName);
                    }
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

            // Remove elements from the current Geraet and save
            Geraet currentGeraet = getCurrentGeraet();
            currentGeraet._elemente.clear();
            saveCurrentKind();

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
    private void loadSavedUebungen() {
        File directory = getFilesDir();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".creator.kind"));
        Log.d("DEBUG", "Filecount: "+ files.length);
        uebungenList.clear();

        // Always add the default exercise first
        uebungenList.add("Übung 1");

        // Add existing saved exercises
        if(files != null && files.length > 0) {
            for (File file : files) {
                String name = file.getName().replace(".creator.kind", "");
                Log.d("DEBUG", "File: " + name);
                if(!uebungenList.contains(name)) {  // Prevent duplicates
                    uebungenList.add(name);
                }
            }
        }

        // Always add "add exercise" at the end
        uebungenList.add("Übung hinzufügen");

        uebungAdapter.notifyDataSetChanged();
    }

    private void loadUebung(String uebungName) {
        try {
            currentKind = saveFileManager.loadKind(uebungscreator.this, uebungName);
            currentUebungName = uebungName;

            // Update UI for current category
            refreshDropZone();

            // Update category spinner if needed
            String loadedCategory = currentKind._geraete.isEmpty() ?
                    selectedCategory :
                    currentKind._geraete.get(0)._geraetname;

            if(!loadedCategory.equals(selectedCategory)) {
                int position = new ArrayList<>(elementData.keySet()).indexOf(loadedCategory);
                if(position >= 0) {
                    categorySpinner.setSelection(position);
                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateElementOrderInModel() {
        Geraet geraet = getCurrentGeraet();
        geraet._elemente.clear();

        // Read elements from drop zone in current order
        for(int i = 0; i < dropZone.getChildCount(); i += 2) { // Skip separators
            View child = dropZone.getChildAt(i);
            if(child instanceof TextView) {
                geraet._elemente.add(((TextView) child).getText().toString());
            }
        }
        saveCurrentKind();
    }
    private void refreshDropZone() {
        clearRightPanel();
        List<String> elements = currentKind.getGeraetElements(selectedCategory);
        // Add elements in stored order and disable groups
        for (String element : elements) {
            String group = getGroupForElement(element);
            addElementToDropZone(element, group);
            disableAlternativeElements(element);
        }
    }
    private void showAddUebungDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Übung hinzufügen");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Hinzufügen", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                // Create new Kind
                Kind newKind = new Kind();
                newKind._name = newName;
                try {
                    saveFileManager.saveKind(uebungscreator.this, newKind, newName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                uebungenList.add(uebungenList.size() - 1, newName);
                uebungAdapter.notifyDataSetChanged();
                uebungSpinner.setSelection(uebungenList.indexOf(newName));
                loadUebung(newName);
            }
        });
        builder.setNegativeButton("Abbrechen", null);
        builder.show();
    }

    private void deleteCurrentUebung() {
        int position = uebungSpinner.getSelectedItemPosition();
        String current = uebungenList.get(position);

        if (position == 0 || current.equals("Übung hinzufügen")) return;

        // Delete file
        File file = new File(getFilesDir(), current + ".creator.kind");
        if (file.exists()) file.delete();

        uebungenList.remove(position);
        uebungAdapter.notifyDataSetChanged();
        uebungSpinner.setSelection(position > 0 ? position - 1 : 0);
        loadUebung(uebungenList.get(uebungSpinner.getSelectedItemPosition()));
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
                refreshDropZone();
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
                        updateElementOrderInModel();

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
            saveCurrentKind(); // Add this line
            return true;
        });
    }
    private void addElementToCurrentKind(String element) {
        Geraet geraet = getCurrentGeraet();
        if (!geraet._elemente.contains(element)) {
            geraet._elemente.add(element);
        }
    }
    private void removeElementFromCurrentKind(String element) {
        Geraet geraet = getCurrentGeraet();
        geraet._elemente.remove(element);
    }

    private Geraet getCurrentGeraet() {
        for (Geraet g : currentKind._geraete) {
            if (g._geraetname.equals(selectedCategory)) {
                return g;
            }
        }
        Geraet newGeraet = new Geraet();
        newGeraet._geraetname = selectedCategory;
        currentKind._geraete.add(newGeraet);
        return newGeraet;
    }

    private void saveCurrentKind() {
        Log.d("DEBUG", "Saving Kind");
        try {
            saveFileManager.saveKind(uebungscreator.this, currentKind, currentUebungName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DEBUG", "Error saving Kind");
        }
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

        // Add to current Kind and save
        addElementToCurrentKind(element);
        saveCurrentKind();
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
        addedGroups.remove(groupIdentifier);
        enableElementsInGroup(element);

        // Remove from current Kind and save
        removeElementFromCurrentKind(element);
        saveCurrentKind();
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
