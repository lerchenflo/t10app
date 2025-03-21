package com.lerchenflo.t10elementekatalog;

import android.content.ClipData;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.view.MotionEvent;
import android.widget.ListView;

import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.FileOutputStream;

public class uebungscreator extends AppCompatActivity {

    private RecyclerView elementList;
    private LinearLayout dropZone;
    private Spinner geraeteSpinner;
    private uebungscreator_ElementAdapter adapter;
    private List<String> elements;
    private HashMap<String, String[][]> elementData = new HashMap<>();
    private String selectedgeraet = "Boden"; // Default category
    private int draggedIndex = -1; // Track the index of the dragged element

    private Set<String> addedGroups = new HashSet<>(); // To track groups already added to the drop zone
    private Spinner uebungSpinner;
    private ArrayAdapter<String> uebungAdapter;
    private List<String> uebungenList = new ArrayList<>();
    private SaveFileManager saveFileManager = new SaveFileManager();
    private Kind currentKind = new Kind();
    private String currentUebungName = "Übung1";


    private CardView pdfContainer;
    private PDFView pdfView;
    private View dragHandle;
    private float initialY;
    private int initialHeight;
    private final int MIN_HEIGHT_DP = 150;
    private final int MAX_HEIGHT_DP = 800;
    private int minHeightPx;
    private int maxHeightPx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_uebungscreator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize new views
        uebungSpinner = findViewById(R.id.uebungSpinner);
        dropZone = findViewById(R.id.dropZone);

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


        setupUebungSpinnerLongPress();
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        findViewById(R.id.clear).setOnClickListener(v -> {
            clearRightPanel();
            adapter.resetDisabledElements();

            // Remove elements from the current Geraet and save
            Geraet currentGeraet = getCurrentGeraet();
            currentGeraet._elemente.clear();
            saveCurrentKind();

        });

        dropZone = findViewById(R.id.dropZone);
        geraeteSpinner = findViewById(R.id.geraeteSpinner);

        // Load initial exercise

        // Initialize geraet data
        elementData.put("Boden", constants.Boden);
        elementData.put("Barren", constants.Barren);
        elementData.put("Balken", constants.Balken);
        elementData.put("Pferd", constants.Pferd);
        elementData.put("Stufenbarren", constants.Stufenbarren);
        elementData.put("Hochreck", constants.Hochreck);
        elementData.put("Tiefreck", constants.Tiefreck);
        elementData.put("Ringe", constants.Ringe);

        setupgeraeteSpinner();
        loadElements(selectedgeraet);
        elementList = findViewById(R.id.recyclerView);
        adapter = new uebungscreator_ElementAdapter(elements, this);
        elementList.setLayoutManager(new LinearLayoutManager(this));
        elementList.setAdapter(adapter);

        elementList.addItemDecoration(new uebungscreator_RightDividerItemDecoration(
                this,
                getResources().getColor(android.R.color.darker_gray),
                2, 20
        ));
        loadUebung(currentUebungName);

        setupDragAndDrop();
        setupPDFviewer();

