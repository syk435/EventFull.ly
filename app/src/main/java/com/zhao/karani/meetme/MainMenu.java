package com.zhao.karani.meetme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;


public class MainMenu extends Activity {

    private static boolean currentActivity = true;

    public static boolean getCurrentActivity(){
        return currentActivity;
    }

    public static void setCurrentActivity(boolean m) {
        currentActivity = m;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        String result = HttpRequest.getData("http://meetmeutece.appspot.com/checkCurrentEventAvailability?userEmail="+LoginActivity.getEmail(), "");
        JSONObject jsonObj;
        String addFriendResult;
        Log.d("result", result);
        try {
            jsonObj = new JSONObject(result);
            addFriendResult = (String) jsonObj.get("currentEventAvailable");
        }catch(JSONException e){
            jsonObj = null;
            addFriendResult = null;
            Log.d("Json Error", e.toString());
        }
        if(addFriendResult.equals("Yes")){
            setCurrentActivity(true);
        }
        else{
            setCurrentActivity(false);
        }

        if(getCurrentActivity()){
            Button button = (Button) findViewById(R.id.addEventButton);
            button.setEnabled(false);

            button = (Button) findViewById(R.id.ongoingEventButton);
            button.setEnabled(true);
        }
        else{
            Button button = (Button) findViewById(R.id.ongoingEventButton);
            button.setEnabled(false);

            button = (Button) findViewById(R.id.addEventButton);
            button.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {

        return;
    }

    public void manageFriends(View view) {
        Intent intent = new Intent(this, ManageFriends.class);
        startActivity(intent);
    }

    public void addEvent(View view) {
        Intent intent = new Intent(this, CreateEventFirstPage.class);
        startActivity(intent);
    }

    public void ongoingEvent(View view) {
        Intent intent = new Intent(this, OngoingEvent.class);
        startActivity(intent);
    }

    public void testMethod(View view) {
        Intent intent = new Intent(this, ViewEvents.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
}
