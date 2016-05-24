package app.num.umasstechnologies.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Imdad on 4/30/2016.
 */
public class Vehicle {

    public String id;
    public String deviceid;
    public String trackerName;
    public String trackerGenStatus;
    public String trackerGenColor;
    public String last_move;
    public String last_status;
    public String last_gprs;
    public String engineStatus;

    public Vehicle(String pDeviceId, String pTrackerName, String pEngineStatus, String pTrackerColor) {

        deviceid = pDeviceId;
        trackerName = pTrackerName;
        engineStatus = pEngineStatus;
        trackerGenColor = pTrackerColor;

    }


}
