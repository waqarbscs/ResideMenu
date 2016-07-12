package app.num.MassUAETracking.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Imdad on 5/27/2016.
 */
public class TrackerData implements Parcelable {

    public String name;
    public String device_id;
    public String engine_status;
    public String latitude;
    public String longitude;

    public String output_bit;
    public String input_bit_1,input_bit_2,input_bit_3,input_bit_4;

    public String mileage;
    public String username;
    public String speed;


    public String last_engine_on;
    public String last_engine_off;
    public String last_location;
    public String last_move;
    public String last_signal;

    public String gps;
    public String gsm;
    public String battery;

    public TrackerData() {
        SetInitialValues();
    }



    public void SetInitialValues(){

        name = "NA";
        device_id = "NA";
        engine_status = "NA";
        latitude = "NA";
        longitude = "NA";

        output_bit = "NA";
        input_bit_1 = "NA";
        input_bit_2 = "NA";
        input_bit_3 = "NA";
        input_bit_4 = "NA";

        mileage = "NA";
        username = "NA";
        speed = "NA";

        last_engine_on = "NA";
        last_engine_off = "NA";
        last_location = "NA";
        last_move = "NA";
        last_signal = "NA";

        gps="NA";
        gsm="NA";
        battery="NA";

    }

    protected TrackerData(Parcel in) {
        name = in.readString();
        device_id = in.readString();
        engine_status = in.readString();
        latitude = in.readString();
        longitude = in.readString();

        output_bit = in.readString();
        input_bit_1 = in.readString();
        input_bit_2 = in.readString();
        input_bit_3 = in.readString();
        input_bit_4 = in.readString();

        mileage = in.readString();
        username = in.readString();
        speed = in.readString();

        last_engine_on = in.readString();
        last_engine_off = in.readString();
        last_location = in.readString();
        last_move = in.readString();
        last_signal = in.readString();

        gps=in.readString();
        gsm=in.readString();
        battery=in.readString();
    }

    public static final Creator<TrackerData> CREATOR = new Creator<TrackerData>() {
        @Override
        public TrackerData createFromParcel(Parcel in) {
            return new TrackerData(in);
        }

        @Override
        public TrackerData[] newArray(int size) {
            return new TrackerData[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString( name );
        dest.writeString(device_id );
        dest.writeString(engine_status );
        dest.writeString(latitude );
        dest.writeString(longitude );

        dest.writeString(output_bit );
        dest.writeString(input_bit_1 );
        dest.writeString(input_bit_2 );
        dest.writeString(input_bit_3 );
        dest.writeString(input_bit_4 );

        dest.writeString(mileage );
        dest.writeString(username );
        dest.writeString(speed );

        dest.writeString(last_engine_on );
        dest.writeString(last_engine_off );
        dest.writeString(last_location );
        dest.writeString(last_move );
        dest.writeString(last_signal );

        dest.writeString(gps);
        dest.writeString(gsm);
        dest.writeString(battery);

    }
}
