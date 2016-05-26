package app.num.umasstechnologies.CustomDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import app.num.umasstechnologies.R;

/**
 * Created by Imdad on 5/26/2016.
 */
public class TrackerDetailDialog {

    public void showDialog(Activity activity, String msg){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.detail_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView txt_deviceid = (TextView) dialog.findViewById(R.id.txt_deviceid);
        TextView txt_engineStatusOnnOff = (TextView) dialog.findViewById(R.id.txt_engineStatusOnnOff);
        TextView txt_last_engine_off = (TextView) dialog.findViewById(R.id.txt_last_engine_off);
        TextView txt_last_engine_on = (TextView) dialog.findViewById(R.id.txt_last_engine_on);
        TextView txt_last_location = (TextView) dialog.findViewById(R.id.txt_last_location);
        TextView txt_last_move = (TextView) dialog.findViewById(R.id.txt_last_move);
        TextView txt_last_signal = (TextView) dialog.findViewById(R.id.txt_last_signal);

        TextView txt_username = (TextView) dialog.findViewById(R.id.txt_username);
        TextView txt_speed = (TextView) dialog.findViewById(R.id.txt_speed);

        TextView txt_output = (TextView) dialog.findViewById(R.id.txt_output);
        TextView txt_input1 = (TextView) dialog.findViewById(R.id.txt_input1);
        TextView txt_input2 = (TextView) dialog.findViewById(R.id.txt_input2);
        TextView txt_input3 = (TextView) dialog.findViewById(R.id.txt_input3);
        TextView txt_input4 = (TextView) dialog.findViewById(R.id.txt_input4);

        TextView text = (TextView) dialog.findViewById(R.id.txtv_dialogWarning);


        Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setAttributes(lp);

        dialog.show();

    }
}
