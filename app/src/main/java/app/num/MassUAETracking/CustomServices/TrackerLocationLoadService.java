package app.num.MassUAETracking.CustomServices;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.num.MassUAETracking.Singleton.AppManager;

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

                        if(jobject.toString().contains("false")) {}
                        else {

                            JSONArray jsonArray = new JSONObject(response).getJSONArray("engine_on");

                            int len = jsonArray.length();

                            double[] lats = new double[len];
                            double[] lons = new double[len];

                            for(int index = 0; index < len ; index++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(index);

                                String data_latitude = jsonObject.getString("data_latitude");
                                String data_latitude_n_s = jsonObject.getString("data_latitude_n_s");
                                String data_longitude = jsonObject.getString("data_longitude");
                                String data_longitude_e_w = jsonObject.getString("data_longitude_e_w");
                                LatLng latlong = AppManager.getInstance().getTheLatitudeLongitude(data_latitude, data_latitude_n_s, data_longitude, data_longitude_e_w);

                                lats[index] = latlong.latitude;
                                lons[index] = latlong.longitude;

                            }


                            Intent intentSuc = new Intent(ACTION_Success);

                            intentSuc.putExtra("latitude", lats);
                            intentSuc.putExtra("longitude", lons);

                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentSuc);
                        }
                    }
                }
            }

        }
        catch (JSONException je) {
            Intent intentE = new Intent(ACTION_Error);
            intentE.putExtra("Message","json execption server may have changed the data formate.");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentE);
        }
        catch (Exception ex) {
            Intent intentE = new Intent(ACTION_Error);
            intentE.putExtra("Message","Exception: "+ex.getMessage());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentE);
        }

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
