package app.num.umasstechnologies.CustomDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import app.num.umasstechnologies.MainActivity;
import app.num.umasstechnologies.Models.CompanyInfo;
import app.num.umasstechnologies.R;
import app.num.umasstechnologies.Singleton.AppManager;

/**
 * Created by Imdad on 5/23/2016.
 */
public class CompanyDialog implements Spinner.OnItemSelectedListener {

    ArrayAdapter<String> spnAdapter;
    String[] namesList;
    CompanyInfo[] companyInfos;

    int selectedPosition = 0;


    public void showDialog(final Activity activity, CompanyInfo[] msg){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_companylist);

        companyInfos = msg;

        int len = msg.length;
        namesList = new String[len];

        for (int index = 0; index < len; index++) {
            namesList[index] = msg[index].getName();
        }

        Spinner spn_companyList = (Spinner) dialog.findViewById(R.id.spn_companylist);

        spnAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,namesList);

        spn_companyList.setAdapter(spnAdapter);

        spn_companyList.setOnItemSelectedListener(this);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_select);
        dialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

                AppManager.getInstance().setCompanyInPreferences(companyInfos[selectedPosition]);
                Intent intentMainScreen = new Intent(activity, MainActivity.class);
                activity.startActivity(intentMainScreen);

            }
        });

        dialog.getWindow().setAttributes(lp);

        dialog.show();

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //this is the methode that will be called whe you select any of the item of course indeed
        Toast.makeText(AppManager.getInstance().getCurrentActivity(),"ItemValue: "+namesList[position],Toast.LENGTH_SHORT).show();
        selectedPosition = position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
