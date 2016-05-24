package app.num.umasstechnologies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _inflatedView = inflater.inflate(R.layout.lyt_vehicle,null,false);

        //we got the reference to our listView
        listViewVehicles = (ListView) _inflatedView.findViewById(R.id.lstViewVehicles);
        vehicleList = new ArrayList<>();



        setTestData();

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


    public  void setTestData() {

        vehicleList.add(new Vehicle("Car1","Description of the car.","",""));

    }
}
