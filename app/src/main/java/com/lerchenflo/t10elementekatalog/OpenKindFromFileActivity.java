package com.lerchenflo.t10elementekatalog;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OpenKindFromFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open_kind_from_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Retrieve the data from the intent
        Intent intent = getIntent();
        Uri fileUri = intent.getData();

        if (fileUri != null) {
            // Handle the file URI accordingly
            // For example, you might open an InputStream to read the file:
            try {
                StringBuilder stringBuilder = new StringBuilder();
                try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                }

                //Kind eingelesen, popup
                AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
                inputDialog.setTitle("Kindname eingeben");

                final EditText input = new EditText(this);
                inputDialog.setView(input);

                inputDialog.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        // Use the entered name
                        //Toast.makeText(getApplicationContext(), "Entered: " + name, Toast.LENGTH_SHORT).show();
                        Gson gson = new Gson();
                        Kind kind = gson.fromJson(stringBuilder.toString(), Kind.class);


                        SaveFileManager s = new SaveFileManager();
                        try {
                            s.saveKind(OpenKindFromFileActivity.this, kind, name);
                            Intent i = new Intent(OpenKindFromFileActivity.this, uebungscreator.class);
                            startActivity(i);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                inputDialog.setNegativeButton("Abbrechen", null);
                inputDialog.show();

            } catch (Exception e) {
                Toast.makeText(OpenKindFromFileActivity.this, "Konnte nicht geladen werden", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

    }
}