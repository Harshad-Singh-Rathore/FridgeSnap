package com.example.fridgesnap;

import java.util.List;

public class RecipeResponse {

    private List<Recipe> results;
    private String sourceUrl;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public List<Recipe> getResults() {
        return results;
    }
}
