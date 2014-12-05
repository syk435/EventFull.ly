package com.zhao.karani.meetme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class CreateEventSecondPage extends Activity {

    private String eventID;
    private int radius;
    private double destLong;
    private double destLat;
    private String eventTitle;

    private HashMap<String,String> friendNameEmailMap = new HashMap<String,String>();
    private HashMap<Integer,String>idFriendMap = new HashMap<Integer,String>();
    private LinearLayout myLInearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_second_page);

        Intent intent = getIntent();
        this.eventID = intent.getStringExtra("eventID");
        this.radius = Integer.parseInt(intent.getStringExtra("radius"));
        this.destLat = Double.parseDouble(intent.getStringExtra("destLat"));
        this.destLong = Double.parseDouble(intent.getStringExtra("destLong"));
        this.eventTitle = intent.getStringExtra("eventTitle");

        myLInearLayout =(LinearLayout) findViewById(R.id.addFriendsEventLayout);

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

//        myLInearLayout.removeViews(2,friendNameEmailMap.size());
//        friendNameEmailMap.clear();
//        idFriendMap.clear();

        int i = 2; //starting at fourth thing in the linear layout
        for(String key:friendNameEmailMap.keySet()){

            String email = friendNameEmailMap.get(key);
            //TextView tempV = new TextView(getActivity());
            //tempV.setText(email);
            //tempV.setTextSize(20);
            //tempV.setGravity(Gravity.CENTER);

            Button tempB = new Button(this);
            idFriendMap.put(tempB.getId(),email);
            tempB.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_add,0,0);
            //tempB.setWidth(50);
            //tempB.setHeight(40);
            tempB.setText(email);
            tempB.setGravity(Gravity.CENTER);
            tempB.setOnClickListener(getOnClickDoSomething(tempB));

            LinearLayout tempL = new LinearLayout(this);
            tempL.setMinimumWidth(myLInearLayout.getWidth());
            tempL.setGravity(Gravity.RIGHT);
            tempL.setOrientation(LinearLayout.HORIZONTAL);
//                    tempL.addView(tempV);
            tempL.addView(tempB);

            myLInearLayout.addView(tempL,i++);
//                    Log.d("textview", tempV.getText().toString());
        }
    }

    View.OnClickListener getOnClickDoSomething(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //button.setText("text now set.. ");
                String result = addFriend(button.getText().toString());
                //if result = success, toast("success") else...
                if(result.equals("Invite Successfully")) {
                    Toast.makeText(getApplicationContext(), button.getText().toString() + " Added Successfully", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), button.getText().toString()+" Already In An Event", Toast.LENGTH_LONG).show();
                }
                button.setEnabled(false);
            }
        };
    }

    @Override
    public void onBackPressed() {

        return;
    }

    public void doneCreateEvent(View view) {
        Intent intent = new Intent(this, OngoingEvent.class);
        intent.putExtra("eventTitle", this.eventTitle);
        intent.putExtra("radius", this.radius);
        intent.putExtra("destLat", this.destLat);
        intent.putExtra("destLong", this.destLong);
        intent.putExtra("eventID", this.eventID);
        startActivity(intent);
    }

    public String addFriend(String friendEmail) {
        String result = HttpRequest.getData("http://meetmeutece.appspot.com/inviteFriendsHandler?eventID="+eventID+"&friendEmail="+friendEmail, "");
        JSONObject jsonObj;
        String addFriendResult;
        try {
            jsonObj = new JSONObject(result);
            addFriendResult = (String) jsonObj.get("InviteResult");
            //Log.d("Json", addFriendResult);
        }catch(JSONException e){
            jsonObj = null;
            addFriendResult = null;
            Log.d("Json Error", e.toString());
        }

        return addFriendResult;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_event_second_page, menu);
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
