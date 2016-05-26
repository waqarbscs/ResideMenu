package app.num.umasstechnologies.Singleton;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import app.num.umasstechnologies.Models.CompanyInfo;

/**
 * Created by Imdad on 4/30/2016.
 */
public class AppManager {

    public static final String Hand_Shake = "M@ssu@e";
    private static AppManager ourInstance = new AppManager();

    public static AppManager getInstance() {
        return ourInstance;
    }

    private Activity CurrentActivity;

    public void setCompanyInPreferences(CompanyInfo cInfo) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getCurrentActivity().getApplicationContext());
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cInfo);
        editor.putString("company",json);
        editor.commit();

    }

    public void removeCompany() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getCurrentActivity().getApplicationContext());
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.remove("company");
        editor.commit();
    }

    public CompanyInfo getCurrentCompany() {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getCurrentActivity().getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("company", "");

        if(json.equals(""))
            return null;

        CompanyInfo mCompanies = gson.fromJson(json, CompanyInfo.class);

        return mCompanies;
    }

    public LatLng getTheLatitudeLongitude(String data_latitude, String data_latitude_n_s, String data_longitude, String data_longitude_e_w) {

        Double twoOfLat = Double.parseDouble( data_latitude.substring(0,2) );
        Double remOfLat = Double.parseDouble( data_latitude.substring(2) );

        Double threeDataLon = Double.parseDouble( data_longitude.substring(0,3) );
        Double remOfLon = Double.parseDouble( data_longitude.substring(3) );

        Double tracker_latitude = 0.0;
        Double tracker_longitude = 0.0;


        if(data_latitude_n_s.toLowerCase().equals("n")){
            tracker_latitude = twoOfLat + (remOfLat/60.0);
        }
        else if (data_latitude_n_s.toLowerCase().equals("s")){
            tracker_latitude = - (twoOfLat + (remOfLat/60.0));
        }

        if(data_longitude_e_w.toLowerCase().equals("e")) {
            tracker_longitude = threeDataLon + (remOfLon/60);
        }
        else if(data_longitude_e_w.toLowerCase().equals("w")) {
            tracker_longitude = -(threeDataLon + (remOfLon/60));
        }

        return new LatLng(tracker_latitude,tracker_longitude);

    }





    public void setCurrentActivity(Activity pCurrAct) {
        CurrentActivity = pCurrAct;
    }


    public Activity getCurrentActivity() { return  CurrentActivity; }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)  CurrentActivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private AppManager() {
    }
}
