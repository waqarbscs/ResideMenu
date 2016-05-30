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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

import app.num.umasstechnologies.Adapters.lstVehicleAdapter;
import app.num.umasstechnologies.CustomServices.IntentDataLoadService;
import app.num.umasstechnologies.DatabaseClasses.DatabaseHandler;
import app.num.umasstechnologies.Models.CompanyInfo;
import app.num.umasstechnologies.Models.Members;
import app.num.umasstechnologies.Models.Vehicle;
import app.num.umasstechnologies.Singleton.AppManager;

/**
 * Created by Imdad on 4/30/2016.
 */
public class VehichleFragment extends Fragment implements Spinner.OnItemSelectedListener, ListView.OnItemClickListener {

    private boolean firstTimeLoad = true;

    private View _inflatedView = null;
    private ListView listViewVehicles;

    private lstVehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;

    private ProgressDialog progressDialog;

    private BroadcastReceiver mBroadCastReciever;

    private ArrayAdapter<String> spnAdapter;
    private int selectedAdapterItem;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _inflatedView = inflater.inflate(R.layout.lyt_vehicle,null,false);

        //we got the reference to our listView
        listViewVehicles = (ListView) _inflatedView.findViewById(R.id.lstViewVehicles);

        listViewVehicles.setOnItemClickListener(this);
        vehicleList = new ArrayList<>();

        //here is our broadcast listner

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

                    //here we got hte success yahoo.. :D

                    //lets first empty the list of the vehicles ok lets do that..

                    //it is going to bring the updated list of the vehicles..
                    //we have to check for list first in database.. if we have or not..

