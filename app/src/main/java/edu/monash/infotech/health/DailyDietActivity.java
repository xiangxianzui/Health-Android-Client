package edu.monash.infotech.health;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import edu.monash.infotech.health.fragment.BreadFragment;
import edu.monash.infotech.health.fragment.CakeFragment;
import edu.monash.infotech.health.fragment.DrinkFragment;
import edu.monash.infotech.health.fragment.FruitFragment;
import edu.monash.infotech.health.fragment.MealFragment;
import edu.monash.infotech.health.fragment.MeatFragment;
import edu.monash.infotech.health.fragment.SnackFragment;
import edu.monash.infotech.health.fragment.VeggieFragment;


public class DailyDietActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton drinkImageButton;
    private ImageButton mealImageButton;
    private ImageButton meatImageButton;
    private ImageButton snackImageButton;
    private ImageButton breadImageButton;
    private ImageButton cakeImageButton;
    private ImageButton fruitImageButton;
    private ImageButton veggieImageButton;

    private DrinkFragment drinkFragment;
    private MealFragment mealFragment;
    private MeatFragment meatFragment;
    private SnackFragment snackFragment;
    private BreadFragment breadFragment;
    private CakeFragment cakeFragment;
    private FruitFragment fruitFragment;
    private VeggieFragment veggieFragment;
    private FragmentManager fm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_diet);

        drinkImageButton = (ImageButton)findViewById(R.id.drinkImageButton);
        mealImageButton = (ImageButton)findViewById(R.id.mealImageButton);
        meatImageButton = (ImageButton)findViewById(R.id.meatImageButton);
        snackImageButton = (ImageButton)findViewById(R.id.snackImageButton);
        breadImageButton = (ImageButton)findViewById(R.id.breadImageButton);
        cakeImageButton = (ImageButton)findViewById(R.id.cakeImageButton);
        fruitImageButton = (ImageButton)findViewById(R.id.fruitImageButton);
        veggieImageButton = (ImageButton)findViewById(R.id.veggieImageButton);
        drinkImageButton.setOnClickListener(this);
        mealImageButton.setOnClickListener(this);
        meatImageButton.setOnClickListener(this);
        snackImageButton.setOnClickListener(this);
        breadImageButton.setOnClickListener(this);
        cakeImageButton.setOnClickListener(this);
        fruitImageButton.setOnClickListener(this);
        veggieImageButton.setOnClickListener(this);
        fm = getFragmentManager();
        //setDefaultFragment();

    }

    private void setDefaultFragment(){
        FragmentTransaction transaction = fm.beginTransaction();
//        drinkFragment = new DrinkFragment();
        if(drinkFragment==null){
            drinkFragment = new DrinkFragment();
            transaction.add(R.id.id_daily_diet_content,drinkFragment, "drinkFragment");
        }
        else{
            transaction.show(drinkFragment);
        }

        transaction.commit();
    }

    @Override
    public void onClick(View v){
//        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        switch (v.getId()){
            case R.id.drinkImageButton:
                if(drinkFragment==null){
                    drinkFragment = new DrinkFragment();
                    transaction.add(R.id.id_daily_diet_content,drinkFragment, "drinkFragment");
                }
                else{
                    transaction.show(drinkFragment);
                }
                break;
            case R.id.mealImageButton:
                if(mealFragment==null){
                    mealFragment = new MealFragment();
                    transaction.add(R.id.id_daily_diet_content,mealFragment, "mealFragment");
                }
                else{
                    transaction.show(mealFragment);
                }
                //transaction.replace(R.id.id_daily_diet_content, mealFragment);
                break;
            case R.id.meatImageButton:
                if(meatFragment==null){
                    meatFragment = new MeatFragment();
                    transaction.add(R.id.id_daily_diet_content,meatFragment, "meatFragment");
                }
                else{
                    transaction.show(meatFragment);
                }
                break;
            case R.id.snackImageButton:
                if(snackFragment==null){
                    snackFragment = new SnackFragment();
                    transaction.add(R.id.id_daily_diet_content,snackFragment, "snackFragment");
                }
                else{
                    transaction.show(snackFragment);
                }
                break;
            case R.id.breadImageButton:
                if(breadFragment==null){
                    breadFragment = new BreadFragment();
                    transaction.add(R.id.id_daily_diet_content,breadFragment, "breadFragment");
                }
                else{
                    transaction.show(breadFragment);
                }
                break;
            case R.id.cakeImageButton:
                if(cakeFragment==null){
                    cakeFragment = new CakeFragment();
                    transaction.add(R.id.id_daily_diet_content,cakeFragment, "cakeFragment");
                }
                else{
                    transaction.show(cakeFragment);
                }
                break;
            case R.id.fruitImageButton:
                if(fruitFragment==null){
                    fruitFragment = new FruitFragment();
                    transaction.add(R.id.id_daily_diet_content,fruitFragment, "fruitFragment");
                }
                else{
                    transaction.show(fruitFragment);
                }
                break;
            case R.id.veggieImageButton:
                if(veggieFragment==null){
                    veggieFragment = new VeggieFragment();
                    transaction.add(R.id.id_daily_diet_content,veggieFragment, "veggieFragment");
                }
                else{
                    transaction.show(veggieFragment);
                }
                break;
        }

        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if(drinkFragment != null){
            transaction.hide(drinkFragment);
        }
        if(mealFragment != null){
            transaction.hide(mealFragment);
        }
        if(breadFragment != null){
            transaction.hide(breadFragment);
        }
        if(cakeFragment != null){
            transaction.hide(cakeFragment);
        }
        if(fruitFragment != null){
            transaction.hide(fruitFragment);
        }
        if(meatFragment != null){
            transaction.hide(meatFragment);
        }
        if(snackFragment != null){
            transaction.hide(snackFragment);
        }
        if(veggieFragment != null){
            transaction.hide(veggieFragment);
        }

    }

}
