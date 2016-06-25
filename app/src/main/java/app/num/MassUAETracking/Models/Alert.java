package app.num.MassUAETracking.Models;

/**
 * Created by Imdad on 4/30/2016.
 */
public class Alert {
    public String title;
    public String description;
    public String message;

    public Alert() {}
    public Alert(String pTitle, String pDescription, String pMessage) {
        title = pTitle;
        description = pDescription;
        message = pMessage;
    }
}
