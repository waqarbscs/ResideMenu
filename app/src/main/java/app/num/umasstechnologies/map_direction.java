package app.num.umasstechnologies;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import app.num.umasstechnologies.CustomDialogs.TrackerDetailDialog;
import app.num.umasstechnologies.CustomServices.IntentDataLoadService;
import app.num.umasstechnologies.CustomServices.TrackerLocationLoadService;

public class map_direction extends AppCompatActivity implements View.OnClickListener {

    private GoogleMap mGoogleMap;

    private String tracker_id;
    private String engineStatus;


    private BroadcastReceiver mBroadCastReciever;


    private ProgressDialog progressDialog;
    LatLng latLng;


    FloatingActionButton fab_showdetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_direction);


        tracker_id = getIntent().getStringExtra("tracker_id");
        engineStatus = getIntent().getStringExtra("engine_status");
        latLng = new LatLng(getIntent().getDoubleExtra("lat",0.0),getIntent().getDoubleExtra("lon",0.0));


        SupportMapFragment fragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Google Map
        mGoogleMap = fragment.getMap();

        //we need to start the service get the data from the service, get the first gps
        startProgressDialog("Loading vehicle position location"); //first time location load.s

        //lets start the service for the first time to get the gps data for the on engine thing blah blah
        Intent intentLoadLocation = new Intent(this, TrackerLocationLoadService.class);
        intentLoadLocation.putExtra("action","getlocation");


        intentLoadLocation.putExtra("tracker_id",tracker_id);
        startService(intentLoadLocation);

        fab_showdetail = (FloatingActionButton) findViewById(R.id.fab_showdetail);
        fab_showdetail.setOnClickListener(this);


        //region broadcast reciever..
        mBroadCastReciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                stopProgressDialog();

                if(intent.getAction().endsWith(TrackerLocationLoadService.ACTION_Error)) {
                    Toast.makeText(map_direction.this, "There was error loading location.",Toast.LENGTH_SHORT).show();

                    setMapCurrentLocation(latLng);

                }
                else if (intent.getAction().endsWith(TrackerLocationLoadService.ACTION_Fail)) {
                    Toast.makeText(map_direction.this, "There was error loading location.",Toast.LENGTH_SHORT).show();
                    setMapCurrentLocation(latLng);
                }
                else if (intent.getAction().endsWith(TrackerLocationLoadService.ACTION_Success)){

                    //well here we will update the map
                    String lat = intent.getStringExtra("lat");
                    String lon = intent.getStringExtra("lon");

                    //and here we need to update the map

                    setMapCurrentLocation(new LatLng(Double.parseDouble(lat),Double.parseDouble(lon)));

                }

            }
        };
        //end of the broeadcast reciever

    }

    private void setMapCurrentLocation(LatLng latlong) {
        CameraUpdate cmf = CameraUpdateFactory.newLatLngZoom(latlong,13);

        MarkerOptions mo = new MarkerOptions();
        mo.position(latlong);
        mGoogleMap.addMarker(mo);
        mGoogleMap.animateCamera(cmf);
    }

    public void startProgressDialog(String message) {
        if(progressDialog == null)
            progressDialog = new ProgressDialog(this);

        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setMessage(message);

        progressDialog.show();
    }

    public void stopProgressDialog() {

        if(progressDialog!=null)
            progressDialog.hide();

    }




    @Override
    protected void onResume() {

        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(TrackerLocationLoadService.ACTION_Error));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(TrackerLocationLoadService.ACTION_Fail));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(TrackerLocationLoadService.ACTION_Success));

    }

    @Override
    protected void onPause() {

        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadCastReciever);
    }

    TrackerDetailDialog tddialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_showdetail:

                tddialog = new TrackerDetailDialog();

                break;
        }
    }
}
