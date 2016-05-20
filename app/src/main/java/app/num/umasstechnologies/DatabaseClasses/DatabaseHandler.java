package app.num.umasstechnologies.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.num.umasstechnologies.Models.user;

/**
 * Created by Imdad on 5/18/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "masstech_dataholder";
    private static final String TABLE_USER = "tbl_user";

    private static final String tbluser_USERNAME = "username";
    private static final String tbluser_PASSWORD = "username";
    private static final String tbluser_USERID = "userid";
    private static final String tbluser_REFERENCE = "reference";
    private static final String tbluser_TYPE = "type";
    private static final String tbluser_COMPANYNAME = "companyname";
    private static final String tbluser_COMPANYID = "companyid";
    private static final String tbluser_COMPANYLOGO = "companylogo";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + tbluser_USERID + " INTEGER PRIMARY KEY," + tbluser_USERNAME + " TEXT,"+ tbluser_PASSWORD + " TEXT,"
                + tbluser_COMPANYNAME + " TEXT,"+ tbluser_COMPANYLOGO + " TEXT,"
                + tbluser_REFERENCE + " INTEGER," + tbluser_COMPANYID + " INTEGER,"
                + tbluser_TYPE + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        //we would create other users to..


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addUser(user pUser) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tbluser_COMPANYID, pUser.getCompanyInfo().getId()); // Contact Name
        values.put(tbluser_USERNAME, pUser.getUsername()); // Contact Phone
        values.put(tbluser_USERID,pUser.getId()); // Contact Name
        values.put(tbluser_COMPANYLOGO, pUser.getCompanyInfo().getLogo()); // Contact Phone
        values.put(tbluser_COMPANYNAME, pUser.getCompanyInfo().getName()); // Contact Name
        values.put(tbluser_REFERENCE, pUser.getReference()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public user getUser() {
        //this fucntion will be used to get any suer
        return new user();
    }
}
