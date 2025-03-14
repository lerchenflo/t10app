package com.lerchenflo.t10elementekatalog;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveFileManager {

    public void saveKind(Context context, Kind kind, String Kindname) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(context.getFilesDir() + Kindname + ".creator.kind")) {
            gson.toJson(kind, writer);
        }
    }

    public Kind loadKind(Context context, String Kindname) throws IOException {
        File file = new File(context.getFilesDir()+ Kindname + ".creator.kind");
        if (!file.exists()) {
            return new Kind(); // Return a new empty Kind if the file doesn't exist
        }

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(file)) {
            Kind kind = gson.fromJson(reader, Kind.class);
            return kind != null ? kind : new Kind();
        }
    }
}

class Kind {
    public String _name = "";
    public List<Geraet> _geraete = new ArrayList<>();

    public List<String> getGeraetElements(String geraetName) {
        for (Geraet geraet : _geraete) {
            if (geraet._geraetname.equals(geraetName)) {
                return geraet._elemente;
            }
        }
        return new ArrayList<>(); // Return an empty list if no matching Geraet is found
    }
}

class Geraet {
    public String _geraetname = ""; // Added name field
    public List<String> _elemente = new ArrayList<>();
}

