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
            "C+Position in Rückenlage 2 Sek. ODER Kopfstand 2 Sek ODER 1/1 Dr. vw. auf gestreckem Bein",
            "VERBINDUNG Chassé – Pferdchen-/Schersprung ODER Sprung mit 1/1 Dr.",
            "Beliebige Standwaage 2 Sek. ODER Spagat 2 Sek. ODER Winkelstütz 2 Sek",
            "Handstand ODER Handstand mit mind. ½ Dr.",
            "Rad ODER einarmiges Rad",
            "Rondat (Radwende)",
            "Handstützüberschlag vw. ODER Handstützüberschlag rw. (Flick-Flack)",
            "Salto vorwärts geh. ODER Salto rückwärts geh."
    };

    private String[] Barren = {
            "Sprung in den Stütz und Vorschwung ODER beliebige (andere) Schwungstemme",
            "4 Schwünge",
            "Vorschwung in den Grätschsitz ODER Oberarmkippe in den Grätschsitz",
            "Außenquersitz",
            "Winkelstütz 2 Sek.",
            "Rolle vw. ODER Rolle rw.",
            "Oberarmstand 2 Sek.",
            "Beliebige Kippe in den Stütz",
            "Beliebige (andere) Schwungstemme ODER Rückschwung in den Handstand",
            "Abgang: Kehre ODER Wende ODER Kreishocke"
    };

    private String[] Ringe = {
            "Stütz 2 Sek.",
            "Winkelstütz 2 Sek. ODER Grätschwinkelstütz 2 Sek.",
            "Kontrolliertes Absenken ODER Abrollen aus dem Stütz",
            "Beugehang 2 Sek.",
            "Winkelhang 2 Sek. mit gehockten ODER gestreckten Beinen",
            "Strecksturzhang 2 Sek.",
            "Aus (Streck-)Sturzhang senken zum Hang(stand) rl.",
            "Heben vom Hang rl. zum Sturzhang",
            "4 Schwünge",
            "Abgang: Salto vorwärts ODER Salto rückwärts"
    };



    private String[] Tiefreck = {
            "Hüftaufschwung",
            "Sprung in den Stütz vl. ODER Vor- oder Rückspreizen eines Beines",
            "Rück- und Vordrehen im Hang ODER Spreizumschwung (Mühle) ODER Sitzumschwung",
            "Hüftumschwung vorlings vorwärts ODER Hüftabschwung vl. vw.",
            "Kipphang 2 Sek. ODER Vorschweben-Rückschweben ODER Vorschweben zum Kästchen 'Beliebige Kippe'",
            "Beliebige Kippe",
            "Rückschwung aus dem Stütz (auch zum Niedersprung)",
            "Hüftumschwung vorlings rückwärts",
            "VERBINDUNG Umschwung - Unterschwung",
            "Abgang: Beliebiger Unterschwung ODER Hocke/Hockwende"
    };

    private String[] Hochreck = {
            "Hüftaufschwung ODER Schwungaufschwung ODER 2. Kippe ODER Schwungstemme rw.",
            "Vor- oder Rückspreizen eines Beines ODER Schwung im Hangverhalten mit ½ Dr.",
            "4 Schwünge ODER Spreizumschwung (Mühle)",
            "Hüftumschwung vorlings vorwärts ODER Hüftabschwung vl. vw.",
            "Kipphang 2 Sek. ODER Vorschwingen zum Kästchen 'Beliebige Kippe'",
            "Beliebige Kippe",
            "Rückschwung aus dem Stütz (auch zum Niedersprung)",
            "Hüftumschwung vorlings rückwärts",
            "VERBINDUNG Umschwung - Unterschwung",
            "Beliebiger Unterschwung"
    };

    private String[] Stufenbarren = {
            "Hüftaufschwung ODER Schwungaufschwung ODER 2. Kippe",
            "Rückschwung zum Aufhocken/-bücken und Sprung zum Hang am o.H.",
            "4 Schwünge ODER Spreizumschwung (Mühle)",
            "Hüftumschwung vorlings vorwärts ODER Hüftabschwung vl. vw.",
            "Kipphang 2 Sek. ODER Vorschweben-Rückschweben ODER Vorschweben/-schwingen zum Kästchen 'Beliebige Kippe'",
            "Beliebige Kippe",
            "Rückschwung aus dem Stütz (auch zum Niedersprung)",
            "Hüftumschwung vorlings rückwärts",
            "VERBINDUNG Umschwung - Unterschwung",
            "Beliebiger Unterschwung"
    };

    private String[] Balken = {
            "Beliebiger Aufgang",
            "Arabeske 2 Sek. ODER ½ Drehung auf beiden Beinen",
            "Beliebige Standwaage 2 Sek.",
            "½ Drehung vw. auf einem Bein",
            "Beidbeiniger Sprung (ohne LAD und Querspreizwinkel) auch als Abgang",
            "Pferdchensprung ODER Schersprung",
            "Sprung mit Querspreizwinkel 90° (alle Varianten)",
            "Handstand",
            "Rad ODER beliebige Rolle vw./rw.",
            "Abgang: Rondat ODER Überschlag vw. ODER Salto vw./rw."
    };

    private String[] Pferd = {
            "Sprung in den Seitsütz vl.",
            "Gewichtsverlagerung links und rechts im Seitsütz vl.",
            "Seitschwingen mit Spreizen links und rechts im Seitsütz vl.",
            "Seitschwingen mit Vorspreizen",
            "Seitschwingen mit Rückspreizen",
            "VERBINDUNG von Seitschwingen mit Vorspreizen und Seitschwingen mit Rückspreizen",
            "VERBINDUNG auf der anderen Seite: Seitschwingen mit Vorspreizen und Seitschwingen mit Rückspreizen",
            "Kreisspreizen eines Beines",
            "Schere vw.",
            "Abgang: Kehre ODER Flanke ODER Wende"
    };




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
