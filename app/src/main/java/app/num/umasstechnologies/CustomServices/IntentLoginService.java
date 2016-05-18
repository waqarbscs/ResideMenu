package app.num.umasstechnologies.CustomServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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

import app.num.umasstechnologies.Singleton.AppManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentLoginService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String BroadCastSuccess = "app.num.umasstechnologies.CustomServices.Success.login";
    public static final String BroadCastError = "app.num.umasstechnologies.CustomServices.Fail.login";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "app.num.umasstechnologies.CustomServices.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "app.num.umasstechnologies.CustomServices.extra.PARAM2";

    private static final String Tag = "IntentLoginService";

    public IntentLoginService() {
        super("IntentLoginService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();

            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            LoginTheUser(username,password);

        }
    }


    public void LoginTheUser(String username, String password) {

        String link = "http://massuae.dyndns.org:8088/tracking_zone/mob_app_srvcs/login/index/"+ AppManager.Hand_Shake+"/"+username+"/"+password;

        try {

            String result = getStringResultFromService_POST("http://onewindowsol.com/NimazTime/getMasjidDetail.php");
            Log.w("Response",result);

            JSONObject jsonObject = new JSONObject(result);

            //add things to object.
            //add object to database.
            //send the broadcast, that you have updated the database.

            Intent dataRecieve = new Intent(BroadCastSuccess);
            dataRecieve.putExtra("Message","Updated Database.");
            LocalBroadcastManager.getInstance(this).sendBroadcast(dataRecieve);


        } catch (Exception ex) {

            ex.printStackTrace();
            Intent dataRecieve = new Intent(BroadCastError);
            LocalBroadcastManager.getInstance(this).sendBroadcast(dataRecieve);

        }
    }


    public String getStringResultFromService_POST(String serviceURL) {

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

        try {

            Log.w("BodyString","url connection opening");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            //httpURLConnection.setFixedLengthStreamingMode(bytes.length);

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
