package com.wu.osuinfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by wu on 2017/7/22.
 */



public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.trendViewHolder>{

    private Context trendContext;
    private List<Trend> trendList;

    public TrendAdapter (Context context, List<Trend> trendList){
        this.trendContext = context;
        this.trendList = trendList;
    }

    @Override
    public trendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trendcard, parent, false);

        return new trendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final trendViewHolder holder, int position) {
        final Trend trend = trendList.get(position);

        holder.uname.setText(trend.getUsername());
        holder.uavatar.setImageDrawable(trend.getUseravatar());
        holder.ubg.setImageDrawable(trend.getUserblurredavatar());
        holder.uprescore.setText(trend.getUserpretotalscore());
        holder.uprecount.setText(trend.getUserpreplaycount());
        // holder.uscore.setText(trend.getUsertotalscore());
        // holder.ucount.setText(trend.getUserplaycount());
        holder.urankpp.setText(trend.getUserpp() + "pp @ " + trend.getUserrank() + "th");
        holder.udetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Debug Only
                // Log.i("Cardview Events", "Bu'n Pressed!");
                // Toast.makeText(v.getContext(), "Showing up detail...", Toast.LENGTH_SHORT).show();
                getUserJSONParams info = new getUserJSONParams(trend.getUsername(), v, v.getResources().getString(R.string.apikey));
                getUserJSON task = new getUserJSON();
                task.execute(info);
            }
        });

        holder.uavatar.setLongClickable(true);
        holder.uavatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveAvatarToLocal saveTask = new saveAvatarToLocal(v.getContext());
                holder.uavatar.buildDrawingCache();
                Bitmap b = holder.uavatar.getDrawingCache();
                saveTask.execute(new saveTaskParams(b, trend.getUsername()));
                return true;
            }
        });

        // Dealing with 0 change
        String scoreChange = trend.getUsertotalscore();
        String countChange = trend.getUserplaycount();

        if(Objects.equals(scoreChange, "0")){
            trend.setUsertotalscore("+0");
        }

        if(Objects.equals(countChange, "0")){
            trend.setUserplaycount("+0");
        }

        // Now set the change textview
        holder.uscore.setText(trend.getUsertotalscore());
        holder.ucount.setText(trend.getUserplaycount());
    }

    @Override
    public int getItemCount() {
        return trendList.size();
    }

    private static class getUserJSONParams {
        String username;
        View view;
        String ak;

        getUserJSONParams(String s, View v, String ak) {
            this.username = s;
            this.view = v;
            this.ak = ak;
        }
    }

    private static class saveTaskParams {
        Bitmap bitmap;
        String username;

        saveTaskParams(Bitmap b, String s) {
            this.bitmap = b;
            this.username = s;
        }
    }

    public class getUserJSON extends AsyncTask<getUserJSONParams, Integer, getUserJSONParams> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("getUserJSON", "PreExecuting...");
        }

        @Override
        protected getUserJSONParams doInBackground(getUserJSONParams... params) {

            if(!Objects.equals(params[0], "")){
                Log.i("", "Start Fetching JSON...");
                String token = params[0].ak;
                String username = params[0].username;
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
                getUserJSONParams g = new getUserJSONParams(json, params[0].view, "");
                return g;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(getUserJSONParams s) {
            super.onPostExecute(s);
            if(Objects.equals(s, null)){
            } else {
                // All Log.i/e are Debug only and should be quoted in further implement.
                // Log.i("", s);
                String resultpackage[] = s.username.split("\\|");
                String json = resultpackage[0];
                String username = resultpackage[1];

                final Intent gotoDetail = new Intent(s.view.getContext(), DetailActivity.class);

                if (!Objects.equals(json, "[]")) {

                    Log.i("", "Get JSON Data: " + json);
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
                        s.view.getContext().startActivity(gotoDetail);

                    } catch (Exception e) {
                        Log.e("JSONParsingError", "Something went wrong parsing JSON.");
                        Log.e("JSONParsingError", e.getMessage());
                    }


                } else {
                    // Log.e("", "No user found!");
                }
            }

        }

    }

    public class saveAvatarToLocal extends AsyncTask<TrendAdapter.saveTaskParams, Integer, String> {

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
        protected String doInBackground(TrendAdapter.saveTaskParams... params) {
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

    public class trendViewHolder extends RecyclerView.ViewHolder {
        public TextView uname, urankpp, uscore, ucount, uprescore, uprecount;
        public ImageView uavatar, ubg;
        public Button udetail, usub;

        public trendViewHolder (View view){
            super(view);
            uname = (TextView)view.findViewById(R.id.trend_username);
            urankpp = (TextView)view.findViewById(R.id.trend_user_rank);
            uprescore = (TextView)view.findViewById(R.id.trend_pre_totalscore);
            uprecount = (TextView)view.findViewById(R.id.trend_pre_playcount);
            uscore = (TextView)view.findViewById(R.id.trend_totalscorechange);
            ucount = (TextView)view.findViewById(R.id.trend_playcount_change);
            uavatar = (ImageView)view.findViewById(R.id.trend_user_avatar);
            udetail = (Button)view.findViewById(R.id.trend_playerdetail);
            // usub = (Button)view.findViewById(R.id.trend_unsub);
            ubg = (ImageView)view.findViewById(R.id.trend_bg);
        }

    }
}
