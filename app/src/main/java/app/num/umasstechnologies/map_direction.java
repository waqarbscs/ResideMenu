package app.num.umasstechnologies;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import app.num.umasstechnologies.CustomDialogs.TrackerDetailDialog;
import app.num.umasstechnologies.CustomServices.IntentDataLoadService;
import app.num.umasstechnologies.CustomServices.TrackerLocationLoadService;
import app.num.umasstechnologies.Models.TrackerData;
import app.num.umasstechnologies.Singleton.AppManager;

public class map_direction extends AppCompatActivity implements View.OnClickListener {

    private GoogleMap mGoogleMap;

    private String tracker_id;
    private String engineStatus;


    private BroadcastReceiver mBroadCastReciever;


    private ProgressDialog progressDialog;
    LatLng latLng;


    FloatingActionButton fab_showdetail;
    TrackerData tData;

    private static final int Data_Fetch_TimeLimit = 10; //after every x second fetch the data
    private static final int MS = 1000; //multiplied to make it seconds.


    private Handler handler;

    private Runnable mRunnableServerTask = new Runnable() {
        @Override
        public void run() {

            StartServiceForNewDetails();

            if(handler != null)
                handler.postDelayed(mRunnableServerTask,MS*Data_Fetch_TimeLimit);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_direction);

        handler = new Handler();

        handler.postDelayed(mRunnableServerTask,MS*Data_Fetch_TimeLimit);

        Intent mainIntent = getIntent();


        tracker_id = mainIntent.getStringExtra("tracker_id");
        engineStatus = mainIntent.getStringExtra("engine_status");
        latLng = new LatLng(mainIntent.getDoubleExtra("latitude",0.0),getIntent().getDoubleExtra("longitude",0.0));

        tData =  new TrackerData();

        tData.name = mainIntent.getStringExtra("name");
        tData.device_id = mainIntent.getStringExtra("device_id");
        tData.engine_status = mainIntent.getStringExtra("engine_status");
        tData.latitude = String.valueOf( mainIntent.getDoubleExtra("latitude",0.0) );
        tData.longitude = String.valueOf( mainIntent.getDoubleExtra("longitude",0.0) );



        tData.output_bit = mainIntent.getStringExtra("output_bit");
        tData.input_bit_1 = mainIntent.getStringExtra("input_bit_1");
        tData.input_bit_2 = mainIntent.getStringExtra("input_bit_2");
        tData.input_bit_3 = mainIntent.getStringExtra("input_bit_3");
        tData.input_bit_4 = mainIntent.getStringExtra("input_bit_4");

        tData.username = mainIntent.getStringExtra("username");
        tData.mileage = mainIntent.getStringExtra("mileage");
        tData.speed = mainIntent.getStringExtra("speed");

        tData.last_engine_on = mainIntent.getStringExtra("last_engine_on");
        tData.last_engine_off = mainIntent.getStringExtra("last_engine_off");
        tData.last_move = mainIntent.getStringExtra("last_move");
        tData.last_location = mainIntent.getStringExtra("last_gprs");
        tData.last_signal = mainIntent.getStringExtra("last_signal");



        SupportMapFragment fragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Google Map
        mGoogleMap = fragment.getMap();

        //we need to start the service get the data from the service, get the first gps
        startProgressDialog("Loading vehicle location"); //first time location load.s

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

                    double[] lat = intent.getDoubleArrayExtra("latitude");
                    double[] lon = intent.getDoubleArrayExtra("longitude");

                    //and here we need to update the map
                    setMapCurrentLocation( new LatLng (lat[0],lon[0] ) );
                    setMapCurrentRoute(lat,lon);

                }

            }
        };
        //end of the broeadcast reciever

    }



    public void StartServiceForNewDetails(){
        //this will start two services..

        //first one to update the object of tracker object detail
        // second one to update the route..

        //first check if any of the service is already running if it is than close it and rerun it..
        if(AppManager.getInstance().isMyServiceRunning(IntentDataLoadService.class)) {

            if(!AppManager.getInstance().isInternetAvailable()) {
                //if internet is not available and the service is running plz stop it

                stopService(new Intent(map_direction.this,IntentDataLoadService.class));

            }

        }
        else {
            //otherwise run it to get the latest data blah blah blah...


        }

        if(AppManager.getInstance().isMyServiceRunning(TrackerLocationLoadService.class)) {

            if(!AppManager.getInstance().isInternetAvailable()) {
                //if internet is not available and the service is running plz stop it
                stopService(new Intent(map_direction.this,TrackerLocationLoadService.class));
            }
        }
        else {

        }



    }

    private void setMapCurrentLocation(LatLng latlong) {
        CameraUpdate cmf = CameraUpdateFactory.newLatLngZoom(latlong,13);

        MarkerOptions mo = new MarkerOptions();
        mo.position(latlong);
        mGoogleMap.addMarker(mo);
        mGoogleMap.animateCamera(cmf);
    }

    private void setMapCurrentRoute(double[] lat, double[] lons) {

        int len = lat.length;

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

        for (int z = 0; z < len; z++) {

            LatLng point = new LatLng(lat[z], lons[z]);
            options.add(point);
        }

        mGoogleMap.addPolyline(options);
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



                if(tData != null) {
                    tddialog = new TrackerDetailDialog();

                    tddialog.showDialog(map_direction.this, tData);
                }
                else {

                }

                break;
        }
    }
}
