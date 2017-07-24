package com.wu.osuinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link searchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link searchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class searchFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public searchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment searchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static searchFragment newInstance(String param1, String param2) {
        searchFragment fragment = new searchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        final Button searchbtn = (Button)v.findViewById(R.id.searchbtn);
        final EditText input = (EditText)v.findViewById(R.id.user_name);
        final ProgressBar loadingBar = (ProgressBar)v.findViewById(R.id.loadingBar);
        final TextInputLayout textInputLayout = (TextInputLayout)v.findViewById(R.id.user_name_layout);
        final Animation shakespeare = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);



        final Button clearsubs = (Button)v.findViewById(R.id.debug_clearsubs); // DEBUG ONLY
        clearsubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = v.getContext().getSharedPreferences("subList", Context.MODE_PRIVATE);
                SharedPreferences.Editor spe = sp.edit();

                spe.clear();

                spe.commit();
            }
        });




        loadingBar.setVisibility(View.INVISIBLE);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        v.findViewById(R.id.searchbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("UI Interact", "Search Button Clicked!");
                getUserJSON uTask = new getUserJSON();
                String what = input.getText().toString();
                if (what.matches("[\\w_\\-\\s\\[\\]]*?")){
                    uTask.execute(input.getText().toString());
                } else {
                    input.setAnimation(shakespeare);
                    textInputLayout.setError("Invalid character(s)!");
                }
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
        void onFragmentInteraction(Uri uri);
    }

    public class getUserJSON extends AsyncTask<String, Integer, String> {

        final Button searchButton = (Button)getView().findViewById(R.id.searchbtn);
        final ProgressBar pgBar = (ProgressBar)getView().findViewById(R.id.loadingBar);
        final AlertDialog.Builder uNotFoundBuilder = new AlertDialog.Builder(getContext());
        final TextInputLayout textInputLayout = (TextInputLayout)getView().findViewById(R.id.user_name_layout);
        final EditText editText = (EditText)getView().findViewById(R.id.user_name);
        final Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("getUserJSON", "PreExecuting...");
            searchButton.setEnabled(false);
            pgBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            if(!Objects.equals(params[0], "")){
                Log.i("", "Start Fetching JSON...");
                String token = getResources().getString(R.string.apikey);
                String username = params[0];
                String url = "https://osu.ppy.sh/api/get_user";
                String param = "k=" + token + "&u=" + username;
                String json = "";
                BufferedReader in = null;
                try {
                    String urlNameString = url + "?" + param;
                    URL realUrl = new URL(urlNameString);
                    URLConnection connection = realUrl.openConnection();
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    connection.setRequestProperty("user-agent",
                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                    connection.connect();
                    Map<String, List<String>> map = connection.getHeaderFields();
                    for (String key : map.keySet()) {
                        System.out.println(key + "--->" + map.get(key));
                    }
                    in = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        json += line;
                    }
                } catch (Exception e) {
                    System.out.println("Error sending URL Request!" + e);
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                json = json + "|" + params[0];
                return json;
            } else {
                return "empty";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(Objects.equals(s, "empty")){
                textInputLayout.setError("Please input username!");
                editText.startAnimation(shake);
                searchButton.setEnabled(true);
                pgBar.setVisibility(View.INVISIBLE);
            } else {
                // All Log.i/e are Debug only and should be quoted in further implement.
                // Log.i("", s);
                String resultpackage[] = s.split("\\|");
                String json = resultpackage[0];
                String username = resultpackage[1];

                final Intent gotoDetail = new Intent(getContext(), DetailActivity.class);

                searchButton.setEnabled(true);
                pgBar.setVisibility(View.INVISIBLE);
                uNotFoundBuilder.setTitle("Oops!")
                        .setMessage("Couldn't find player: " + username)
                        .setPositiveButton("OK", null)
                        .setCancelable(false);
                if (!Objects.equals(json, "[]")) {

                    Log.i("", "Get JSON Data: " + json + "And editing...");
                    // json = "{\"userinfo\":" + json + "}";
                    // Log.i("Manipulation", "After editing: " + json);
                    // Can add more strings.
                    String re_username = "";
                    String re_playcount = "";
                    String re_pp = "";
                    String re_grank = "";
                    String re_crank = "";
                    String re_countss = "";
                    String re_counts = "";
                    String re_counta = "";
                    String re_totalscore = "";
                    String re_userid = "";
                    String re_usercountry = "";

                    try {
                        JSONArray JArray = new JSONArray(json);
                        for (int i = 0; i < JArray.length(); i++) {
                            JSONObject jObject = JArray.optJSONObject(i);
                            re_username = jObject.optString("username");
                            re_playcount = jObject.optString("playcount");
                            re_pp = jObject.optString("pp_raw");
                            re_grank = jObject.optString("pp_rank");
                            re_crank = jObject.optString("pp_country_rank");
                            re_countss = jObject.optString("count_rank_ss");
                            re_counts = jObject.optString("count_rank_s");
                            re_counta = jObject.optString("count_rank_a");
                            re_totalscore = jObject.optString("total_score");
                            re_userid = jObject.optString("user_id");
                            re_usercountry = jObject.optString("country");
                        }
                        Log.i("", "Received info:");
                        Log.i("JSONData", re_username + re_playcount + re_pp + re_grank + re_crank);
                        String[] parsedPackage = {re_username, re_pp, re_playcount, re_grank, re_crank, re_countss, re_counts, re_counta, re_totalscore, re_userid,re_usercountry};

                        // Now open an activity of detail figures.
                        gotoDetail.putExtra("detailValue", parsedPackage);
                        startActivity(gotoDetail);

                    } catch (Exception e) {
                        Log.e("JSONParsingError", "Something went wrong parsing JSON.");
                        Log.e("JSONParsingError", e.getMessage());
                    }


                } else {
                    // Log.e("", "No user found!");
                    Exception e = new Exception("Player Not Found");
                    textInputLayout.setError("Couldn't find player!");
                    editText.startAnimation(shake);
                }
            }

        }

    }
}
