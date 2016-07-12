package app.num.MassUAETracking.Models;

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
    public String tracker_icon;

    public Vehicle(String pDeviceId, String pTrackerName, String pEngineStatus, String pTrackerColor)
                   /*String pLastMove,String pLastStatus,String pLastGprs,String pTrackerGeneralStatus,
                    String ptrackerIcon)*/ {

        deviceid = pDeviceId;
        trackerName = pTrackerName;
        engineStatus = pEngineStatus;
        trackerGenColor = pTrackerColor;
        /*
        last_move=pLastMove;
        last_gprs=pLastGprs;
        last_status=pLastStatus;
        trackerGenStatus=pTrackerGeneralStatus;
        tracker_icon=ptrackerIcon;
        */
    }

    public Vehicle() {}


}
