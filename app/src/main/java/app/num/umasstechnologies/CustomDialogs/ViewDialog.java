package app.num.umasstechnologies.CustomDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import app.num.umasstechnologies.R;

/**
 * Created by Imdad on 5/20/2016.
 */
public class ViewDialog {

    public void showDialog(Activity activity, String msg){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_login);

        TextView text = (TextView) dialog.findViewById(R.id.txtv_dialogWarning);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_login);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
