package app.num.MassUAETracking;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import app.num.MassUAETracking.CustomDialogs.CompanyDialog;
import app.num.MassUAETracking.CustomDialogs.ViewDialog;
import app.num.MassUAETracking.CustomServices.IntentLoginService;
import app.num.MassUAETracking.DatabaseClasses.DatabaseHandler;
import app.num.MassUAETracking.Models.user;
import app.num.MassUAETracking.Singleton.AppManager;

public class Login extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout txtil_username;
    TextInputLayout txtil_password;

    EditText edt_username;
    EditText edt_password;

    //Button btn_login;
    AppCompatButton btn_login;
    ProgressDialog progressDialog;

    BroadcastReceiver mBroadCastReciever;

    //we have to get ime and also the gcm token over here not in main activity.. send boht.. than when loggin out we can
    //use imei to logout

    private String deviceId;

    private String tokenGCM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        TelephonyManager tm = ( TelephonyManager ) getSystemService(Context.TELEPHONY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(Login.this,
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                deviceId =  tm.getDeviceId();
            }
        } else {
            //Not in api-23, no need to prompt
            deviceId =  tm.getDeviceId();
        }


        AppManager.getInstance().setCurrentActivity(this);

        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        txtil_password = (TextInputLayout) findViewById(R.id.til_password);
        txtil_username = (TextInputLayout) findViewById(R.id.til_username);

        edt_username = (EditText) findViewById(R.id.input_username);
        edt_password = (EditText) findViewById(R.id.input_password);

        edt_username.setTextColor(Color.BLACK);
        edt_password.setTextColor(Color.BLACK);

        txtil_password.setHint("Password");
        txtil_username.setHint("Username");

        mBroadCastReciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().endsWith(IntentLoginService.BroadCastError)){

                   // Toast.makeText(Login.this,"Check Your Internet.",Toast.LENGTH_SHORT).show();

                    ViewDialog vDialog = new ViewDialog();
                    vDialog.showDialog(Login.this, "Check your internet, network error");

                }
                else if(intent.getAction().endsWith(IntentLoginService.BroadCastSuccess)) {
                    //here we have logged in
                    //we have to findout which type of login we have done for now..
                    //if login was success full we need to move move move to next page

                    //here we will check the user type first
                    //if he is the admin and has more than one those than we will show him activity
                    //filled with possible selected blah blah
                    DatabaseHandler dbhandler = new DatabaseHandler(Login.this);

                    user currentUser = dbhandler.getUser();


                    if(currentUser == null) {
                        Toast.makeText(Login.this,"Could not load user from database.",Toast.LENGTH_SHORT).show();
                    }
                    else {


                        //here we have to check.. that we have only one or many companies.. :p...


                        DatabaseHandler dbhan = new DatabaseHandler(Login.this);

                        int numberOfCompanies = dbhan.countCompanys();

                        if(numberOfCompanies > 1) {

                            CompanyDialog vDialog = new CompanyDialog();
                            vDialog.showDialog(Login.this, dbhan.getArrayOfCompanies() );

                        }
                        else {
                            Intent intentMainScreen = new Intent(Login.this, MainActivity.class);
                            intentMainScreen.putExtra("mana",edt_username.getText().toString());
                            intentMainScreen.putExtra("login_status","just_login");
                            startActivity(intentMainScreen);
                        }

                    }
                }
                else if(intent.getAction().endsWith(IntentLoginService.BroadCastFail)) {

                    //Toast.makeText(Login.this,"Wrong username/password",Toast.LENGTH_SHORT).show();
                    //ViewDialog vDialog = new ViewDialog();
                    //vDialog.showDialog(Login.this, "Wrong Username/Password");

                    final AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(Login.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(Login.this);
                    }
                    //A lertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    //builder.setTitle("Location Services Not Active");
                    builder.setMessage("This is an alert dialog");
                    builder.setTitle("Alert !");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // The neutral button was clicked
                           arg0.dismiss();
                        }
                    });

                    Dialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                }

                if(progressDialog != null)
                    progressDialog.hide(); //close the progress dialog..

            }
        };

        btn_login.setOnClickListener(this);


    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(Login.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user asynchronously -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(Login.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(Login.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(Login.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Login.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(IntentLoginService.BroadCastError));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(IntentLoginService.BroadCastFail));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, new IntentFilter(IntentLoginService.BroadCastSuccess));



    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadCastReciever);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //User has previously accepted this permission
                    if (ActivityCompat.checkSelfPermission(Login.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        if(progressDialog == null) {
                            progressDialog = new ProgressDialog(Login.this);
                        }

                        progressDialog.setMessage("authenticating.");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        if(AppManager.getInstance().isMyServiceRunning(IntentLoginService.class)) {
                            return; //don't start it again..
                        }

                        if(edt_username.getText().toString().equals("")) {
                            txtil_username.setError("Please Enter Username.");
                            progressDialog.hide();
                        }
                        else if(edt_password.getText().toString().equals("")) {
                            txtil_password.setError("Please Enter Password.");
                            txtil_username.setError("");
                            progressDialog.hide();
                        }
                        else {

                            txtil_password.setError("");
                            txtil_username.setError("");

                            Intent intent = new Intent(Login.this,IntentLoginService.class);

                            intent.putExtra("username",edt_username.getText().toString());
                            intent.putExtra("password",edt_password.getText().toString());
                            intent.putExtra("token",tokenGCM);
                            intent.putExtra("deviceid",deviceId);

                            Log.d("Service","Starting the login service.");
                            this.startService(intent);
                        }
                    }
                    else{
                        checkLocationPermission();
                    }
                } else {
                    //Not in api-23, no need to prompt
                    if(progressDialog == null) {
                        progressDialog = new ProgressDialog(Login.this);
                    }

                    progressDialog.setMessage("authenticating.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    if(AppManager.getInstance().isMyServiceRunning(IntentLoginService.class)) {
                        return; //don't start it again..
                    }

                    if(edt_username.getText().toString().equals("")) {
                        txtil_username.setError("Please Enter Username.");
                    }
                    else if(edt_password.getText().toString().equals("")) {
                        txtil_password.setError("Please Enter Password.");
                        txtil_username.setError("");
                    }
                    else {

                        txtil_password.setError("");
                        txtil_username.setError("");

                        Intent intent = new Intent(Login.this,IntentLoginService.class);

                        intent.putExtra("username",edt_username.getText().toString());
                        intent.putExtra("password",edt_password.getText().toString());
                        intent.putExtra("token",tokenGCM);
                        intent.putExtra("deviceid",deviceId);

                        Log.d("Service","Starting the login service.");
                        this.startService(intent);
                    }
                }

                break;
        }
    }
}
