package app.num.umasstechnologies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

import app.num.umasstechnologies.Adapters.lstVehicleAdapter;
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

        return _inflatedView;
    }

    public  void setTestData() {

        vehicleList.add(new Vehicle("Car1","Description of the car."));
        vehicleList.add(new Vehicle("Car2","Description of the car."));
        vehicleList.add(new Vehicle("Car3","Description of the car."));
        vehicleList.add(new Vehicle("Car4","Description of the car."));
        vehicleList.add(new Vehicle("Truck","Description of the Truck."));
        vehicleList.add(new Vehicle("Truck","Description of the Truck."));
        vehicleList.add(new Vehicle("Truck","Description of the Truck."));
        vehicleList.add(new Vehicle("Truck","Description of the Truck."));
        vehicleList.add(new Vehicle("Truck","Description of the Truck."));
        vehicleList.add(new Vehicle("Truck","Description of the Truck."));
        vehicleList.add(new Vehicle("Truck","Description of the Truck."));

    }
}
