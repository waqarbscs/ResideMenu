package app.num.umasstechnologies.CustomServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.num.umasstechnologies.DatabaseClasses.DatabaseHandler;
import app.num.umasstechnologies.Models.CompanyInfo;
import app.num.umasstechnologies.Models.user;
import app.num.umasstechnologies.Singleton.AppManager;

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
    private static final String Action_Success = "app.num.umasstechnologies.CustomServices.IntentDataLoadService.Success";
    private static final String Action_Fail = "app.num.umasstechnologies.CustomServices.IntentDataLoadService.Fail";
    private static final String Action_Error = "app.num.umasstechnologies.CustomServices.IntentDataLoadService.Error";

    private static final String Tag = "IntentDataLoadService";

    public IntentDataLoadService() {
        super("IntentDataLoadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            if (intent != null) {
                String action = intent.getStringExtra("action");
                if (action.equals("getTracker")) {
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

                        JSONObject mainJson = new JSONObject(response);
                        JSONArray memberslist =  mainJson.getJSONArray("members");
                        JSONArray trackerslist = mainJson.getJSONArray("trackers");

                        //we need to add both of them into database .. but first decode them ..


                        Log.w(Tag, "TrakcerMemberList: " + response);

                        //here once we get the response we will see how it will work..
                        //we have to add members in members table..
                        //and we have to add trackers in trackers table
                        //after emptying both of the tables.. keeping only needed data..

                    }


                } else if (action.equals("sendFeedback")) {

                    //we need to send the feedback
                    String message = intent.getStringExtra("Message");
                    String name = intent.getStringExtra("Name");
                    String phoneNumber = intent.getStringExtra("PhoneNumber");


                }
            }
        }
        catch (JSONException jE) {

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



}
