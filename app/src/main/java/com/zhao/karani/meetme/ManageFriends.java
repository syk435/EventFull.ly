package com.zhao.karani.meetme;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ManageFriends extends Activity implements SearchFriends.OnFragmentInteractionListener {

    //final ArrayList<String> myFriendNames = new ArrayList<String>();
    final HashMap<String,String> friendNameEmailMap = new HashMap<String,String>();
    //final HashMap<String,Button> userAddButtonMap = new HashMap<String,Button>();

    private LinearLayout myLInearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        LoginActivity.setCurrentClass(ManageFriends.class);

        String result = HttpRequest.getData("http://meetmeutece.appspot.com/getUserFriendsHandler?userEmail="+LoginActivity.getEmail(), "");
        JSONArray jsonObj;
        try {
            jsonObj = new JSONArray(result);
            for(int i=0;i<jsonObj.length();i++) {
                JSONObject a = jsonObj.getJSONObject(i);
                String userName = (String) a.get("userName");
                String userEmail = (String) a.get("userEmail");
                friendNameEmailMap.put(userName,userEmail);
            }
        }catch(JSONException e){
            jsonObj = null;
            Log.d("Json Error", e.toString());
        }

        myLInearLayout =(LinearLayout) findViewById(R.id.manageFriendsLayout);
        int i = 2; //starting at second thing in the linear layout
        for(String key:friendNameEmailMap.keySet()){
            String email = friendNameEmailMap.get(key);
            TextView tempV = new TextView(this);
            tempV.setText(email);
            tempV.setTextSize(20);
            //Button tempB;
            LinearLayout tempL = new LinearLayout(this);
            tempL.addView(tempV);
            myLInearLayout.addView(tempL,i++);
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_friends, menu);
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
