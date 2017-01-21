package com.shubham.uberclone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class YourLoaction extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    LocationManager locationManager;

    String provider;

    TextView infoTextView;

    Button requestUberButton;

    Boolean requestActive = false;

    private GoogleApiClient googleApiClient;

    String driverUsername ="";

    ParseGeoPoint driverLocation = new ParseGeoPoint(0,0);

    Handler handler = new Handler();

    public void requestUber(View view) {

        if (requestActive == false) {

            Log.i("MyApp", "Uber Requested");


            ParseObject request = new ParseObject("Requests");

            request.put("requesterUsername", ParseUser.getCurrentUser().getUsername());

            ParseACL parseACL = new ParseACL();

            parseACL.setPublicWriteAccess(true);

            parseACL.setPublicReadAccess(true);

            request.setACL(parseACL);

            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {


                        infoTextView.setText("Finding Uber Driver...");

                        requestUberButton.setText("Cancel Uber");

                        requestActive = true;

                    }

                }
            });
        } else {

            infoTextView.setText("Uber Cancelled");

            requestUberButton.setText("Requested Uber");

            requestActive = true;


            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Requests");

            query.whereEqualTo("requesterUsername", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            for (ParseObject object : list) {
                                object.deleteInBackground();
                            }
                        }
                    }
                }
            });
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_loaction);

        infoTextView = (TextView) findViewById(R.id.infotext);

        requestUberButton = (Button) findViewById(R.id.requestUber);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 124);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }


    @Override
    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 124);
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 124);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(final Location location) {

        mMap.clear();

       if(requestActive == false)
       {
           ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
           query.whereEqualTo("requestUsername",ParseUser.getCurrentUser().getUsername());
           query.findInBackground(new FindCallback<ParseObject>() {
               @Override
               public void done(List<ParseObject> list, ParseException e) {

                   if(e == null)
                   {
                       if(list.size()>0)
                       {
                           for (ParseObject object : list)
                           {
                               requestActive = true;

                               infoTextView.setText("Finding Uber driver...");

                               requestUberButton.setText("Cancel Uber");

                               if(object.get("driverUsername") != null)
                               {
                                  driverUsername = object.getString("driverUsername");

                                   infoTextView.setText("Your driver is on their way!");

                                   requestUberButton.setVisibility(View.INVISIBLE);

                                   Log.i("AppInfo",driverUsername);
                               }
                           }
                       }
                   }
               }
           });
       }

        if(driverUsername.equals(""))
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));

            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location"));

        }


        if(requestActive == true)

        {

            if(!driverUsername.equals("") )
            {
               ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.whereEqualTo("username",driverUsername);
                userQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> list, ParseException e) {

                        if( e == null)
                        {
                            if(list.size()>0)
                            {
                                for (ParseUser driver : list)
                                {
                                    driverLocation = driver.getParseGeoPoint("location");
                                }
                            }
                        }

                    }
                });
                if(driverLocation.getLatitude()!=0 && driverLocation.getLongitude()!=0)
                {

                    Double  distanceInMiles = driverLocation.distanceInMilesTo(new ParseGeoPoint(location.getLatitude(),location.getLongitude()));

                    Double distanceOneDp = (double)Math.round(distanceInMiles*10)/10;

                    infoTextView.setText("Your driver is" +distanceOneDp.toString() + "miles away");

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    ArrayList<Marker> markers = new ArrayList<Marker>();

                    markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(driverLocation.getLatitude(),driverLocation.getLongitude())).title("Driver Location")));

                    markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Your Location")));


                    for(Marker marker : markers )
                    {
                        builder.include(marker.getPosition());
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 100;

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
/*
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(i.getDoubleExtra("latitude",0), i.getDoubleExtra("longitudes",0)), 10));
*/

                    mMap.animateCamera(cu);

                }


            }

            final ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Requests");

            query.whereEqualTo("requesterUsername", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            for (ParseObject object : list)
                            {
                                object.put("requesterLocation",userLocation);
                                object.saveInBackground();
                            }
                        }
                    }
                }
            });

        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                onLocationChanged(location);

            }
        },5000);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(YourLoaction.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(YourLoaction.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();

                }
                break;

            case 124:

                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(YourLoaction.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(YourLoaction.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();

                }
                break;
            default:
                break;
        }


    }

}
