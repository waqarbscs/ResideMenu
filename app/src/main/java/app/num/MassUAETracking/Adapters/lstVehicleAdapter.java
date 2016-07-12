package app.num.MassUAETracking.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.num.MassUAETracking.Models.Vehicle;
import app.num.MassUAETracking.R;

/**
 * Created by Imdad on 4/30/2016.
 */
public class lstVehicleAdapter extends BaseAdapter {

    private List<Vehicle> vehicleList;
    private LayoutInflater inflater;
    private Activity _activity;
    private int _resId;

    public lstVehicleAdapter(Activity pActivity,List<Vehicle> pVehicleList,int resId) {
        vehicleList = pVehicleList;
        _activity = pActivity;
        _resId=resId;
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

        ((TextView) convertView.findViewById(R.id.txtvTrackerName)).setText(vehicleList.get(position).trackerName);
        ((TextView) convertView.findViewById(R.id.txtvDeviceId)).setText(vehicleList.get(position).deviceid);

        ((TextView) convertView.findViewById(R.id.trackerid)).setText(vehicleList.get(position).id);

        //((ImageView) convertView.findViewById(R.id.imvEngineStatus)).setBackgroundColor((vehicleList.get(position).engineStatus.equals("0")? Color.RED:Color.GREEN));
        //changes
        //((ImageView) convertView.findViewById(R.id.imvEngineStatus)).setBackgroundColor((vehicleList.get(position).engineStatus.equals("0")? Color.RED:Color.GREEN));
        ImageView i= ((ImageView) convertView.findViewById(R.id.imvEngineStatus));
        if((vehicleList.get(position).engineStatus.equals("0")))
            i.setImageResource(R.drawable.engoff);
        else
            i.setImageResource(R.drawable.engon);

        //((ImageView) convertView.findViewById(R.id.imv_iconcolor)).setBackgroundColor( Color.parseColor (vehicleList.get(position).trackerGenColor));
        ((ImageView) convertView.findViewById(R.id.imv_iconcolor)).setBackgroundResource(_resId);
        return convertView;
    }
}
