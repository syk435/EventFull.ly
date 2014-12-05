package com.zhao.karani.meetme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();

        if(intent.getStringExtra("duplicate")!=null){
            TextView reselectText;
            reselectText = (TextView) findViewById(R.id.reselect);
            reselectText.setVisibility(View.VISIBLE);
        }

        if(intent.getStringExtra("badName")!=null){
            TextView reselectText;
            reselectText = (TextView) findViewById(R.id.reselect1);
            reselectText.setVisibility(View.VISIBLE);
        }

        if(intent.getStringExtra("badPwd")!=null){
            TextView reselectText;
            reselectText = (TextView) findViewById(R.id.reselect2);
            reselectText.setVisibility(View.VISIBLE);
        }

        if(intent.getStringExtra("badAge")!=null){
            TextView reselectText;
            reselectText = (TextView) findViewById(R.id.reselect3);
            reselectText.setVisibility(View.VISIBLE);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void signUp(View view) {
        boolean signUpGood = true;
        boolean goodName = true;
        boolean goodPwd = true;
        boolean goodAge = true;

        EditText editText;

        editText = (EditText) findViewById(R.id.userEmailSignUp);
        String email = editText.getText().toString();
        if (email.length() < 3 || !email.contains("@")) {
            signUpGood = false;
            goodName = false;
        }

        editText = (EditText) findViewById(R.id.userPasswordSignUp);
        String userPwd = editText.getText().toString();
        if (userPwd.length() < 3) {
            signUpGood = false;
            goodPwd = false;
        }

        editText = (EditText) findViewById(R.id.userNameSignUp);
        String userName = editText.getText().toString();

        editText = (EditText) findViewById(R.id.userCitySignUp);
        String userCity= editText.getText().toString();

        editText = (EditText) findViewById(R.id.userOccupationSignUp);
        String userOccupation = editText.getText().toString();

        editText = (EditText) findViewById(R.id.userAgeSignUp);
        String userAge = editText.getText().toString();
        try{
            Integer.parseInt(userAge);
        } catch(Exception e){
            signUpGood = false;
            goodAge = false;
        }

        if(signUpGood) {
            //call signuphandler to create new user
            String result = HttpRequest.getData("http://meetmeutece.appspot.com/signUpHandler?userEmail="+email+"&userPassword="+userPwd+"&userName="+userName.replaceAll(" ","%20")+"&currentCity="+userCity+"&occupation="+userOccupation+"&age="+userAge, "");
            JSONObject jsonObj;
            String signUpResult;
            try {
                jsonObj = new JSONObject(result);
                signUpResult = (String) jsonObj.get("signUpResult");
                Log.d("Json", signUpResult);
            }catch(JSONException e){
                jsonObj = null;
                signUpResult = null;
                Log.d("Json Error", e.toString());
            }

            if(jsonObj!=null && signUpResult.equals("Signup Successful")) {
                Intent intent = new Intent(this, LoginActivity.class);
                LoginActivity.setEmail(email);
                intent.putExtra("fromSignUp","yo");
                startActivity(intent);

                //START TIMER HERE
            }
            else{
                Intent intent = new Intent(this, SignUp.class);
                intent.putExtra("duplicate", "duplicate");
                startActivity(intent);
            }
        }
        else{
            Intent intent = new Intent(this, SignUp.class);
            if(!goodName){
                intent.putExtra("badName", "badName");
            }
            if(!goodPwd){
                intent.putExtra("badPwd", "badPwd");
            }
            if(!goodAge){
                intent.putExtra("badAge", "badAge");
            }
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
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
