package app.num.umasstechnologies;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import app.num.umasstechnologies.CustomDialogs.CompanyDialog;
import app.num.umasstechnologies.CustomDialogs.ViewDialog;
import app.num.umasstechnologies.CustomServices.IntentLoginService;
import app.num.umasstechnologies.DatabaseClasses.DatabaseHandler;
import app.num.umasstechnologies.Models.CompanyInfo;
import app.num.umasstechnologies.Models.user;
import app.num.umasstechnologies.Singleton.AppManager;

public class Login extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout txtil_username;
    TextInputLayout txtil_password;

    EditText edt_username;
    EditText edt_password;

    //Button btn_login;
    AppCompatButton btn_login;
    ProgressDialog progressDialog;

    BroadcastReceiver mBroadCastReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppManager.getInstance().setCurrentActivity(this);

        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        txtil_password = (TextInputLayout) findViewById(R.id.til_password);
        txtil_username = (TextInputLayout) findViewById(R.id.til_username);

        edt_username = (EditText) findViewById(R.id.input_username);
        edt_password = (EditText) findViewById(R.id.input_password);

        txtil_password.setHint("Password");
        txtil_username.setHint("Username");

        mBroadCastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().endsWith(IntentLoginService.BroadCastError)){

                    Toast.makeText(Login.this,"Check Your Internet or Json Exception.",Toast.LENGTH_SHORT).show();

                    ViewDialog vDialog = new ViewDialog();
                    vDialog.showDialog(Login.this, "Check your internet, network error or json exception");

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
                            //we have to send the companies list of course..

                            vDialog.showDialog(Login.this, dbhan.getArrayOfCompanies() );
                        }
                        else {
                            Intent intentMainScreen = new Intent(Login.this, MainActivity.class);
                            startActivity(intentMainScreen);
                        }

                    }
                }
                else if(intent.getAction().endsWith(IntentLoginService.BroadCastFail)) {

                    Toast.makeText(Login.this,"Wrong username/password",Toast.LENGTH_SHORT).show();
                    ViewDialog vDialog = new ViewDialog();
                    vDialog.showDialog(Login.this, "Wrong Username/Password");
                }

                if(progressDialog != null)
                    progressDialog.hide(); //close the progress dialog..

            }
        };

        btn_login.setOnClickListener(this);

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

                if(progressDialog == null) {
                    progressDialog = new ProgressDialog(Login.this);
                }

                progressDialog.setMessage("logging in.");
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

                    Log.d("Service","Starting the login service.");
                    this.startService(intent);

                }

                break;
        }
    }
}
