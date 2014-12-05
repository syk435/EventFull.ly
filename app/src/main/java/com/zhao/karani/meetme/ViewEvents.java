package com.zhao.karani.meetme;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ViewEvents extends Activity {

    private final SparseArray<Group> groups = new SparseArray<Group>();
    public LayoutInflater inflater;
    public Activity activity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        createData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);


    }

    private void createData() {
        String result = HttpRequest.getData("http://meetmeutece.appspot.com/displayPastEventHandler?userEmail="+LoginActivity.getEmail(), "");
        JSONArray jsonObj;
        try {
            jsonObj = new JSONArray(result);
            for(int i=0;i<jsonObj.length();i++) {
                JSONObject a = jsonObj.getJSONObject(i);
                String eventTitle = (String) a.get("eventTitle");
                String eventCreator = (String) a.get("eventCreator");
                String dateCreated = (String) a.get("dateTimeCreated");
                String dateOfMeeting = (String) a.get("dateTimeOfMeeting");
                String dateFinished = (String) a.get("dateTimeFinished");
                String numInvited = (String) a.get("numberOfFriendsInvited");

                Group group = new Group(eventTitle);
                group.children.add("Created by: " + eventCreator);
                group.children.add("DateTime created: " + dateCreated);
                group.children.add("DateTime of Meeting: " + dateOfMeeting);
                group.children.add("DateTime finished: " + dateFinished);
                group.children.add("Number of attendees: " + numInvited);
                groups.append(i, group);
            }
        }catch(JSONException e){
            jsonObj = null;
            Log.d("Json Error", e.toString());
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_events, menu);
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
