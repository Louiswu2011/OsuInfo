package com.wu.osuinfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        Trend trend = trendList.get(position);

        holder.uname.setText(trend.getUsername());
        holder.uavatar.setImageDrawable(trend.getUseravatar());
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
    }

    @Override
    public int getItemCount() {
        return trendList.size();
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
