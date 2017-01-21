package com.shubham.uberclone;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity
{
    Switch riderOrDriverSwitch;

    String riderOrDriver;


    public void getStarted(View view)
    {
         riderOrDriver ="rider";

         if(riderOrDriverSwitch.isChecked())
         {

           riderOrDriver = "driver";

         }
       ParseUser.getCurrentUser().put("riderOrDriver",riderOrDriver);

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e)
            {

                if(e == null)
                {
                    redirectUser();
                }

            }
        });

    }

    public void redirectUser()
    {
        if(ParseUser.getCurrentUser().get("riderOrDriver").equals("rider"))
        {
            Intent i = new Intent(getApplicationContext(),YourLoaction.class);
            startActivity(i);
        }
        else
        {
            Intent i = new Intent(getApplicationContext(),ViewRequests.class);
            startActivity(i);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        riderOrDriverSwitch = (Switch) findViewById(R.id.riderOrDriver);

        Parse.initialize(new Parse.Configuration.Builder(MainActivity.this)
                .applicationId("8VeZ6yC6rmsFJOYZQr9GBBDjSw4PaXNW9SsFVOkv")
                .clientKey("duPtaYGuFWJsVFgUMI42phSJ60SzZJz2xD8rIbXX")
                .server("https://parseapi.back4app.com") // The trailing slash is important.


                .build()
        );

        getSupportActionBar().hide();

        if(ParseUser.getCurrentUser() == null)

        {

            ParseAnonymousUtils.logIn(new LogInCallback()
            {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null) {
                        Log.d("MyApp", "Anonymous login failed.");
                    } else {
                        Log.d("MyApp", "Anonymous user logged in.");
                    }
                }
            });
        }

        else
        {
            if (ParseUser.getCurrentUser().get("riderOrDriver") != null)
            {
                      redirectUser();
            }

        }

    }
}
