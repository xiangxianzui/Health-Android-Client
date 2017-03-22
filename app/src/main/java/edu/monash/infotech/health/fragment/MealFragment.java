package edu.monash.infotech.health.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import edu.monash.infotech.health.FoodDetailActivity;
import edu.monash.infotech.health.R;
import edu.monash.infotech.health.basic.BasicConfig;
import edu.monash.infotech.health.network.HTTPURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class MealFragment extends Fragment {

    private ListView mealItemListView;
    private ArrayList<String> mealArrayList;

    String serverIP = new BasicConfig().getServerIP();
    String serverPORT = new BasicConfig().getSERVER_PORT();
    private HTTPURLConnection conn;
    private String path = "http://" + serverIP + ":" + serverPORT + "/HealthServer/webresources/com.entity.health.food/findByCategory/meal";
    private ArrayAdapter<String> arrayAdapter = null;
    public MealFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_daily_diet_meal, container, false);
        conn = new HTTPURLConnection();
        new getMealFromWS().execute();

        mealItemListView = (ListView)view.findViewById(R.id.mealItemListView);
        mealArrayList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, mealArrayList);
        mealItemListView.setAdapter(arrayAdapter);

        mealItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("click list item: " + mealArrayList.get(position));
                String foodName = mealArrayList.get(position);
                Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
                intent.putExtra("foodName", foodName);
                startActivity(intent);
            }
        });

        return view;
    }

    private class getMealFromWS extends AsyncTask<Void, Void, Void> {
        String response = "";
        @Override
        protected Void doInBackground(Void... arg0){
            response = conn.GetFromWebServer(path);
            System.out.println("server response: " + response);
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
                    for(int i=0; i<jsonArray.length(); i++){
                        String name = jsonArray.getJSONObject(i).getString("name");
                        mealArrayList.add(name);
                        arrayAdapter.notifyDataSetChanged();
                    }


                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }

}
