package app.num.MassUAETracking;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import app.num.MassUAETracking.CustomDialogs.ViewDialog;
import app.num.MassUAETracking.CustomServices.IntentDataLoadService;
import app.num.MassUAETracking.Singleton.AppManager;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    private View inflatedView;
    private EditText edt_name;
    private EditText edt_contact;
    private EditText edt_email;
    private EditText edt_comment;

    private Button btn_send;

    private TextInputLayout til_name, til_email, til_contact, til_comment;

    private BroadcastReceiver mBroadCastReciver;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflatedView = inflater.inflate(R.layout.settings, container, false);

        til_name = (TextInputLayout)inflatedView.findViewById(R.id.til_name);
        til_contact = (TextInputLayout) inflatedView.findViewById(R.id.til_contactnumber);
        til_comment = (TextInputLayout) inflatedView.findViewById(R.id.til_comment);
        til_email = (TextInputLayout) inflatedView.findViewById(R.id.til_email);

        btn_send = (Button) inflatedView.findViewById(R.id.btn_sendfeedback);
        btn_send.setOnClickListener(this);

        edt_comment = (EditText) inflatedView.findViewById(R.id.input_comment);
        edt_name = (EditText) inflatedView.findViewById(R.id.input_name);
        edt_contact = (EditText) inflatedView.findViewById(R.id.input_contactnumber);
        edt_email = (EditText) inflatedView.findViewById(R.id.input_email);

        mBroadCastReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if(progressDialog != null)
                    progressDialog.hide();

                String message = intent.getStringExtra("message");

                if(intent.getAction().endsWith(IntentDataLoadService.Action_Email_Sent_Error)) {
                    ViewDialog viewDialog = new ViewDialog();
                    viewDialog.showDialog(AppManager.getInstance().getCurrentActivity(),"Email send failed check your interent connection.");
                }
                else if (intent.getAction().endsWith(IntentDataLoadService.Action_Email_Sent_Successfully) ) {

                    edt_comment.setText("");
                    edt_contact.setText("");
                    edt_email.setText("");
                    edt_name.setText("");

                    ViewDialog viewDialog = new ViewDialog();
                    viewDialog.showDialog(AppManager.getInstance().getCurrentActivity(),"Email Send Successfull");
                }
                else if (intent.getAction().endsWith(IntentDataLoadService.Action_Error)) {
                    ViewDialog viewDialog = new ViewDialog();
                    viewDialog.showDialog(AppManager.getInstance().getCurrentActivity(),"Failed, Check your internet connection.");
                }

            }
        };


        return inflatedView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sendfeedback:

                if(  edt_name.getText().toString().isEmpty()) {
                    til_name.setError("This field is required");
                }
                else if (edt_email.getText().toString().isEmpty()) {
                    til_email.setError("This field is required");
                }
                else if(isValidEmail(edt_email.getText().toString())){
                    til_email.setError("Please specify correct email address");
                }
                else if (edt_contact.getText().toString().isEmpty()) {
                    til_contact.setError("This field is required");
                }
                else if (edt_comment.getText().toString().isEmpty()){
                    til_comment.setError("This field is required");
                }
                else{

                    til_comment.setError("");
                    til_contact.setError("");
                    til_email.setError("");
                    til_name.setError("");

                    //lets start the service.. send the data to the server ...
                    Intent intentFeedback = new Intent(AppManager.getInstance().getCurrentActivity(), IntentDataLoadService.class);
                    intentFeedback.putExtra("name",edt_name.getText().toString());
                    intentFeedback.putExtra("email",edt_email.getText().toString());
                    intentFeedback.putExtra("contact",edt_contact.getText().toString());
                    intentFeedback.putExtra("comment",edt_comment.getText().toString());
                    intentFeedback.putExtra("action","sendfeedback");


                    if(progressDialog == null)
                        progressDialog = new ProgressDialog(AppManager.getInstance().getCurrentActivity());

                    progressDialog.setMessage("Sending Email");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);

                    progressDialog.show();

                    AppManager.getInstance().getCurrentActivity().startService(intentFeedback);
                }

                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager
                .getInstance(AppManager.getInstance()
                .getCurrentActivity())
                .registerReceiver(mBroadCastReciver, new IntentFilter( IntentDataLoadService.Action_Email_Sent_Error) );

        LocalBroadcastManager
                .getInstance(AppManager.getInstance()
                        .getCurrentActivity())
                .registerReceiver(mBroadCastReciver, new IntentFilter( IntentDataLoadService.Action_Error) );

        LocalBroadcastManager
                        .getInstance(AppManager.getInstance()
                        .getCurrentActivity())
                        .registerReceiver(mBroadCastReciver, new IntentFilter( IntentDataLoadService.Action_Email_Sent_Successfully) );

    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).unregisterReceiver(mBroadCastReciver);
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return !android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
