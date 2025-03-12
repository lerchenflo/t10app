package com.lerchenflo.t10elementekatalog;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;
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
    private ElementAdapter adapter;
    private List<String> elements;
    private HashMap<String, String[]> elementGroups = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uebungscreator);

        elementList = findViewById(R.id.recyclerView);
        dropZone = findViewById(R.id.dropZone);

        elements = loadElements();
        adapter = new ElementAdapter(elements, this);

        elementList.setLayoutManager(new LinearLayoutManager(this));
        elementList.setAdapter(adapter);

        setupDragAndDrop();
        // Back button: returns to MainActivity.
        findViewById(R.id.backbutton_uebungscreator).setOnClickListener(v -> finish());

    }

    private List<String> loadElements() {
        List<String> allElements = new ArrayList<>();

        addElementsFromArray("Boden", constants.Boden, allElements);
        addElementsFromArray("Barren", constants.Barren, allElements);
        addElementsFromArray("Ringe", constants.Ringe, allElements);

        return allElements;
    }

    private void addElementsFromArray(String category, String[][] array, List<String> list) {
        for (String[] group : array) {
            for (String item : group) {
                list.add(item);
                elementGroups.put(item, group);
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
            if (child instanceof TextView) { // Check if it's a TextView before casting
                TextView textView = (TextView) child;
                if (textView.getText().toString().equals(element)) {
                    return true;
                }
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
                LinearLayout.LayoutParams.MATCH_PARENT, // Full width
                2 // Height of the separator (adjust as needed)
        ));
        separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        dropZone.addView(textView);
        dropZone.addView(separator);
    }

    //private void addElementToDropZone(String element) {
    //    TextView textView = new TextView(this);
    //    textView.setText(element);
    //    textView.setPadding(10, 10, 10, 10);
    //    dropZone.addView(textView);
    //}

    private void disableAlternativeElements(String selected) {
        String[] group = elementGroups.get(selected);
        if (group != null) {
            adapter.disableElements(group);
        }
    }
}
