package app.num.umasstechnologies.CustomServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class IntentLoginService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String BroadCastSuccess = "app.num.umasstechnologies.CustomServices.Success.login";
    public static final String BroadCastFail = "app.num.umasstechnologies.CustomServices.Fail.login";
    public static final String BroadCastError = "app.num.umasstechnologies.CustomServices.Error.login";


    private static final String Tag = "IntentLoginService";

    public IntentLoginService() {
        super("IntentLoginService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            String token = intent.getStringExtra("token");
            String deviceid = intent.getStringExtra("deviceid");
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            LoginTheUser(username,password);

        }
    }

    public int SaveLoginDetails(String username, String devicdeid, String token) {
        String link = "http://massuae.dyndns.org:8088/tracking_zone/mob_app_srvcs/login/save_login/"+ AppManager.Hand_Shake+"/"+username+"/"+"/$token/$device_type";

        int codeStatus = -1; // 1 = Error
        String errorMessage = "NA";

        try {
            String result = getStringResultFromService_POST(link);
            if(result == null) {
                //the server didn't return anything..
                codeStatus = 1;
                errorMessage = "server retuned null.";
                return codeStatus;
            }

            result = result.replace("<br>", " ");

            Log.w("Response",result);


            JSONObject jsonObject  = new JSONObject(result);

            if(jsonObject.has("user_data")) {

                String resData = jsonObject.get("user_data").toString();


                if(resData.equals("false")) {

                    Intent failIntent = new Intent(BroadCastFail);
                    LocalBroadcastManager.getInstance(IntentLoginService.this).sendBroadcast(failIntent);

                }
                else if (  jsonObject.has("company_info")) { //it will have atleast one company important

                    //if it has company info mean its one company
                    //else it will have company list
                    //got the user ofcourse...

                    String uData = jsonObject.get("user_data").toString();
                    String ciData = jsonObject.get("company_info").toString();

                    JSONArray userDataArray = new JSONArray(uData);
                    JSONArray userCompInfoArray = new JSONArray(ciData);



                    JSONObject userDataObject = new JSONObject( userDataArray.getString(0).toString() );
                    JSONObject companyInforObject = new JSONObject( userCompInfoArray.getString(0).toString() );

                    user foundUser =  new user();

                    foundUser.setId( Integer.valueOf( userDataObject.get("id").toString() ) );
                    foundUser.setUsername( userDataObject.get("name").toString() );
                    foundUser.setType( Integer.valueOf( userDataObject.get("type").toString() ) );
                    foundUser.setReference( Integer.valueOf( userDataObject.get("ref_company").toString() ) );

                    CompanyInfo cInformation = new CompanyInfo();
                    cInformation.setName(companyInforObject.getString("name"));
                    cInformation.setId(Integer.valueOf(companyInforObject.getString("id")));
                    cInformation.setLogo(companyInforObject.getString("logo"));


                    //first we need to remove all the data of old user .. and than we need to add data
                    //for new user..

                    DatabaseHandler dbhandler = new DatabaseHandler(getApplicationContext());

                    dbhandler.addUser(foundUser); //this will also set user type so we know the stuff..
                    dbhandler.addCompanyInfo(cInformation); //this shall insert company information..

                    //adding information in database...

                    //we also have to delte information on logout.. :O..

                    //chalo bhai database mein fields daldi..
                    AppManager.getInstance().setCompanyInPreferences(cInformation);
                    Intent successIntent = new Intent(BroadCastSuccess);
                    LocalBroadcastManager.getInstance(IntentLoginService.this).sendBroadcast(successIntent);

                }
                else if (jsonObject.has("company_list")) {

                    //we have multiple objects of that whatever
                    String uData = jsonObject.get("user_data").toString();
                    String ciData = jsonObject.get("company_list").toString();

                    JSONArray userDataArray = new JSONArray(uData);
                    JSONArray userCompInfoArray = jsonObject.getJSONArray("company_list");

                    JSONObject userDataObject = new JSONObject( userDataArray.getString(0).toString() );

                    user foundUser =  new user();

                    foundUser.setId( Integer.valueOf( userDataObject.get("id").toString() ) );
                    foundUser.setUsername( userDataObject.get("name").toString() );
                    foundUser.setType( Integer.valueOf( userDataObject.get("type").toString() ) );
                    foundUser.setReference( Integer.valueOf( userDataObject.get("ref_company").toString() ) );

                    int len = userCompInfoArray.length();

                    DatabaseHandler dbhandler = new DatabaseHandler(getApplicationContext());

                    dbhandler.addUser(foundUser); //this will also set user type so we know the stuff..

                    List<CompanyInfo> cInformationList = new ArrayList<>();

                    for(int index = 0; index < len; index++) {
                        JSONObject tempObject = userCompInfoArray.getJSONObject(index);
                        CompanyInfo cInformation = new CompanyInfo();
                        cInformation.setName(tempObject.getString("name"));
                        cInformation.setId(Integer.valueOf(tempObject.getString("id")));
                        // cInformation.setLogo(tempObject.getString("logo"));
                        cInformationList.add(cInformation);
                        dbhandler.addCompanyInfo(cInformation); //this shall insert company information..
                    }

                    //chalo bhai database mein fields daldi..
                    Intent successIntent = new Intent(BroadCastSuccess);
                    LocalBroadcastManager.getInstance(IntentLoginService.this).sendBroadcast(successIntent);
                }

            }
            else {
                Intent errorIntent = new Intent(BroadCastError);
                LocalBroadcastManager.getInstance(IntentLoginService.this).sendBroadcast(errorIntent);
            }

        } catch (Exception ex) {

            ex.printStackTrace();
            Intent dataRecieve = new Intent(BroadCastError);
            LocalBroadcastManager.getInstance(this).sendBroadcast(dataRecieve);

            codeStatus = 1;
            return codeStatus;

        }
        //      catch (JSONException jex) {
//        }

        return codeStatus;

    }


    public int LoginTheUser(String username, String password) {

        String link = "http://massuae.dyndns.org:8088/tracking_zone/mob_app_srvcs/login/index/"+ AppManager.Hand_Shake+"/"+username+"/"+password;

        int codeStatus = -1; // 1 = Error
        String errorMessage = "NA";

        try {
            String result = getStringResultFromService_POST(link);
            if(result == null) {
                //the server didn't return anything..
                codeStatus = 1;
                errorMessage = "server retuned null.";
                Intent failIntent = new Intent(BroadCastFail);
                LocalBroadcastManager.getInstance(IntentLoginService.this).sendBroadcast(failIntent);
                return codeStatus;
            }

            result = result.replace("<br>", " ");

            Log.w("Response",result);


            JSONObject jsonObject  = new JSONObject(result);

            if(jsonObject.has("user_data")) {

                String resData = jsonObject.get("user_data").toString();


                if(resData.equals("false")) {

                    Intent failIntent = new Intent(BroadCastFail);
                    LocalBroadcastManager.getInstance(IntentLoginService.this).sendBroadcast(failIntent);

                }
                else if (  jsonObject.has("company_info")) { //it will have atleast one company important

                    //if it has company info mean its one company
                    //else it will have company list
                    //got the user ofcourse...

                    String uData = jsonObject.get("user_data").toString();
                    String ciData = jsonObject.get("company_info").toString();

                    JSONArray userDataArray = new JSONArray(uData);
                    JSONArray userCompInfoArray = new JSONArray(ciData);



                    JSONObject userDataObject = new JSONObject( userDataArray.getString(0).toString() );
                    JSONObject companyInforObject = new JSONObject( userCompInfoArray.getString(0).toString() );

                    user foundUser =  new user();

                    foundUser.setId( Integer.valueOf( userDataObject.get("id").toString() ) );
                    foundUser.setUsername( userDataObject.get("name").toString() );
                    foundUser.setType( Integer.valueOf( userDataObject.get("type").toString() ) );
                    foundUser.setReference( Integer.valueOf( userDataObject.get("ref_company").toString() ) );

                    CompanyInfo cInformation = new CompanyInfo();
                    cInformation.setName(companyInforObject.getString("name"));
                    cInformation.setId(Integer.valueOf(companyInforObject.getString("id")));
                    cInformation.setLogo(companyInforObject.getString("logo"));


                    //first we need to remove all the data of old user .. and than we need to add data
                    //for new user..

                    DatabaseHandler dbhandler = new DatabaseHandler(getApplicationContext());

                    dbhandler.addUser(foundUser); //this will also set user type so we know the stuff..
                    dbhandler.addCompanyInfo(cInformation); //this shall insert company information..

                    //adding information in database...

                    //we also have to delte information on logout.. :O..

                    //chalo bhai database mein fields daldi..
                    AppManager.getInstance().setCompanyInPreferences(cInformation);
                    Intent successIntent = new Intent(BroadCastSuccess);
                    LocalBroadcastManager.getInstance(IntentLoginService.this).sendBroadcast(successIntent);

                }
                else if (jsonObject.has("company_list")) {

                    //we have multiple objects of that whatever
                    String uData = jsonObject.get("user_data").toString();
                    String ciData = jsonObject.get("company_list").toString();

                    JSONArray userDataArray = new JSONArray(uData);
                    JSONArray userCompInfoArray = jsonObject.getJSONArray("company_list");

                    JSONObject userDataObject = new JSONObject( userDataArray.getString(0).toString() );

                    user foundUser =  new user();

                    foundUser.setId( Integer.valueOf( userDataObject.get("id").toString() ) );
                    foundUser.setUsername( userDataObject.get("name").toString() );
                    foundUser.setType( Integer.valueOf( userDataObject.get("type").toString() ) );
                    foundUser.setReference( Integer.valueOf( userDataObject.get("ref_company").toString() ) );

                    int len = userCompInfoArray.length();

                    DatabaseHandler dbhandler = new DatabaseHandler(getApplicationContext());

                    dbhandler.addUser(foundUser); //this will also set user type so we know the stuff..

                    List<CompanyInfo> cInformationList = new ArrayList<>();

                    for(int index = 0; index < len; index++) {
                        JSONObject tempObject = userCompInfoArray.getJSONObject(index);
                        CompanyInfo cInformation = new CompanyInfo();
                        cInformation.setName(tempObject.getString("name"));
                        cInformation.setId(Integer.valueOf(tempObject.getString("id")));
                       // cInformation.setLogo(tempObject.getString("logo"));
                        cInformationList.add(cInformation);
                        dbhandler.addCompanyInfo(cInformation); //this shall insert company information..
                    }

                    //chalo bhai database mein fields daldi..
                    Intent successIntent = new Intent(BroadCastSuccess);
                    LocalBroadcastManager.getInstance(IntentLoginService.this).sendBroadcast(successIntent);
                }

            }
            else {
                Intent errorIntent = new Intent(BroadCastError);
                LocalBroadcastManager.getInstance(IntentLoginService.this).sendBroadcast(errorIntent);
            }

        } catch (Exception ex) {

            ex.printStackTrace();
            Intent dataRecieve = new Intent(BroadCastError);
            LocalBroadcastManager.getInstance(this).sendBroadcast(dataRecieve);

            codeStatus = 1;
            return codeStatus;

        }
  //      catch (JSONException jex) {
//        }

        return codeStatus;
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

            httpURLConnection.setReadTimeout(2000);

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
