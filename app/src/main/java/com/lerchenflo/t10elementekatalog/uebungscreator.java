package com.lerchenflo.t10elementekatalog;

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
import java.util.List;

public class uebungscreator extends AppCompatActivity {

    private RecyclerView elementList;
    private LinearLayout dropZone;
    private Spinner categorySpinner;
    private ElementAdapter adapter;
    private List<String> elements;
    private HashMap<String, String[][]> elementData = new HashMap<>();
    private String selectedCategory = "Boden"; // Default category

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uebungscreator);

        elementList = findViewById(R.id.recyclerView);
        dropZone = findViewById(R.id.dropZone);
        categorySpinner = findViewById(R.id.categorySpinner);

        // Initialize category data
        elementData.put("Boden", constants.Boden);
        elementData.put("Barren", constants.Barren);
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
                adapter.updateElements(elements); // Update the RecyclerView
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

                    if (!isElementInDropZone(element)) {
                        addElementToDropZone(element);
                        disableAlternativeElements(element);
                    }
                    break;

            }
            return true;
        });
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

    private void addElementToDropZone(String element) {
        TextView textView = new TextView(this);
        textView.setText(element);
        textView.setPadding(10, 10, 10, 10);

        View separator = new View(this);
        separator.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2
        ));
        //separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        dropZone.addView(textView);
        dropZone.addView(separator);
    }
    private void disableAlternativeElements(String selected) {
        // Get the groups for the selected category
        String[][] groups = elementData.get(selected);
        if (groups != null) {
            // Flatten the groups into a single list of elements
            List<String> allElementsInGroup = new ArrayList<>();
            for (String[] group : groups) {
                for (String item : group) {
                    allElementsInGroup.add(item);
                }
            }
            // Disable all elements in the group
            adapter.disableElements(allElementsInGroup.toArray(new String[0]));
        }
    }
}
