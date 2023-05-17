package com.example.fridgesnap;

import java.util.List;


public class Recipe {
    String name;
    String sourceUrl;
    String image;
    String title;
    String thumbnail;
    String summary;
    String imageURL;
    List<String> ingredients;
    String aisle;


    public Recipe(String title, String imageURL, String aisle, List<String> ingredients, String sourceUrl, String summary) {
        this.title = title;
        this.imageURL = imageURL;
        this.ingredients = ingredients;
        this.sourceUrl = sourceUrl;
        this.summary = summary;
        this.aisle = aisle;
    }

    public String getAisle() {
        return aisle;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getImage() {
        return image;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
