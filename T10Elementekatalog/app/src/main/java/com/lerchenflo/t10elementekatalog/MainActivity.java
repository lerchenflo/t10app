package com.lerchenflo.t10elementekatalog;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        pdfView = findViewById(R.id.pdfView);  // Set up the PDFView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);



        // Programmatically create the menu with a submenu
        Menu menuBuilder = navigationView.getMenu();
        menuBuilder.add(0,1,0,"Geräte & Leitbilder");

        // Add a submenu titled "Geräte & Leitbilder"
        SubMenu submenu = menuBuilder.addSubMenu(0,2,0, "");

        // Add items to the submenu
        submenu.add(1, 1, 0, "Barren");
        submenu.add(2, 2, 0, "Reck");
        submenu.add(3, 3, 0, "Minitrampolin");
        submenu.add(4, 4, 0, "Sprung");
        submenu.add(5, 5, 0, "Balken");

        loadPdf("Alle.pdf");

        // Handle menu item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    loadPdf("Barren.pdf");
                    break;
                case 2:
                    loadPdf("Reck.pdf");
                    break;
                case 3:
                    loadPdf("Minitrampolin.pdf");
                    break;
                case 4:
                    loadPdf("Sprung.pdf");
                    break;
                case 5:
                    loadPdf("Balken.pdf");
                    break;
                default:
                    return false;
            }
            drawerLayout.closeDrawers();
            return true;
        });


        // Drawer Toggle setup
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadPdf(String assetFileName) {

        pdfView.fromAsset(assetFileName)
                .enableSwipe(true) // Enable swiping to change pages
                .swipeHorizontal(false) // Set false for vertical swiping
                .enableDoubletap(true) // Enable double tap to zoom
                .defaultPage(0) // Show the first page
                .load();
    }


}
