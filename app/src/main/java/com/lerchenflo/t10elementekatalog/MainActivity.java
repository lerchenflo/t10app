package com.lerchenflo.t10elementekatalog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private PDFView pdfView;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        pdfView = findViewById(R.id.pdfView);  // Set up the PDFView
        drawerLayout = findViewById(R.id.main);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        String submenuIndent = "  ";

        // Programmatically create the menu with a submenu
        Menu menuBuilder = navigationView.getMenu();

        // Add a parent menu item for "Geräte & Leitbilder"
        MenuItem parentItem = menuBuilder.add(0, 0, 0, "Geräte & Leitbilder");
        parentItem.setCheckable(true);
        parentItem.setIcon(com.google.android.material.R.drawable.mtrl_ic_arrow_drop_up);


        // Add submenu items under the parent item
        MenuItem alle = menuBuilder.add(0, 1, 0, submenuIndent + "Alle Elemente");
        MenuItem balken = menuBuilder.add(0, 2, 0, submenuIndent + "Balken");
        MenuItem barren = menuBuilder.add(0, 3, 0, submenuIndent + "Barren");
        MenuItem boden = menuBuilder.add(0, 4, 0, submenuIndent + "Boden");
        MenuItem minitramp = menuBuilder.add(0, 5, 0, submenuIndent + "Minitrampolin");
        MenuItem pferd = menuBuilder.add(0, 6, 0, submenuIndent + "Pferd");
        MenuItem reck = menuBuilder.add(0, 7, 0, submenuIndent + "Reck");
        MenuItem ringe = menuBuilder.add(0, 8, 0, submenuIndent + "Ringe");
        MenuItem sprung = menuBuilder.add(0, 9, 0, submenuIndent + "Sprung");

        // Track the visibility state of submenu items
        boolean[] isSubmenuVisible = {true};

        // Add a parent menu item for "Geräte & Leitbilder"
        MenuItem countermenu = menuBuilder.add(1, 10, 0, "Punktezähler(A)(Basis)");
        parentItem.setCheckable(true);

        MenuItem uebungmenu = menuBuilder.add(1, 20, 0, "Übungsassistent(Basis)");
        parentItem.setCheckable(true);


        MenuItem bugreportmenu = menuBuilder.add(2, 30, 0, "Fehler melden / Vorschläge");
        parentItem.setCheckable(true);


        TextView versiontextview = findViewById(R.id.versionTextView);
        try {
            versiontextview.setText("Version "+ getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            versiontextview.setVisibility(NavigationView.GONE);
        }

        versiontextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Information");
                builder.setMessage("Authors: Florian Lerchenmüller, Manuel Mayrhofer\nVerein: TSZ Dornbirn");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });




        loadPdf("Alle.pdf");

        // Handle menu item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    loadPdf("Alle.pdf");
                    break;
                case 2:
                    loadPdf("Balken.pdf");
                    break;
                case 3:
                    loadPdf("Barren.pdf");
                    break;
                case 4:
                    loadPdf("Boden.pdf");
                    break;
                case 5:
                    loadPdf("Minitrampolin.pdf");
                    break;
                case 6:
                    loadPdf("Pferd.pdf");
                    break;
                case 7:
                    loadPdf("Reck.pdf");
                    break;
                case 8:
                    loadPdf("Ringe.pdf");
                    break;
                case 9:
                    loadPdf("Sprung.pdf");
                    break;
                case 0:
                    //Submenu togglen
                    isSubmenuVisible[0] = !isSubmenuVisible[0];
                    boolean visibility = isSubmenuVisible[0];
                    parentItem.setIcon(visibility ? com.google.android.material.R.drawable.mtrl_ic_arrow_drop_up : com.google.android.material.R.drawable.mtrl_ic_arrow_drop_down);
                    alle.setVisible(visibility);
                    barren.setVisible(visibility);
                    reck.setVisible(visibility);
                    minitramp.setVisible(visibility);
                    sprung.setVisible(visibility);
                    balken.setVisible(visibility);
                    boden.setVisible(visibility);
                    ringe.setVisible(visibility);
                    pferd.setVisible(visibility);
                    barren.setVisible(visibility);
                    return true;

                case 10: //Counter
                    Intent i = new Intent(MainActivity.this, punktezaehlerActivity.class);
                    startActivity(i);
                    break;
                case 20: //Übungsassistent
                    Intent j = new Intent(MainActivity.this, uebungscreator.class);
                    startActivity(j);
                    break;
                case 30: //Bugreport
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Auswählen:")
                            .setItems(new String[]{"Bug Report", "Feature Request", "Schließen"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        String subject = "";
                                        String body = "";
                                        if (which == 0) { // Bug Report
                                            subject = "Bug Report T10 App";
                                            body = "Hallo Flo,\n\nich habe ein Problem:\n\nWann tritt das Problem auf:\n\nWas ist das Problem:\n\nVielen Dank und auf Wiedersehen!";
                                        } else if (which == 1) { // Feature Request
                                            subject = "Feature Request T10 App";
                                            body = "Hallo Flo,\n\nNeues Feature mit genauer Beschreibung:\n\nVielen Dank und auf Wiedersehen!";
                                        }else if (which==2){
                                            return;
                                        }
                                        String mailto = "mailto:manuel@mtmayr.com" +
                                                "?subject=" + Uri.encode(subject) +
                                                "&body=" + Uri.encode(body);
                                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                        emailIntent.setData(Uri.parse(mailto));
                                        startActivity(emailIntent);
                                    }catch (Exception e){
                                        //Für alle Handys ohne Emailprogramm
                                        Toast.makeText(MainActivity.this, "Kein EMAIL - Programm installiert", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                    builder.show();


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


        //Appstart fertig, Google play update luaga
        // Create an instance of AppUpdateManager
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns a Task<AppUpdateInfo> object
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Check if an update is available and if immediate update is allowed
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    Log.i("Google Play", "Update verfügbar");
                    try {
                        // Start the immediate update flow
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                MainActivity.this,
                                0);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private void loadPdf(String assetFileName) {

        pdfView.fromAsset(assetFileName)
                .enableSwipe(true) // Enable swiping to change pages
                .swipeHorizontal(false) // Set false for vertical swiping
                .enableDoubletap(true) // Enable double tap to zoom
                .enableAntialiasing(true)
                .enableAnnotationRendering(true)
                .defaultPage(0) // Show the first page
                .load();
        Log.d("PDF geladen", assetFileName);
    }
}
