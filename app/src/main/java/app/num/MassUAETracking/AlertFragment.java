package app.num.MassUAETracking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.num.MassUAETracking.Adapters.lstAlertAdapter;
import app.num.MassUAETracking.Models.Alert;
import app.num.MassUAETracking.Singleton.AppManager;

/**
 * Created by Imdad on 4/30/2016.
 */
public class AlertFragment extends Fragment {

    private View _inflatedView = null;
    private ListView listViewAlert;

    private lstAlertAdapter alertAdapter;
    private List<Alert> alertList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _inflatedView = inflater.inflate(R.layout.lyt_alerts,null,false);

        //we got the reference to our listView
        listViewAlert = (ListView) _inflatedView.findViewById(R.id.lstViewAlerts);
        alertList = new ArrayList<>();

        setTestData();

        alertAdapter = new lstAlertAdapter(AppManager.getInstance().getCurrentActivity(),alertList);

        listViewAlert.setAdapter(alertAdapter);

        return _inflatedView;
    }

    public  void setTestData() {

        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));

        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));

        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));

        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));
        alertList.add(new Alert("Alert","Description of the Alert.",""));

    }
}
