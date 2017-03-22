package edu.monash.infotech.health.fragment;


import android.app.DatePickerDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import edu.monash.infotech.health.DatabaseHelper;
import edu.monash.infotech.health.R;
import edu.monash.infotech.health.basic.ShareApplication;
import edu.monash.infotech.health.models.Step;
import edu.monash.infotech.health.network.HTTPURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class OneDayReportFragment extends Fragment {

    private String USERNAME = "";
    private DatabaseHelper dbHelper = null;
    private String SELECTED_DATE = "";

    private TextView hiddenTextView;
    private EditText selectDateEditText;
    private Button showBarButton;
    private Button showLineButton;
    private Button showBarButton1;
    private Button showLineButton1;

    private GraphView barGraph;
    private GraphView lineGraph;
    private GraphView barGraph1;
    private GraphView lineGraph1;
    private String Bar_Line_Switcher = "line";

    private DataPoint[] CalorieTimeSeries;


    public OneDayReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_oneday, container, false);

        USERNAME = ((ShareApplication)getActivity().getApplication()).getValue();
        dbHelper = new DatabaseHelper(getActivity());

        hiddenTextView = (TextView)view.findViewById(R.id.hiddenTextView);
        selectDateEditText = (EditText)view.findViewById(R.id.selectDateEditText);
        showBarButton = (Button)view.findViewById(R.id.showBarButton);
        showLineButton = (Button)view.findViewById(R.id.showLineButton);
        showBarButton1 = (Button)view.findViewById(R.id.showBarButton1);
        showLineButton1 = (Button)view.findViewById(R.id.showLineButton1);

        lineGraph = (GraphView)view.findViewById(R.id.lineGraph);
        barGraph = (GraphView)view.findViewById(R.id.barGragh);
        lineGraph1 = (GraphView)view.findViewById(R.id.lineGraph1);
        barGraph1 = (GraphView)view.findViewById(R.id.barGragh1);

        selectDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateEditText.setText("Select a date");
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker dp, int year, int month, int day) {
                                String date = year + "-" + (month + 1) + "-" + day;
                                hiddenTextView.setText(date);
                                Calendar calendar = Calendar.getInstance();
                                String today = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                                if (date.equals(today)) {
                                    System.out.println("=");
                                    selectDateEditText.setText("Today (" + today + ")");
                                } else {
                                    System.out.println("!=");
                                    selectDateEditText.setText(date);
                                }

                                String username = USERNAME;
                                new getReportFromWS().execute(username.replaceAll(" ", "%20"), date);

                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });




        showBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_DATE = hiddenTextView.getText().toString();
                if(SELECTED_DATE.equals("")){
                    Toast.makeText(getActivity(), "Select a date first!", Toast.LENGTH_SHORT).show();
                }
                else{
                    lineGraph.setVisibility(View.GONE);
                    barGraph.setVisibility(View.VISIBLE);
                    showBarButton.setVisibility(View.GONE);
                    showLineButton.setVisibility(View.VISIBLE);
                    showBarGraph(SELECTED_DATE);
                }
            }
        });
        showLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_DATE = hiddenTextView.getText().toString();
                if(SELECTED_DATE.equals("")){
                    Toast.makeText(getActivity(), "Select a date first!", Toast.LENGTH_SHORT).show();
                }
                else{
                    barGraph.setVisibility(View.GONE);
                    lineGraph.setVisibility(View.VISIBLE);
                    showLineButton.setVisibility(View.GONE);
                    showBarButton.setVisibility(View.VISIBLE);
                    showLineGraph(SELECTED_DATE);
                }
            }
        });
        showBarButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_DATE = hiddenTextView.getText().toString();
                if(SELECTED_DATE.equals("")){
                    Toast.makeText(getActivity(), "Select a date first!", Toast.LENGTH_SHORT).show();
                }
                else{
                    lineGraph1.setVisibility(View.GONE);
                    barGraph1.setVisibility(View.VISIBLE);
                    showBarButton1.setVisibility(View.GONE);
                    showLineButton1.setVisibility(View.VISIBLE);
                    Bar_Line_Switcher = "bar";
                    new getCalorieTimeSeriesFromWS().execute(USERNAME, SELECTED_DATE);
                }

            }
        });
        showLineButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_DATE = hiddenTextView.getText().toString();
                if(SELECTED_DATE.equals("")){
                    Toast.makeText(getActivity(), "Select a date first!", Toast.LENGTH_SHORT).show();
                }
                else{
                    barGraph1.setVisibility(View.GONE);
                    lineGraph1.setVisibility(View.VISIBLE);
                    showLineButton1.setVisibility(View.GONE);
                    showBarButton1.setVisibility(View.VISIBLE);
                    Bar_Line_Switcher = "line";
                    new getCalorieTimeSeriesFromWS().execute(USERNAME, SELECTED_DATE);
                }

            }
        });


        //System.out.println(getPeriodStepsOneDay("0:0:0", "2:20:10"));

        return view;
    }

    public void showLineGraph(String date){
        lineGraph.removeAllSeries();
        lineGraph.setTitle("Line Graph");
        LineGraphSeries<DataPoint> step_time_series_1 = new LineGraphSeries<>(getStepTimeSeries(date));

        StaticLabelsFormatter formatter = new StaticLabelsFormatter(lineGraph);
        formatter.setHorizontalLabels(new String[]{
                "0", "6", "12", "18", "24"
        });
        lineGraph.getGridLabelRenderer().setLabelFormatter(formatter);

        lineGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return super.formatLabel(value, isValueX);
                } else {
                    return super.formatLabel(value, isValueX) + "h";
                }
            }
        });

        step_time_series_1.setTitle("Steps");
        step_time_series_1.setColor(Color.rgb(46, 139, 87));//SeaGreen: #2E8B57
        step_time_series_1.setDrawDataPoints(true);
        step_time_series_1.setDataPointsRadius(10);

        lineGraph.getViewport().setXAxisBoundsManual(true);
        lineGraph.getViewport().setMinX(0);
        lineGraph.getViewport().setMaxX(24);
        lineGraph.addSeries(step_time_series_1);
        step_time_series_1.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int X = (int)dataPointInterface.getX();
                int Y = (int)dataPointInterface.getY();
                Toast.makeText(getActivity(), "Time Period: " + X+":00:00 - "+(X)+":59:59" + "\n" + "Steps: " + Y, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBarGraph(String date){
        barGraph.removeAllSeries();
        barGraph.setTitle("Bar Graph");
        BarGraphSeries<DataPoint> step_time_series = new BarGraphSeries<>(getStepTimeSeries(date));

        StaticLabelsFormatter formatter = new StaticLabelsFormatter(barGraph);
        formatter.setHorizontalLabels(new String[]{
                "0", "6", "12", "18", "24"
        });
        barGraph.getGridLabelRenderer().setLabelFormatter(formatter);

        barGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return super.formatLabel(value, isValueX);
                } else {
                    return super.formatLabel(value, isValueX) + "h";
                }
            }
        });

        step_time_series.setTitle("Steps");
        step_time_series.setColor(Color.rgb(46, 139, 87));//SeaGreen: #2E8B57

        barGraph.getViewport().setXAxisBoundsManual(true);
        barGraph.getViewport().setMinX(0);
        barGraph.getViewport().setMaxX(24);

        barGraph.addSeries(step_time_series);
        step_time_series.setSpacing(10);
