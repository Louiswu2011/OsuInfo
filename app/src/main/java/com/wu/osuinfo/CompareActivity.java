package com.wu.osuinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

public class CompareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent getParsedPackage = getIntent();
        String[] receivedParsedPackage = getParsedPackage.getStringArrayExtra("compareValue");  // Remember to check MainActivity for the content of the String Array.

        // initialize some comments
        final String SSRANKER = " gets more SS than ";
        final String SSRANKER_LOWER = " gets less SS than ";
        final String SSRANKER_SAME = " gets the same amount of SS ";
        final String HIGHRANKER = " ranks higher than ";
        final String HIGHRANKER_LOWER = " ranks lower than ";
        final String EFFICIENCY = " more efficient than ";
        final String EFFICIENCY_LOWER = " less efficient than ";

        // some index
        Float player1efficiency = 0.0f;
        Float player2efficiency = 0.0f;

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
        TextView comment1 = (TextView)findViewById(R.id.compare_comment1);
        TextView comment2 = (TextView)findViewById(R.id.compare_comment2);

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

        // calculate index
        player1efficiency = Float.parseFloat(re_totalscore1) / Float.parseFloat(re_playcount1);
        player2efficiency = Float.parseFloat(re_totalscore2) / Float.parseFloat(re_playcount2);
        Log.i("Calculations", player1efficiency.toString() + " vs " + player2efficiency.toString());

        // SECTION START
        // construct comments
        // Step 1 : look for rankings
        String comment_player1 = re_username1;
        String comment_player2 = re_username2;
        int player1step1 = 0, player1step2 = 0, player1step3 = 0;  // 1 to win the step, 0 to lose the step
        int player2step1 = 0, player2step2 = 0, player2step3 = 0;  // gets "and" when win, "but" when lose

        if (Integer.parseInt(re_grank1) < Integer.parseInt(re_grank2)){
            comment_player1 += (HIGHRANKER + re_username2 + " ");
            player1step1 = 1;
            comment_player2 += (HIGHRANKER_LOWER + re_username2 + " ");
            player2step1 = 0;

        } else {
            comment_player2 += (HIGHRANKER + re_username1 + " ");
            player1step1 = 0;
            comment_player1 += (HIGHRANKER_LOWER + re_username2 + " ");
            player2step1 = 1;
        }

        // Step 2 : look for ss count
        if (Integer.parseInt(re_ss_count1) > Integer.parseInt(re_ss_count2)){
            if (player1step1 == 1){
                comment_player1 += ("and" + SSRANKER + re_username2 + " ");
                player1step2 = 1; // that said player2step1 must be 0, not need to use if again
                comment_player2 += ("and" + SSRANKER_LOWER + re_username1 + " ");
                player2step2 = 0;
            } else {
                comment_player1 += ("but" + SSRANKER + re_username2 + " ");
                player1step2 = 1; // that said player2step1 must be 1, not need to use if again
                comment_player2 += ("but" + SSRANKER_LOWER + re_username1 + " ");
                player2step2 = 0;
            }
        } else if (Integer.parseInt(re_ss_count1) == Integer.parseInt(re_ss_count2)){
            // both gets win
            player1step2 = 1;
            player2step2 = 1;
            comment_player1 += ("and" + SSRANKER_SAME);
            comment_player2 += ("and" + SSRANKER_SAME);
        } else {
            if (player2step1 == 1){
                comment_player2 += ("and" + SSRANKER + re_username1 + " ");
                player1step2 = 1; // that said player1step1 must be 0, not need to use if again
                comment_player1 += ("and" + SSRANKER_LOWER + re_username2 + " ");
                player2step2 = 0;
            } else {
                comment_player2 += ("but" + SSRANKER + re_username1 + " ");
                player1step2 = 1; // that said player1step1 must be 1, not need to use if again
                comment_player1 += ("but" + SSRANKER_LOWER + re_username2 + " ");
                player2step2 = 0;
            }
        }

        // Step 3 : look for efficiency e.g. the ratio between TotalScore and PlayCount (that's bullsh*t)
        if (player1efficiency > player2efficiency){ // and there can't be equal situation so no need for elseif
            if (player1step2 == 1){
                comment_player1 += ("and" + EFFICIENCY + re_username2 + " ");
                player1step3 = 1;
                comment_player2 += ("and" + EFFICIENCY_LOWER + re_username1 + " ");
                player2step3 = 0;
            } else {
                comment_player1 += ("but" + EFFICIENCY + re_username2 + " ");
                player1step3 = 1;
                comment_player2 += ("but" + EFFICIENCY_LOWER + re_username1 + " ");
                player2step3 = 0;
            }
        } else {
            if (player2step2 == 1){
                comment_player2 += ("and" + EFFICIENCY + re_username1 + " ");
                player2step3 = 1;
                comment_player1 += ("and" + EFFICIENCY_LOWER + re_username2 + " ");
                player1step3 = 0;
            } else {
                comment_player2 += ("but" + EFFICIENCY + re_username1 + " ");
                player2step3 = 1;
                comment_player1 += ("but" + EFFICIENCY_LOWER + re_username2 + " ");
                player1step3 = 0;
            }
        }

        // Final step (Whooo..Tiring Coding) : show result
        comment1.setText(comment_player1);
        comment2.setText(comment_player2);

        //SECTION CLOSED

    }

}
