package app.num.MassUAETracking.CustomServices;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import app.num.MassUAETracking.DatabaseClasses.DatabaseHandler;
import app.num.MassUAETracking.Models.CompanyInfo;
import app.num.MassUAETracking.Models.Members;
import app.num.MassUAETracking.Models.Vehicle;
import app.num.MassUAETracking.Models.user;
import app.num.MassUAETracking.Singleton.AppManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentDataLoadService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String Action_Success = "app.num.umasstechnologies.CustomServices.IntentDataLoadService.Success";
    public static final String Action_Fail = "app.num.umasstechnologies.CustomServices.IntentDataLoadService.Fail";
    public static final String Action_Error = "app.num.umasstechnologies.CustomServices.IntentDataLoadService.Error";
    public static final String Action_TrackerInfo = "app.num.umasstechnologies.CustomServices.IntentDataLoadService.TrackerInfo";


    public static final String Action_Email_Sent_Successfully = "app.num.umasstechnologies.CustomServices.IntentDataLoadService.EmailSent.Success";
    public static final String Action_Email_Sent_Error = "app.num.umasstechnologies.CustomServices.IntentDataLoadService.EmailSent.Error";

    private String UserName;
    private static final String Tag = "IntentDataLoadService";

    public IntentDataLoadService() {
        super("IntentDataLoadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            if (intent != null) {
                String action = intent.getStringExtra("action");

                if (action.equals("getTrackerInfo")){

                    String trackerId = intent.getStringExtra("tracker_id");
                    String link = "http://massuae.dyndns.org:8088/tracking_zone/mob_app_srvcs/track/get_tracker_info/" + AppManager.Hand_Shake + "/" +trackerId;
                    String response = getStringResultFromService_POSTForTrackers(link);

                    if(response == null) {
                        Intent errorbroadcast = new Intent(Action_Error);
                        errorbroadcast.putExtra("message","Check Your Internet Connection.");
                        AppManager.getInstance().setVariableInPreferences("missed_event",51); //innternet connection problem
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(errorbroadcast);
                    }

                    else if( (new JSONObject(response)).getString("tracker_info").toString().equals("false") ){
                        Intent intentEStatus = new Intent(Action_TrackerInfo);
                        intentEStatus.putExtra("engine_status", "-1");
                        intentEStatus.putExtra("tracker_id",trackerId);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentEStatus);
                    }
                    else {

                        JSONObject jsonObject = (new JSONObject(response)).getJSONArray("tracker_info").getJSONObject(0);

                        String engineStatus = jsonObject.getString("engine_status");
                        String data_latitude = jsonObject.getString("data_latitude");
                        String data_latitude_n_s = jsonObject.getString("data_latitude_n_s");
                        String data_longitude = jsonObject.getString("data_longitude");
                        String data_longitude_e_w = jsonObject.getString("data_longitude_e_w");

                        LatLng latlong =  AppManager.getInstance().getTheLatitudeLongitude(data_latitude,data_latitude_n_s,data_longitude,data_longitude_e_w);

                        Intent intentEStatus = new Intent(Action_TrackerInfo);

                        intentEStatus.putExtra("engine_status", engineStatus);
                        intentEStatus.putExtra("tracker_id",trackerId);
                        intentEStatus.putExtra("latitude",latlong.latitude);
                        intentEStatus.putExtra("longitude",latlong.longitude);

                        intentEStatus.putExtra("last_gprs",String.valueOf(jsonObject.getString("last_gprs")));
                        intentEStatus.putExtra("last_signal",String.valueOf(jsonObject.getString("last_signal")));
                        intentEStatus.putExtra("last_move",String.valueOf(jsonObject.getString("last_move")));
                        intentEStatus.putExtra("last_engine_on",String.valueOf(jsonObject.getString("last_engine_on")));
                        intentEStatus.putExtra("last_engine_off",String.valueOf(jsonObject.getString("last_engine_off")));

                        //intentEStatus.putExtra("device_id",String.valueOf(jsonObject.getString("id")));
                        intentEStatus.putExtra("device_id",String.valueOf(jsonObject.getString("device_id")));
                        intentEStatus.putExtra("name",String.valueOf(jsonObject.getString("tracker_name")));
                        intentEStatus.putExtra("mileage",String.valueOf(jsonObject.getString("tracker_mileage_total")));
                        intentEStatus.putExtra("mileage_type",String.valueOf(jsonObject.getString("tracker_mileage_type")));
                        intentEStatus.putExtra("username",String.valueOf(jsonObject.getString("user_name")));


                        //Changes by waqar
                        intentEStatus.putExtra("gps",String.valueOf(jsonObject.getString("gps_signal_level")));
                        intentEStatus.putExtra("gsm",String.valueOf(jsonObject.getString("gsm_signal_level")));
                        intentEStatus.putExtra("battery",String.valueOf(jsonObject.getString("voltage_charge_value")));

                        intentEStatus.putExtra("tracker_general_status",String.valueOf(jsonObject.getString("tracker_general_status")));
                        intentEStatus.putExtra("tracker_general_color",String.valueOf(jsonObject.getString("tracker_general_color")));
                        intentEStatus.putExtra("last_signal",String.valueOf(jsonObject.getString("last_signal")));
                        intentEStatus.putExtra("last_gprs",String.valueOf(jsonObject.getString("last_gprs")));
                        intentEStatus.putExtra("last_move",String.valueOf(jsonObject.getString("last_move")));
                        intentEStatus.putExtra("tracker_icon",String.valueOf(jsonObject.getString("tracker_icon")));


                        String[] inputOutput = jsonObject.getString("inputs_outputs").split("|");

                        intentEStatus.putExtra("output_bit",String.valueOf(inputOutput[6]));

                        intentEStatus.putExtra("input_bit_1",String.valueOf(inputOutput[1]));
                        intentEStatus.putExtra("input_bit_2",String.valueOf(inputOutput[2]));
                        intentEStatus.putExtra("input_bit_3",String.valueOf(inputOutput[3]));
                        intentEStatus.putExtra("input_bit_4",String.valueOf(inputOutput[4]));

                        intentEStatus.putExtra("speed",String.valueOf(jsonObject.getString("speed_value")));

                        AppManager.getInstance().setVariableInPreferences("missed_event",31); //setting that you have missed it
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentEStatus);
                    }
                }
                else if (action.equals("sendfeedback")) {

                    String name = intent.getStringExtra("name");
                    String email =  intent.getStringExtra("email");
                    String contact = intent.getStringExtra("contact");
                    String comment = intent.getStringExtra("comment");

                    String url = "http://massuae.dyndns.org:8088/tracking_zone/mob_app_srvcs/track/feedback/";

                    Map params = new HashMap();
                    params.put("hand_shake",AppManager.Hand_Shake);
                    params.put("Name",name);
                    params.put("contact_no", contact);
                    params.put("email",email);
                    params.put("comment",comment);
                    params.put("username","admin");
                    params.put("password","M@ss@dmin");

                    String result = getStringResultFromService_POSTVariables(url,params);
                    Log.w("Result",result);

                    if(result == null || result.contains("false")) {
                        Intent errorbroadcast = new Intent(Action_Email_Sent_Successfully);
                        errorbroadcast.putExtra("message","Server Error while sending email.");
                        AppManager.getInstance().setVariableInPreferences("missed_event",21); //setting that you have missed it
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(errorbroadcast);
                    }
                    else if(result.contains("true")) {

                        Intent errorbroadcast = new Intent(Action_Email_Sent_Successfully);
                        errorbroadcast.putExtra("message","Email has been sent successfully.");
                        AppManager.getInstance().setVariableInPreferences("missed_event",21); //setting that you have missed it
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(errorbroadcast);

                    }

                }
                else if (action.equals("getTracker")) {
                    //we need to load the bluddy trackers..

                    String memberId = intent.getStringExtra("memberid");

                    //get the user
                    //get the company
                    DatabaseHandler dbhandler = new DatabaseHandler(getApplicationContext());
                    user currentUser = dbhandler.getUser();

                    CompanyInfo companyInfo = AppManager.getInstance().getCurrentCompany();

                    Intent cIntent = new Intent(Action_Error);

                    if (currentUser == null || companyInfo == null) {
                        cIntent.putExtra("Message", "User or Company is null.");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(cIntent);
                    } else {
                        int selectedComp = 0;
                        if ((currentUser.gettype() == 2 || currentUser.gettype() == 1) && currentUser.getReference() == 0) {
                            selectedComp = companyInfo.getId();
                        }

                        String link = "http://massuae.dyndns.org:8088/tracking_zone/mob_app_srvcs/track/index/" + AppManager.Hand_Shake + "/" + currentUser.getId() + "/" + currentUser.getReference() + "/" + selectedComp + "/" + currentUser.gettype() + "/" + memberId;
                        String response = getStringResultFromService_POSTForTrackers(link);


                        DatabaseHandler db = new DatabaseHandler(AppManager.getInstance().getCurrentActivity());
                        JSONObject mainJson = new JSONObject(response);
                        JSONArray memberslist =  mainJson.getJSONArray("members");


                        dbhandler.deleteTable(DatabaseHandler.TABLE_MEMBER);
                        dbhandler.deleteTable(DatabaseHandler.TABLE_TRACKER);


                        if(mainJson.getString("trackers").toString().equals("false")) {

                        }
                        else {
                            JSONArray trackerslist = mainJson.getJSONArray("trackers");
                            int trackerlen = trackerslist.length();

                            for (int index = 0; index < trackerlen; index++) {

                                JSONObject tempObject = trackerslist.getJSONObject(index);
                                Vehicle vehicle = new Vehicle();

                                vehicle.id = tempObject.getString("id");
                                vehicle.deviceid = tempObject.getString("device_id");
                                vehicle.trackerGenColor = tempObject.getString("tracker_general_color");
                                vehicle.trackerName = tempObject.getString("tracker_name");
                                vehicle.engineStatus = tempObject.getString("engine_status");
                                vehicle.trackerGenStatus=tempObject.getString("tracker_general_status");
                                vehicle.last_status=tempObject.getString("last_signal");
                                vehicle.last_gprs=tempObject.getString("last_gprs");
                                vehicle.last_move=tempObject.getString("last_move");
                                vehicle.tracker_icon=tempObject.getString("tracker_icon");

                                db.addTracker(vehicle);
                            }
                        }
                        //we need to add both of them into database .. but first decode them ..

                        int memberslen = memberslist.length();



                        for (int index = 0; index < memberslen; index++) {

                            JSONObject tempObject = memberslist.getJSONObject(index);
                            Members members = new Members();

                            members.id = tempObject.getString("id");
                            members.username = tempObject.getString("user_name");
                            members.name = tempObject.getString("name");
                            db.addMember(members);

                        }

                        Intent errorbroadcast = new Intent(Action_Success);
                        AppManager.getInstance().setVariableInPreferences("missed_event",11); //setting that you have missed it
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(errorbroadcast);

                    }


                }


            }
        }
        catch (JSONException jE) {
            Intent errorbroadcast = new Intent(Action_Error);
            errorbroadcast.putExtra("message",jE.getMessage());

            AppManager.getInstance().setVariableInPreferences("missed_event",12); //setting that you have missed it
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(errorbroadcast);
        }
        catch (Exception ex) {
            Intent errorbroadcast = new Intent(Action_Error);
            errorbroadcast.putExtra("message",ex.getMessage());

            AppManager.getInstance().setVariableInPreferences("missed_event",12); //setting that you have missed it
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(errorbroadcast);
        }
    }

    //we have the url.. we need to get the data from here
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

            httpURLConnection.setReadTimeout(3000);

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

            Log.w(Tag,"BuffBuilder: "+buffBuilder.toString());

            return buffBuilder.toString();
        }
        catch (Exception ex) {
            Log.w("BodyString","Excption: "+ex.getMessage());
            ex.printStackTrace();
            return  null;
        }

    }

    public String getStringResultFromService_POSTVariables(String serviceURL, Map<String,String> params) {

        String resultString = null;
        HttpURLConnection httpURLConnection = null;
        String line = null;
        URL url = null;


        Log.w("URL",serviceURL);

        try {
            url = new URL(serviceURL);
        }catch (MalformedURLException urlException) {
            throw new IllegalArgumentException("URL: "+serviceURL);
        }

        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String,String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append("=").append( URLEncoder.encode( String.valueOf( param.getValue()) ) );

            if(iterator.hasNext()){
                bodyBuilder.append("&");
            }
        }

        String body = bodyBuilder.toString();
        Log.w("BodyString","BodyString: "+body);
        byte[] bytes = body.getBytes();

        try {
            Log.w("BodyString","url connection opening");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            httpURLConnection.setFixedLengthStreamingMode(bytes.length);

            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();

            httpURLConnection.setReadTimeout(3000);

            //handling the response
            int requestCode = httpURLConnection.getResponseCode();

            if(requestCode != 200) {
                throw  new IOException("PostFailed: StatusCode="+requestCode);
            }

            Log.w(Tag,"Request Code: "+requestCode);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()) );

            StringBuilder buffBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine())!= null) {
                buffBuilder.append(line+"\n");
            }

            Log.w(Tag,"BuffBuilder: "+buffBuilder.toString());

            return buffBuilder.toString();
        }
        catch (Exception ex) {
            Log.w("BodyString","Excption: "+ex.getMessage());
            ex.printStackTrace();
            return  null;
        }

    }


}
