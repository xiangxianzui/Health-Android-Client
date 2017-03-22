package edu.monash.infotech.health.fragment;


import android.app.DatePickerDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.monash.infotech.health.R;
import edu.monash.infotech.health.basic.ShareApplication;
import edu.monash.infotech.health.network.HTTPURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeriodFragment extends Fragment {

    private String USERNAME = "";
    private String START_DATE = "";
    private String END_DATE = "";

    private String Bar_Line_Switcher = "line";
    private String Bar_Line_Switcher1 = "line";

    private EditText startDateEditText;
    private EditText endDateEditText;
    private TextView hiddenTextView1;
    private TextView hiddenTextView2;

    private Button showBarButton;
    private Button showLineButton;
    //private Button showBarButton1;
    private Button showLineButton1;

    private GraphView lineGraph;
    private GraphView barGraph;
    private GraphView lineGraph1;
    private GraphView barGraph1;

    private DataPoint[] step_date_series;
    private DataPoint[] calorieconsumed_date_series;
    private DataPoint[] calorieburned_date_series;
    private DataPoint[] caloriegoal_date_series;
    private DataPoint[] remaining_date_series;

    public PeriodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_period, container, false);

        USERNAME = ((ShareApplication)getActivity().getApplication()).getValue();

        startDateEditText = (EditText)view.findViewById(R.id.startDateEditText);
        endDateEditText = (EditText)view.findViewById(R.id.endDateEditText);
        hiddenTextView1 = (TextView)view.findViewById(R.id.hiddenTextView1);
        hiddenTextView2 = (TextView)view.findViewById(R.id.hiddenTextView2);

        showBarButton = (Button)view.findViewById(R.id.showBarButton);
        showLineButton = (Button)view.findViewById(R.id.showLineButton);
        //showBarButton1 = (Button)view.findViewById(R.id.showBarButton1);
        showLineButton1 = (Button)view.findViewById(R.id.showLineButton1);

        lineGraph = (GraphView)view.findViewById(R.id.lineGraph);
        barGraph = (GraphView)view.findViewById(R.id.barGragh);
        lineGraph1 = (GraphView)view.findViewById(R.id.lineGraph1);
        barGraph1 = (GraphView)view.findViewById(R.id.barGragh1);

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDateEditText.setText("Select start date");
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker dp, int year, int month, int day) {
                                String date = year + "-" + (month + 1) + "-" + day;
                                hiddenTextView1.setText(date);
                                Calendar calendar = Calendar.getInstance();
                                String today = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                                if (date.equals(today)) {
                                    System.out.println("=");
                                    startDateEditText.setText("Today (" + today + ")");
                                } else {
                                    System.out.println("!=");
                                    startDateEditText.setText(date);
                                }

//                                String username = USERNAME;
//                                new getReportFromWS().execute(username.replaceAll(" ", "%20"), date);

                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDateEditText.setText("Select end date");
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker dp, int year, int month, int day) {
                                String date = year + "-" + (month + 1) + "-" + day;
                                hiddenTextView2.setText(date);
                                Calendar calendar = Calendar.getInstance();
                                String today = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                                if (date.equals(today)) {
                                    System.out.println("=");
                                    endDateEditText.setText("Today (" + today + ")");
                                } else {
                                    System.out.println("!=");
                                    endDateEditText.setText(date);
                                }

//                                String username = USERNAME;
//                                new getReportFromWS().execute(username.replaceAll(" ", "%20"), date);

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
                START_DATE = hiddenTextView1.getText().toString();
                END_DATE = hiddenTextView2.getText().toString();
                if(checkDateCorrectness(START_DATE, END_DATE)){
                    lineGraph.setVisibility(View.GONE);
                    barGraph.setVisibility(View.VISIBLE);
                    showBarButton.setVisibility(View.GONE);
                    showLineButton.setVisibility(View.VISIBLE);
                    Bar_Line_Switcher = "bar";
                    new getStepSeriesFromWS().execute(USERNAME, START_DATE, END_DATE);
                }
            }
        });
        showLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                START_DATE = hiddenTextView1.getText().toString();
                END_DATE = hiddenTextView2.getText().toString();
                if(checkDateCorrectness(START_DATE, END_DATE)){
                    barGraph.setVisibility(View.GONE);
                    lineGraph.setVisibility(View.VISIBLE);
                    showLineButton.setVisibility(View.GONE);
                    showBarButton.setVisibility(View.VISIBLE);
                    Bar_Line_Switcher = "line";
                    new getStepSeriesFromWS().execute(USERNAME, START_DATE, END_DATE);
                }
            }
        });

