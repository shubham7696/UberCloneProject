package com.shubham.uberclone;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ViewRequests extends AppCompatActivity implements LocationListener{

    ListView listView;

    LocationManager locationManager;

    String provider;

    ArrayList<String> listViewContent;

    ArrayAdapter arrayAdapter;

    ArrayList<String> usernames;

    ArrayList<Double>  latitude;

    ArrayList<Double>  longitudes;

    Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);


        listView =(ListView)findViewById(R.id.listView);

        listViewContent = new ArrayList<String>();

        usernames = new ArrayList<String>();

        latitude = new ArrayList<Double>();

        longitudes = new ArrayList<Double>();

        listViewContent.add("Finding the nearby requests....");

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listViewContent);

        listView.setAdapter(arrayAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 124);
        }
         location =  locationManager.getLastKnownLocation(provider);

        if (location !=null)
        {
          updateLocation();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {

                Intent i = new Intent(getApplicationContext(),ViewRiderLocation.class);
                i.putExtra("username",usernames.get(position));
                i.putExtra("latitude",latitude.get(position));
                i.putExtra("longitudes",longitudes.get(position));
                i.putExtra("userLatitude",location.getLatitude());
                i.putExtra("userLongitude",location.getLongitude());


                startActivity(i);


            }
        });

    }

    public  void updateLocation()
    {
        final ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

        ParseUser.getCurrentUser().put("location",userLocation);

        ParseUser.getCurrentUser().saveInBackground();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");

        query.whereDoesNotExist("driverUsername");


        query.whereNear("requesterLocation", userLocation);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> list, ParseException e)
            {

                if(list.size()>0)
                {

                    listViewContent.clear();

                    usernames.clear();

                    latitude.clear();

                    longitudes.clear();

                    for(ParseObject lists : list)
                    {

                        if(lists.get("driverUsername")== null)
                        {


                            Double  distanceInMiles = userLocation.distanceInMilesTo((ParseGeoPoint) lists.get("requesterLocation"));


                            Double distanceOneDp = (double)Math.round(distanceInMiles*10)/10;

                            listViewContent.add( distanceOneDp.toString() + "miles");

                            usernames.add(lists.getString("requesterUsername"));

                            latitude.add(lists.getParseGeoPoint("requesterLocation").getLatitude());

                            longitudes.add(lists.getParseGeoPoint("requesterLocation").getLongitude());


                        }
                    }

                    arrayAdapter.notifyDataSetChanged();

                }

            }

        });
    }

    @Override
    public void onLocationChanged(Location location) {

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

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}
