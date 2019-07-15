package com.example.home.wakemethere;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    protected LocationManager locationManager;
    ListView alarmList;
    FloatingActionButton addAlarmButton;
    AlarmAdapter adapter;
    DatabaseHelper myDb;
    FusedLocationProviderClient fusedLocationProviderClient;

    static MainActivity instance;
    private LocationRequest locationRequest;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        updateLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
        myDb = new DatabaseHelper(MainActivity.this);

        try {
            alarmList = (ListView) findViewById(R.id.alarmList);

            final ArrayList<Map<String, String>> alarms = getAlarmsDbList();
            adapter = new AlarmAdapter(getApplicationContext(), R.layout.layout, alarms);
            alarmList.setAdapter(adapter);
            for (int i = 0; i < alarms.size(); i++) {
                AlarmListDataProvider dataProvider = new AlarmListDataProvider(
                        alarms.get(i).get("name"),
                        Integer.parseInt(alarms.get(i).get("id")),
                        alarms.get(i).get("latlng"),
                        Integer.parseInt(alarms.get(i).get("distance")),
                        alarms.get(i).get("isenabled").equals("1"),
                        alarms.get(i).get("message")
                );
                adapter.add(dataProvider);
            }
            alarmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                    Toast.makeText(MainActivity.this, ((TextView)view.findViewById(R.id.textView)).getText()+" "+position, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LocationEditSettings.class);
                    intent.putExtra("LatLongValues", alarms.get(position).get("latlng"));
                    intent.putExtra("name", alarms.get(position).get("name"));
                    intent.putExtra("message", alarms.get(position).get("message"));
                    intent.putExtra("dist", alarms.get(position).get("distance"));
                    intent.putExtra("mode", "edit");
                    intent.putExtra("id", alarms.get(position).get("id"));
                    startActivity(intent);
                }
            });
            adapter.notifyDataSetChanged();
            addAlarmButton = (FloatingActionButton) findViewById(R.id.addAlarmBtn);
            addAlarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> permissionList = new ArrayList<>();
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);

                    }
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(Manifest.permission.INTERNET);
//                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.INTERNET},1);
                    }
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
//                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},1);
                    }
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                    }

                    if (permissionList.isEmpty()) {
                        locationManager = (LocationManager) MainActivity.this.getSystemService(LOCATION_SERVICE);
                        boolean isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
                        boolean isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

                        if (!isGPSEnabled) {
                            AlertDialog.Builder dilag = new AlertDialog.Builder(MainActivity.this);
                            dilag.setMessage("Please enable GPS/Location Service");
                            dilag.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    MainActivity.this.startActivity(gpsIntent);
                                }
                            });
                            dilag.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dilag.show();
                        } else if (!isNetworkEnabled) {
                            AlertDialog.Builder dilag = new AlertDialog.Builder(MainActivity.this);
                            dilag.setMessage("Please enable Network");
                            dilag.setPositiveButton("Enable Network", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent gpsIntent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                                    MainActivity.this.startActivity(gpsIntent);
                                }
                            });
                            dilag.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dilag.show();
                        } else {
//                                int PLACE_PICKER_REQUEST = 1;
//                                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                                try {
//                                      startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
//                                }
                            Intent intent = new Intent(getApplicationContext(), MapSearch.class);
                            try{
                                startActivity(intent);
                            }catch (Exception ex){
                                Toast.makeText(MainActivity.this, ex.getMessage()+"", Toast.LENGTH_SHORT).show();
                            }

                        }


                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, permissionList.toArray(new String[0]), 1);
                    }


                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }


    }

    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
       Intent intent = new Intent(this, LocationService.class);
       intent.setAction(LocationService.ACTION_PROCESS_UPDATE);
       return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setSmallestDisplacement(10f);


    }


    protected ArrayList<Map<String, String>> getAlarmsDbList() {
        Cursor res = myDb.getAllData();
        ArrayList<Map<String, String>> alarms = new ArrayList<>();
        while (res.moveToNext()) {
            Map<String, String> each = new HashMap<>();
            each.put("id", Integer.toString(res.getInt(0)));
            each.put("name", res.getString(1));
            each.put("latlng", res.getString(2));
            each.put("distance", res.getString(3));
            each.put("isenabled", res.getString(4));
            each.put("message", res.getString(5));


            alarms.add(each);
        }
        return alarms;
    }

    public void showResultOnScreen(final String value ){
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,value,Toast.LENGTH_SHORT).show();
            }
        });
    }
}


