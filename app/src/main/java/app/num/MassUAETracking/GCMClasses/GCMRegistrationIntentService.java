package app.num.MassUAETracking.GCMClasses;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

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

import app.num.MassUAETracking.R;

/**
 * Created by Imdad on 5/2/2016.
 */
public class GCMRegistrationIntentService extends IntentService {

    public static final String REGISTRATION_SUCCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    public static final String TAG = "GCM";


    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        registerGCM();
    }

    public void registerGCM() {

        SharedPreferences sharedPreferences = getSharedPreferences("GCM", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent registrationComplete = null;
        String token = null;

        try{
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            Log.w("GCMRegIntentService","Token: "+token);
            registrationComplete = new Intent(REGISTRATION_SUCCCESS);
            registrationComplete.putExtra("Token",token);

            editor.remove(TAG);
            editor.commit();

            String oldToken = sharedPreferences.getString(TAG,"");
            Log.w("GCMRegIntentService","OldToken: "+oldToken);

            if(!"".equals(token) && !oldToken.equals(token)) {
                SaveTokenToServer(token);
                editor.putString(TAG,token);
                editor.commit();
            }

        }
        catch (Exception ex) {
            Log.w("","Token:"+token);
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    public void SaveTokenToServer(String token) {
        Log.w("Start","Starting to save");
        Map paramPost = new HashMap();
        paramPost.put("action","add");
        paramPost.put("token",token);

        try {

            String result = getStringResultFromService_POST("http://onewindowsol.com/test/gcmtoken.php",paramPost);
            Log.w("Response",result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getStringResultFromService_POST(String serviceURL, Map<String,String> params) {
        String resultString = null;
        HttpURLConnection httpURLConnection = null;
        String line = null;
        URL url = null;

        try {
            url = new URL(serviceURL);
        }catch (MalformedURLException urlException) {
            throw new IllegalArgumentException("URL: "+serviceURL);
        }

        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String,String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append("=").append(URLEncoder.encode( param.getValue() ) );

            if(iterator.hasNext()){
                bodyBuilder.append("&");
            }
        }

        String body = bodyBuilder.toString();
        Log.w("BodyString","BodyString: "+body);
        byte[] bytes = body.getBytes();

        try {

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            httpURLConnection.setFixedLengthStreamingMode(bytes.length);
            Log.w("BodyString","Len: "+bytes.length);

            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();

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
