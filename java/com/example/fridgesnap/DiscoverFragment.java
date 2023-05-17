    package com.example.fridgesnap;

    import static android.view.View.GONE;

    import androidx.annotation.Nullable;
    import androidx.cardview.widget.CardView;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;

    import com.google.gson.Gson;
    import com.squareup.picasso.Picasso;

    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.net.Uri;
    import android.os.Bundle;
    import android.text.Html;
    import android.text.TextUtils;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.CompoundButton;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.ToggleButton;

    import com.bumptech.glide.Glide;

    import java.lang.reflect.Array;
    import java.util.ArrayList;
    import java.util.List;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;
    import retrofit2.http.GET;
    import retrofit2.http.Query;

    public class DiscoverFragment extends Fragment {
        private static final String BASE_URL = "https://api.spoonacular.com/recipes/"; //initializes the spoonacular API
        private static final String API_KEY = "e62a61ea67fb42bf96dc111776020ae3";
        private ArrayList<String> predictionList; //initializes other variables to be used by this class
        private ViewGroup recipeList;
        private RecipeService recipeService;
        ToggleButton toggleButton;
        ArrayList<String> favRecipeTitles = new ArrayList<>();




        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getArguments() != null) { //gets the contents of the predictionList class from the previous cameraUse class.
                predictionList = getArguments().getStringArrayList("predictionList");
            }
            Retrofit retrofit = new Retrofit.Builder() //creates a retrofit builder
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            recipeService = retrofit.create(RecipeService.class);



        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_discover, container, false); //creates and inflates a view
            recipeList = view.findViewById(R.id.recipe_list); //sets the contents of the recipeList to the view from the discover fragment xml file.
            LayoutInflater inflater_two = LayoutInflater.from(requireContext()); //creates a layoutinflater
            View recipeCard = inflater_two.inflate(R.layout.recipe_card, null);

            loadRecipes(); //calls the loadRecipes method

            return view;


        }

        private void loadRecipes() {
            for (int i = 0; i < predictionList.size(); i++) { //initializes for loop - set to run for the amount of items within the predictionList
                recipeService.getRecipes(API_KEY, predictionList.get(i)).enqueue(new Callback<RecipeResponse>() {
                    @Override
                    public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                        RecipeResponse recipeResponse = response.body();
                        if (recipeResponse != null) {
                            List<Recipe> recipes = recipeResponse.getResults();
                            for (Recipe recipe : recipes) {
                                addRecipeCard(recipe);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RecipeResponse> call, Throwable t) {

                    }
                });
            }
        }

        private void addRecipeCard(Recipe recipe) { //creates the cards for each recipe
            CardView recipeCard = (CardView) getLayoutInflater().inflate(R.layout.recipe_card, recipeList, false);
            ImageView recipeImage = recipeCard.findViewById(R.id.recipe_image); //gets the image of the recipe
            TextView recipeTitle = recipeCard.findViewById(R.id.recipe_title); //sets the title of the recipe
            Button favBtn = recipeCard.findViewById(R.id.favbtn); //adds a favourite and hide button to each card.
            Button hideBtn = recipeCard.findViewById(R.id.hidebtn);

            hideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recipeCard.setVisibility(GONE); //hides the card when the hide button is clicked
                }
            });

            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favRecipeTitles.add(recipe.getTitle());
                        //converts the list to a json string
                        String jsonString = new Gson().toJson(favRecipeTitles);
                        //initalizes sharedpreferences and stores the json string
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("favRecipeTitles", jsonString);
                        editor.apply();
                }
            });

            recipeTitle.setText(recipe.getTitle());

            String recipeUrl = recipe.getSourceUrl();

            Glide.with(requireContext())
                   .load(recipe.getImage())
                    .into(recipeImage);



            recipeCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String recipeString = recipeTitle.getText().toString();
                    Uri uri = Uri.parse("https://www.google.com/search?q=" + recipeString);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

            recipeList.addView(recipeCard);
        }




        private interface RecipeService {
            @GET("complexSearch")
            Call<RecipeResponse> getRecipes(@Query("apiKey") String apiKey, @Query("query") String query);
        }


    }
