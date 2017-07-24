package com.wu.osuinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public RecyclerView recyclerView;
    public TrendAdapter trendAdapter;
    public List<Trend> trendList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;



    public TrendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendFragment newInstance(String param1, String param2) {
        TrendFragment fragment = new TrendFragment();
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
        View v = inflater.inflate(R.layout.fragment_trend, container, false);

        String[] subList = {};

        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fab);

        trendList = new ArrayList<>();
        trendAdapter = new TrendAdapter(getContext(), trendList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(trendAdapter);

        SwipeableRecyclerViewTouchListener swipeListener = new SwipeableRecyclerViewTouchListener(recyclerView, new SwipeableRecyclerViewTouchListener.SwipeListener() {
            @Override
            public boolean canSwipeLeft(int position) {
                return true;
            }

            @Override
            public boolean canSwipeRight(int position) {
                return true;
            }

            @Override
            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                SharedPreferences sp = getContext().getSharedPreferences("subList", Context.MODE_PRIVATE);
                String oldSubList = sp.getString("listString", "");
                String[] realOldSubList = oldSubList.split("\\|");
                for (int position : reverseSortedPositions){
                    trendList.remove(position);
                    trendAdapter.notifyItemRemoved(position);

                }
                trendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions){
                    trendList.remove(position);
                    trendAdapter.notifyItemRemoved(position);
                }
                trendAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.addOnItemTouchListener(swipeListener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSubsManagement = new Intent(v.getContext(), SubscriptionManageActivity.class);
                startActivity(gotoSubsManagement);
            }
        });


        // Now fetch the subscription list
        SharedPreferences slist = v.getContext().getSharedPreferences("subList", Context.MODE_PRIVATE);
        String listString = slist.getString("listString", "124493|4787150|2558286");  // here we got Cookiezi|Vaxei|Rafis XD

        subList = listString.split("\\|");

        try {
            prepareTrends(subList, v);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }



    private void prepareTrends(String[] subscription, View v) throws Exception {
        // Debug only.
        // Trend cookiezi = new Trend("cookiezi", "13928", "13827", "1", "2", "160571088386", "160563443901", "18437", "18436", getContext().getDrawable(R.mipmap.ic_cookiezi));
        // Trend vaxei = new Trend("Vaxei", "13407", "13424", "2", "3", "166023938049", "166020091321", "106000", "105890", getContext().getDrawable(R.mipmap.ic_vaxei));
        // Trend rafis = new Trend("Rafis", "13415", "13415", "3", "4", "390291064248", "390230188212", "216468", "216453", getContext().getDrawable(R.mipmap.ic_rafis));
        // trendList.add(cookiezi);
        // trendList.add(vaxei);
        // trendList.add(rafis);

        // need to get: name, pp, rank, totalscore, playcount, avatar.
        int listLength = subscription.length;
        int i;
        for (i = 0; i < listLength; i++){
            if(!Objects.equals(subscription[i], "")){
                Log.i("Arranging Cards Beta", "i:" + Integer.toString(i) + " listLength:" + Integer.toString(listLength));
                String subUser = subscription[i];
                Log.i("Arranging Cards Beta", "Now arranging: " + subUser);
                getCardInfoParams g = new getCardInfoParams(v, subUser);
                AsyncTaskTool.execute(new getCardInfoTask(), g);
            }
        }
    }

    public void createTrend(String uName, String uPP, String uRank, String uTotalScore, String uPlayCount, Drawable uAvatar, Drawable uAvatarBlurred){
        Trend subUser = new Trend(uName, uPP, uPP, uRank, uRank, uTotalScore, uTotalScore, uPlayCount, uPlayCount, uAvatar, uAvatarBlurred);
        trendList.add(subUser);
        trendAdapter.notifyDataSetChanged();
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

    private static class getCardInfoParams {
        String username;
        View view;

        getCardInfoParams(View v, String s) {
            this.view = v;
            this.username = s;
        }
    }

    public class getCardInfoTask extends AsyncTask<getCardInfoParams, Integer, Object[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object[] doInBackground(getCardInfoParams... params) {
            if(!Objects.equals(params[0], "")){
                Log.i("", "Start Fetching JSON...");
                String token = getResources().getString(R.string.apikey);
                String username = params[0].username;
                String url = "https://osu.ppy.sh/api/get_user";
                String param = "k=" + token + "&u=" + username;
                String json = "";
                Drawable avatar = null;
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
                json = json + "|" + params[0].username;
                Object[] p = {null, null, null, null};
                InputStream is = null;
                try {
                    is = (InputStream) new URL("https://a.ppy.sh/" + params[0].username).getContent();
                    avatar = Drawable.createFromStream(is, "src name");
                    FastBlur fb = new FastBlur();
                    try{
                        Bitmap b1 = fb.fastblur((((BitmapDrawable)avatar).getBitmap()), 1f ,30);
                        p[2] = new BitmapDrawable(b1);
                    } catch (NullPointerException e) {
                        Bitmap b1 = null;
                        p[2] = avatar;
                    }
                    p[0] = json;
                    p[1] = avatar;
                    p[3] = params[0].view;
                    return p;
                } catch (IOException e) {
                    e.printStackTrace();
                    p[0] = json;
                    p[1] = getResources().getDrawable(R.mipmap.no_avatar);
                    FastBlur fb = new FastBlur();
                    Bitmap b2 = fb.fastblur((((BitmapDrawable)p[1]).getBitmap()), 1f ,30);
                    p[2] = new BitmapDrawable(b2);
                    p[3] = params[0].view;
                    return p;
                }



            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object[] d) {
            super.onPostExecute(d);
            if(Objects.equals(d, null)){
                return;
            } else {
                // All Log.i/e are Debug only and should be quoted in further implement.
                // Log.i("", s);
                String s = (String)d[0];
                String resultpackage[] = s.split("\\|");
                String json = resultpackage[0];
                // String username = resultpackage[1];
                Drawable re_avatar = (Drawable)d[1];
                Drawable re_avatar_blurred = (Drawable)d[2];
                View v = (View)d[3];


                final ProgressBar ldBar = (ProgressBar)v.findViewById(R.id.trend_loadingIndicator);
                final Intent gotoDetail = new Intent(getContext(), DetailActivity.class);
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
                    // String re_countss = "";
                    // String re_counts = "";
                    // String re_counta = "";
                    String re_totalscore = "";
                    // String re_userid = "";
                    // String re_usercountry = "";

                    try {
                        JSONArray JArray = new JSONArray(json);
                        for (int i = 0; i < JArray.length(); i++) {
                            JSONObject jObject = JArray.optJSONObject(i);
                            re_username = jObject.optString("username");
                            re_playcount = jObject.optString("playcount");
                            re_pp = jObject.optString("pp_raw");
                            re_grank = jObject.optString("pp_rank");
                            re_crank = jObject.optString("pp_country_rank");
                            // re_countss = jObject.optString("count_rank_ss");
                            // re_counts = jObject.optString("count_rank_s");
                            // re_counta = jObject.optString("count_rank_a");
                            re_totalscore = jObject.optString("total_score");
                            // re_userid = jObject.optString("user_id");
                            // re_usercountry = jObject.optString("country");
                        }
                        Log.i("", "Received info:");
                        Log.i("JSONData", re_username + re_playcount + re_pp + re_grank + re_crank);
                        // String[] parsedPackage = {re_username, re_pp, re_playcount, re_grank, re_crank, re_countss, re_counts, re_counta, re_totalscore, re_userid,re_usercountry};

                        // Initialize Cards
                        createTrend(re_username, re_pp, re_grank, re_totalscore, re_playcount, re_avatar, re_avatar_blurred);
                        ldBar.setVisibility(View.INVISIBLE);


                    } catch (Exception e) {
                        Log.e("JSONParsingError", "Something went wrong parsing JSON.");
                        Log.e("JSONParsingError", e.getMessage());
                    }


                } else {
                    // Log.e("", "No user found!");
                    Exception e = new Exception("Player Not Found");
                }
            }
        }

    }


}