//        step_time_series.setDrawValuesOnTop(true);
//        step_time_series.setValuesOnTopColor(Color.RED);
        step_time_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int X = (int)dataPointInterface.getX();
                int Y = (int)dataPointInterface.getY();
                Toast.makeText(getActivity(), "Time Period: " + X+":00:00 - "+(X)+":59:59" + "\n" + "Steps: " + Y, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showLineGraph1(){
        lineGraph1.removeAllSeries();
        lineGraph1.setTitle("Line Graph");
        LineGraphSeries<DataPoint> step_time_series_1 = new LineGraphSeries<>(CalorieTimeSeries);

        StaticLabelsFormatter formatter = new StaticLabelsFormatter(lineGraph1);
        formatter.setHorizontalLabels(new String[]{
                "0", "6", "12", "18", "24"
        });
        lineGraph1.getGridLabelRenderer().setLabelFormatter(formatter);

        lineGraph1.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return super.formatLabel(value, isValueX);
                } else {
                    return super.formatLabel(value, isValueX) + "h";
                }
            }
        });

        lineGraph1.getViewport().setXAxisBoundsManual(true);
        lineGraph1.getViewport().setMinX(0);
        lineGraph1.getViewport().setMaxX(24);

        step_time_series_1.setTitle("Consumed");
        step_time_series_1.setColor(Color.rgb(255, 69, 0));//OrangeRed: #FF4500
        step_time_series_1.setDrawDataPoints(true);
        step_time_series_1.setDataPointsRadius(10);

        lineGraph1.addSeries(step_time_series_1);
        step_time_series_1.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int X = (int)dataPointInterface.getX();
                int Y = (int)dataPointInterface.getY();
                Toast.makeText(getActivity(), "Time Period: " + X+":00:00 - "+(X)+":59:59" + "\n" + "Calorie Consumed: " + Y, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBarGraph1(){
        barGraph1.removeAllSeries();
        barGraph1.setTitle("Bar Graph");
        BarGraphSeries<DataPoint> calorie_time_series = new BarGraphSeries<>(CalorieTimeSeries);

        StaticLabelsFormatter formatter = new StaticLabelsFormatter(barGraph1);
        formatter.setHorizontalLabels(new String[]{
                "0", "6", "12", "18", "24"
        });
        barGraph1.getGridLabelRenderer().setLabelFormatter(formatter);

        barGraph1.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return super.formatLabel(value, isValueX);
                } else {
                    return super.formatLabel(value, isValueX) + "h";
                }
            }
        });

        barGraph1.getViewport().setXAxisBoundsManual(true);
        barGraph1.getViewport().setMinX(0);
        barGraph1.getViewport().setMaxX(24);

        calorie_time_series.setTitle("Consumed");
        calorie_time_series.setColor(Color.rgb(255, 69, 0));//OrangeRed: #FF4500

        barGraph1.addSeries(calorie_time_series);
        calorie_time_series.setSpacing(10);
