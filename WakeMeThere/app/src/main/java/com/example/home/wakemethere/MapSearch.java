package com.example.home.wakemethere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class MapSearch extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mGoogleMsp;
    //    FloatingActionButton searchFab;
    FloatingActionButton selectLocation;
    LocationManager mLocationManager;
//    private FusedLocationProviderClient mFusedLocationClient;

    LatLng currentLatLng;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        final PlaceAutocompleteFragment search = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.search_field);
        search.getView().setBackground(getResources().getDrawable(R.drawable.capsule));

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();
        search.setFilter(typeFilter);

        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);
        final MapSearch mainActivityInstance = this;

        search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Toast.makeText(MapSearch.this, place.getName() + " @ " + place.getLatLng().latitude + " , " + place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
               // final GoogleMap map = mapFragment.getMapAsync(mainActivityInstance);//mapFragment.getMap();
                map.clear();
                map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(place.getLatLng()).zoom(14).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);

                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        Toast.makeText(MapSearch.this, cameraPosition.target.latitude + " , " + cameraPosition.target.longitude, Toast.LENGTH_SHORT).show();
                        map.clear();
                        Geocoder geocoder = new Geocoder(MapSearch.this);
                        List<Address> location = null;
                        try {
                            location = geocoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 1);
                        } catch (IOException e) {
                            Toast.makeText(MapSearch.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(MapSearch.this, location.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                        Marker marker = map.addMarker(new MarkerOptions().position(cameraPosition.target));
                        currentLatLng = cameraPosition.target;
                    }
                });

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(MapSearch.this, "An error occurred: " + status, Toast.LENGTH_SHORT).show();
            }

        });
//        searchFab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        selectLocation = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Geocoder geocoder = new Geocoder(MapSearch.this);
                List<Address> location = null;
                try {
                    location = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
                } catch (IOException e) {
                    Toast.makeText(MapSearch.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Snackbar.make(v, location.get(0).getAddressLine(0), Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LocationEditSettings.class);
                intent.putExtra("LatLongValues", currentLatLng.latitude + "," + currentLatLng.longitude);
                intent.putExtra("mode", "new");
                startActivity(intent);


            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        try {

            mGoogleMsp = googleMap;
            map = googleMap;
            mGoogleMsp.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMsp.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMsp.getUiSettings().setCompassEnabled(true);

//            mGoogleMsp.getUiSettings().
            LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//            return;
            }
            Location location = this.getLastKnownLocation();
//                    LocationServices.FusedLocationApi.getLastLocation(
//                    new GoogleApiClient.Builder(this)
//                            .addConnectionCallbacks(this)
//                            .addOnConnectionFailedListener(this)
//                            .addApi(LocationServices.API)
//                            .build());//manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            //remove
            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Toast.makeText(MapSearch.this, cameraPosition.target.latitude + " , " + cameraPosition.target.longitude, Toast.LENGTH_SHORT).show();
                    googleMap.clear();
                    Geocoder geocoder = new Geocoder(MapSearch.this);
                    List<Address> location = null;
                    try {
                        location = geocoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 1);
                    } catch (IOException e) {
                        Toast.makeText(MapSearch.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(MapSearch.this, location.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(cameraPosition.target));
//                        marker.
                    currentLatLng = cameraPosition.target;

                }
            });
            //remove
            currentLatLng = new LatLng(latitude, longitude);

            Toast.makeText(getApplicationContext(), "" + latitude + "," + longitude, Toast.LENGTH_LONG).show();
            gotoLocZoom(latitude, longitude, 17);
//        gotoLocZoom(17.397460f, 78.540799f,15);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void gotoLocZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMsp.animateCamera(update);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
