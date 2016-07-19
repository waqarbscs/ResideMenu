package app.num.MassUAETracking.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import app.num.MassUAETracking.Models.Vehicle;
import app.num.MassUAETracking.R;

/**
 * Created by Imdad on 4/30/2016.
 */
public class lstVehicleAdapter extends BaseAdapter {

    private List<Vehicle> vehicleList;
    private LayoutInflater inflater;
    private Activity _activity;
    private int resid;


    public lstVehicleAdapter(Activity pActivity,List<Vehicle> pVehicleList) {
        vehicleList = pVehicleList;
        _activity = pActivity;
    }

    @Override
    public int getCount() {

        if(vehicleList == null)
            return 0;

        return vehicleList.size();
    }

    @Override
    public Object getItem(int position) {

        if(vehicleList == null)
            return null;

        if(position > vehicleList.size())
            return null;

        return vehicleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(inflater == null)
            inflater = _activity.getLayoutInflater();


        if(convertView == null)
            convertView = inflater.inflate(R.layout.lyt_vehicle_row,null,false);

        ((TextView) convertView.findViewById(R.id.txtvTrackerName)).setText(vehicleList.get(position).trackerName);
        ((TextView) convertView.findViewById(R.id.txtvDeviceId)).setText(vehicleList.get(position).deviceid);

        ((TextView) convertView.findViewById(R.id.trackerid)).setText(vehicleList.get(position).id);

        //((ImageView) convertView.findViewById(R.id.imvEngineStatus)).setBackgroundColor((vehicleList.get(position).engineStatus.equals("0")? Color.RED:Color.GREEN));
        //changes
        //((ImageView) convertView.findViewById(R.id.imvEngineStatus)).setBackgroundColor((vehicleList.get(position).engineStatus.equals("0")? Color.RED:Color.GREEN));
        ImageView i= ((ImageView) convertView.findViewById(R.id.imvEngineStatus));
        if((vehicleList.get(position).engineStatus.equals("0")))
            i.setImageResource(R.drawable.engoff);
        else
            i.setImageResource(R.drawable.engon);
        String engineStatus=vehicleList.get(position).engineStatus;
        String last_signal=vehicleList.get(position).last_status;
        String last_gprs=vehicleList.get(position).last_gprs;
        String last_move=vehicleList.get(position).last_move;
        String tracker_general_color=vehicleList.get(position).trackerGenColor;
        String tracker_general_status=vehicleList.get(position).trackerGenStatus;
        int tracker_icon=Integer.parseInt(vehicleList.get(position).tracker_icon);

        Calendar cal=Calendar.getInstance();
        int currYear=cal.get(Calendar.YEAR);
        int curr_month=cal.get(Calendar.MONTH);
        int curr_date=cal.get(Calendar.DATE);
        int curr_hour=cal.get(Calendar.HOUR_OF_DAY);

        int last_year=Integer.parseInt(last_signal.substring(0,4));
        int last_month=Integer.parseInt(last_signal.substring(5,7));
        int last_date=Integer.parseInt(last_signal.substring(8,10));
        int last_hour=Integer.parseInt(last_signal.substring(11,13));

        int gprs_year=Integer.parseInt(last_gprs.substring(0,4));
        int gprs_month=Integer.parseInt(last_gprs.substring(5,7));
        int gprs_date=Integer.parseInt(last_gprs.substring(8,10));
        int gprs_hour=Integer.parseInt(last_gprs.substring(11,13));


        int last_signal_years=currYear-last_year;
        int last_signal_months=(curr_month+1)-last_month;
        int last_signal_days=curr_date-last_date;
        int last_signal_hours=curr_hour-last_hour;


        int last_gprs_years=currYear-gprs_year;
        int last_gprs_months=(curr_month+1)-gprs_month;
        int last_gprs_days=curr_date-gprs_date;
        int last_gprs_hours=curr_hour-gprs_hour;

        String tracker_icon_color = "gray";
        resid=R.mipmap.ic_launcher;
        if(tracker_icon==0) {
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                //tracker_icon_color = "gray";
                resid=R.drawable.car_grey;
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.car_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.car_orange;
                    //tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.car_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.car_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.car_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.car_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.car_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.car_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.car_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==1){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                //tracker_icon_color = "gray";
                resid=R.drawable.map_grey;
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.map_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.map_orange;
                    //tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.map_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.map_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.map_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.map_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.map_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.map_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.map_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==2){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.male_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.male_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.male_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.male_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.male_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.male_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.male_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // green
                    resid=R.drawable.male_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.male_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.male_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==3){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.female_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.female_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.female_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.female_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.female_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.female_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.female_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.female_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.female_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.female_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==4){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.bike_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.bike_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.bike_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.bike_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow

                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.bike_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // red
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.bike_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.bike_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.bike_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==5){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.bus_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.bus_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.bus_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.bus_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00")|| tracker_general_status == "stop") { // yellow
                    resid=R.drawable.bus_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.bus_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.bus_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.bus_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.bus_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.bus_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==6){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.truck_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.truck_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.truck_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.truck_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.truck_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.truck_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.truck_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.truck_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.truck_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.truck_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==7){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.road_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.road_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.road_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.road_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.road_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.road_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.road_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.road_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.road_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF")|| tracker_general_status == "outside")) { // blue
                    resid=R.drawable.road_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==8){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.ship_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.ship_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.ship_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.ship_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.ship_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.ship_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.ship_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.ship_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.ship_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.ship_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==9){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.train_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.train_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.train_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.train_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.train_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.train_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.train_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // green
                    resid=R.drawable.train_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.train_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.train_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==10){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.aero_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.aero_grey;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.aero_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.aero_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.aero_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.aero_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.aero_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // green
                    resid=R.drawable.aero_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.aero_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.aero_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==11){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.container_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.container_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.container_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.container_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.container_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.container_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.container_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // green
                    resid=R.drawable.container_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.container_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.container_blue;
                    tracker_icon_color = "blue";
                }
            }
        }else if(tracker_icon==12){
            if(last_signal_years > 0 || last_signal_months > 0 || last_signal_days > 0 || last_signal_hours >= 10) // gray
            {
                resid=R.drawable.auto_grey;
                tracker_icon_color = "gray";
            }

            else if(last_gprs_years > 0 || last_gprs_months > 0 || last_gprs_days > 0 || last_gprs_hours >= 10) // pink
            {
                resid=R.drawable.auto_pink;
                tracker_icon_color = "pink";
            }
            else
            {
                if (tracker_general_color.equals("#FF6600") || tracker_general_status == "archive") { // orange
                    resid=R.drawable.auto_orange;
                    tracker_icon_color = "orange";
                } else if (tracker_general_color.equals("#996600") || tracker_general_status == "cancel") { // brown
                    resid=R.drawable.auto_brown;
                    tracker_icon_color = "brown";
                } else if (tracker_general_color.equals("#FFFF00") || tracker_general_status == "stop") { // yellow
                    resid=R.drawable.auto_yellow;
                    tracker_icon_color = "yellow";
                } else if (tracker_general_color.equals("#660000") || tracker_general_status == "lost") { // reddark
                    resid=R.drawable.auto_reddark;
                    tracker_icon_color = " reddark ";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#009900") || tracker_general_status == "inside")) { // red
                    resid=R.drawable.auto_red;
                    tracker_icon_color = "red";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#009900")|| tracker_general_status == "inside")) { // green
                    resid=R.drawable.auto_green;
                    tracker_icon_color = "green";
                } else if (engineStatus.equals("0") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // bluelight
                    resid=R.drawable.auto_sky;
                    tracker_icon_color = " bluelight ";
                } else if (engineStatus.equals("1") && (tracker_general_color.equals("#0000FF") || tracker_general_status == "outside")) { // blue
                    resid=R.drawable.auto_blue;
                    tracker_icon_color = "blue";
                }
            }
        }


        //((ImageView) convertView.findViewById(R.id.imv_iconcolor)).setBackgroundColor( Color.parseColor (vehicleList.get(position).trackerGenColor));
        ((ImageView) convertView.findViewById(R.id.imv_iconcolor)).setBackgroundResource(resid);
        return convertView;
    }
}
