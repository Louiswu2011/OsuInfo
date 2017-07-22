package com.wu.osuinfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TrendActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TrendAdapter trendAdapter;
    private List<Trend> trendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        trendList = new ArrayList<>();
        trendAdapter = new TrendAdapter(this, trendList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(trendAdapter);

        setTitle("Trends");

        try {
            prepareTrends();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareTrends() throws Exception {
        // Debug only.
        Trend cookiezi = new Trend("cookiezi", "13928", "13827", "1", "2", "160571088386", "160563443901", "18437", "18436", getDrawable(R.mipmap.ic_cookiezi));
        Trend vaxei = new Trend("Vaxei", "13407", "13424", "2", "3", "166023938049", "166020091321", "106000", "105890", getDrawable(R.mipmap.ic_vaxei));
        Trend rafis = new Trend("Rafis", "13415", "13415", "3", "4", "390291064248", "390230188212", "216468", "216453", getDrawable(R.mipmap.ic_rafis));
        trendList.add(cookiezi);
        trendList.add(vaxei);
        trendList.add(rafis);
        trendAdapter.notifyDataSetChanged();
    }



}
