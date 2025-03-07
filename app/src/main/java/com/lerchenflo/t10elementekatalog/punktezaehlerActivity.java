package com.lerchenflo.t10elementekatalog;

import static com.lerchenflo.t10elementekatalog.constants.Balken;
import static com.lerchenflo.t10elementekatalog.constants.Barren;
import static com.lerchenflo.t10elementekatalog.constants.Boden;
import static com.lerchenflo.t10elementekatalog.constants.Hochreck;
import static com.lerchenflo.t10elementekatalog.constants.Pferd;
import static com.lerchenflo.t10elementekatalog.constants.Ringe;
import static com.lerchenflo.t10elementekatalog.constants.Stufenbarren;
import static com.lerchenflo.t10elementekatalog.constants.Tiefreck;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class punktezaehlerActivity extends AppCompatActivity {

    private Button[] buttons = new Button[10];
    // New arrays with "ODER" options




    // Combine arrays into one array of arrays.
    private String[][] buttonTextArrays = { Boden, Barren, Balken, Pferd, Ringe, Tiefreck, Hochreck, Stufenbarren};

    // Spinner options corresponding to the arrays.
    private String[] spinnerItems = {"Boden", "Barren", "Balken", "Pferd", "Ringe", "Tiefreck", "Hochreck", "Stufenbarren"};

    // Default color matching the XML background tint (#CCCCCC)
    private final int defaultColor = Color.parseColor("#CCCCCC");


    // Counter for the number of selected buttons
    private int selectedCount = 0;
    private TextView counterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punktezaehler);

        // Setup the Spinner (title) with a custom layout for bigger, centered text
        Spinner spinner = findViewById(R.id.titleSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout_punktecounter, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Get the counter TextView from layout
        counterText = findViewById(R.id.counterText);
        updateCounter();

        // Initialize buttons: assign from XML, set default texts, and attach listeners.
        for (int i = 0; i < 10; i++) {
            int resID = getResources().getIdentifier("button" + (i + 1), "id", getPackageName());
            buttons[i] = findViewById(resID);
            // Initially use the first array (Boden)
            if (i < Boden.length) {
                buttons[i].setText(Boden[i]);
                buttons[i].setEnabled(true);
            } else {
                buttons[i].setText("");
                buttons[i].setEnabled(false);
            }
            // Use a tag to track selection status (false initially)
            buttons[i].setTag(false);

            // On click: if not already selected, mark as selected, change color, and update counter.
            buttons[i].setOnClickListener(v -> {
                if (!Boolean.TRUE.equals(v.getTag())) {
                    v.setBackgroundColor(Color.GREEN);
                    v.setTag(true);
                    selectedCount++;
                    updateCounter();
                }
            });

            // On long click: if selected, reset background and update counter.
            buttons[i].setOnLongClickListener(v -> {
                if (Boolean.TRUE.equals(v.getTag())) {
                    v.setBackgroundColor(defaultColor);
                    v.setTag(false);
                    selectedCount = Math.max(0, selectedCount - 1);
                    updateCounter();
                }
                return true;
            });
        }

        // When a new spinner item is selected, update all button texts based on the selected array.
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] selectedArray = buttonTextArrays[position];
                // Reset counter since we're changing the set.
                selectedCount = 0;
                updateCounter();
                for (int i = 0; i < 10; i++) {
                    if (i < selectedArray.length) {
                        buttons[i].setText(selectedArray[i]);
                        buttons[i].setEnabled(true);
                    } else {
                        buttons[i].setText("");
                        buttons[i].setEnabled(false);
                    }
                    // Also reset the background and selection tag.
                    buttons[i].setBackgroundColor(defaultColor);
                    buttons[i].setTag(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Reset button: restore all buttons to default background and clear selection counter.
        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> {
            resetButtons();
            selectedCount = 0;
            updateCounter();
        });

        //Backbutton
        findViewById(R.id.backbutton_punktecounter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(punktezaehlerActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }

    // Method to update the counter TextView.
    private void updateCounter() {
        counterText.setText(selectedCount + "/10");
    }

    // Method to reset all buttons to default appearance.
    private void resetButtons() {
        for (Button button : buttons) {
            button.setBackgroundColor(defaultColor);
            button.setTag(false);
        }
    }
}
