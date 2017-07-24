package com.wu.osuinfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by wu on 2017/7/24.
 */

public class SubscriptionManagerArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] subs;

    public SubscriptionManagerArrayAdapter(Context context, String[] subs){
        super(context, -1, subs);
        this.context = context;
        this.subs = subs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_sub, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.list_username);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_avatar);

        listTaskParams l = new listTaskParams(subs[position], rowView, rowView.getResources().getString(R.string.apikey));
        getUserAvatarAndNameFromUrl task = new getUserAvatarAndNameFromUrl();
        task.execute(l);



        return rowView;
    }

    public static class listTaskParams {
        String uid;
        View v;
        String ak;

        listTaskParams(String s, View view, String a){
            this.uid = s;
            this.v = view;
            this.ak = a;
        }
    }

    public class getUserAvatarAndNameFromUrl extends AsyncTask<listTaskParams, Integer, Object[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object[] doInBackground(listTaskParams... params) {
            Object[] ds = {null, null, null, null};

                Log.i("", "Start Fetching JSON...");
                String token = params[0].ak;
                String username = params[0].uid;
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
                    ds[3] = json;
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
                try {
                    InputStream is = (InputStream) new URL("https://a.ppy.sh/" + params[0].uid).getContent();
                    Drawable d = Drawable.createFromStream(is, "src name");
                    FastBlur fb = new FastBlur();
                    Bitmap b = fb.fastblur((((BitmapDrawable) d).getBitmap()), 1f, 30);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(b);
                    Drawable bd = bitmapDrawable;
                    ds[0] = d;
                    ds[1] = bd;
                    ds[2] = params[0].v;
                } catch (IOException e) {
                    ds[0] = null;
                    ds[1] = null;
                    ds[2] = params[0].v;
                    ds[3] = null;
                }
                return ds;
        }

        @Override
        protected void onPostExecute(Object[] ds) {
            super.onPostExecute(ds);
            View v = (View)ds[2];
            ImageView avatar = (ImageView)v.findViewById(R.id.list_avatar);
            TextView name = (TextView)v.findViewById(R.id.list_username);
            String json = (String)ds[3];

            if(ds[0] != null){
                avatar.setImageDrawable((Drawable)ds[0]);
            } else {
                avatar.setImageDrawable(getContext().getDrawable(R.mipmap.no_avatar));
            }
            Log.i("", "Get JSON Data: " + json);
            // json = "{\"userinfo\":" + json + "}";
            // Log.i("Manipulation", "After editing: " + json);
            // Can add more strings.
            String re_username = "";

            try {
                JSONArray JArray = new JSONArray(json);
                for (int i = 0; i < JArray.length(); i++) {
                    JSONObject jObject = JArray.optJSONObject(i);
                    re_username = jObject.optString("username");
                }
                Log.i("", "Received info:");
                Log.i("JSONData", re_username);
                // String[] parsedPackage = {re_username, re_pp, re_playcount, re_grank, re_crank, re_countss, re_counts, re_counta, re_totalscore, re_userid,re_usercountry};
                name.setText(re_username);

            } catch (Exception e) {
                Log.e("JSONParsingError", "Something went wrong parsing JSON.");
                Log.e("JSONParsingError", e.getMessage());
            }

        }
    }
}
