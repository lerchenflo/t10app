package com.lerchenflo.t10elementekatalog;

import android.content.ClipData;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.net.Uri;

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

        // Initialize views
        uebungSpinner = findViewById(R.id.uebungSpinner);
        dropZone = findViewById(R.id.dropZone);
        geraeteSpinner = findViewById(R.id.geraeteSpinner);

        // Setup Übung Spinner
        uebungAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_dark, uebungenList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_item_dark, parent, false);
                }
                TextView textView = convertView.findViewById(android.R.id.text1);
                textView.setText(uebungenList.get(position));
                textView.setTextColor(Color.WHITE);
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_dropdown_item_dark, parent, false);
                }
                TextView textView = convertView.findViewById(android.R.id.text1);
                textView.setText(uebungenList.get(position));
                textView.setTextColor(Color.WHITE);
                return convertView;
            }
        };
        uebungSpinner.setAdapter(uebungAdapter);
        loadSavedUebungen();

        // Initialize geraet data
        elementData.put("Boden", constants.Boden);
        elementData.put("Barren", constants.Barren);
        elementData.put("Balken", constants.Balken);
        elementData.put("Pferd", constants.Pferd);
        elementData.put("Stufenbarren", constants.Stufenbarren);
        elementData.put("Hochreck", constants.Hochreck);
        elementData.put("Tiefreck", constants.Tiefreck);
        elementData.put("Ringe", constants.Ringe);

        // Setup Geraete Spinner
        List<String> categories = new ArrayList<>(elementData.keySet());
        Log.d("GeraeteSpinner", "Categories size: " + categories.size() + ", Contents: " + categories.toString());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_dark, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_item_dark, parent, false);
                }
                TextView textView = convertView.findViewById(android.R.id.text1);
                String item = getItem(position);
                textView.setText(item);
                textView.setTextColor(Color.WHITE);
                Log.d("GeraeteSpinner", "getView position: " + position + ", item: " + item);
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_dropdown_item_dark, parent, false);
                }
                TextView textView = convertView.findViewById(android.R.id.text1);
                String item = getItem(position);
                textView.setText(item);
                textView.setTextColor(Color.WHITE);
                Log.d("GeraeteSpinner", "getDropDownView position: " + position + ", item: " + item);
                return convertView;
            }
        };
        geraeteSpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
        geraeteSpinner.setEnabled(true);
        geraeteSpinner.setClickable(true);
        if (!categories.isEmpty()) {
            geraeteSpinner.setSelection(0);
        }

        // Rest of onCreate
        findViewById(R.id.backbutton_uebungscreator).setOnClickListener(v -> finish());
        setupUebungSpinnerLongPress();
        uebungSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = uebungenList.get(position);
                if (selected.equals("Übung hinzufügen")) {
                    showAddUebungDialog();
                    uebungSpinner.setSelection(0);
                } else if (!selected.equals(currentUebungName)) {
                    currentUebungName = selected;
                    loadUebung(currentUebungName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        findViewById(R.id.clear).setOnClickListener(v -> {
            clearRightPanel();
            adapter.resetDisabledElements();
            Geraet currentGeraet = getCurrentGeraet();
            currentGeraet._elemente.clear();
            saveCurrentKind();
        });

        setupgeraeteSpinner();
        loadElements(selectedgeraet);
        elementList = findViewById(R.id.recyclerView);
        adapter = new uebungscreator_ElementAdapter(elements, this) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                if (holder.itemView.getAlpha() < 1.0f) {
                    holder.itemView.setBackgroundResource(R.drawable.disabled_item_background);
                } else {
                    holder.itemView.setBackgroundResource(R.drawable.spinner_item_background);
                }
            }
        };
        elementList.setLayoutManager(new LinearLayoutManager(this));
        elementList.setAdapter(adapter);

        // Add spacing instead of divider
        int spacingInPixels = (int) (8 * getResources().getDisplayMetrics().density); // 8dp converted to pixels
        elementList.addItemDecoration(new SpacingItemDecoration(spacingInPixels));

        loadUebung(currentUebungName);
        setupDragAndDrop();
        setupPDFviewer();
    }

    // New SpacingItemDecoration class
    public static class SpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public SpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            // Add spacing to all items except the last one
            if (position != parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = spacing;
            }
            // Add top spacing to the first item only
            if (position == 0) {
                outRect.top = spacing;
            }
        }
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
        Log.d("DEBUG", "Filecount: " + (files != null ? files.length : 0));
        uebungenList.clear();
        uebungenList.add("Übung1");
        if (files != null && files.length > 0) {
            for (File file : files) {
                String name = file.getName().replace(".creator.kind", "");
                if (!uebungenList.contains(name)) {
                    uebungenList.add(name);
                }
            }
        }
        uebungenList.add("Übung hinzufügen");
        uebungAdapter.notifyDataSetChanged();
    }

    private void loadUebung(String uebungName) {
        try {
            currentKind = saveFileManager.loadKind(this, uebungName);
            currentUebungName = uebungName;
            refreshDropZone();
            String loadedgeraet = currentKind._geraete.isEmpty() ? selectedgeraet : currentKind._geraete.get(0)._geraetname;
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
        for (int i = 0; i < dropZone.getChildCount(); i++) {
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
            adapter.resetDisabledElements();
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
                Kind newKind = new Kind();
                newKind._name = newName;
                try {
                    saveFileManager.saveKind(this, newKind, newName);
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
        File file = new File(getFilesDir(), current + ".creator.kind");
        if (file.exists()) file.delete();
        uebungenList.remove(position);
        uebungAdapter.notifyDataSetChanged();
        uebungSpinner.setSelection(position > 0 ? position - 1 : 0);
        loadUebung(uebungenList.get(uebungSpinner.getSelectedItemPosition()));
    }

    private void setupgeraeteSpinner() {
        geraeteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedgeraet = parent.getItemAtPosition(position).toString();
                loadElements(selectedgeraet);
                clearRightPanel();
                adapter.resetDisabledElements();
                refreshDropZone();
                adapter.updateElements(elements);
                PDFView pdfView = findViewById(R.id.pdfview_uebungscreator);
                String pdfname = selectedgeraet;
                pdfname = pdfname.contains("reck") ? "Reck" : pdfname;
                pdfname = pdfname.contains("Stufenbarren") ? "Reck" : pdfname;
                pdfView.fromAsset(pdfname + ".pdf")
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .enableAntialiasing(true)
                        .enableAnnotationRendering(true)
                        .defaultPage(0)
                        .load();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
        dropZone.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    View draggedView = (View) event.getLocalState();
                    if (draggedView.getParent() == dropZone) {
                        draggedIndex = dropZone.indexOfChild(draggedView);
                    }
                    return true;

                case DragEvent.ACTION_DROP:
                    draggedView = (View) event.getLocalState();
                    String element = ((TextView) draggedView).getText().toString();
                    if (draggedView.getParent() == dropZone) {
                        int insertIndex = findInsertIndex(event.getY());
                        dropZone.removeViewAt(draggedIndex);
                        if (draggedIndex < insertIndex) insertIndex--;
                        dropZone.addView(draggedView, insertIndex);
                        updateElementOrderInModel();
                    } else {
                        String group = getGroupForElement(element);
                        if (!addedGroups.contains(group) && !isElementInDropZone(element)) {
                            addElementToDropZone(element, group);
                            disableAlternativeElements(element);
                        }
                    }
                    return true;
            }
            saveCurrentKind();
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
            saveFileManager.saveKind(this, currentKind, currentUebungName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DEBUG", "Error saving Kind");
        }
    }

    private String getGroupForElement(String element) {
        for (String[][] groupArray : elementData.values()) {
            for (String[] group : groupArray) {
                for (String item : group) {
                    if (item.equals(element)) {
                        return String.join(", ", group);
                    }
                }
            }
        }
        return "";
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
        textView.setBackgroundResource(R.drawable.spinner_item_background);
        textView.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 8); // 8dp spacing
        textView.setLayoutParams(params);

        textView.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("element", element);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });

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
        addedGroups.add(group);
        addElementToCurrentKind(element);
        saveCurrentKind();
    }

    private void disableAlternativeElements(String selectedElement) {
        String[][] groups = elementData.get(selectedgeraet);
        if (groups != null) {
            List<String> elementsToDisable = new ArrayList<>();
            for (String[] group : groups) {
                for (String item : group) {
                    if (item.equals(selectedElement)) {
                        for (String element : group) {
                            elementsToDisable.add(element);
                        }
                        break;
                    }
                }
                if (!elementsToDisable.isEmpty()) break;
            }
            adapter.disableElements(elementsToDisable.toArray(new String[0]));
        }
    }

    private void clearRightPanel() {
        dropZone.removeAllViews();
        addedGroups.clear();
    }

    private void removeElementFromDropZone(String element) {
        for (int i = 0; i < dropZone.getChildCount(); i++) {
            View child = dropZone.getChildAt(i);
            if (child instanceof TextView && ((TextView) child).getText().toString().equals(element)) {
                dropZone.removeViewAt(i);
                break;
            }
        }
        String groupIdentifier = getGroupForElement(element);
        addedGroups.remove(groupIdentifier);
        enableElementsInGroup(element);
        removeElementFromCurrentKind(element);
        saveCurrentKind();
    }

    private void enableElementsInGroup(String element) {
        String[][] groups = elementData.get(selectedgeraet);
        if (groups != null) {
            for (String[] group : groups) {
                for (String item : group) {
                    if (item.equals(element)) {
                        adapter.enableElements(group);
                        break;
                    }
                }
            }
        }
    }

    private int findInsertIndex(float y) {
        for (int i = 0; i < dropZone.getChildCount(); i++) {
            View child = dropZone.getChildAt(i);
            if (child instanceof TextView && y < child.getY() + child.getHeight() / 2) {
                return i;
            }
        }
        return dropZone.getChildCount();
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
                                listPopup.show();
                            }
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
                }, 300);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.removeCallbacks(null);
            }
            return event.getAction() == MotionEvent.ACTION_DOWN;
        });
    }

    private void showContextMenu(View anchorView, int position) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.uebung_context_menu, popup.getMenu());
        int totalKinds = uebungenList.size() - 1;
        boolean isLastKind = totalKinds <= 1;
        MenuItem deleteItem = popup.getMenu().findItem(R.id.delete);
        deleteItem.setVisible(!isLastKind);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete && totalKinds > 1) {
                deleteUebung(position);
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
        int totalKinds = uebungenList.size() - 1;
        if (totalKinds <= 1 || position == uebungenList.size() - 1 || uebungName.equals("Übung hinzufügen")) return;
        File file = new File(getFilesDir(), uebungName + ".creator.kind");
        if (file.exists()) file.delete();
        uebungenList.remove(position);
        uebungAdapter.notifyDataSetChanged();
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
            File file = new File(getCacheDir(), uebungName + ".sharedkind");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.getBytes());
            fos.close();
            Uri contentUri = FileProvider.getUriForFile(this, "com.lerchenflo.t10elementekatalog.fileprovider", file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("*/*");
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
            if (draggedView.getParent() != dropZone) return false;
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(getResources().getColor(R.color.drag_target));
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    return true;
                case DragEvent.ACTION_DROP:
                    String element = ((TextView) draggedView).getText().toString();
                    removeElementFromDropZone(element);
                    v.setBackgroundColor(Color.TRANSPARENT);
                    return true;
                default:
                    return false;
            }
        });
    }
}