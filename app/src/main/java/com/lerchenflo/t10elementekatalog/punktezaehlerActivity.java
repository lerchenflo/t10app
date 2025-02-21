package com.lerchenflo.t10elementekatalog;

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
    private String[] Boden = {
            "Beliebige Rolle vorwärts",
            "Beliebige Rolle rückwärts ODER Kerze 2 Sek.",
            "Krafthandstand ODER Sprung mit Querspreizwinkel 135°",
            "C+Position in Rückenlage 2 Sek. ODER Kopfstand 2 Sek.",
            "1/1 Dr. vw. auf gestrecktem Bein ODER Überschlag vw.",
            "VERBINDUNG Chassé – Pferdchen-/Schersprung ODER Sprung mit 1/1 Dr.",
            "Salto vorwärts (ohne LAD)",
            "Beliebige Standwaage 2 Sek. ODER Spagat 2 Sek.",
            "Winkelstütz 2 Sek.",
            "Rondat (Radwende) ODER Rad"
    };

    private String[] Barren = {
            "Sprung in den Stütz und Vorschwung ODER beliebige Schwungstemme",
            "Winkelstütz 2 Sek.",
            "4 Schwünge",
            "Rolle vw. ODER Rolle rw.",
            "Vorschwung in den Grätschsitz ODER Oberarmkippe in den Grätschsitz",
            "Oberarmstand 2 Sek.",
            "Außenquersitz ODER beliebige Kippe in den Stütz",
            "Schwungstemme vw. ODER Schwungstemme rw.",
            "Handstand 2 Sek.",
            "Abgang: Kreishocke ODER Salto vw. aus dem Stütz ODER Salto rw. aus dem Stütz"
    };

    private String[] Reck = {
            "Hüftaufschwung",
            "Hüftumschwung vorlings vorwärts ODER Hüftumschwung vorlings rückwärts",
            "Freie Felge (mind. waagrecht) ODER 2. Riesenfelge",
            "4 Schwünge",
            "Beliebiger Unterschwung ODER Konterunterschwung aus dem Hang",
            "Schwung im Hangverhalten mit ½ Dr. ODER Holmwechsel vom unteren Holm zum Hang am oberen Holm",
            "Sohlumschwung vw. ODER Sohlumschwung rw.",
            "Ausgrätschen in den Hang ODER VERBINDUNG Freie Felge – Riesenfelge rw.",
            "Riesenfelge vw. ODER Riesenfelge rw.",
            "Abgang beliebiger Salto rw. aus dem Vorschwung"
    };

    private String[] Sprung = {
            "Sprung mit ¾ Drehung",
            "Hocksprung",
            "Bücksprung",
            "Strecksprung",
            "Hock-Bück-Streck",
            "Überschlag vw.",
            "Yamashita",
            "Sprung mit mind. 1 ½ Dr. ODER Mind. 1 ½ Dr. vw. auf gestrecktem Bein",
            "Salto vorwärts mit ½ Dr. ODER Salto rückwärts mit ½ Dr.",
            "Salto vorwärts mit 1/1 Dr. ODER Salto rückwärts mit 1/1 Dr."
    };

    private String[] Minitrampolin = {
            "Strecksprung",
            "Hocksprung",
            "Bücksprung",
            "Grätschsprung",
            "Sprung mit ½ Drehung",
            "Sprung mit 1/1 Drehung",
            "Salto vorwärts",
            "Salto vorwärts mit ½ Dr. ODER Salto rückwärts mit ½ Dr.",
            "Salto vorwärts mit 1/1 Dr. ODER Salto rückwärts mit 1/1 Dr.",
            "Salto seitwärts"
    };

    private String[] Balken = {
            "Arabeske 2 Sek.",
            "Standwaage 2 Sek. ODER Spagat 2 Sek.",
            "1/2 Drehung auf beiden Beinen",
            "1/1 Drehung vorwärts",
            "Rad ODER Rondat",
            "Bogengang rückwärts",
            "Beidbeiniger Sprung mit mind. ¾ Dr.",
            "Beidbeiniger Strecksprung ODER beliebiger Sprung ohne LAD",
            "Überschlag vw. ODER Überschlag rw. (Flick-Flack)",
            "Abgang: Rondat ODER Überschlag vw. ODER Salto vw./rw."
    };

    private String[] Pferd = {
            "Sprung in den Seitstütz vorlings",
            "Gewichtsverlagerung links und rechts im Seitstütz",
            "Seitschwingen mit Spreizen",
            "Schere vorwärts ODER Schere rückwärts",
            "Beliebige Schere mit ½ Drehung",
            "Kreisflanke",
            "Abgang: Kehre ODER Flanke ODER Wende",
            "Schwungvolle Kreisflanke ODER Kombination aus zwei verschiedenen Scheren",
            "Doppelschere ODER Kehre-Flanke",
            "Zwei Kreisflanken hintereinander"
    };

    private String[] Ringe = {
            "Stütz 2 Sek.",
            "Winkelstütz 2 Sek. ODER Beugehang 2 Sek.",
            "4 Schwünge",
            "Überschlag rückwärts",
            "Schwungstemme vorwärts ODER Schwungstemme rückwärts",
            "Kraftschwung zum Stütz ODER Felge vorlings",
            "Krafthandstand",
            "Riesenfelge vorwärts ODER Riesenfelge rückwärts",
            "Drehung in den Hang ODER Riesenfelge mit ½ Drehung",
            "Abgang: Salto vorwärts ODER Salto rückwärts"
    };

    // Combine arrays into one array of arrays.
    private String[][] buttonTextArrays = { Boden, Barren, Reck, Sprung, Minitrampolin, Balken, Pferd, Ringe };

    // Spinner options corresponding to the arrays.
    private String[] spinnerItems = {"Boden", "Barren", "Reck", "Sprung", "Minitrampolin", "Balken", "Pferd", "Ringe"};

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
