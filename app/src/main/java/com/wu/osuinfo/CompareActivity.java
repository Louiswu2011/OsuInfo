package com.wu.osuinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Objects;

public class CompareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent getParsedPackage = getIntent();
        String[] receivedParsedPackage = getParsedPackage.getStringArrayExtra("compareValue");  // Remember to check MainActivity for the content of the String Array.

        // initialize all the variables...
        String re_username1 = receivedParsedPackage[0];
        String re_pp1 = receivedParsedPackage[1];
        String re_playcount1 = receivedParsedPackage[2];
        String re_grank1 = receivedParsedPackage[3];
        String re_crank1 = receivedParsedPackage[4];
        String re_ss_count1 = receivedParsedPackage[5];
        String re_s_count1 = receivedParsedPackage[6];
        String re_a_count1 = receivedParsedPackage[7];
        String re_username2 = receivedParsedPackage[9];
        String re_totalscore1 = receivedParsedPackage[8];
        String re_pp2 = receivedParsedPackage[10];
        String re_playcount2 = receivedParsedPackage[11];
        String re_grank2 = receivedParsedPackage[12];
        String re_crank2 = receivedParsedPackage[13];
        String re_ss_count2 = receivedParsedPackage[14];
        String re_s_count2 = receivedParsedPackage[15];
        String re_a_count2 = receivedParsedPackage[16];
        String re_totalscore2 = receivedParsedPackage[17];

        // Log.i("Received Stuff", receivedParsedPackage.toString());

        TextView name1 = (TextView)findViewById(R.id.compare_username1);
        TextView name2 = (TextView)findViewById(R.id.compare_username2);
        TextView pp1 = (TextView)findViewById(R.id.compare_pp1);
        TextView pp2 = (TextView)findViewById(R.id.compare_pp2);
        TextView playcount1 = (TextView)findViewById(R.id.compare_playcount1);
        TextView playcount2 = (TextView)findViewById(R.id.compare_playcount2);
        TextView totalscore1 = (TextView)findViewById(R.id.compare_totalscore1);
        TextView totalscore2 = (TextView)findViewById(R.id.compare_totalscore2);
        TextView sssacount1 = (TextView)findViewById(R.id.compare_sssatime1);
        TextView sssacount2 = (TextView)findViewById(R.id.compare_sssatime2);

        setTitle(re_username1 + " vs " + re_username2);

        name1.setText(re_username1);
        name2.setText(re_username2);
        switch (re_grank1){
            case "1":
                pp1.setText(re_pp1 + "pp @ 1st");
                break;
            case "2":
                pp1.setText(re_pp1 + "pp @ 2nd");
                break;
            case "3":
                pp1.setText(re_pp1 + "pp @ 3rd");
                break;
            default:
                pp1.setText(re_pp1 + "pp @ " + re_grank1 + "th");
        }
        switch (re_grank2){
            case "1":
                pp2.setText(re_pp2 + "pp @ 1st");
                break;
            case "2":
                pp2.setText(re_pp2 + "pp @ 2nd");
                break;
            case "3":
                pp2.setText(re_pp2 + "pp @ 3rd");
                break;
            default:
                pp2.setText(re_pp2 + "pp @ " + re_grank2 + "th");
        }
        playcount1.setText("Has played " + re_playcount1 + " times.");
        playcount2.setText("Has played " + re_playcount2 + " times.");
        totalscore1.setText("Gained " + re_totalscore1 + " points.");
        totalscore2.setText("Gained " + re_totalscore2 + " points.");
        sssacount1.setText("Got " + re_ss_count1 + " SS, " + re_s_count1 + " S and " + re_a_count1 + " A.");
        sssacount2.setText("Got " + re_ss_count2 + " SS, " + re_s_count2 + " S and " + re_a_count2 + " A.");
    }

}
