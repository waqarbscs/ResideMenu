package app.num.umasstechnologies;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

import app.num.umasstechnologies.Adapters.lstVehicleAdapter;
import app.num.umasstechnologies.CustomServices.IntentDataLoadService;
import app.num.umasstechnologies.Models.CompanyInfo;
import app.num.umasstechnologies.Models.Vehicle;
import app.num.umasstechnologies.Singleton.AppManager;

/**
 * Created by Imdad on 4/30/2016.
 */
public class VehichleFragment extends Fragment {

    private View _inflatedView = null;
    private ListView listViewVehicles;

    private lstVehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;

    private ProgressDialog progressDialog;

    private BroadcastReceiver mBroadCastReciever;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _inflatedView = inflater.inflate(R.layout.lyt_vehicle,null,false);

        //we got the reference to our listView
        listViewVehicles = (ListView) _inflatedView.findViewById(R.id.lstViewVehicles);
        vehicleList = new ArrayList<>();

        mBroadCastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //here we recieve the broadcast
                if(progressDialog != null)
                    progressDialog.hide(); //close the progress dialog once we get the broadcast.

                if(intent.getAction().endsWith(IntentDataLoadService.Action_Error)) {
                    //the action error thingi..

                }
                else if(intent.getAction().endsWith(IntentDataLoadService.Action_Fail)) {
                    //if we are not successful and we have failed..

                }
                else if(intent.getAction().endsWith(IntentDataLoadService.Action_Success)) {
                    // if we are successfull.....

                }


            }
        };



        setTestData();

        //we dont have to set the test data we must get the data from the database and get that data :D

        vehicleAdapter = new lstVehicleAdapter(AppManager.getInstance().getCurrentActivity(),vehicleList);

        listViewVehicles.setAdapter(vehicleAdapter);

         CompanyInfo cInformation = AppManager.getInstance().getCurrentCompany();

        if(cInformation == null) {
            Toast.makeText(AppManager.getInstance().getCurrentActivity(), "Could not find company information.",Toast.LENGTH_SHORT).show();
        }
        else {
            //we have to run the service which will bring data from there server of course indeed..
            if(progressDialog == null)
                progressDialog = new ProgressDialog(AppManager.getInstance().getCurrentActivity());
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.setMessage("loading trackers.");
            progressDialog.show();

            //here we are loading the progress dialog whatever..
            Intent intentDataLoader = new Intent(AppManager.getInstance().getCurrentActivity() ,IntentDataLoadService.class);
            intentDataLoader.putExtra("action","getTracker");
            intentDataLoader.putExtra("memberid","0");

            AppManager.getInstance().getCurrentActivity().startService(intentDataLoader);

        }

        return _inflatedView;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).registerReceiver(mBroadCastReciever,new IntentFilter(IntentDataLoadService.Action_Error));
        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).registerReceiver(mBroadCastReciever,new IntentFilter(IntentDataLoadService.Action_Fail));
        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).registerReceiver(mBroadCastReciever,new IntentFilter(IntentDataLoadService.Action_Success));
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).unregisterReceiver(mBroadCastReciever);
    }

    public  void setTestData() {

        vehicleList.add(new Vehicle("Car1","Description of the car.","0","0"));

    }
}
