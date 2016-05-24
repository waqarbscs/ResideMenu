package app.num.umasstechnologies.Singleton;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.SupportMapFragment;
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
