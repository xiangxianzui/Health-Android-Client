package edu.monash.infotech.health;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import edu.monash.infotech.health.fragment.OneDayReportFragment;
import edu.monash.infotech.health.fragment.PeriodFragment;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener{

    private Button OneDayReportButton;
    private Button PeriodReportButton;

    private OneDayReportFragment oneDayReportFragment;
    private PeriodFragment periodReportFragment;
    private FragmentManager fm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        OneDayReportButton = (Button)findViewById(R.id.OneDayReportButton);
        PeriodReportButton = (Button)findViewById(R.id.PeriodReportButton);

        OneDayReportButton.setOnClickListener(this);
        PeriodReportButton.setOnClickListener(this);

        fm = getFragmentManager();
        setDefaultFragment();
        
    }

    private void setDefaultFragment(){
        FragmentTransaction transaction = fm.beginTransaction();
        if(oneDayReportFragment==null){
            oneDayReportFragment = new OneDayReportFragment();
            transaction.add(R.id.id_report_content,oneDayReportFragment, "oneDayReportFragment");
        }
        else{
            transaction.show(oneDayReportFragment);
        }

        transaction.commit();
    }

    @Override
    public void onClick(View v){
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        switch (v.getId()){
            case R.id.OneDayReportButton:
                OneDayReportButton.setBackgroundColor(Color.parseColor("#FF6347"));
                PeriodReportButton.setBackgroundColor(Color.TRANSPARENT);
                if(oneDayReportFragment == null){
                    oneDayReportFragment = new OneDayReportFragment();
                    transaction.add(R.id.id_report_content, oneDayReportFragment, "oneDayReportFragment");
                }
                else{
                    transaction.show(oneDayReportFragment);
                }
                break;
            case R.id.PeriodReportButton:
                PeriodReportButton.setBackgroundColor(Color.parseColor("#FF6347"));
                OneDayReportButton.setBackgroundColor(Color.TRANSPARENT);
                if(periodReportFragment == null){
                    periodReportFragment = new PeriodFragment();
                    transaction.add(R.id.id_report_content, periodReportFragment, "periodReportFragment");
                }
                else {
                    transaction.show(periodReportFragment);
                }
                break;
        }

        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if(oneDayReportFragment != null){
            transaction.hide(oneDayReportFragment);
        }
        if(periodReportFragment != null){
            transaction.hide(periodReportFragment);
        }
    }

}
