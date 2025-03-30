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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Field;

public class punktezaehlerActivity extends AppCompatActivity {

    private MaterialButton[] buttons = new MaterialButton[10];
    private final String[][][] buttonTextArrays = { Boden, Barren, Balken, Pferd, Ringe, Tiefreck, Hochreck, Stufenbarren };
    private final String[] spinnerItems = {"Boden", "Barren", "Balken", "Pferd", "Ringe", "Tiefreck", "Hochreck", "Stufenbarren"};
    private final int defaultColor = Color.parseColor("#2D2D2D");
    private final int selectedColor = Color.parseColor("#4CAF50");
    private int selectedCount = 0;
    private TextView counterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_punktezaehler);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        Spinner spinner = findViewById(R.id.titleSpinner);
        counterText = findViewById(R.id.counterText);
        ImageButton resetButton = findViewById(R.id.resetButton);
        ImageButton backButton = findViewById(R.id.backbutton_punktecounter);


        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_dark,
                spinnerItems
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_dark);
        spinner.setAdapter(adapter);

        
        try {
            Field popupField = Spinner.class.getDeclaredField("mPopup");
            popupField.setAccessible(true);
            ListPopupWindow popup = (ListPopupWindow) popupField.get(spinner);
            popup.setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_dropdown_background));
        } catch (Exception e) {
            Log.e("SpinnerBackground", "Failed to set uebungSpinner background", e);
        }

        // Button initialization
        for (int i = 0; i < 10; i++) {
            int resID = getResources().getIdentifier("button" + (i + 1), "id", getPackageName());
            buttons[i] = findViewById(resID);
            buttons[i].setBackgroundTintList(ColorStateList.valueOf(defaultColor));
            buttons[i].setTag(false);

            final int index = i;
            buttons[i].setOnClickListener(v -> handleButtonClick(index));
            buttons[i].setOnLongClickListener(v -> handleLongClick(index));
        }

        // Spinner selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateButtonsForSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Button click listeners
        resetButton.setOnClickListener(v -> resetAll());
        backButton.setOnClickListener(v -> navigateToMain());

    }

    private void handleButtonClick(int index) {
        MaterialButton button = buttons[index];
        if (!Boolean.TRUE.equals(button.getTag())) {
            button.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
            button.setTag(true);
            selectedCount++;
            updateCounter();
        }
    }

    private boolean handleLongClick(int index) {
        MaterialButton button = buttons[index];
        if (Boolean.TRUE.equals(button.getTag())) {
            button.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
            button.setTag(false);
            selectedCount = Math.max(0, selectedCount - 1);
            updateCounter();
        }
        return true;
    }

    private void updateButtonsForSelection(int position) {
        String[][] selectedArray = buttonTextArrays[position];
        selectedCount = 0;
        updateCounter();

        for (int i = 0; i < 10; i++) {
            MaterialButton button = buttons[i];
            if (i < selectedArray.length) {
                button.setText(joinWithOder(selectedArray[i]));
                button.setEnabled(true);
            } else {
                button.setText("");
                button.setEnabled(false);
            }
            button.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
            button.setTag(false);
        }
    }

    private void resetAll() {
        for (MaterialButton button : buttons) {
            if (button.isEnabled()) {
                button.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                button.setTag(false);
            }
        }
        selectedCount = 0;
        updateCounter();

    }

    private void updateCounter() {
        counterText.setText(String.format("%d/10", selectedCount));
    }

    private String joinWithOder(String[] texts) {
        return String.join(" ODER ", texts);
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void handleSave() {
        // Implement save functionality
    }
}