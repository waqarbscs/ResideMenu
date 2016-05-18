package app.num.umasstechnologies.Models;

import io.realm.RealmObject;

/**
 * Created by Imdad on 5/17/2016.
 */
public class user extends RealmObject {
    /*
    *
    * User is going to be the one containing data about the user :D
    * */

    private String username;
    private String password;
    private int id;
    private int reference; //company reference id
    private int type; //user type

    CompanyInfo companyInfo;

    public user() {

        username = "NA";
        password = "NA";
        id = -1;
        reference = -1;
        type = -1;
        companyInfo = new CompanyInfo();

    }

}
