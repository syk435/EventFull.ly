package com.zhao.karani.meetme;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFriends.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFriends#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SearchFriends extends Fragment implements Button.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private HashMap<String,String> friendNameEmailMap = new HashMap<String,String>();
    private HashMap<Integer,String>idFriendMap = new HashMap<Integer,String>();
    private EditText editText;
    private LinearLayout myLInearLayout;
    private TextView numResults;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFriends.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFriends newInstance(String param1, String param2) {
        SearchFriends fragment = new SearchFriends();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SearchFriends() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public String addFriend(String friendEmail) {
        String result = HttpRequest.getData("http://meetmeutece.appspot.com/addFriendHandler?userEmail="+LoginActivity.getEmail()+"&friendEmail="+friendEmail, "");
        JSONObject jsonObj;
        String addFriendResult;
        try {
            jsonObj = new JSONObject(result);
            addFriendResult = (String) jsonObj.get("addFriendResult");
            //Log.d("Json", addFriendResult);
        }catch(JSONException e){
            jsonObj = null;
            addFriendResult = null;
            Log.d("Json Error", e.toString());
        }

        return addFriendResult;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_friends, container, false);

        Button buttonSayHi = (Button) view.findViewById(R.id.searchButton);
        buttonSayHi.setOnClickListener(this);

        editText = (EditText) view.findViewById(R.id.userSearch);
        myLInearLayout =(LinearLayout) view.findViewById(R.id.searchFragmentLayout);
        numResults = (TextView) view.findViewById(R.id.numSearchResults);

        return view;
    }

    public void onClick(View v){
        switch(v.getId()){
//            case R.id.testAdd:
//                String result = HttpRequest.getData("http://meetmeutece.appspot.com/addFriendHandler?userEmail="+LoginActivity.getEmail()+"&friendEmail="+"kevzsolo@gmail.com", "");
//                JSONObject jsonObj;
//                String addFriendResult;
//                try {
//                    jsonObj = new JSONObject(result);
//                    addFriendResult = (String) jsonObj.get("addFriendResult");
//                    Log.d("Json", addFriendResult);
//                }catch(JSONException e){
//                    jsonObj = null;
//                    addFriendResult = null;
//                    Log.d("Json Error", e.toString());
//                }
//                break;
            case R.id.searchButton:
                myLInearLayout.removeViews(4,friendNameEmailMap.size());
                friendNameEmailMap.clear();
                idFriendMap.clear();

                String userSearch = editText.getText().toString();
                String result1 = "";
                try {
                    result1 = HttpRequest.getData("http://meetmeutece.appspot.com/searchFriends?searchContent=" + URLEncoder.encode(userSearch, "UTF-8"), "");
                }catch(Exception e) {
                    Log.d("Encoding Error", e.toString());
                }
                JSONArray jsonObj1;
                try {
                    jsonObj1 = new JSONArray(result1);
                    for(int i=0;i<jsonObj1.length();i++) {
                        JSONObject a = jsonObj1.getJSONObject(i);
                        String userName = (String) a.get("userName");
                        String userEmail = (String) a.get("userEmail");
                        friendNameEmailMap.put(userName, userEmail);
                    }
                    numResults.setText(Integer.toString(jsonObj1.length())+" results found for " + userSearch);
                    numResults.setVisibility(View.VISIBLE);
                }catch(JSONException e){
                    jsonObj1 = null;
                    Log.d("Json Error", e.toString());
                }

                int i = 4; //starting at fourth thing in the linear layout
                for(String key:friendNameEmailMap.keySet()){

                    String email = friendNameEmailMap.get(key);
                    //TextView tempV = new TextView(getActivity());
                    //tempV.setText(email);
                    //tempV.setTextSize(20);
                    //tempV.setGravity(Gravity.CENTER);

                    Button tempB = new Button(getActivity());
                    idFriendMap.put(tempB.getId(),email);
                    tempB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add,0,0,0);
                    //tempB.setWidth(50);
                    //tempB.setHeight(40);
                    tempB.setText(email);
                    tempB.setBackgroundColor(Color.parseColor("#00ffffff"));
                    tempB.setGravity(Gravity.CENTER);
                    tempB.setOnClickListener(getOnClickDoSomething(tempB));

                    LinearLayout tempL = new LinearLayout(getActivity());
                    tempL.setMinimumWidth(myLInearLayout.getWidth());
                    tempL.setGravity(Gravity.LEFT);
                    tempL.setOrientation(LinearLayout.HORIZONTAL);
//                    tempL.addView(tempV);
                    tempL.addView(tempB);

                    myLInearLayout.addView(tempL,i++);
//                    Log.d("textview", tempV.getText().toString());
                }

                break;
        }

    }

    View.OnClickListener getOnClickDoSomething(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //button.setText("text now set.. ");
                String result = addFriend(button.getText().toString());
                //if result = success, toast("success") else...
                if(result.equals("Add Successful")) {
                    Toast.makeText(getActivity(), button.getText().toString() + " Added Successfully", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getActivity(), button.getText().toString()+" Already Added!", Toast.LENGTH_LONG).show();
                }
                button.setEnabled(false);
            }
        };
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
