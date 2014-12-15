package com.zhao.karani.meetme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.location.LocationListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends Activity {

    private static String userEmail = "";
    private static Timer timer;
    private static Class currentClass = MainMenu.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        if(intent.getStringExtra("fromSignUp")!=null && intent.getStringExtra("fromSignUp").equals("yo")){
            TextView t = (TextView) findViewById(R.id.fromSignUp);
            t.setVisibility(View.VISIBLE);
        }

        setCurrentClass(LoginActivity.class);
    }

    @Override
    public void onBackPressed() {

        return;
    }

    public static String getEmail(){
        return userEmail;
    }
    public static Timer getTimer() { return timer;}
    public static void setCurrentClass(Class c) {currentClass = c;}
    public static Class getCurrentClass() {return currentClass;}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void coverScreen(View view) {
        Intent intent = new Intent(this, CoverScreen.class);
        startActivity(intent);
    }

    /** Called when the user clicks the login button */
    public void logIn(View view) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        EditText editText = (EditText) findViewById(R.id.email1);
        String email = editText.getText().toString();
        email = email.toLowerCase();

        EditText editText1 = (EditText) findViewById(R.id.password1);
        String pwd = editText1.getText().toString();

        String result = HttpRequest.getData("http://meetmeutece.appspot.com/loginHandler?userEmail="+email+"&userPwd="+pwd, "");
        JSONObject jsonObj;
        String loginResult;
        try {
            jsonObj = new JSONObject(result);
            loginResult = (String) jsonObj.get("loginResult");
            Log.d("Json", loginResult);
        }catch(JSONException e){
            jsonObj = null;
            loginResult = null;
            Log.d("Json Error", e.toString());
        }
        if(jsonObj!=null && loginResult.equals("Login Successful")) {
            Intent intent = new Intent(this, MainMenu.class);

            if (email.length() > 3 && email.contains("@")) {
                setEmail(email);

                //start thread
                int delay = 500; // delay for 1 sec.
                int period = 10000; // repeat every 10 sec.
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTaskConnect(), delay, period);


                startActivity(intent);
            }
        }
        else{
            TextView reselectText;
            reselectText = (TextView) findViewById(R.id.userNotExist);
            reselectText.setVisibility(View.VISIBLE);
        }
    }

    private class TimerTaskConnect extends TimerTask{
        private double latt = -9999;
        private double longt = -9999;
        private LocationManager locationManager;
        private Criteria criteria;


        public TimerTaskConnect() {
            // Get the location manager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Define the criteria how to select the location provider
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default
            criteria.setCostAllowed(false);
            // get the best provider depending on the criteria
            String provider = locationManager.getBestProvider(criteria, true);
            // the last known location of this provider
            Location location = locationManager.getLastKnownLocation(provider);
            MyLocationListener mylistener = new MyLocationListener();
            //mylistener.onLocationChanged(location);
            // location updates: at least 1 meter and 200millsecs change
            locationManager.requestLocationUpdates(provider, 1000, 1, mylistener);

            Criteria criteria1 = new Criteria();
            criteria1.setAccuracy(Criteria.ACCURACY_FINE);
            criteria1.setCostAllowed(false);
            String provider1 = locationManager.getBestProvider(criteria1, true);
            MyLocationListener mylistener1 = new MyLocationListener();
            locationManager.requestLocationUpdates(provider1, 1000, 1, mylistener1);
        }


        public void run()
        {
            //Log.d("[TIMER ALIVE]", "[TIMER ALIVE]");  // display the data
            String result = HttpRequest.getData("http://meetmeutece.appspot.com/checkCurrentEventAvailability?userEmail="+LoginActivity.getEmail(), "");
            JSONObject jsonObj;
            String addFriendResult;
            Log.d("result",result);
            try {
                jsonObj = new JSONObject(result);
                addFriendResult = (String) jsonObj.get("currentEventAvailable");
            }catch(JSONException e){
                jsonObj = null;
                addFriendResult = null;
                Log.d("Json Error", e.toString());
            }
            if(addFriendResult.equals("Yes")){
                MainMenu.setCurrentActivity(true);

                if(latt!=-9999) {
                    String geoUpdate = HttpRequest.getData("http://meetmeutece.appspot.com/updateGeoHandler?userEmail=" + LoginActivity.getEmail() + "&longitude=" + Double.toString(longt) + "&latitude=" + Double.toString(latt), "");
                }
                result = HttpRequest.getData("http://meetmeutece.appspot.com/cronHandler", "");

                result = HttpRequest.getData("http://meetmeutece.appspot.com/getUserCurrentEventInformation?userEmail="+LoginActivity.getEmail(), "");
                JSONObject jsonObj2;
                //Log.d("eventinfobaby",result);
                try {
                    jsonObj2 = new JSONObject(result);
                    if(OngoingEvent.getDestLat()==0) {
                        OngoingEvent.setEventTitle((String) jsonObj2.get("title"));
                        OngoingEvent.setEventID((String) jsonObj2.get("eventID"));
                        OngoingEvent.setRadius(Integer.parseInt((String) jsonObj2.get("radius")));
                        OngoingEvent.setDestLat(Double.parseDouble((String) jsonObj2.get("destinationLatitude")));
                        OngoingEvent.setDestLong(Double.parseDouble((String) jsonObj2.get("destinationLongitude")));
                    }
                }catch(JSONException e){
                    jsonObj2 = null;
                    addFriendResult = null;
                    Log.d("Json Error", e.toString());
                }

            }
            else{
                MainMenu.setCurrentActivity(false);
            }
        }

        private class MyLocationListener implements LocationListener {

            @Override

            public void onLocationChanged(Location location) {
                Log.d("latListener",String.valueOf(location.getLatitude()));
                latt = location.getLatitude();
                longt = location.getLongitude();
                Log.d("longListener",String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Toast.makeText(MainActivity.this, provider + "'s status changed to "+status +"!",
                //        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProviderEnabled(String provider) {
                //Toast.makeText(MainActivity.this, "Provider " + provider + " enabled!",
                //       Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onProviderDisabled(String provider) {
                //Toast.makeText(MainActivity.this, "Provider " + provider + " disabled!",
                //        Toast.LENGTH_SHORT).show();
            }
        }
    }


    /** Called when the user clicks the signu*/


    public static void setEmail(String m) {
        userEmail = m;
    }
}
