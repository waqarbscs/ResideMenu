package app.num.umasstechnologies.Models;

import io.realm.RealmObject;

/**
 * Created by Imdad on 5/17/2016.
 */
public class CompanyInfo extends RealmObject {

    private String name;
    private int id;
    private String logo;

    public CompanyInfo () {
        name = "NA";
        id = -1;
        logo = "NA";

    }

}
