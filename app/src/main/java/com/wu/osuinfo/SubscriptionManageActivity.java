package com.wu.osuinfo;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.commons.lang3.ArrayUtils;

public class SubscriptionManageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_manage);

        final ListView listView = (ListView)findViewById(R.id.manage_listview);
        final SharedPreferences sp = getSharedPreferences("subList", MODE_PRIVATE);
        String[] pseudoSubArray = sp.getString("listString", "").split("\\|");
        final String[] subArray = ArrayUtils.removeElement(pseudoSubArray, "");

        setTitle("Manage Subscriptions");


        SubscriptionManagerArrayAdapter smaa = new SubscriptionManagerArrayAdapter(this, subArray);
        listView.setAdapter(smaa);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder confirmDelete = new AlertDialog.Builder(SubscriptionManageActivity.this);
                confirmDelete.setTitle("Are you sure?")
                        .setMessage("Do you want to unsubscribe this player?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete those subs here
                                String[] newSubList = ArrayUtils.removeElement(sp.getString("listString", "").split("\\|"),subArray[position]);
                                String saveList = "";
                                for(int o = 0; o < newSubList.length; o++){
                                    saveList += "|" + newSubList[o];
                                }
                                SharedPreferences.Editor spe = sp.edit();
                                spe.putString("listString", saveList);
                                spe.apply();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return true;
            }
        });


    }

}
