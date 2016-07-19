package app.num.MassUAETracking.CustomDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.num.MassUAETracking.Models.TrackerData;
import app.num.MassUAETracking.R;

/**
 * Created by Imdad on 5/26/2016.
 */
public class TrackerDetailDialog {

    public void showDialog(Activity activity, TrackerData tData){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.detail_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);




        TextView txt_name = (TextView) dialog.findViewById(R.id.txt_name);
        txt_name.setText(tData.name);

        TextView txt_deviceid = (TextView) dialog.findViewById(R.id.txt_deviceid);
        txt_deviceid.setText(tData.device_id);

        ImageView txt_engineStatusOnnOff = (ImageView) dialog.findViewById(R.id.imv_engineStatusOnnOff);
        txt_engineStatusOnnOff.setBackgroundColor( (tData.engine_status.equals("0")? Color.RED: Color.GREEN) );

        TextView txt_latitude = (TextView) dialog.findViewById(R.id.txt_latitude);
        txt_latitude.setText(tData.latitude);

        TextView txt_longitude = (TextView) dialog.findViewById(R.id.txt_longitude);
        txt_longitude.setText(tData.longitude);

        TextView txt_output = (TextView) dialog.findViewById(R.id.txt_output);
        txt_output.setText(tData.output_bit);

        TextView txt_input1 = (TextView) dialog.findViewById(R.id.txt_input1);
        txt_input1.setText(tData.input_bit_1);

        TextView txt_input2 = (TextView) dialog.findViewById(R.id.txt_input2);
        txt_input2.setText(tData.input_bit_2);

        TextView txt_input3 = (TextView) dialog.findViewById(R.id.txt_input3);
        txt_input3.setText(tData.input_bit_3);

        TextView txt_input4 = (TextView) dialog.findViewById(R.id.txt_input4);
        txt_input4.setText(tData.input_bit_4);

        TextView txt_mileage = (TextView) dialog.findViewById(R.id.txt_mileage);
        txt_mileage.setText(tData.mileage);

        TextView txt_username = (TextView) dialog.findViewById(R.id.txt_username);
        txt_username.setText(tData.username);

        TextView txt_speed = (TextView) dialog.findViewById(R.id.txt_speed);
        txt_speed.setText(tData.speed);

        TextView txt_last_engine_off = (TextView) dialog.findViewById(R.id.txt_last_engine_off);
        txt_last_engine_off.setText(tData.last_engine_off);

        TextView txt_last_engine_on = (TextView) dialog.findViewById(R.id.txt_last_engine_on);
        txt_last_engine_on.setText(tData.last_engine_on);

        TextView txt_last_location = (TextView) dialog.findViewById(R.id.txt_last_location);
        txt_last_location.setText(tData.last_location);

        TextView txt_last_move = (TextView) dialog.findViewById(R.id.txt_last_move);
        txt_last_move.setText(tData.last_move);

        TextView txt_last_signal = (TextView) dialog.findViewById(R.id.txt_last_signal);
        txt_last_signal.setText(tData.last_signal);
        /*
        TextView txt_gps=(TextView)dialog.findViewById(R.id.txt_gps);
        txt_gps.setText(tData.gps);


        TextView txt_gsm=(TextView)dialog.findViewById(R.id.txt_gsm);
        txt_gsm.setText(tData.gsm);

        TextView txt_battery=(TextView)dialog.findViewById(R.id.txt_battery);
        txt_battery.setText(tData.battery);
        */
        ProgressBar pGPS=(ProgressBar)dialog.findViewById(R.id.progressGPS);
        pGPS.setProgress((int)Double.parseDouble(tData.gps));

        ProgressBar pGSM=(ProgressBar)dialog.findViewById(R.id.progressGSM);
        pGSM.setProgress((int)Double.parseDouble(tData.gsm));

        ProgressBar pbattery=(ProgressBar)dialog.findViewById(R.id.progressBattery);
        pbattery.setProgress((int)Double.parseDouble(tData.battery));

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }
}
