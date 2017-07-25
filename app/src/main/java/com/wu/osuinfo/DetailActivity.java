package com.wu.osuinfo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    final public String[] modes = {"0", "1", "2", "3"};
    final public String[] modesName = {"osu!Standard", "osu!Taiko", "osu!CTB", "osu!Mania"};

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
        TextView playcount = (TextView)findViewById(R.id.detail_playcount);
        // TextView grank = (TextView)findViewById(R.id.rank);
        TextView crank = (TextView)findViewById(R.id.crank);
        TextView sscount = (TextView)findViewById(R.id.ss_count);
        TextView scount = (TextView)findViewById(R.id.s_count);
        TextView acount = (TextView)findViewById(R.id.a_count);
        TextView country = (TextView)findViewById(R.id.user_country);
        TextView mode = (TextView)findViewById(R.id.detail_mode);

        // final Button backButton = (Button)findViewById(R.id.back);
        final Button compareButton = (Button)findViewById(R.id.compare);
        final Button subButton = (Button)findViewById(R.id.detail_subscribe);

        final ImageView avatarContainer = (ImageView)findViewById(R.id.user_avatar);
        final ImageView blurredBackground = (ImageView)findViewById(R.id.user_avatar_blurred);

        final String re_username = receivedParsedPackage[0];
        String re_pp = receivedParsedPackage[1];
        String re_playcount = receivedParsedPackage[2];
        String re_grank = receivedParsedPackage[3];
        String re_crank = receivedParsedPackage[4];
        String re_ss_count = receivedParsedPackage[5];
        String re_s_count = receivedParsedPackage[6];
        String re_a_count = receivedParsedPackage[7];
        final String re_userid = receivedParsedPackage[9];
        String re_usercountry = receivedParsedPackage[10];
        final String re_modeNumber = receivedParsedPackage[11];
        String re_mode = modesName[Integer.parseInt(re_modeNumber)];

        String re_crankt = "";

        Locale locale = new Locale("en", re_usercountry);

        Drawable avatar = null;

        Bitmap avatarBitmap = null;
        Bitmap avatarBlurredBitmap = null;

        FastBlur fb = new FastBlur();

        Float re_pp_number = 0f;

        try{
            re_pp_number = Float.parseFloat(re_pp);
        }catch (NumberFormatException e){
            re_pp_number = 0f;
        }
        Integer re_pp_number_int = re_pp_number.intValue();
        re_pp = re_pp_number_int.toString() + "pp";

        switch (re_grank){
            case "1":
                re_pp += " @ 1st";
                break;
            case "2":
                re_pp += " @ 2nd";
                break;
            case "3":
                re_pp += " @ 3rd";
                break;
            case "null":
                re_pp += " @ No Rank";
                break;
            default:
                re_pp += " @ " + re_grank + "th";
        }

        country.setText("from " + locale.getDisplayCountry());

        re_crankt = "Ranked";
        switch (re_crank){
            case "1":
                re_crankt += " 1st";
                break;
            case "2":
                re_crankt += " 2nd";
                break;
            case "3":
                re_crankt += " 3rd";
                break;
            case "null":
                re_crankt = "Doesn't have ranked yet";
                break;
            default:
                re_crankt += " " + re_crank + "th";
        }
        re_crankt += " in ";
        re_crankt += locale.getDisplayCountry();


        if(!Objects.equals(re_playcount, "null")){
            re_playcount = "Played " + re_playcount + " times.";
        } else {
            re_playcount = "Haven't played once recently";
        }

        if(Objects.equals(re_ss_count, "null")){
            re_ss_count = "0";
        }

        if(Objects.equals(re_s_count, "null")){
            re_s_count = "0";
        }

        if(Objects.equals(re_a_count, "null")){
            re_a_count = "0";
        }

        Log.i("Debug Mode", re_ss_count + re_s_count + re_a_count + re_playcount);

        username.setText(re_username);
        pp.setText(re_pp);
        playcount.setText(re_playcount);
        // grank.setText(re_grank);
        crank.setText(re_crankt);
        sscount.setText(re_ss_count);
        scount.setText(re_s_count);
        acount.setText(re_a_count);
        mode.setText("In " + re_mode + " ...");

        // Hide soft keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


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

                        uTask.execute(username, re_modeNumber);
                    }
                });

                inputDialogBuilder.show();
            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder check = new AlertDialog.Builder(DetailActivity.this);
                check.setTitle("Add to subscription list?")
                        .setMessage("Do you want to add " + re_username + " to you subscription list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Add to subList
                                SharedPreferences sp = DetailActivity.this.getSharedPreferences("subList", Context.MODE_PRIVATE);
                                String newSubList = sp.getString("listString", "") + "|" + re_userid;

                                SharedPreferences.Editor spe = sp.edit();

                                spe.putString("listString", newSubList);
                                spe.apply();

                                Toast.makeText(DetailActivity.this, "Successfully added " + re_username + " to your subscription list!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Let's pretend nothing happened.
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        avatarContainer.setLongClickable(true);
        avatarContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder saveDialog = new AlertDialog.Builder(DetailActivity.this);
                saveDialog.setTitle("Save Avatar?")
                          .setMessage("Do you want to save " + re_username + "'s avatar to your phone?")
                          .setNegativeButton("No", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  dialog.dismiss();
                              }
                          })
                          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  // Do the saving stuff async
                                  saveAvatarToLocal saveTask = new saveAvatarToLocal(DetailActivity.this);

                                  avatarContainer.buildDrawingCache();
                                  Bitmap save = avatarContainer.getDrawingCache();

                                  saveTaskParams saveParams = new saveTaskParams(save, re_username);
                                  saveTask.execute(saveParams);
                              }
                          })
                          .setCancelable(false)
                          .show();
                return true;
            }
        });

        getUserAvatarFromUrl getUserAvatarFromUrl = new getUserAvatarFromUrl();
        getUserAvatarFromUrl.execute(re_userid);

    }

    private static class saveTaskParams {
        Bitmap bitmap;
        String username;

        saveTaskParams(Bitmap b, String s) {
            this.bitmap = b;
            this.username = s;
        }
    }

    public class saveAvatarToLocal extends AsyncTask<saveTaskParams, Integer, String>{

        private Context mContext;

        public saveAvatarToLocal(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(mContext, "Saving avatar...", Toast.LENGTH_SHORT);
        }

        @Override
        protected String doInBackground(saveTaskParams... params) {
            OutputStream fOut = null;
            Uri outputFileUri;
            String fullDir = "";
            try {
                File root = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "osuAvatars" + File.separator);
                root.mkdirs();
                File sdImageMainDirectory = new File(root, params[0].username + ".jpg");
                outputFileUri = Uri.fromFile(sdImageMainDirectory);
                fOut = new FileOutputStream(sdImageMainDirectory);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                params[0].bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            fullDir = Environment.getExternalStorageDirectory()
                    + File.separator + "osuAvatars" + File.separator + params[0].username + ".jpg";
            return fullDir;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(this.mContext, "Avatar has saved to \"" + s + "\"", Toast.LENGTH_LONG).show();
        }
    }

    public class getUserAvatarFromUrl extends AsyncTask<String, Integer, Drawable[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Drawable[] doInBackground(String... params) {
            try {
                Drawable[] ds = {null, null};

                InputStream is = (InputStream) new URL("https://a.ppy.sh/" + params[0]).getContent();
                Drawable d = Drawable.createFromStream(is, "src name");
                FastBlur fb = new FastBlur();
                Bitmap b = fb.fastblur((((BitmapDrawable)d).getBitmap()), 1f ,30);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(b);
                Drawable bd = bitmapDrawable;
                ds[0] = d;
                ds[1] = bd;
                return ds;
            } catch (IOException e) {
                Drawable[] ds = {null, null};
                e.printStackTrace();
                return ds;
            }
        }
        @Override
        protected void onPostExecute(Drawable[] ds) {
            super.onPostExecute(ds);

            final ImageView avatarContainer = (ImageView)findViewById(R.id.user_avatar);
            final ImageView blurredBackground = (ImageView)findViewById(R.id.user_avatar_blurred);

            if(ds[0] != null){
                avatarContainer.setImageDrawable(ds[0]);
                blurredBackground.setImageDrawable(ds[1]);
                avatarContainer.setBackgroundResource(R.drawable.frame);
            } else {
                avatarContainer.setImageResource(R.mipmap.no_avatar);
            }

        }
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
            String mode = params[1];
            String url = "https://osu.ppy.sh/api/get_user";
            String param = "k=" + token + "&u=" + username + "&m=" + mode;
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
                String re_userid = "";

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
                    }
                    Log.i("", "Received info:");
                    Log.i("JSONData", re_username + re_playcount + re_pp + re_grank + re_crank);
                    String[] parsedPackage = {re_username, re_pp, re_playcount, re_grank, re_crank, re_countss, re_counts, re_counta, re_totalscore, re_userid};

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
