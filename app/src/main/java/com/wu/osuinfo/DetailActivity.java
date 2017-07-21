package com.wu.osuinfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
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



        Intent getParsedPackage = getIntent();
        String[] receivedParsedPackage = getParsedPackage.getStringArrayExtra("detailValue");  // Remember to check MainActivity for the content of the String Array.

        TextView username = (TextView)findViewById(R.id.user_name);
        TextView pp = (TextView)findViewById(R.id.pp);
        TextView playcount = (TextView)findViewById(R.id.play_count);
        TextView grank = (TextView)findViewById(R.id.rank);
        TextView crank = (TextView)findViewById(R.id.crank);
        TextView sscount = (TextView)findViewById(R.id.ss_count);
        TextView scount = (TextView)findViewById(R.id.s_count);
        TextView acount = (TextView)findViewById(R.id.a_count);

        Button backButton = (Button)findViewById(R.id.back);
        Button compareButton = (Button)findViewById(R.id.compare);

        String re_username = receivedParsedPackage[0];
        String re_pp = receivedParsedPackage[1];
        String re_playcount = receivedParsedPackage[2];
        String re_grank = receivedParsedPackage[3];
        String re_crank = receivedParsedPackage[4];
        String re_ss_count = receivedParsedPackage[5];
        String re_s_count = receivedParsedPackage[6];
        String re_a_count = receivedParsedPackage[7];

        username.setText(re_username);
        pp.setText(re_pp);
        playcount.setText(re_playcount);
        grank.setText(re_grank);
        crank.setText(re_crank);
        sscount.setText(re_ss_count);
        scount.setText(re_s_count);
        acount.setText(re_a_count);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(DetailActivity.this);
                View dialogView = layoutInflater.inflate(R.layout.inputdialog, null);
                AlertDialog.Builder inputDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
                inputDialogBuilder.setView(dialogView);

                inputDialogBuilder.setCancelable(true)
                                  .setTitle("To whom?");

                final ProgressBar pgBar = (ProgressBar)dialogView.findViewById(R.id.input_pgBar);
                final Button confirm = (Button)dialogView.findViewById(R.id.input_button);
                final EditText compareInput = (EditText)dialogView.findViewById(R.id.compare_input);

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final getUserJSON uTask = new getUserJSON();
                        String username = compareInput.getText().toString();

                        uTask.execute(username);
                    }
                });

                inputDialogBuilder.show();
            }
        });

    }


    public class getUserJSON extends AsyncTask<String, Integer, String> {


        final AlertDialog.Builder uNotFoundBuilder = new AlertDialog.Builder(DetailActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("getUserJSON", "PreExecuting...");
        }

        @Override
        protected String doInBackground(String... params) {
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
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // All Log.i/e are Debug only and should be quoted in further implement.
            // Log.i("", s);
            String resultpackage[] = s.split("\\|");
            String json = resultpackage[0];
            String username = resultpackage[1];

            // final Intent gotoDetail = new Intent(MainActivity.this, DetailActivity.class);
            Intent gotoCompare = new Intent(DetailActivity.this, CompareActivity.class);

            // searchButton.setEnabled(true);
            // pgBar.setVisibility(View.INVISIBLE);

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

                    // Now open an activity of COMPARE figures.

                    // Join 2 arrays together
                    Intent getFirstPackage = getIntent();
                    String[] firstPackage = getFirstPackage.getStringArrayExtra("detailValue");
                    Log.i("Join works", "First package is:" + firstPackage);

                    Collection<String> getCombinedPackage = new ArrayList<String>();
                    getCombinedPackage.addAll(Arrays.asList(firstPackage));
                    getCombinedPackage.addAll(Arrays.asList(parsedPackage));
                    String[] combinedPackage = getCombinedPackage.toArray(new String[] {});
                    gotoCompare.putExtra("compareValue", combinedPackage);
                    startActivity(gotoCompare);

                    // gotoDetail.putExtra("detailValue", parsedPackage);
                    // startActivity(gotoDetail);

                } catch (Exception e) {
                    Log.e("JSONParsingError", "Something went wrong parsing JSON.");
                    Log.e("JSONParsingError", e.getMessage());
                }


            } else {
                // Log.e("", "No user found!");
                Exception e = new Exception("Player Not Found");
                uNotFoundBuilder.show();
            }

        }

    }

}
