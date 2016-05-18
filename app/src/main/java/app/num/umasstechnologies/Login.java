package app.num.umasstechnologies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import app.num.umasstechnologies.CustomServices.IntentLoginService;

public class Login extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout txtil_username;
    TextInputLayout txtil_password;
    Button btn_login;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = (Button) findViewById(R.id.btn_login);
        txtil_password = (TextInputLayout) findViewById(R.id.til_password);
        txtil_username = (TextInputLayout) findViewById(R.id.til_username);

        btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                if(txtil_username.getEditText().equals("")) {
                    txtil_username.setError("Please Enter Username.");
                }
                else if(txtil_password.getEditText().equals("")) {
                    txtil_password.setError("Please Enter Password.");
                }
                else {

                    Intent intent = new Intent(Login.this,IntentLoginService.class);

                    intent.putExtra("username",txtil_username.getEditText().toString());
                    intent.putExtra("password",txtil_password.getEditText().toString());

                    Log.d("Service","Starting the login service.");
                    this.startService(intent);
                }

                break;
        }
    }
}