//        step_time_series.setDrawValuesOnTop(true);
//        step_time_series.setValuesOnTopColor(Color.RED);
        calorie_time_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int X = (int)dataPointInterface.getX();
                int Y = (int)dataPointInterface.getY();
                Toast.makeText(getActivity(), "Time Period: " + X+":00:00 - "+(X)+":59:59" + "\n" + "Calorie Consumed: " + Y, Toast.LENGTH_SHORT).show();
            }
        });
    }






    private class getReportFromWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.userreport/findByUserNameAndDate";
        HTTPURLConnection conn;

        @Override
        protected Void doInBackground(String... params){
            conn = new HTTPURLConnection();
            for(int i=0; i<params.length; i++){
                path1 = path1 + "/" + params[i];
            }
            System.out.println(path1);
            response = conn.GetFromWebServer(path1);
            System.out.println("server response1: " + response);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!response.equals("") && !response.equals("[]")){
                JSONArray jsonArray = null;
                try{
                    jsonArray = new JSONArray(response);
//                    String calConsumed = jsonArray.getJSONObject(0).getString("calConsumed");
//                    String calBurned = jsonArray.getJSONObject(0).getString("calBurned");
//                    String steps = jsonArray.getJSONObject(0).getString("steps");
//                    String calorieGoal = jsonArray.getJSONObject(0).getString("calorieGoal");
//                    String remaining = jsonArray.getJSONObject(0).getString("remaining");
//                    System.out.println(calConsumed + ", " + calBurned + ", " + steps + ", " + calorieGoal + ", " + remaining);

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                //user does not have a report on this date
                Toast.makeText(getContext(), "No record found on this date! Select another date!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public double getPeriodStepsOneDay(String date, String startTime, String endTime){
        double periodSteps = 0;
        ArrayList<Step> steps = dbHelper.getAllSteps();
        long thisId = dbHelper.getUserIdByName(USERNAME);
        if(!steps.isEmpty()){
            for(int i=0; i<steps.size(); i++){
                Step s = steps.get(i);
                if(s.getUserId() == thisId && s.getDate().equals(date)){
                    if(isLessThan(s.getTime(), endTime) && !isLessThan(s.getTime(), startTime)){
                        periodSteps = periodSteps + s.getStepsNum();
                    }
                }
            }
        }
        return periodSteps;
    }

    public DataPoint[] getStepTimeSeries(String date){
        DataPoint[] dataPoints = new DataPoint[24];
        for(int i=0; i<=23; i++){
            double step = getPeriodStepsOneDay(date, i+":0:0", (i+1)+":0:0");
            dataPoints[i] = new DataPoint(i, step);
            System.out.println("X: " + i + ", Y: " + step);
        }
        return dataPoints;
    }

    //compare two time
    public boolean isLessThan(String t1, String t2){
        int t1_h = Integer.valueOf(t1.split(":")[0]);
        int t1_m = Integer.valueOf(t1.split(":")[1]);
        int t1_s = Integer.valueOf(t1.split(":")[2]);
        int t2_h = Integer.valueOf(t2.split(":")[0]);
        int t2_m = Integer.valueOf(t2.split(":")[1]);
        int t2_s = Integer.valueOf(t2.split(":")[2]);
        if(t1_h < t2_h){
            return true;
        }else if(t1_h > t2_h){
            return false;
        }else{
            if(t1_m < t2_m){
                return true;
            }else if(t1_m > t2_m){
                return false;
            }else{
                if(t1_s < t2_s){
                    return true;
                }else if(t1_s > t2_s){
                    return false;
                }else{
                    return false;
                }
            }
        }
    }

    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        String currentDate = year + "-" + month + "-" + day;
        return currentDate;
    }

    private class getCalorieTimeSeriesFromWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.userfood/findByUserNameAndDateOrderByTime";
        HTTPURLConnection conn;

        @Override
        protected Void doInBackground(String... params){
            conn = new HTTPURLConnection();
            for(int i=0; i<params.length; i++){
                path1 = path1 + "/" + params[i];
            }
            System.out.println(path1);
            response = conn.GetFromWebServer(path1);
            System.out.println("server response1: " + response);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!response.equals("") && !response.equals("[]")){
                JSONArray jsonArray = null;
                try{
                    jsonArray = new JSONArray(response);
                    double[] periodCalorieConsumed = new double[24];
                    for(int i=0; i<24; i++){
                        periodCalorieConsumed[i] = 0;
                    }
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String time = jsonObject.getString("time").split("T")[1].split("\\+")[0];
                        System.out.println(time);
                        for(int j=0; j<24; j++){
                            if(isLessThan(time, (j+1)+":0:0") && !isLessThan(time, j+":0:0")){
                                String amount = jsonObject.getString("amount");
                                String calorie = jsonObject.getJSONObject("food").getString("calorie");
                                String serving = jsonObject.getJSONObject("food").getString("serving");
                                double thisCalorieConsumed = Double.valueOf(amount)/Double.valueOf(serving) * Double.valueOf(calorie);
                                periodCalorieConsumed[j] = periodCalorieConsumed[j] + thisCalorieConsumed;
                            }

                        }
                    }

                    CalorieTimeSeries = new DataPoint[24];
                    for(int i=0; i<24; i++){
                        CalorieTimeSeries[i] = new DataPoint(i, periodCalorieConsumed[i]);
                        System.out.println("X1: " + i + ", Y1: " + periodCalorieConsumed[i]);
                    }

                    if(Bar_Line_Switcher.equals("bar")){
                        showBarGraph1();
                    }
                    if(Bar_Line_Switcher.equals("line")){
                        showLineGraph1();
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{//reponse为空
                CalorieTimeSeries = new DataPoint[24];
                for(int i=0; i<24; i++){
                    CalorieTimeSeries[i] = new DataPoint(i, 0);
                }
            }

        }
    }



}
