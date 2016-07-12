package app.num.MassUAETracking;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Calendar;

import app.num.MassUAETracking.CustomDialogs.TrackerDetailDialog;
import app.num.MassUAETracking.CustomServices.IntentDataLoadService;
import app.num.MassUAETracking.CustomServices.TrackerLocationLoadService;
import app.num.MassUAETracking.DatabaseClasses.DatabaseHandler;
import app.num.MassUAETracking.Models.TrackerData;
import app.num.MassUAETracking.Singleton.AppManager;

public class map_direction extends AppCompatActivity  {

    private GoogleMap mGoogleMap;

    private String tracker_id;
    private String engineStatus;

    int resid;
    private BroadcastReceiver mBroadCastReciever;


    private ProgressDialog progressDialog;
    LatLng latLng;


    TrackerData tData;

    Button sat,nor;
    Button btnVehicle,btnAlert,btnLogOut;

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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Map");


        handler = new Handler();

        handler.postDelayed(mRunnableServerTask,MS*Data_Fetch_TimeLimit);

        Intent mainIntent = getIntent();

        sat=(Button)findViewById(R.id.button);
        nor=(Button)findViewById(R.id.button2);

        btnVehicle=(Button)findViewById(R.id.btnVehicle);
        btnAlert=(Button)findViewById(R.id.btnAlert);
        btnLogOut=(Button)findViewById(R.id.btnLogOut);
       final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        btnVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(map_direction.this,MainActivity.class);
                startActivity(i);
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DatabaseHandler dbhandler = new DatabaseHandler(map_direction.this);
                    dbhandler.deleteAllInformation();
                    Intent intentLoginScreen = new Intent(map_direction.this,Login.class);

                    AppManager.getInstance().removeCompany();

