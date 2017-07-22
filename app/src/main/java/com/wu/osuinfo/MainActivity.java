package com.wu.osuinfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ASYNC_TASK";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        final Button searchbtn = (Button)findViewById(R.id.searchbtn);
        final EditText input = (EditText)findViewById(R.id.user_name);
        final ProgressBar loadingBar = (ProgressBar)findViewById(R.id.loadingBar);
        final TextInputLayout textInputLayout = (TextInputLayout)findViewById(R.id.user_name_layout);
        final Animation shakespeare = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);

        loadingBar.setVisibility(View.INVISIBLE);


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getUserJSON uTask = new getUserJSON();
            String what = input.getText().toString();
            if (what.matches("[a-zA-Z0-9.? ]*")){
                uTask.execute(input.getText().toString());
            } else {
                input.setAnimation(shakespeare);
                textInputLayout.setError("Invalid character(s)!");
            }

            }
        });

        navigationView.setCheckedItem(0);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Home" :
                        // Home Click Event here
                        break;
                    case "Trend" :
                        // Trend Click Event here
                        Intent gotoTrend = new Intent(MainActivity.this, TrendActivity.class);
                        startActivity(gotoTrend);
                        break;
                    case "Setting" :
                        // Setting Click Event here
                        break;
                    case "Share" :
                        // Share Click Event here
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class getUserJSON extends AsyncTask<String, Integer, String> {

        final Button searchButton = (Button)findViewById(R.id.searchbtn);
        final ProgressBar pgBar = (ProgressBar)findViewById(R.id.loadingBar);
        final AlertDialog.Builder uNotFoundBuilder = new AlertDialog.Builder(MainActivity.this);
        final TextInputLayout textInputLayout = (TextInputLayout)findViewById(R.id.user_name_layout);
        final EditText editText = (EditText)findViewById(R.id.user_name);
        final Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);

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

                final Intent gotoDetail = new Intent(MainActivity.this, DetailActivity.class);

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
                        }
                        Log.i("", "Received info:");
                        Log.i("JSONData", re_username + re_playcount + re_pp + re_grank + re_crank);
                        String[] parsedPackage = {re_username, re_pp, re_playcount, re_grank, re_crank, re_countss, re_counts, re_counta, re_totalscore};

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
