package com.wu.osuinfo;

import android.content.Context;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by wu on 2017/7/22.
 */

public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.trendViewHolder> {

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
        holder.urankpp.setText(trend.getUserpp() + "pp @ " + trend.getUserrank() + "th");
        holder.udetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Cardview Events", "Bu'n Pressed!");
                Toast.makeText(v.getContext(), "Showing up detail...", Toast.LENGTH_SHORT).show();
            }
        });
        holder.usub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Cardview Events", "Bu'n Pressed!");
                Toast.makeText(v.getContext(), "Unsubscribing...", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public int getItemCount() {
        return trendList.size();
    }

    private static class saveTaskParams {
        Bitmap bitmap;
        String username;

        saveTaskParams(Bitmap b, String s) {
            this.bitmap = b;
            this.username = s;
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
            usub = (Button)view.findViewById(R.id.trend_unsub);
            ubg = (ImageView)view.findViewById(R.id.trend_bg);
        }

    }
}