        findViewById(R.id.backbutton_uebungscreator).setOnClickListener(v -> finish());

    }

    private void setupPDFviewer() {
        Button showpdfbutton = findViewById(R.id.showkatalogbutton);
        pdfContainer = findViewById(R.id.pdfview_container);
        pdfView = findViewById(R.id.pdfview_uebungscreator);
        dragHandle = findViewById(R.id.drag_handle);

        // Convert dp to pixels
        minHeightPx = (int) (MIN_HEIGHT_DP * getResources().getDisplayMetrics().density);
        maxHeightPx = (int) (MAX_HEIGHT_DP * getResources().getDisplayMetrics().density);

        // Set up drag listener
        dragHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = event.getRawY();
                        initialHeight = pdfContainer.getHeight();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaY = initialY - event.getRawY();
                        int newHeight = (int) (initialHeight + deltaY);

                        // Constrain height
                        newHeight = Math.max(minHeightPx, Math.min(newHeight, maxHeightPx));

                        // Update layout
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) pdfContainer.getLayoutParams();
                        params.height = newHeight;
                        pdfContainer.setLayoutParams(params);

                        // Optional: Add haptic feedback
                        if (newHeight == minHeightPx || newHeight == maxHeightPx) {
                            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        }
                        return true;
                }
                return false;
            }
        });

        showpdfbutton.setOnClickListener(v -> {
            if (pdfContainer.getVisibility() == View.GONE) {
                pdfContainer.setVisibility(View.VISIBLE);
                // Reset to default height when showing
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) pdfContainer.getLayoutParams();
                params.height = (int) (330 * getResources().getDisplayMetrics().density);
                pdfContainer.setLayoutParams(params);
            } else {
                pdfContainer.setVisibility(View.GONE);
            }
        });
    }


    private void loadSavedUebungen() {
        File directory = getFilesDir();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".creator.kind"));
        Log.d("DEBUG", "Filecount: " + files.length);
        uebungenList.clear();

        // Always add the default exercise first
        uebungenList.add("Übung1");

        // Add existing saved exercises
        if (files != null && files.length > 0) {
            for (File file : files) {
                String name = file.getName().replace(".creator.kind", "");
                Log.d("DEBUG", "File: " + name);
                if (!uebungenList.contains(name)) {  // Prevent duplicates
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

            // Update UI for current geraet
            refreshDropZone();

            // Update geraet spinner if needed
            String loadedgeraet = currentKind._geraete.isEmpty() ?
                    selectedgeraet :
                    currentKind._geraete.get(0)._geraetname;

            if (!loadedgeraet.equals(selectedgeraet)) {
                int position = new ArrayList<>(elementData.keySet()).indexOf(loadedgeraet);
                if (position >= 0) {
                    geraeteSpinner.setSelection(position);
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
        for (int i = 0; i < dropZone.getChildCount(); i += 2) { // Skip separators
            View child = dropZone.getChildAt(i);
            if (child instanceof TextView) {
                geraet._elemente.add(((TextView) child).getText().toString());
            }
        }
        saveCurrentKind();
    }

    private void refreshDropZone() {
        clearRightPanel();
        if (adapter != null) {
            adapter.resetDisabledElements(); // Reset disabled elements
        } else {
            Log.e("refreshDropZone", "Adapter is null!");
        }
        List<String> elements = currentKind.getGeraetElements(selectedgeraet);
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

    private void deleteCurrentKind() {
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

    private void setupgeraeteSpinner() {
        List<String> categories = new ArrayList<>(elementData.keySet());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        geraeteSpinner.setAdapter(spinnerAdapter);

        geraeteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedgeraet = categories.get(position);
                loadElements(selectedgeraet);

                // Clear the right panel before updating
                clearRightPanel();
                adapter.resetDisabledElements();
                refreshDropZone();
                // Update the RecyclerView with the new elements
                adapter.updateElements(elements);

                //PDF View finden
                PDFView pdfView = findViewById(R.id.pdfview_uebungscreator);
                String pdfname = selectedgeraet;
                //Namen übersetzen
                pdfname = pdfname.contains("reck")? "Reck": pdfname;
                pdfname = pdfname.contains("Stufenbarren")? "Reck": pdfname;
                //PDF laden
                pdfView.fromAsset(pdfname + ".pdf")
                        .enableSwipe(true) // Enable swiping to change pages
                        .swipeHorizontal(false) // Set false for vertical swiping
                        .enableDoubletap(true) // Enable double tap to zoom
                        .enableAntialiasing(true)
                        .enableAnnotationRendering(true)
                        .defaultPage(0) // Show the first page
                        .load();
                Log.d("PDF geladen", selectedgeraet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadElements(String geraet) {
        elements = new ArrayList<>();
        if (elementData.containsKey(geraet)) {
            for (String[] group : elementData.get(geraet)) {
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
        setupLeftPanelDropHandler();
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
            if (g._geraetname.equals(selectedgeraet)) {
                return g;
            }
        }
        Geraet newGeraet = new Geraet();
        newGeraet._geraetname = selectedgeraet;
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

        // Enable drag for reordering and deletion
        textView.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("element", element);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });

        // Add click feedback
        textView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("element", element);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
            return false;
        });

        dropZone.addView(textView);
        dropZone.addView(createSeparator());
        addedGroups.add(group);
        addElementToCurrentKind(element);
        saveCurrentKind();
    }

    private void disableAlternativeElements(String selectedElement) {
        // Get the groups for the current geraet
        String[][] groups = elementData.get(selectedgeraet);
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
        // Get groups for the current geraet
        String[][] groups = elementData.get(selectedgeraet);
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

    private void setupUebungSpinnerLongPress() {
        uebungSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.postDelayed(() -> {
                    try {
                        Field popupField = Spinner.class.getDeclaredField("mPopup");
                        popupField.setAccessible(true);
                        Object popup = popupField.get(uebungSpinner);

                        if (popup instanceof ListPopupWindow) {
                            ListPopupWindow listPopup = (ListPopupWindow) popup;

                            if (!listPopup.isShowing()) {
                                listPopup.show(); // Open dropdown only on long press
                            }

                            // Ensure listView is ready before setting long click
                            v.postDelayed(() -> {
                                ListView listView = listPopup.getListView();
                                if (listView != null) {
                                    listView.setOnItemLongClickListener((parent, view, position, id) -> {
                                        String selectedUebung = uebungenList.get(position);
                                        if (!selectedUebung.equals("Übung hinzufügen")) {
                                            showContextMenu(view, position);
                                        }
                                        return true;
                                    });
                                }
                            }, 50);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 300); // Longer delay to distinguish from normal taps
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.removeCallbacks(null); // Cancel long press if user releases early
            }
            return event.getAction() == MotionEvent.ACTION_DOWN; // Consume only long presses
        });
    }


    private void showContextMenu(View anchorView, int position) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.uebung_context_menu, popup.getMenu());

        // Check if it's the last Kind (excluding "Übung hinzufügen")
        int totalKinds = uebungenList.size() - 1;
        boolean isLastKind = totalKinds <= 1;

        // Hide delete option if last Kind
        MenuItem deleteItem = popup.getMenu().findItem(R.id.delete);
        deleteItem.setVisible(!isLastKind);

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete) {
                if (totalKinds > 1) { // Additional check
                    deleteUebung(position);
                }
                return true;
            } else if (item.getItemId() == R.id.share) {
                shareUebung(position);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void deleteUebung(int position) {
        String uebungName = uebungenList.get(position);
        int totalKinds = uebungenList.size() - 1; // Exclude "Übung hinzufügen"
        if (totalKinds <= 1) return; // Prevent deletion of last Kind

        if (position == uebungenList.size() - 1 || uebungName.equals("Übung hinzufügen")) return;

        // Delete file and update list
        File file = new File(getFilesDir(), uebungName + ".creator.kind");
        if (file.exists()) file.delete();

        uebungenList.remove(position);
        uebungAdapter.notifyDataSetChanged();

        // Load the first Kind if current was deleted
        if (currentUebungName.equals(uebungName)) {
            currentUebungName = uebungenList.get(0);
            loadUebung(currentUebungName);
            uebungSpinner.setSelection(0);
        }
    }

    private void shareUebung(int position) {
        String uebungName = uebungenList.get(position);
        if (uebungName.equals("Übung hinzufügen")) return;

        try {
            Kind kindToShare = saveFileManager.loadKind(this, uebungName);
            String json = kindToShare.toJson();

            // Use .sharedkind extension for the file
            File file = new File(getCacheDir(), uebungName + ".sharedkind");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.getBytes());
            fos.close();

            Uri contentUri = FileProvider.getUriForFile(this,
                    "com.lerchenflo.t10elementekatalog.fileprovider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("*/*"); // MIME type for text files

            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Übung"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupLeftPanelDropHandler() {
        elementList.setOnDragListener((v, event) -> {
            View draggedView = (View) event.getLocalState();

            // Only handle drags originating from the drop zone
            if (draggedView.getParent() != dropZone) {
                return false;
            }

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Accept drags from drop zone
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    // Visual feedback if needed
                    v.setBackgroundColor(getResources().getColor(R.color.drag_target));
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    // Clear visual feedback
                    v.setBackgroundColor(Color.TRANSPARENT);
                    return true;

                case DragEvent.ACTION_DROP:
                    String element = ((TextView) draggedView).getText().toString();
                    removeElementFromDropZone(element);
                    v.setBackgroundColor(Color.TRANSPARENT);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    return true;

                default:
                    return false;
            }
        });
    }
}