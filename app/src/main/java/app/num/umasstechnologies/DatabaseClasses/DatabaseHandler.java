package app.num.umasstechnologies.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

import app.num.umasstechnologies.Models.CompanyInfo;
import app.num.umasstechnologies.Models.Members;
import app.num.umasstechnologies.Models.Vehicle;
import app.num.umasstechnologies.Models.user;

/**
 * Created by Imdad on 5/18/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "masstech_dataholder";

    private static final String TABLE_USER = "tbl_user";
    private static final String TABLE_COMPANY = "tbl_company";
    private static final String TABLE_MEMBER = "tbl_member";
    private static final String TABLE_TRACKER = "tbl_tracker";

    private static final String tbluser_USERNAME = "username";
    private static final String tbluser_PASSWORD = "upassword";
    private static final String tbluser_USERID = "userid";
    private static final String tbluser_REFERENCE = "reference";
    private static final String tbluser_TYPE = "type";

    private static final String tblcompany_NAME = "companyname";
    private static final String tblcompany_ID = "companyid";
    private static final String ttblcompany_LOGO = "companylogo";


    private static final String tblmember_name = "memberName";
    private static final String tblmember_username = "memberUsername";
    private static final String tblmember_id = "userid";

    private static final String tbltracker_deviceid = "deviceid";
    private static final String tbltracker_color = "color";
    private static final String tbltracker_id = "id";
    private static final String tbltracker_engineStatus = "engineStatus";
    private static final String tbltracker_name = "name";

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_MEMBER_TABLE = " CREATE TABLE "+TABLE_MEMBER + "( "+tblmember_id+" TEXT, "
                +tblmember_name+" TEXT, "+tblmember_username+" TEXT )";

        db.execSQL(CREATE_MEMBER_TABLE);

        String CREATE_TRACKER_TABLE = " CREATE TABLE "+TABLE_TRACKER + "( "+tbltracker_id+" TEXT, "+
                tbltracker_name+" TEXT, "+tbltracker_color+" TEXT, "+tbltracker_deviceid+" TEXT, "+
                tbltracker_engineStatus+" TEXT )";

        db.execSQL(CREATE_TRACKER_TABLE);

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + tbluser_USERID + " INTEGER PRIMARY KEY," + tbluser_USERNAME + " TEXT,"+ tbluser_PASSWORD + " TEXT,"
                + tbluser_REFERENCE + " INTEGER,"
                + tbluser_TYPE + " INTEGER" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_COMPANY_TABLE = "CREATE TABLE " + TABLE_COMPANY + "( "+
                tblcompany_ID+" INTEGER PRIMARY KEY, "+tblcompany_NAME+" TEXT, "+ ttblcompany_LOGO+" TEXT  )";

        db.execSQL(CREATE_COMPANY_TABLE);
        //we have table for both of them..
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANY);

        // Create tables again
        onCreate(db);
    }

    // these are the methods to insert the users, contact information and other things in database..
    public void addUser(user pUser) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tbluser_USERNAME, pUser.getUsername()); // Contact Phone
        values.put(tbluser_USERID,pUser.getId()); // Contact Name
        values.put(tbluser_REFERENCE, pUser.getReference()); // Contact Phone
        values.put(tbluser_TYPE,pUser.gettype());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public void addMember(Members pMember) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tblmember_id, pMember.id); // Contact Phone
        values.put(tblmember_name,pMember.name); // Contact Name
        values.put(tblmember_username, pMember.username); // Contact Phone

        // Inserting Row
        db.insert(TABLE_MEMBER, null, values);
        db.close(); // Closing database connection
    }

    public void addTracker(Vehicle pVehicle) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tbltracker_color, pVehicle.trackerGenColor); // Contact Phone
        values.put(tbltracker_deviceid,pVehicle.deviceid); // Contact Name
        values.put(tbltracker_engineStatus, pVehicle.engineStatus); // Contact Phone
        values.put(tbltracker_id,pVehicle.id);
        values.put(tbltracker_name,pVehicle.trackerName);

        // Inserting Row
        db.insert(TABLE_TRACKER, null, values);
        db.close(); // Closing database connection
    }


    public void addCompanyInfo(CompanyInfo cInfo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tblcompany_ID, cInfo.getId()); // Contact Name
        values.put(tblcompany_NAME, cInfo.getName()); // Contact Phone
        values.put(ttblcompany_LOGO,cInfo.getLogo()); // Contact Name

        // Inserting Row
        db.insert(TABLE_COMPANY, null, values);
        db.close(); // Closing database connection

    }


    //here we have all the get methods

    public user getUser() {

        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = {};
        Cursor cursor = db.rawQuery("select "+tbluser_USERID+", "+tbluser_REFERENCE+", "+tbluser_TYPE+", "+tbluser_USERNAME+" from "+TABLE_USER, args);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        if(cursor.getCount() <= 0)
            return null;

        user currentUser = new user();
        currentUser.setId( Integer.valueOf( cursor.getString(0) ) );
        currentUser.setReference( Integer.valueOf( cursor.getString(1) ) );
        currentUser.setType( Integer.valueOf( cursor.getString(2) ) );
        currentUser.setUsername( cursor.getString(3) );

        db.close();


        return currentUser;
    }

    //will be used for the user who will only have one company.. or say this is the selected company actually..
    public CompanyInfo getCompany() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = {};
        Cursor cursor = db.rawQuery("select "+tblcompany_NAME+", "+tblcompany_ID+", "+ttblcompany_LOGO+" from "+TABLE_COMPANY, args);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        CompanyInfo currentCompany = new CompanyInfo();
        currentCompany.setName( cursor.getString(0)  );
        currentCompany.setId( Integer.valueOf( cursor.getString(1) ) );
        currentCompany.setLogo(  cursor.getString(2)  );

        db.close();

        return currentCompany;
    }

    public List<Vehicle> getListOfTrackers() {

        List<Vehicle> listTrackers = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = {};
        Cursor cursor = db.rawQuery("select "+tbltracker_id+", "+tbltracker_deviceid+", "+tbltracker_name+", "
                +tbltracker_engineStatus+", "+tbltracker_color+" from "+TABLE_TRACKER, args);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        int len = cursor.getCount();
        int index = 0;

        while (index < len) {
            Vehicle tempV = new Vehicle(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
            tempV.id = cursor.getString(0);
            listTrackers.add(tempV);
            index++;
        }

        return listTrackers;
    }

    public List<Members> getListOfMembers() {
        List<Members> listMembers = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = {};
        Cursor cursor = db.rawQuery("select "+tblmember_username+", "
                +tblmember_name+", "+tblmember_id+" from "+TABLE_MEMBER, args);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        int len = cursor.getCount();
        int index = 0;

        while (index < len) {

            Members tempM = new Members();
            tempM.username = cursor.getString(0);
            tempM.name = cursor.getString(1);
            tempM.id = cursor.getString(2);
            listMembers.add(tempM);
            index++;

        }

        return listMembers;
    }

    public CompanyInfo[] getArrayOfCompanies() {

        int numberOfCompanies = countCompanys();
        CompanyInfo[] companiesArray = new CompanyInfo[numberOfCompanies];

        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = {};
        Cursor cursor = db.rawQuery("select "+tblcompany_NAME+", "+tblcompany_ID+", "+ttblcompany_LOGO+" from "+TABLE_COMPANY, args);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        int index = 0;

        int lent = cursor.getCount();

        while(cursor.getCount() > index) {

            cursor.moveToPosition(index);
            CompanyInfo currentCompany = new CompanyInfo();
            currentCompany.setName(cursor.getString(0));
            currentCompany.setId(Integer.valueOf(cursor.getString(1)));
            currentCompany.setLogo(cursor.getString(2));

            companiesArray[index] = currentCompany;

            index++;
        }

        db.close();

        return companiesArray;

    }

    public int countCompanys() {

        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = {};
        Cursor cursor = db.rawQuery("select count(*) from "+TABLE_COMPANY, args);

        if (cursor != null)
            cursor.moveToFirst();
        else
            return -1;

        db.close();
        return Integer.valueOf( cursor.getString(0) );
    }

    public void deleteAllInformation() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from "+TABLE_COMPANY);
        db.execSQL("delete from "+TABLE_USER);
        db.execSQL("delete from "+TABLE_MEMBER);
        db.execSQL("delete from "+TABLE_TRACKER);

        db.close();
    }


}