                    finish();
                    startActivity(intentLoginScreen);
            }
        });
        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(map_direction.this,MainActivity.class);
                startActivity(i);
            }
        });
        tracker_id = mainIntent.getStringExtra("tracker_id");
        engineStatus = mainIntent.getStringExtra("engine_status");
        latLng = new LatLng(mainIntent.getDoubleExtra("latitude",0.0),getIntent().getDoubleExtra("longitude",0.0));

        tData =  new TrackerData();

        tData.name = mainIntent.getStringExtra("name");
        tData.device_id = mainIntent.getStringExtra("device_id");
        tData.engine_status = mainIntent.getStringExtra("engine_status");
        tData.latitude = String.valueOf( String.format("%.5f",mainIntent.getDoubleExtra("latitude",0.0)) );
        tData.longitude = String.valueOf( String.format("%.5f",mainIntent.getDoubleExtra("longitude",0.0)));

        tData.output_bit = mainIntent.getStringExtra("output_bit");
        tData.input_bit_1 = mainIntent.getStringExtra("input_bit_1");
        tData.input_bit_2 = mainIntent.getStringExtra("input_bit_2");
        tData.input_bit_3 = mainIntent.getStringExtra("input_bit_3");
        tData.input_bit_4 = mainIntent.getStringExtra("input_bit_4");

        tData.username = mainIntent.getStringExtra("username");


        //changes by waqar

        String m_type=mainIntent.getStringExtra("mileage_type");
        int ty=Integer.parseInt(m_type);
        String unit="";
        if(ty==0){
            unit="KM";
        }else if(ty==1){
            unit="Mile";
        }
        tData.mileage = mainIntent.getStringExtra("mileage")+unit;
        tData.speed = mainIntent.getStringExtra("speed")+unit;

        tData.last_engine_on = mainIntent.getStringExtra("last_engine_on");
        tData.last_engine_off = mainIntent.getStringExtra("last_engine_off");
        tData.last_move = mainIntent.getStringExtra("last_move");
        tData.last_location = mainIntent.getStringExtra("last_gprs");
        tData.last_signal = mainIntent.getStringExtra("last_signal");

        String last_signal=mainIntent.getStringExtra("last_signal");
        String last_gprs=mainIntent.getStringExtra("last_gprs");
        String tracker_general_color=mainIntent.getStringExtra("tracker_general_color");
        String tracker_general_status=mainIntent.getStringExtra("tracker_general_status");
        int tracker_icon=Integer.parseInt(mainIntent.getStringExtra("tracker_icon"));

        //Changes by waqar
        String gps=mainIntent.getStringExtra("gps");
        String gsm=mainIntent.getStringExtra("gsm");
        String battery=mainIntent.getStringExtra("battery");


        double gps_level=0,gsm_level=0,battery_power=0;
        if(gps.equals("a")||gps.equals("b")||gps.equals("c")||gps.equals("c")){
            gps_level = 100.0;
        }else if (Integer.parseInt(gps) >=0 && Integer.parseInt(gps)<=9){
            gps_level = Integer.parseInt(gps)*10;
        }
        if(gsm.equals("a")||gsm.equals("b")||gsm.equals("c")||gsm.equals("d")){
            gsm_level = 100.0;
        }else if (Integer.parseInt(gsm) >=0 && Integer.parseInt(gsm)<=9){
            gsm_level = Integer.parseInt(gsm)*10;
        }
        if(battery.equals("L")){
            battery_power=9.0;
        }else if(battery.equals("F")){
            battery_power=100.0;
        }else if(Integer.parseInt(battery)>=0&&Integer.parseInt(battery)<=9.0){
            battery_power=battery_power*10;
        }else if(battery.equals("-")){
            battery_power=0.0;
        }

        tData.gps=Double.toString(gps_level)+"%";
        tData.gsm=Double.toString(gsm_level)+"%";
        tData.battery=Double.toString(battery_power)+"%";

        Calendar cal=Calendar.getInstance();
        int currYear=cal.get(Calendar.YEAR);
        int curr_month=cal.get(Calendar.MONTH);
        int curr_date=cal.get(Calendar.DATE);
        int curr_hour=cal.get(Calendar.HOUR_OF_DAY);

        int last_year=Integer.parseInt(last_signal.substring(0,4));
        int last_month=Integer.parseInt(last_signal.substring(5,7));
        int last_date=Integer.parseInt(last_signal.substring(8,10));
        int last_hour=Integer.parseInt(last_signal.substring(11,13));

        int gprs_year=Integer.parseInt(last_gprs.substring(0,4));
        int gprs_month=Integer.parseInt(last_gprs.substring(5,7));
        int gprs_date=Integer.parseInt(last_gprs.substring(8,10));
        int gprs_hour=Integer.parseInt(last_gprs.substring(11,13));


        int last_signal_years=currYear-last_year;
        int last_signal_months=(curr_month+1)-last_month;
        int last_signal_days=curr_date-last_date;
        int last_signal_hours=curr_hour-last_hour;


        int last_gprs_years=currYear-gprs_year;
        int last_gprs_months=(curr_month+1)-gprs_month;
        int last_gprs_days=curr_date-gprs_date;
        int last_gprs_hours=curr_hour-gprs_hour;

        String tracker_icon_color = "gray";
        resid=R.drawable.engon;
        if(tracker_icon==0) {
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==1){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==2){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==3){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==4){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==5){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==6){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==7){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==8){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==9){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==10){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {

                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color == "#FF6600" || tracker_general_status == "archive") { // orange

                    tracker_icon_color = "orange";
                } else if (tracker_general_color == "#996600" || tracker_general_status == "cancel") { // brown

                    tracker_icon_color = "brown";
                } else if (tracker_general_color == "#FFFF00" || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color == "#660000" || tracker_general_status == "lost") { // reddark
                    tracker_icon_color = " reddark ";
                } else if (engineStatus == "0" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus == "1" && (tracker_general_color == "#009900" || tracker_general_status == "inside")) { // green
                    tracker_icon_color = "green";
                } else if (engineStatus == "0" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // bluelight
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus == "1" && (tracker_general_color == "#0000FF" || tracker_general_status == "outside")) { // blue
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==11){

        }else if(tracker_icon==12){

        }



        SupportMapFragment fragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Google Map
        mGoogleMap = fragment.getMap();
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);

        nor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        //we need to start the service get the data from the service, get the first gps
        startProgressDialog("Loading vehicle location"); //first time location load.s

        //lets start the service for the first time to get the gps data for the on engine thing blah blah
        Intent intentLoadLocation = new Intent(this, TrackerLocationLoadService.class);
        intentLoadLocation.putExtra("action","getlocation");


        intentLoadLocation.putExtra("tracker_id",tracker_id);
        startService(intentLoadLocation);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                if(tData != null) {
                    tddialog = new TrackerDetailDialog();

                    tddialog.showDialog(map_direction.this, tData);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


        //MarkerOptions mo = new MarkerOptions();
        //mo.position(latlong);
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latlong)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_icon)));
        //mGoogleMap.addMarker(mo);

    }

    private void setMapCurrentRoute(double[] lat, double[] lons) {
        //float f=SphericalUtil.computeHeading();
        int len = lat.length;

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        MarkerOptions loc=new MarkerOptions();
        for (int z = 0; z < len; z++) {
            LatLng point = new LatLng(lat[z], lons[z]);
            options.add(point);
            if(z<len-1&&z!=0) {
                double f = SphericalUtil.computeHeading(new LatLng(lat[z], lons[z]),new LatLng(lat[z + 1], lons[z + 1]));
                float s = (float) f;
                mGoogleMap.addMarker(new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_icon))
                .rotation(s));
                //loc.position(new LatLng(lat[z], lons[z]));
                //loc.anchor(0.5f, 0.5f);
                //loc.rotation(s);
                //mGoogleMap.addMarker(loc);
            }else if(z==len-1&&z!=0){
                CameraUpdate cmf = CameraUpdateFactory.newLatLngZoom(new LatLng(lat[z],lons[z]),18);
                mGoogleMap.addMarker(new MarkerOptions()
                .position(point)
                .title(tData.name)
                .icon(BitmapDescriptorFactory.fromResource(resid)));
                mGoogleMap.animateCamera(cmf);
            }

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


}
