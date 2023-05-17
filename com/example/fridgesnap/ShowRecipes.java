package com.example.fridgesnap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowRecipes extends AppCompatActivity {

    TextView discover, fav;
    Button closeApp;
    ArrayList<String> predictionList;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DiscoverFragment discoverFragment;
    private FavFragment favFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipes);

        closeApp = findViewById(R.id.closeApp);
        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowRecipes.this, MainActivity.class));
            }
        });

        predictionList = getIntent().getStringArrayListExtra("predictionList");
        //testText.setText(predictionList.toString());

        //List is transferred to this activity from FoodItemList, stored as predictionList
        //need to pass this list to an API

        discoverFragment = new DiscoverFragment();
        favFragment = new FavFragment();
        discover = findViewById(R.id.discoverTxt);
        fav = findViewById(R.id.favTxt);
        discover.setTypeface(null, Typeface.BOLD);
        fav.setTextSize(15);
        discover.setTextSize(20);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("predictionList", predictionList);
        discoverFragment.setArguments(bundle);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment, discoverFragment);
        fragmentTransaction.commit();



        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, discoverFragment);
                fragmentTransaction.commit();
                discover.setTypeface(null, Typeface.BOLD);
                fav.setTextSize(15);
                discover.setTextSize(20);
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, favFragment);
                fragmentTransaction.commit();
                fav.setTextSize(20);
                discover.setTextSize(15);
                fav.setTypeface(null, Typeface.BOLD);
            }
        });
    }
}