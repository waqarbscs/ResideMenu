package app.num.MassUAETracking;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.Calendar;

import app.num.MassUAETracking.CustomDialogs.TrackerDetailDialog;
import app.num.MassUAETracking.CustomDialogs.ViewDialog;
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

    private static final int Data_Fetch_TimeLimit = 30; //after every x second fetch the data
    private static final int MS = 1000; //multiplied to make it seconds.

    private Handler handler;


    public String unit;

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
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#f4801f")));
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
                SharedPreferences sharedPref4 = getSharedPreferences("abc",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("alert","yes");
                editor.commit();
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
        unit="";
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
        if(gps.equals("none")){

        }else  if(gps.equals("a")||gps.equals("b")||gps.equals("c")||gps.equals("d")){
            gps_level = 100.0;
        }else if (Integer.parseInt(gps) >=0 && Integer.parseInt(gps)<=9){
            gps_level = Integer.parseInt(gps)*10;
        }
        if(gsm.equals("none")){

        }else  if(gsm.equals("a")||gsm.equals("b")||gsm.equals("c")||gsm.equals("d")){
            gsm_level = 100.0;
        }else if (Integer.parseInt(gsm) >=0 && Integer.parseInt(gsm)<=9){
            gsm_level = Integer.parseInt(gsm)*10;
        }
        if(battery.equals("L")){
            battery_power=9.0;
        }else if(battery.equals("F")){
            battery_power=100.0;
        }else if(Double.parseDouble(battery)>=0&&Double.parseDouble(battery)<=9.0){
            battery_power=battery_power*10;
        }else if(battery.equals("-")){
            battery_power=0.0;
        }

        tData.gps=Double.toString(gps_level);
        tData.gsm=Double.toString(gsm_level);
        tData.battery=Double.toString(battery_power);

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
        resid=R.mipmap.ic_launcher;
        if(tracker_icon==0) {
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                //tracker_icon_color = "gray";
                resid=R.drawable.car_grey;
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.car_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.car_orange;
                    //tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.car_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.car_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.car_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.car_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.car_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.car_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.car_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==1){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                //tracker_icon_color = "gray";
                resid=R.drawable.map_grey;
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.map_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.map_orange;
                    //tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.map_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.map_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.map_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.map_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.map_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.map_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.map_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==2){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.male_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.male_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.male_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.male_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.male_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.male_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.male_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // green
                    resid=R.drawable.male_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.male_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.male_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==3){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.female_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.female_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.female_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.female_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.female_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.female_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.female_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.female_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.female_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.female_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==4){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.bike_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.bike_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.bike_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.bike_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.bike_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.bike_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.bike_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.bike_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==5){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.bus_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.bus_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.bus_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.bus_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00")|| tracker_general_status == "stop") { // yellow
                    resid=R.drawable.bus_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.bus_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.bus_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.bus_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.bus_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.bus_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==6){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.truck_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.truck_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.truck_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.truck_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.truck_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.truck_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.truck_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.truck_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.truck_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.truck_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==7){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.road_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.road_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.road_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.road_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.road_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.road_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.road_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.road_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.road_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF")|| tracker_general_status == "outside")) { // blue
                    resid=R.drawable.road_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==8){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.ship_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.ship_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.ship_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.ship_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.ship_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.ship_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.ship_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.ship_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.ship_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.ship_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==9){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.train_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.train_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.train_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.train_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.train_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.train_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.train_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.train_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.train_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.train_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==10){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.aero_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.aero_grey;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.aero_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.aero_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.aero_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.aero_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.aero_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // green
                    resid=R.drawable.aero_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.aero_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.aero_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==11){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.container_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.container_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.container_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.container_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.container_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.container_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.container_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // green
                    resid=R.drawable.container_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.container_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.container_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==12){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.auto_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.auto_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.auto_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.auto_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.auto_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.auto_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.auto_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // green
                    resid=R.drawable.auto_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.auto_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.auto_blue;
                    tracker_icon_color = "blue";
                }
            }
        }



        SupportMapFragment fragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Google Map
        mGoogleMap = fragment.getMap();
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        //mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        GoogleMapOptions googleMapOptions=new GoogleMapOptions();
        googleMapOptions.zoomControlsEnabled(true);

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

        //changes by waqar

        if(engineStatus.equals("0")){
            stopProgressDialog();
            CameraUpdate cmf = CameraUpdateFactory.newLatLngZoom(latLng,18);
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(resid))
                    .title(tData.name));
            mGoogleMap.animateCamera(cmf);

        }else{
            Intent intentLoadLocation = new Intent(this, TrackerLocationLoadService.class);
            intentLoadLocation.putExtra("action","getlocation");

            intentLoadLocation.putExtra("tracker_id",tracker_id);
            startService(intentLoadLocation);

        }


        //region broadcast reciever..
        mBroadCastReciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                stopProgressDialog();

                if (intent.getAction().endsWith(TrackerLocationLoadService.ACTION_Error)) {
                    Toast.makeText(map_direction.this, "There was error loading location.", Toast.LENGTH_SHORT).show();

                    setMapCurrentLocation(latLng);

                } else if (intent.getAction().endsWith(TrackerLocationLoadService.ACTION_Fail)) {
                    Toast.makeText(map_direction.this, "There was error loading location.", Toast.LENGTH_SHORT).show();
                    setMapCurrentLocation(latLng);
                } else if (intent.getAction().endsWith(TrackerLocationLoadService.ACTION_Success)) {
                    //well here we will update the map
                    double[] lat = intent.getDoubleArrayExtra("latitude");
                    double[] lon = intent.getDoubleArrayExtra("longitude");

                    //and here we need to update the map

                    setMapCurrentLocation(new LatLng(lat[0], lon[0]));
                    setMapCurrentRoute(lat, lon);
                }

                if(intent.getAction().endsWith(IntentDataLoadService.Action_Fail)) {
                    //if we are not successful and we have failed..

                }else  if (intent.getAction().endsWith(IntentDataLoadService.Action_TrackerInfo)) {
                    engineStatus = intent.getStringExtra("engine_status");
                    latLng = new LatLng(intent.getDoubleExtra("latitude", 0.0), getIntent().getDoubleExtra("longitude", 0.0));

                    if (engineStatus.equals("-1")) {
                        Toast.makeText(AppManager.getInstance().getCurrentActivity(), "Could not find tracker information.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        tData = new TrackerData();

                        tData = new TrackerData();

                        tData.name = intent.getStringExtra("name");
                        tData.device_id = intent.getStringExtra("device_id");
                        tData.engine_status = intent.getStringExtra("engine_status");
                        tData.latitude = String.valueOf(String.format("%.5f", intent.getDoubleExtra("latitude", 0.0)));
                        tData.longitude = String.valueOf(String.format("%.5f", intent.getDoubleExtra("longitude", 0.0)));

                        tData.output_bit = intent.getStringExtra("output_bit");
                        tData.input_bit_1 = intent.getStringExtra("input_bit_1");
                        tData.input_bit_2 = intent.getStringExtra("input_bit_2");
                        tData.input_bit_3 = intent.getStringExtra("input_bit_3");
                        tData.input_bit_4 = intent.getStringExtra("input_bit_4");

                        tData.username = intent.getStringExtra("username");

                        tData.mileage = intent.getStringExtra("mileage") + unit;
                        tData.speed = intent.getStringExtra("speed") + unit;

                        tData.last_engine_on = intent.getStringExtra("last_engine_on");
                        tData.last_engine_off = intent.getStringExtra("last_engine_off");
                        tData.last_move = intent.getStringExtra("last_move");
                        tData.last_location = intent.getStringExtra("last_gprs");
                        tData.last_signal = intent.getStringExtra("last_signal");

                        //Changes by waqar
                        String gps = intent.getStringExtra("gps");
                        String gsm = intent.getStringExtra("gsm");
                        String battery = intent.getStringExtra("battery");


                        double gps_level = 0, gsm_level = 0, battery_power = 0;
                        if (gps.equals("none")) {

                        } else if (gps.equals("a") || gps.equals("b") || gps.equals("c") || gps.equals("d")) {
                            gps_level = 100.0;
                        } else if (Integer.parseInt(gps) >= 0 && Integer.parseInt(gps) <= 9) {
                            gps_level = Integer.parseInt(gps) * 10;
                        }
                        if (gsm.equals("none")) {

                        } else if (gsm.equals("a") || gsm.equals("b") || gsm.equals("c") || gsm.equals("d")) {
                            gsm_level = 100.0;
                        } else if (Integer.parseInt(gsm) >= 0 && Integer.parseInt(gsm) <= 9) {
                            gsm_level = Integer.parseInt(gsm) * 10;
                        }
                        if (battery.equals("L")) {
                            battery_power = 9.0;
                        } else if (battery.equals("F")) {
                            battery_power = 100.0;
                        } else if (Double.parseDouble(battery) >= 0 && Double.parseDouble(battery) <= 9.0) {
                            battery_power = battery_power * 10;
                        } else if (battery.equals("-")) {
                            battery_power = 0.0;
                        }

                        tData.gps = Double.toString(gps_level);
                        tData.gsm = Double.toString(gsm_level);
                        tData.battery = Double.toString(battery_power);
                    }
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
            Intent intentTrackerInfo = new Intent(AppManager.getInstance().getCurrentActivity(),IntentDataLoadService.class);
            intentTrackerInfo.putExtra("action","getTrackerInfo");
            intentTrackerInfo.putExtra("tracker_id",tracker_id);
            startService(intentTrackerInfo);

        }

        if(AppManager.getInstance().isMyServiceRunning(TrackerLocationLoadService.class)) {

            if(!AppManager.getInstance().isInternetAvailable()) {
                //if internet is not available and the service is running plz stop it
                stopService(new Intent(map_direction.this,TrackerLocationLoadService.class));
            }
        }
        else {

            Intent intentLoadLocation = new Intent(this, TrackerLocationLoadService.class);
            intentLoadLocation.putExtra("action","getlocation");

            intentLoadLocation.putExtra("tracker_id",tracker_id);
            startService(intentLoadLocation);

        }
    }

    private void setMapCurrentLocation(LatLng latlong) {

        mGoogleMap.addMarker(new MarkerOptions()
                .position(latlong)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_icon)));

    }

    private void setMapCurrentRoute(double[] lat, double[] lons) {

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
                        .icon(BitmapDescriptorFactory.fromResource(resid))
                .title(tData.name));
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

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(IntentDataLoadService.Action_Error));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(IntentDataLoadService.Action_Success));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(IntentDataLoadService.Action_Fail));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(IntentDataLoadService.Action_TrackerInfo));



    }

    @Override
    protected void onPause() {

        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadCastReciever);

    }

    TrackerDetailDialog tddialog;

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(mRunnableServerTask);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(mRunnableServerTask);
        super.onBackPressed();
    }
}
