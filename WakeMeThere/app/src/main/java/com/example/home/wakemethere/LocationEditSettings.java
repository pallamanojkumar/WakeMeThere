package com.example.home.wakemethere;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class LocationEditSettings extends AppCompatActivity {
    DatabaseHelper myDb;
    String mode = "new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_edit_settings);
        myDb = new DatabaseHelper(this);
        mode = getIntent().getExtras().getString("mode", "new");
        final String[] retData = getIntent().getExtras().getString("LatLongValues").split(",");

        Geocoder geocoder = new Geocoder(LocationEditSettings.this);
        List<Address> location = null;
        try {
            location = geocoder.getFromLocation(Float.parseFloat(retData[0]), Float.parseFloat(retData[1]), 1);
        } catch (IOException e) {
            Toast.makeText(LocationEditSettings.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(LocationEditSettings.this, location.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();


        if (findViewById(R.id.preference_fragment) != null) {

            if (savedInstanceState != null) {
                return;
            }
            SettingsFragments fragment = new SettingsFragments();
            Bundle arguments = new Bundle();
            arguments.putString("defaultName", mode.equalsIgnoreCase("edit") ? getIntent().getExtras().getString("name", location.get(0).getAddressLine(0)) : location.get(0).getAddressLine(0));
            arguments.putString("defaultDist", mode.equalsIgnoreCase("edit") ? getIntent().getExtras().getString("dist", "1") : "1");
            arguments.putString("defaultMessage", mode.equalsIgnoreCase("edit") ? getIntent().getExtras().getString("message", "Reached Destination!!!") : "Reached Destination!!!");
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.preference_fragment, fragment).commit();
        }

//        setSummary(location.get(0).getAddressLine(0));

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LocationEditSettings.this);
                Toast.makeText(LocationEditSettings.this, prefs.getString("example_text", "Def value") + "\n\n" + prefs.getString("distkms", "1") + "\n\n" + prefs.getString("message", "message"), Toast.LENGTH_SHORT).show();
                if (mode.equalsIgnoreCase("edit")) {
                    boolean isUpdated = myDb.updateDataInTable(getIntent().getExtras().get("id").toString(), prefs.getString("example_text", "Def value"), retData[0] + "," + retData[1], prefs.getString("distkms", "1"), "1", prefs.getString("message", "message"));
                } else {
                    boolean myb = myDb.insertDataToTable(prefs.getString("example_text", "Def value"), retData[0] + "," + retData[1], prefs.getString("distkms", "1"), "1", prefs.getString("message", "message"));
                    if (myb) {
                        Toast.makeText(LocationEditSettings.this, "inserted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LocationEditSettings.this, "not inserted", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }


//    public void setSummary(String summaryValue) {
//        Map<String, ?> preferencesMap = PreferenceManager.getDefaultSharedPreferences(LocationEditSettings.this).getAll();
//        for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet()) {
//            if (preferenceEntry instanceof EditTextPreference) {
//                if (((EditTextPreference) preferenceEntry).getKey().equals("example_text")) {
//                    ((EditTextPreference) preferenceEntry).setSummary(summaryValue);
//                }
//            }
//        }
//    }

}
