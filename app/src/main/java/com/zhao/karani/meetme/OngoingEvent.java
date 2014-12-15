package com.zhao.karani.meetme;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class OngoingEvent extends Activity {

    private GoogleMap map;
    private boolean cameraMoved = false;
    private TextView textView;
    Circle myCircle;
    final Handler myHandler = new Handler();
    Timer myTimer;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;
    private ArrayList<String> rangList;

    final Runnable myRunnable = new Runnable() {
        public void run() {
            map.clear();

            if (map!=null && getDestLat()!=0){
                LatLng dest = new LatLng(getDestLat(), getDestLong());
                Marker destination = map.addMarker(new MarkerOptions().position(dest)
                        .title("Destination"));
                if(!cameraMoved) {
                    //Move the camera instantly to hamburg with a zoom of 15.
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 15));
                    // Zoom in, animating the camera.
                    map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
                    cameraMoved = true;
                }

                CircleOptions circleOptions = new CircleOptions()
                        .center(dest)   //set center
                        .radius(getRadius())   //set radius in meters
                        .fillColor(0x40ff0000)  //semi-transparent
                        .strokeColor(Color.BLUE)
                        .strokeWidth(5);

                myCircle = map.addCircle(circleOptions);
            }

            String result = HttpRequest.getData("http://meetmeutece.appspot.com/getUserCurrentLocation?userEmail="+LoginActivity.getEmail(), "");
            JSONArray jsonObj1;
            try {
                jsonObj1 = new JSONArray(result);
                for(int i=0;i<jsonObj1.length();i++) {
                    JSONObject a = jsonObj1.getJSONObject(i);
                    LatLng dest1 = new LatLng(Double.parseDouble((String) a.get("latitude")), Double.parseDouble((String) a.get("longitude")));
                    double distance1 = 1000* distance(dest1.latitude,dest1.longitude,getDestLat(),getDestLong(),"K");
                    if(distance1<=getRadius() && !(((String) a.get("userEmail")).equals(LoginActivity.getEmail())) && !(rangList.contains(((String) a.get("userEmail"))))){
                        Toast.makeText(getApplicationContext(),((String) a.get("userEmail")) +" is in the radius", Toast.LENGTH_LONG).show();
                        mBuilder.setContentText(((String) a.get("userEmail")) + " is " + Double.toString(distance1) + " meters away")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotifyManager.notify(0, mBuilder.build());
                        rangList.add(((String) a.get("userEmail")));
                    }
                    map.addMarker(new MarkerOptions().position(dest1).title((String) a.get("userEmail")).snippet(Double.toString(distance1)+" Meters Away"));
                }
            }catch(JSONException e){
                jsonObj1 = null;
                Log.d("Json Error", e.toString());
            }
            Log.d("[MAP_UPDATE]", "[MAP_UPDATE]");

        }

        private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }

        private double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }

        private double rad2deg(double rad) {
            return (rad * 180 / Math.PI);
        }

    };

    private static int radius = 0;
    private static String eventTitle = "";
    private static double destLong = 0;
    private static double destLat = 0;
    private static String eventID = "";

    public static int getRadius(){
        return radius;
    }
    public static double getDestLong(){
        return destLong;
    }
    public static double getDestLat(){
        return destLat;
    }
    public static String getEventTitle(){
        return eventTitle;
    }
    public static String getEventID() { return eventID;}
    public static void setEventTitle(String m) {
        eventTitle = m;
    }
    public static void setEventID(String m) {eventID = m;}
    public static void setRadius(int m) {
        radius = m;
    }
    public static void setDestLat(double m) {
        destLat = m;
    }
    public static void setDestLong(double m) {
        destLong = m;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_event);
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("EventFull.ly Ongoing Event:")
                .setSmallIcon(R.drawable.ic_marker1);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        rangList = new ArrayList<String>();

        LoginActivity.setCurrentClass(OngoingEvent.class);

        Intent intent = getIntent();

        if(intent.getStringExtra("eventTitle")!=null) {
            setEventTitle(intent.getStringExtra("eventTitle"));
            setRadius(intent.getIntExtra("radius", 0));
            setDestLat(intent.getDoubleExtra("destLat", 0));
            setDestLong(intent.getDoubleExtra("destLong", 0));
            setEventID(intent.getStringExtra("eventID"));
        }

        textView = (TextView) findViewById(R.id.eventTitle);
        textView.setText(getEventTitle());

        LatLng dest = new LatLng(getDestLat(), getDestLong());

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        if (map!=null){
            Marker destination = map.addMarker(new MarkerOptions().position(dest)
                    .title("Destination"));
            //Marker kiel = map.addMarker(new MarkerOptions()
            //        .position(KIEL)
            //        .title("Kiel")
            //        .snippet("Kiel is cool")
            //        .icon(BitmapDescriptorFactory
            //                .fromResource(R.drawable.ic_marker1)));
        }
        map.setMyLocationEnabled(true);
        //Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 15));
        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

        CircleOptions circleOptions = new CircleOptions()
                .center(dest)   //set center
                .radius(getRadius())   //set radius in meters
                .fillColor(0x40ff0000)  //semi-transparent
                .strokeColor(Color.BLUE)
                .strokeWidth(5);

        myCircle = map.addCircle(circleOptions);


        String result = HttpRequest.getData("http://meetmeutece.appspot.com/getUserCurrentLocation?userEmail="+LoginActivity.getEmail(), "");
        JSONArray jsonObj1;

        try {
            jsonObj1 = new JSONArray(result);
            for(int i=0;i<jsonObj1.length();i++) {
                JSONObject a = jsonObj1.getJSONObject(i);
                LatLng dest1 = new LatLng(Double.parseDouble((String) a.get("latitude")), Double.parseDouble((String) a.get("longitude")));
                map.addMarker(new MarkerOptions().position(dest1).title((String) a.get("userEmail")));
            }
        }catch(JSONException e){
            jsonObj1 = null;
            Log.d("Json Error", e.toString());
        }

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {UpdateGUI();}
        }, 0, 10000);
    }

    private void UpdateGUI() {
        myHandler.post(myRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTimer.cancel();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ongoing_event, menu);
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

    public void endEvent(View view) {
        //Intent intent = new Intent(this, SignUp.class);
        //startActivity(intent);
        Log.d("eventid",getEventID());
        String result = "";
        try {
            result = HttpRequest.getData("http://meetmeutece.appspot.com/finishEventHandler?eventID=" + URLEncoder.encode(getEventID(), "UTF-8"), "");
        }catch(Exception e) {
            Log.d("enoding error", e.toString());
        }
        JSONObject jsonObj;
        //String FinishedEventResult;
        Log.d("finishedeventresult",result);
        try {
            jsonObj = new JSONObject(result);
            //addFriendResult = (String) jsonObj.get("currentEventAvailable");
        }catch (JSONException e){
            jsonObj = null;
            //addFriendResult = null;
            Log.d("Json Error", e.toString());
        }
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void returnToMenu(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void setRadiusBox(View view) {
        EditText a = (EditText) findViewById(R.id.testAddress);
        setRadius(Integer.parseInt(a.getText().toString()));
    }
}