//        showBarButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("click showbarbutton1");
//                START_DATE = hiddenTextView1.getText().toString();
//                END_DATE = hiddenTextView2.getText().toString();
//                if(checkDateCorrectness(START_DATE, END_DATE)){
//                    lineGraph1.setVisibility(View.GONE);
//                    barGraph1.setVisibility(View.VISIBLE);
//                    showBarButton1.setVisibility(View.GONE);
//                    showLineButton1.setVisibility(View.VISIBLE);
//                    Bar_Line_Switcher1 = "bar";
//                    new getCalorieSeriesFromWS().execute(USERNAME, START_DATE, END_DATE);
//                }
//            }
//        });
        showLineButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                START_DATE = hiddenTextView1.getText().toString();
                END_DATE = hiddenTextView2.getText().toString();
                if(checkDateCorrectness(START_DATE, END_DATE)){
                    barGraph1.setVisibility(View.GONE);
                    lineGraph1.setVisibility(View.VISIBLE);
//                    showLineButton1.setVisibility(View.GONE);
//                    showBarButton1.setVisibility(View.VISIBLE);
                    Bar_Line_Switcher1 = "line";
                    new getCalorieSeriesFromWS().execute(USERNAME, START_DATE, END_DATE);
                }
            }
        });




        return view;
    }


    public void showLineGraph(){
        lineGraph.removeAllSeries();
        lineGraph.setTitle("Line Graph");
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(step_date_series);

        int numOfDate = step_date_series.length;

        lineGraph.getViewport().setXAxisBoundsManual(true);
        lineGraph.getViewport().setMinX(step_date_series[0].getX());
        lineGraph.getViewport().setMaxX(step_date_series[numOfDate - 1].getX());

        series1.setTitle("Steps");
        series1.setColor(Color.rgb(46, 139, 87));//SeaGreen: #2E8B57
        series1.setDrawDataPoints(true);
        series1.setDataPointsRadius(10);

        lineGraph.addSeries(series1);

        lineGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        lineGraph.getGridLabelRenderer().setNumHorizontalLabels(numOfDate);
        lineGraph.getGridLabelRenderer().setHorizontalAxisTitle("Date");

        lineGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()) {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return super.formatLabel(value, isValueX);
                } else {
                    return super.formatLabel(value, isValueX).split("\\/")[0] + "/" + super.formatLabel(value, isValueX).split("\\/")[1];
                }
            }
        });

        series1.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int Y = (int) dataPointInterface.getY();
                Toast.makeText(getActivity(), "Steps: " + Y, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showBarGraph(){
        barGraph.removeAllSeries();
        barGraph.setTitle("Bar Graph");
        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(step_date_series);

        int numOfDate = step_date_series.length;
        System.out.println(numOfDate);

        barGraph.getViewport().setXAxisBoundsManual(true);
        barGraph.getViewport().setMinX(step_date_series[0].getX());
        barGraph.getViewport().setMaxX(step_date_series[numOfDate - 1].getX());

        series1.setTitle("Steps");
        series1.setColor(Color.rgb(46, 139, 87));//SeaGreen: #2E8B57

        barGraph.addSeries(series1);

        barGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        barGraph.getGridLabelRenderer().setNumHorizontalLabels(numOfDate);
        barGraph.getGridLabelRenderer().setHorizontalAxisTitle("Date");

        barGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()) {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return super.formatLabel(value, isValueX);
                } else {
                    return super.formatLabel(value, isValueX).split("\\/")[0] + "/" + super.formatLabel(value, isValueX).split("\\/")[1];
                }
            }
        });

        series1.setSpacing(10);
