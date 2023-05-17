package com.example.fridgesnap;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class FavFragment extends Fragment {
    private ArrayList<String> favRecipeTitles = new ArrayList<>();
    TextView txtTest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav, container, false);



        //retrieves the JSON string from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString("favRecipeTitles", null);

        //converts the JSON string to a list using the Google Gson library
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> favRecipeTitles = new Gson().fromJson(jsonString, type);
        LinearLayout cardContainer = view.findViewById(R.id.fav_list);

        if (favRecipeTitles != null){
            //loops through the recipe titles and create a CardView for each one
            for (String title : favRecipeTitles) {
                //inflates the CardView layout
                View cardView = inflater.inflate(R.layout.fav_card, container, false);

                //gets the TextView for the recipe title and set its value
                TextView titleTextView = cardView.findViewById(R.id.recipe_title);
                titleTextView.setText(title);



                //adds the CardView to the container ViewGroup
                cardContainer.addView(cardView);

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String recipeString = title;
                        Uri uri = Uri.parse("https://www.google.com/search?q=" + recipeString); //searches google with the term of the recipe title
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent); //opens the devices browser
                    }
                });
            }
        }
        else {
            Context ctx = view.getContext();
            String text = "No recipes have been found"; //if no recipes are found that use the item, display this message
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, text, duration);
            toast.show();
        }

        Button unfavBtn = view.findViewById(R.id.unfavbtn);
        unfavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardContainer.setVisibility(GONE); //removes the recipe from the favourites page when the unfavourite button is clicked
            }
        });

        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}