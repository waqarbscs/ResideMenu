package app.num.MassUAETracking.Models;



/**
 * Created by Imdad on 5/17/2016.
 */
public class user  {
    /*
    *
    * User is going to be the one containing data about the user :D
    * */

    private String username;
    private String password;
    private int id;
    private int reference; //company reference id
    private int type; //user type

    private CompanyInfo companyInfo;

    public user() {

        username = "NA";
        password = "NA";
        id = -1;
        reference = -1;
        type = -1;
        companyInfo = new CompanyInfo();

    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getId() { return id; }
    public int getReference() { return reference; }
    public int gettype() { return type; }

    public CompanyInfo getCompanyInfo() { return companyInfo; }

    public void setUsername(String pUsername) { username = pUsername; }
    public void setPassword(String pPassword) { password = pPassword; }
    public void setId(int pId) { id = pId; }
    public void setReference(int pReference) { reference = pReference; }
    public void setType(int pType) { type = pType; }

    public void setCompanyInfo(int pId,String pLogo, String pName) {
        if(companyInfo == null)
            companyInfo = new CompanyInfo();

        companyInfo.setId(pId);
        companyInfo.setLogo(pLogo);
        companyInfo.setName(pName);
    }



}
