package app.num.MassUAETracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import app.num.MassUAETracking.DatabaseClasses.DatabaseHandler;
import app.num.MassUAETracking.GCMClasses.GCMRegistrationIntentService;
import app.num.MassUAETracking.Interfaces.IFragmentEvents;
import app.num.MassUAETracking.Models.user;
import app.num.MassUAETracking.Singleton.AppManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IFragmentEvents{



    private ResideMenu resideMenu;
    private Context mContext;

    private ResideMenuItem itemVehicles;
    private ResideMenuItem itemFeedback;
    private ResideMenuItem itemAlerts;
    private ResideMenuItem itemLogout;

    private TextView headerName;

    private user currentUser = null;


    @Override
    public void onBackPressed() {

        //here to write the code

        if(!resideMenu.isOpened())
            resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
        else
            super.onBackPressed();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppManager.getInstance().setCurrentActivity(this);

        headerName = (TextView) findViewById(R.id.txtvHeaderName);

        headerName.setText("Vehicle");

        mContext = this;

        DatabaseHandler dbhandler = new DatabaseHandler(MainActivity.this);

        currentUser = dbhandler.getUser();

        mRegistrationBroadcastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_SUCCCESS)) {
                    tokenGCM = intent.getStringExtra("Token");
                    //Toast.makeText(getApplicationContext(), "GCMToken: "+tokenGCM, Toast.LENGTH_SHORT).show();
                    //if we get the token we save it and then we use it when logging in..
                }
                else if(intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                     Toast.makeText(getApplicationContext(), "GCMTokenError: ", Toast.LENGTH_SHORT).show();
                }
            }
        };



        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if(ConnectionResult.SUCCESS != resultCode) {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(),"Google play service not installed/enabled in this device.",Toast.LENGTH_SHORT).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode,getApplicationContext());
            }
            else {
                Toast.makeText(getApplicationContext(),"This device does not support google play services.",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Intent intent = new Intent(this,GCMRegistrationIntentService.class);
            startService(intent);
        }


        setUpMenu();
        if( savedInstanceState == null ) {

            if(vehichleFragment == null)
                vehichleFragment =  new VehichleFragment();

            String loginString = getIntent().getStringExtra("login_status");

            if(loginString != null) {

                if (loginString.equals("just_login")) {
                    vehichleFragment.loadItemsFromServer("0", 0);
                }

            }
           // vehichleFragment.loadItemsFromServer("0", 0);
            changeFragment(vehichleFragment);
        }
    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);

        resideMenu.setBackground(R.drawable.background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.5f);
        resideMenu.setShadowVisible(false); //we dont want shadow
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT); //we dont want it to work with right blah blah


        itemVehicles  = new ResideMenuItem(this, R.drawable.icon_profile,  "Vehicles");

        itemAlerts = new ResideMenuItem(this, R.drawable.icon_calendar, "Alerts");
        itemFeedback = new ResideMenuItem(this, R.drawable.icon_settings, "Feedback");
        itemLogout = new ResideMenuItem(this,R.drawable.icon_home, "Logout");


        //get data from shared preference





      //  itemHome.setOnClickListener(this);
        itemVehicles.setOnClickListener(this);
        itemAlerts.setOnClickListener(this);
        itemFeedback.setOnClickListener(this);
        itemLogout.setOnClickListener(this);



      //  resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemVehicles, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemAlerts, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemFeedback, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_LEFT);

        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity","OnResume");

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReciever, new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReciever, new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity","OnPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReciever);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private VehichleFragment vehichleFragment;

    private BroadcastReceiver mRegistrationBroadcastReciever;

    private String tokenGCM;

    @Override
    public void onClick(View view) {

       // if (view == itemHome){
       //     changeFragment(new HomeFragment());
       // }else

        if (view == itemAlerts){
            headerName.setText("Alerts");

            changeFragment(new AlertFragment());

        }else if (view == itemVehicles){

            headerName.setText("Vehicle");

            if(vehichleFragment == null) {
                vehichleFragment = new VehichleFragment();
                vehichleFragment.setOnEventOccurredListener(this);
            }


            //if we are just loged in then plz load the trackers .. this is fine totally i hope so

            if(getIntent().getStringExtra("login_status") != null) {

                if (getIntent().getStringExtra("login_status").toString().equals("just_login")) {
                    vehichleFragment.loadItemsFromServer("0", 0);
                }

            }

            changeFragment(vehichleFragment);


        }else if (view == itemFeedback){
            headerName.setText("FeedBack");
            changeFragment(new SettingsFragment());
        }
        else if (view == itemLogout) {
            //here we will logut.. so delete things from database..
            Logout();

        }



        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
           // Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
           // Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit)
                .commit();

    }

    private void Logout() {
        DatabaseHandler dbhandler = new DatabaseHandler(MainActivity.this);
        dbhandler.deleteAllInformation();
        Intent intentLoginScreen = new Intent(MainActivity.this,Login.class);

        AppManager.getInstance().removeCompany();

        finish();
        startActivity(intentLoginScreen);
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

    @Override
    public void onEventOccured(int eventId) {
        if(eventId == 101) {
            Logout();
        }
    }
}

