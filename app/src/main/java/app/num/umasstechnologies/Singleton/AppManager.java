package app.num.umasstechnologies.Singleton;

import android.app.Activity;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Imdad on 4/30/2016.
 */
public class AppManager {

    private static AppManager ourInstance = new AppManager();

    public static AppManager getInstance() {
        return ourInstance;
    }

    private Activity CurrentActivity;

    public void setCurrentActivity(Activity pCurrAct) {
        CurrentActivity = pCurrAct;
    }


    public Activity getCurrentActivity() { return  CurrentActivity; }

    private AppManager() {
    }
}
