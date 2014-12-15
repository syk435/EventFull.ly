package com.zhao.karani.meetme;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;


public class CreateEventFirstPage extends Activity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    int destMarker = 0;
    Marker marker;
    private GoogleMap myMap;

    Location myLocation;
    double destLat = 9999;
    double destLong = 9999;

    EditText editText;
    //TextView tvLocInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_first_page);

        //tvLocInfo = (TextView)findViewById(R.id.displayLoc);

        FragmentManager myFragmentManager = getFragmentManager();
        MapFragment myMapFragment
                = (MapFragment)myFragmentManager.findFragmentById(R.id.mapSelect);
        myMap = myMapFragment.getMap();
        myMap.setMyLocationEnabled(true);

        //myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        myMap.setOnMapClickListener(this);
        myMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        //tvLocInfo.setText(point.toString());
        myMap.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    public void createEvent(View view) {
        if(destLat!=9999 && destLong!=9999) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            editText = (EditText) findViewById(R.id.userRadius);
            String userRadius = "";
            userRadius = editText.getText().toString();
            boolean goodRadius = true;
            try{
                Integer.parseInt(userRadius);
            } catch(Exception e){
                goodRadius = false;
            }

            editText = (EditText) findViewById(R.id.userEventTitle);
            String eventTitle = "";
            eventTitle = editText.getText().toString();

            editText = (EditText) findViewById(R.id.userDateTime);
            String userDateTime = "";
            userDateTime = editText.getText().toString();

            if(!userRadius.equals("") && !eventTitle.equals("") && !userDateTime.equals("") && goodRadius) {
                String result = "";
                try {
                    result = HttpRequest.getData("http://meetmeutece.appspot.com/createEventHandler?userEmail=" + LoginActivity.getEmail() + "&eventTitle=" + URLEncoder.encode(eventTitle, "UTF-8") + "&dateTimeToMeet=" + URLEncoder.encode(userDateTime,"UTF-8") + "&destinationLongitude=" + destLong + "&destinationLatitude=" + destLat + "&radius=" + userRadius, "");
                } catch (Exception e) {
                    Log.d("Url encoding error",e.toString());
                }
                JSONObject jsonObj;
                String loginResult;
                Log.d("result", result);
                try {
                    jsonObj = new JSONObject(result);
                    loginResult = (String) jsonObj.get("eventID");
                } catch (JSONException e) {
                    jsonObj = null;
                    loginResult = null;
                    Log.d("Json Error", e.toString());
                }

                Intent intent = new Intent(this, CreateEventSecondPage.class);
                intent.putExtra("eventID",loginResult);
                intent.putExtra("radius", userRadius);
                intent.putExtra("eventTitle", eventTitle);
                intent.putExtra("destLong", Double.toString(destLong));
                intent.putExtra("destLat", Double.toString(destLat));
                startActivity(intent);
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        //tvLocInfo.setText("New marker added@" + point.toString());
        if(destMarker==0) {
            marker = myMap.addMarker(new MarkerOptions().position(point).title(point.toString()));
            destLat = point.latitude;
            destLong = point.longitude;
            //tvLocInfo.setText(Double.toString(destLat) + ", " + Double.toString(destLong));
            destMarker++;
        }
        else{
            marker.remove();
            marker = myMap.addMarker(new MarkerOptions().position(point).title(point.toString()));
            destLat = point.latitude;
            destLong = point.longitude;
            //tvLocInfo.setText(Double.toString(destLat) + ", " + Double.toString(destLong));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_event_first_page, menu);
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

//    @Override
//    public void onBackPressed() {
//
//        return;
//    }
//
//    public void returnMenu(View view) {
//        Intent intent = new Intent(this, MainMenu.class);
//        startActivity(intent);
//    }
}