                   loatDataFromDatabase();

                }
                else if (intent.getAction().endsWith(IntentDataLoadService.Action_TrackerInfo)){
                    String engineStatus = intent.getStringExtra("engine_status");
                    String tracker_id = intent.getStringExtra("tracker_id");
                    LatLng latLng = new LatLng(intent.getDoubleExtra("lat",0.0),intent.getDoubleExtra("lon",0.0));

                    if(engineStatus.equals("-1")) {
                        Toast.makeText(AppManager.getInstance().getCurrentActivity(), "Could not find tracker information.", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Intent intentMap = new Intent(AppManager.getInstance().getCurrentActivity(),map_direction.class);
                        intentMap.putExtra("engine_status",engineStatus);
                        intentMap.putExtra("tracker_id",tracker_id);
                        intentMap.putExtra("latitude",latLng.latitude);
                        intentMap.putExtra("longitude",latLng.longitude);

                        intentMap.putExtra("last_gprs",intent.getStringExtra("last_gprs"));
                        intentMap.putExtra("last_signal",intent.getStringExtra("last_signal"));
                        intentMap.putExtra("last_move",intent.getStringExtra("last_move"));
                        intentMap.putExtra("last_engine_on",intent.getStringExtra("last_engine_on"));
                        intentMap.putExtra("last_engine_off",intent.getStringExtra("last_engine_off"));
                        intentMap.putExtra("device_id",intent.getStringExtra("device_id"));

                        intentMap.putExtra("name",intent.getStringExtra("name"));
                        intentMap.putExtra("mileage",intent.getStringExtra("mileage"));
                        intentMap.putExtra("speed",intent.getStringExtra("speed"));

                        intentMap.putExtra("username",intent.getStringExtra("username"));

                        intentMap.putExtra("output_bit",intent.getStringExtra("output_bit"));
                        intentMap.putExtra("input_bit_1",intent.getStringExtra("input_bit_1"));
                        intentMap.putExtra("input_bit_2",intent.getStringExtra("input_bit_2"));
                        intentMap.putExtra("input_bit_3",intent.getStringExtra("input_bit_3"));
                        intentMap.putExtra("input_bit_4",intent.getStringExtra("input_bit_4"));



                        AppManager.getInstance().getCurrentActivity().startActivity(intentMap);

                    }
                }


            }
        };


        vehicleAdapter = new lstVehicleAdapter(AppManager.getInstance().getCurrentActivity(),vehicleList);

        listViewVehicles.setAdapter(vehicleAdapter);

        CompanyInfo cInformation = AppManager.getInstance().getCurrentCompany();

        if(cInformation == null) {
            Toast.makeText(AppManager.getInstance().getCurrentActivity(), "Could not find company information.",Toast.LENGTH_SHORT).show();
        }
        else {

            if(!loatDataFromDatabase()) {

                if (progressDialog == null)
                    progressDialog = new ProgressDialog(AppManager.getInstance().getCurrentActivity());

                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

                progressDialog.setMessage("loading trackers.");
                progressDialog.show();

                //here we are loading the progress dialog whatever..
                Intent intentDataLoader = new Intent(AppManager.getInstance().getCurrentActivity(), IntentDataLoadService.class);
                intentDataLoader.putExtra("action", "getTracker");
                intentDataLoader.putExtra("memberid", "0");

                AppManager.getInstance().getCurrentActivity().startService(intentDataLoader);
            }

        }

        return _inflatedView;
    }
    List<Members> mList;
    public String[] getListOfMembersFromDatabase() {

        DatabaseHandler dbhandler = new DatabaseHandler(AppManager.getInstance().getCurrentActivity());

        mList = dbhandler.getListOfMembers();


        if(mList == null || mList.size() <= 0 )
            return null;

        String[] memberArray = new String[mList.size() + 1];
        memberArray[0] = "All";

        for (int index = 1, col = 0; index <= mList.size(); index++, col++) {
            memberArray[index] = mList.get(col).name;
        }

        return memberArray;

    }

    public boolean loatDataFromDatabase() {

        //loading data from database..
        String[] listOfNames = getListOfMembersFromDatabase();

        Spinner spn_companyList = (Spinner) _inflatedView.findViewById(R.id.spn_member_list);
        spnAdapter = new ArrayAdapter<String>(AppManager.getInstance().getCurrentActivity(),android.R.layout.simple_spinner_item,listOfNames);
        spn_companyList.setAdapter(spnAdapter);
        spn_companyList.setOnItemSelectedListener(this);
        spn_companyList.setSelection(AppManager.getInstance().getIntVariablesInPreferences("selected_list_item"));


        vehicleList.clear();
        vehicleAdapter.notifyDataSetChanged();

        DatabaseHandler dbhandler = new DatabaseHandler(AppManager.getInstance().getCurrentActivity());

        List<Vehicle> vList = dbhandler.getListOfTrackers();

        if(vList == null)
            return false;

        vehicleList.addAll(vList);

        vehicleAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public void onPause() {

        super.onPause();

        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).unregisterReceiver(mBroadCastReciever);
  }

    @Override
    public void onResume() {

        super.onResume();

        //here we must check that did we miss the event and if than we must set accordingly..

        int missedEvent = AppManager.getInstance().getIntVariablesInPreferences("missed_event");

        if(missedEvent == 11) {
            //we have missed the event..

        }

        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).registerReceiver(mBroadCastReciever,new IntentFilter(IntentDataLoadService.Action_Error));
        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).registerReceiver(mBroadCastReciever,new IntentFilter(IntentDataLoadService.Action_Fail));
        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).registerReceiver(mBroadCastReciever,new IntentFilter(IntentDataLoadService.Action_Success));
        LocalBroadcastManager.getInstance(AppManager.getInstance().getCurrentActivity()).registerReceiver(mBroadCastReciever, new IntentFilter(IntentDataLoadService.Action_TrackerInfo));

    }

    public  void setTestData() {

        vehicleList.add(new Vehicle("Car1","Description of the car.","0","0"));

    }

    int selectedItemDropDown = 0;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(firstTimeLoad)
        {
            firstTimeLoad = false;
            return;
        }

        selectedItemDropDown = position; //0 is basically all which means send 0 ..

        if(position == 0)
            loadItemsFromServer("0",position); //this hsould be starting the service to load the data ..
        else
            loadItemsFromServer(mList.get(position-1).id, position);

    }

    public void loadItemsFromServer(String memberid, int selectedItem) {

        if (progressDialog == null)
            progressDialog = new ProgressDialog(AppManager.getInstance().getCurrentActivity());

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setMessage("loading trackers.");
        progressDialog.show();

        //here we are loading the progress dialog whatever..
        Intent intentDataLoader = new Intent(AppManager.getInstance().getCurrentActivity(), IntentDataLoadService.class);

        intentDataLoader.putExtra("action", "getTracker");
        intentDataLoader.putExtra("memberid", memberid);

        //lets set the blah blah thing...

        AppManager.getInstance().setVariableInPreferences("selected_list_item",selectedItem);

        firstTimeLoad = true;
        AppManager.getInstance().getCurrentActivity().startService(intentDataLoader);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //when item is clicked on list view run this event...

        if(progressDialog == null)
            progressDialog = new ProgressDialog(AppManager.getInstance().getCurrentActivity());

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setMessage("Loading tracker status.");
        progressDialog.show();

        String trackerid = ((TextView) view.findViewById(R.id.trackerid)).getText().toString();

        Intent intentTrackerInfo = new Intent(AppManager.getInstance().getCurrentActivity(),IntentDataLoadService.class);
        intentTrackerInfo.putExtra("action","getTrackerInfo");
        intentTrackerInfo.putExtra("tracker_id",trackerid);
        AppManager.getInstance().getCurrentActivity().startService(intentTrackerInfo);

    }

}
