package app.num.umasstechnologies.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.num.umasstechnologies.Models.Vehicle;
import app.num.umasstechnologies.R;

/**
 * Created by Imdad on 4/30/2016.
 */
public class lstVehicleAdapter extends BaseAdapter {

    private List<Vehicle> vehicleList;
    private LayoutInflater inflater;
    private Activity _activity;

    public lstVehicleAdapter(Activity pActivity,List<Vehicle> pVehicleList) {
        vehicleList = pVehicleList;
        _activity = pActivity;
    }

    @Override
    public int getCount() {

        if(vehicleList == null)
            return 0;

        return vehicleList.size();
    }

    @Override
    public Object getItem(int position) {

        if(vehicleList == null)
            return null;

        if(position > vehicleList.size())
            return null;

        return vehicleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(inflater == null)
            inflater = _activity.getLayoutInflater();


        if(convertView == null)
            convertView = inflater.inflate(R.layout.lyt_vehicle_row,null,false);

        ((TextView) convertView.findViewById(R.id.txtvName)).setText(vehicleList.get(position).name);
        ((TextView) convertView.findViewById(R.id.txtvDescription)).setText(vehicleList.get(position).description);

        return convertView;
    }
}