//        step_time_series.setDrawValuesOnTop(true);
//        step_time_series.setValuesOnTopColor(Color.RED);
        series1.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int Y = (int) dataPointInterface.getY();
                Toast.makeText(getActivity(), "Steps: " + Y, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showLineGraph1(){
        lineGraph1.removeAllSeries();
        lineGraph1.setTitle("Line Graph");
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(calorieconsumed_date_series);
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(calorieburned_date_series);
        LineGraphSeries<DataPoint> series4 = new LineGraphSeries<>(caloriegoal_date_series);
        LineGraphSeries<DataPoint> series5 = new LineGraphSeries<>(remaining_date_series);

        int numOfDate = calorieconsumed_date_series.length;

        lineGraph1.getViewport().setXAxisBoundsManual(true);
        lineGraph1.getViewport().setMinX(calorieconsumed_date_series[0].getX());
        lineGraph1.getViewport().setMaxX(calorieconsumed_date_series[numOfDate - 1].getX());

        series2.setTitle("Consumed");
        series2.setColor(Color.rgb(255, 69, 0));//OrangeRed: #FF4500
        series2.setDrawDataPoints(true);
        series2.setDataPointsRadius(10);

        series3.setTitle("Burned");
        series3.setColor(Color.rgb(74, 112, 139));//SkyBlue4: #4A708B
        series3.setDrawDataPoints(true);
        series3.setDataPointsRadius(10);

        series4.setTitle("Goal");
        series4.setColor(Color.rgb(125, 38, 205));//Purple3: #7D26CD
        series4.setDrawDataPoints(true);
        series4.setDataPointsRadius(10);

        series5.setTitle("Remaining");
        series5.setColor(Color.rgb(178, 34, 34));//Firebrick: #B22222
        series5.setDrawDataPoints(true);
        series5.setDataPointsRadius(10);

        lineGraph1.addSeries(series2);
        lineGraph1.addSeries(series3);
        lineGraph1.addSeries(series4);
        lineGraph1.addSeries(series5);

        lineGraph1.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        lineGraph1.getGridLabelRenderer().setNumHorizontalLabels(numOfDate);
        lineGraph1.getGridLabelRenderer().setHorizontalAxisTitle("Date");
       // lineGraph1.getGridLabelRenderer().setVerticalAxisTitle("Calories");


        lineGraph1.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()) {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return super.formatLabel(value, isValueX);
                } else {
                    return super.formatLabel(value, isValueX).split("\\/")[0] + "/" + super.formatLabel(value, isValueX).split("\\/")[1];
                }
            }
        });

        series2.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int Y = (int) dataPointInterface.getY();
                Toast.makeText(getActivity(), "Consumed: " + Y, Toast.LENGTH_SHORT).show();
            }
        });
        series3.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int Y = (int) dataPointInterface.getY();
                Toast.makeText(getActivity(), "Burned: " + Y, Toast.LENGTH_SHORT).show();
            }
        });
        series4.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int Y = (int) dataPointInterface.getY();
                Toast.makeText(getActivity(), "Goal: " + Y, Toast.LENGTH_SHORT).show();
            }
        });
        series5.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPointInterface) {
                int Y = (int) dataPointInterface.getY();
                Toast.makeText(getActivity(), "Remaining: " + Y, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private class getStepSeriesFromWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.userreport/findByUserNameAndStartDateAndEndDate";
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
                    System.out.println(jsonArray.length());
                    int numOfDate = jsonArray.length();
                    Date[] step_date_X = new Date[numOfDate];
                    double[] step_date_Y = new double[numOfDate];
//                    double[] calorieconsumed_date_Y = new double[numOfDate];
//                    double[] calorieburned_date_Y = new double[numOfDate];
//                    double[] caloriegoal_date_Y = new double[numOfDate];
//                    double[] remaining_date_Y = new double[numOfDate];
                    for(int i=0; i<numOfDate; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String date_str = jsonObject.getJSONObject("userreportPK").getString("date").split("T")[0];
                        String calBurned = jsonObject.getString("calBurned");
                        String calConsumed = jsonObject.getString("calConsumed");
                        String calorieGoal = jsonObject.getString("calorieGoal");
                        String steps = jsonObject.getString("steps");
                        step_date_Y[i] = Double.valueOf(steps);
//                        calorieconsumed_date_Y[i] = Double.valueOf(calConsumed);
//                        calorieburned_date_Y[i] = Double.valueOf(calBurned);
//                        caloriegoal_date_Y[i] = Double.valueOf(calorieGoal);
//                        remaining_date_Y[i] = caloriegoal_date_Y[i] - calorieconsumed_date_Y[i] + calorieburned_date_Y[i];
                        try{
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            step_date_X[i] = sdf.parse(date_str);
                        }catch (ParseException e){
                            e.printStackTrace();
                        }

                    }

                    step_date_series = new DataPoint[numOfDate];
                    calorieconsumed_date_series = new DataPoint[numOfDate];
                    calorieburned_date_series = new DataPoint[numOfDate];
                    caloriegoal_date_series = new DataPoint[numOfDate];
                    remaining_date_series = new DataPoint[numOfDate];
                    for(int i=0; i<numOfDate; i++){
                        step_date_series[i] = new DataPoint(step_date_X[i], step_date_Y[i]);
//                        calorieconsumed_date_series[i] = new DataPoint(step_date_X[i], calorieconsumed_date_Y[i]);
//                        calorieburned_date_series[i] = new DataPoint(step_date_X[i], calorieburned_date_Y[i]);
//                        caloriegoal_date_series[i] = new DataPoint(step_date_X[i], caloriegoal_date_Y[i]);
//                        remaining_date_series[i] = new DataPoint(step_date_X[i], remaining_date_Y[i]);
                        //System.out.println("X1: " + i + ", Y1: " + step_date_Y[i]);
                    }


                    if(Bar_Line_Switcher.equals("bar")){
                        showBarGraph();
                    }
                    if(Bar_Line_Switcher.equals("line")){
                        showLineGraph();
                    }


                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{//reponse为空
                Toast.makeText(getActivity(), "No record found during this period.", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class getCalorieSeriesFromWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.userreport/findByUserNameAndStartDateAndEndDate";
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
                    System.out.println(jsonArray.length());
                    int numOfDate = jsonArray.length();
                    Date[] step_date_X = new Date[numOfDate];
                    //double[] step_date_Y = new double[numOfDate];
                    double[] calorieconsumed_date_Y = new double[numOfDate];
                    double[] calorieburned_date_Y = new double[numOfDate];
                    double[] caloriegoal_date_Y = new double[numOfDate];
                    double[] remaining_date_Y = new double[numOfDate];
                    for(int i=0; i<numOfDate; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String date_str = jsonObject.getJSONObject("userreportPK").getString("date").split("T")[0];
                        String calBurned = jsonObject.getString("calBurned");
                        String calConsumed = jsonObject.getString("calConsumed");
                        String calorieGoal = jsonObject.getString("calorieGoal");
                        String steps = jsonObject.getString("steps");
                        //step_date_Y[i] = Double.valueOf(steps);
                        calorieconsumed_date_Y[i] = Double.valueOf(calConsumed);
                        calorieburned_date_Y[i] = Double.valueOf(calBurned);
                        caloriegoal_date_Y[i] = Double.valueOf(calorieGoal);
                        remaining_date_Y[i] = caloriegoal_date_Y[i] - calorieconsumed_date_Y[i] + calorieburned_date_Y[i];
                        try{
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            step_date_X[i] = sdf.parse(date_str);
                        }catch (ParseException e){
                            e.printStackTrace();
                        }

                    }

                    //step_date_series = new DataPoint[numOfDate];
                    calorieconsumed_date_series = new DataPoint[numOfDate];
                    calorieburned_date_series = new DataPoint[numOfDate];
                    caloriegoal_date_series = new DataPoint[numOfDate];
                    remaining_date_series = new DataPoint[numOfDate];
                    for(int i=0; i<numOfDate; i++){
                        //step_date_series[i] = new DataPoint(step_date_X[i], step_date_Y[i]);
                        calorieconsumed_date_series[i] = new DataPoint(step_date_X[i], calorieconsumed_date_Y[i]);
                        calorieburned_date_series[i] = new DataPoint(step_date_X[i], calorieburned_date_Y[i]);
                        caloriegoal_date_series[i] = new DataPoint(step_date_X[i], caloriegoal_date_Y[i]);
                        remaining_date_series[i] = new DataPoint(step_date_X[i], remaining_date_Y[i]);
                        //System.out.println("X1: " + i + ", Y1: " + step_date_Y[i]);
                    }


                    if(Bar_Line_Switcher1.equals("bar")){
                        //showBarGraph1();
                    }
                    if(Bar_Line_Switcher1.equals("line")){
                        System.out.println("iohoihoihoihoihoiho;i");
                        showLineGraph1();
                    }


                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{//reponse为空
                Toast.makeText(getActivity(), "No record found during this period.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //compare two date
    public boolean isLessThan(String d1, String d2){
        int d1_y = Integer.valueOf(d1.split("-")[0]);
        int d1_m = Integer.valueOf(d1.split("-")[1]);
        int d1_d = Integer.valueOf(d1.split("-")[2]);
        int d2_y = Integer.valueOf(d2.split("-")[0]);
        int d2_m = Integer.valueOf(d2.split("-")[1]);
        int d2_d = Integer.valueOf(d2.split("-")[2]);
        if(d1_y < d2_y){
            return true;
        }else if(d1_y > d2_y){
            return false;
        }else{
            if(d1_m < d2_m){
                return true;
            }else if(d1_m > d2_m){
                return false;
            }else{
                if(d1_d < d2_d){
                    return true;
                }else if(d1_d > d2_d){
                    return false;
                }else{
                    return false;
                }
            }
        }
    }

    //check if the input dates are proper
    public boolean checkDateCorrectness(String d1, String d2){
        if(d1.equals("") || d2.equals("")){
            Toast.makeText(getActivity(), "Select date first!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            if(!isLessThan(d2, d1)){
                return true;
            }
            else{
                Toast.makeText(getActivity(), "Start date should be less than end date!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

}
