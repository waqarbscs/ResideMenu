package app.num.umasstechnologies.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Imdad on 4/30/2016.
 */
public class Vehicle {

    //this is basically the tracker i guess..
    public String name;
    public String description;
    public int Type;
    public LatLng position;
    public String address;
    public String owner;
    public boolean CheckEngineLightStatus;
    public boolean EngineStatus;

    public Vehicle () {}
    public Vehicle(String pName, String pDescription) {
        name= pName;
        description = pDescription;
    }

}
