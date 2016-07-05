package com.bcp.bcp.recyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcp.bcp.MainActivity;
import com.bcp.bcp.R;

import java.util.ArrayList;

/**
 * Created by anjup on 4/14/16.
 */
public class ViewLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_location);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.loc_recycler_view);
        ImageView backarrow = (ImageView) findViewById(R.id.backarrow);
        ImageView infobtton = (ImageView) findViewById(R.id.info);

        ArrayList<LocationFenceTrackDetails> diplayListToView = (ArrayList<LocationFenceTrackDetails>) getIntent().getExtras().getSerializable("LIST");

        LocationDetailsAdapter locationDetailsAdapter = new LocationDetailsAdapter(diplayListToView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(locationDetailsAdapter);

        SharedPreferences mSharedPreferences = getSharedPreferences("Shared", Context.MODE_PRIVATE);

        TextView timeText = (TextView) findViewById(R.id.timeText);
        String timeInterval = mSharedPreferences.getString("BeaconTime", "BeaconTime 00:00:00");//BeaconTime 02:00:00
        timeText.setText(timeInterval.substring(10, timeInterval.length()));

        TextView intext = (TextView) findViewById(R.id.inText);
        String bintime = mSharedPreferences.getString("InOutTime", "InOut time 00:00:00 00:00:00");//InOut time xx:xx:xx xx:xx:xx
        intext.setText(bintime.substring(10, 19));

        TextView outtext = (TextView) findViewById(R.id.outText);
        String bouttime = mSharedPreferences.getString("InOutTime", "InOut time 00:00:00 00:00:00");//InOut time xx:xx:xx xx:xx:xx
        outtext.setText(bouttime.subSequence(19, bouttime.length()));

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewLocationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        infobtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewLocationActivity.this);

                builder.setMessage(Html.fromHtml("<font color = \"#ffff00\">Yellow</font> indicates Entry <br> <font color = \"#03A9F4\">Blue</font> indicates Exits <br> Data with <b>(B)</b> indicates data from beacons" +
                        "<br> <b>White</b> indicate out of office tracking details"))
                        .setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Colour Scheme");
                alert.show();
            }
        });
    }
}
