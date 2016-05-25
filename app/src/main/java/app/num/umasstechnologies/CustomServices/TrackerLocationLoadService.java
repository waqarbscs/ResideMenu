package app.num.umasstechnologies.CustomServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.num.umasstechnologies.Singleton.AppManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TrackerLocationLoadService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_Success = "app.num.umasstechnologies.CustomServices.action.trackerload.success";
    public static final String ACTION_Fail = "app.num.umasstechnologies.CustomServices.action.trackerload.fail";
    public static final String ACTION_Error = "app.num.umasstechnologies.CustomServices.action.trackerload.error";

    private static final String Tag = "TrackerLocationLoadService";


    public TrackerLocationLoadService() {
        super("TrackerLocationLoadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            if (intent != null) {

                String action = intent.getStringExtra("action");
                if(action.equals("getlocation")) {

                    String tracker_id = intent.getStringExtra("tracker_id");
                    String link = "http://massuae.dyndns.org:8088/tracking_zone/mob_app_srvcs/track/get_engine_on_data/"+ AppManager.Hand_Shake+"/"+tracker_id;
                    String response = getStringResultFromService_POSTForTrackers(link);

                    if( (new JSONObject(response)).getString("engine_on").toString().equals("false") ){
                        Intent intentFail = new Intent(ACTION_Fail);
                        intentFail.putExtra("engine_state","0");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentFail);
                    }
                    else {


                        JSONObject jobject =  (new JSONObject(response)).getJSONArray("engine_on").getJSONObject(0);

                        String data_latitude =  jobject.getString("data_latitude");
                        String data_latitude_n_s =  jobject.getString("data_latitude");
                        String data_longitude =  jobject.getString("data_latitude");
                        String data_longitude_e_w =  jobject.getString("data_latitude");

                        LatLng latlong =  getTheLatitudeLongitude(data_latitude,data_latitude_n_s,data_longitude,data_longitude_e_w);


                        Intent intentSuc = new Intent(ACTION_Success);

                        intentSuc.putExtra("engine_state","1");
                        intentSuc.putExtra("lat",String.valueOf(latlong.latitude));
                        intentSuc.putExtra("lon",String.valueOf(latlong.longitude));

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentSuc);

                    }
                }
            }

        }
        catch (JSONException je) {

        }
        catch (Exception ex) {

        }

    }

    public LatLng getTheLatitudeLongitude(String data_latitude,String data_latitude_n_s,String data_longitude,String data_longitude_e_w) {

        Double twoOfLat = Double.parseDouble( data_latitude.substring(0,2) );
        Double remOfLat = Double.parseDouble( data_latitude.substring(2) );

        Double threeDataLon = Double.parseDouble( data_longitude.substring(0,3) );
        Double remOfLon = Double.parseDouble( data_longitude.substring(3) );

        Double tracker_latitude = 0.0;
        Double tracker_longitude = 0.0;


        if(data_latitude_n_s.toLowerCase().equals("n")){
            tracker_latitude = twoOfLat + (remOfLat/60);
        }
        else if (data_latitude_n_s.toLowerCase().equals("s")){
            tracker_latitude = - (twoOfLat + (remOfLat/60));
        }

        if(data_longitude_e_w.toLowerCase().equals("e")) {
            tracker_longitude = threeDataLon + (remOfLon/60);
        }
        else if(data_longitude_e_w.toLowerCase().equals("w")) {
            tracker_longitude = -(threeDataLon + (remOfLon/60));
        }

        return new LatLng(tracker_latitude,tracker_longitude);

    }


    public String getStringResultFromService_POSTForTrackers(String serviceURL) {

        String resultString = null;
        HttpURLConnection httpURLConnection = null;
        String line = null;
        URL url = null;

        Log.w("URL","You Gave URL: "+serviceURL);

        try {
            url = new URL(serviceURL);
        }catch (MalformedURLException urlException) {
            throw new IllegalArgumentException("URL: "+serviceURL);
        }

        try {

            Log.w("BodyString","url connection opening");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");

            //handling the response
            int requestCode = httpURLConnection.getResponseCode();
            if(requestCode != 200) {
                throw  new IOException("PostFailed: StatusCode="+requestCode);
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()) );

            StringBuilder buffBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine())!= null) {
                buffBuilder.append(line+"\n");
            }


            return buffBuilder.toString();
        }
        catch (Exception ex) {
            Log.w("BodyString","Excption: "+ex.getMessage());
            ex.printStackTrace();
            return  null;
        }

    }



}
