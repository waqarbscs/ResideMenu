package app.num.umasstechnologies.Models;



/**
 * Created by Imdad on 5/17/2016.
 */
public class CompanyInfo {

    private String name;
    private int id;
    private String logo;

    public CompanyInfo () {
        name = "NA";
        id = -1;
        logo = "NA";

    }

    public String getName() { return name; }
    public int getId() { return id; }
    public String getLogo() { return logo; }

    public void setName(String pName) {
        name = pName;
    }

    public void setId(int pId) {
        id = pId;
    }

    public void setLogo(String pLogo) {
        logo = pLogo;
    }

}
