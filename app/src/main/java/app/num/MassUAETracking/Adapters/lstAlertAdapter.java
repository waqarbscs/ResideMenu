package app.num.MassUAETracking.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.num.MassUAETracking.Models.Alert;
import app.num.MassUAETracking.R;

/**
 * Created by Imdad on 4/30/2016.
 */
public class lstAlertAdapter extends BaseAdapter {

    private List<Alert> alertList;
    private LayoutInflater inflater;
    private Activity _activity;

    public lstAlertAdapter(Activity pActivity,List<Alert> pAlertList) {
        alertList = pAlertList;
        _activity = pActivity;
    }

    @Override
    public int getCount() {

        if(alertList == null)
            return 0;

        return alertList.size();
    }

    @Override
    public Object getItem(int position) {

        if(alertList == null)
            return null;

        if(position > alertList.size())
            return null;

        return alertList.get(position);
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
            convertView = inflater.inflate(R.layout.lyt_alerts_row,null,false);

        ((TextView) convertView.findViewById(R.id.txtvTitle)).setText(alertList.get(position).title);
        ((TextView) convertView.findViewById(R.id.txtvDescription)).setText(alertList.get(position).description);

        return convertView;
    }
}
