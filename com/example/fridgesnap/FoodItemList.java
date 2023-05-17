package com.example.fridgesnap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fridgesnap.ManualItem;
import com.example.fridgesnap.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FoodItemList extends AppCompatActivity {

    Button addManual, deleteBtn, continueList, closeBtn;
    TextView listLoc;
    private ArrayList<String> predictionList;
    private ArrayList<String> manualList;
    private ArrayList<String> predictedList;
    private LinearLayout checkBoxLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_list);

        closeBtn = findViewById(R.id.closeApp);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodItemList.this, MainActivity.class));
            }
        });

        predictionList = new ArrayList<>();

        listLoc = findViewById(R.id.foodItemText);
        deleteBtn = findViewById(R.id.deleteBtn);
        continueList = findViewById(R.id.continueList);
        addManual = findViewById(R.id.addmanual);
        checkBoxLayout = findViewById(R.id.checkbox_layout);
        predictionList = getIntent().getStringArrayListExtra("predictionList");
        showCheckBoxList(predictionList);


        addManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodItemList.this, ManualItem.class);
                intent.putStringArrayListExtra("predictionList", predictionList);
                startActivityForResult(intent, 1);

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> selectItems = new ArrayList<>();
                for (int i = 0; i < checkBoxLayout.getChildCount(); i++){
                    CheckBox checkBox = (CheckBox) checkBoxLayout.getChildAt(i);
                    if (checkBox.isChecked()){
                        selectItems.add(checkBox.getText().toString());
                    }
                }
                predictionList.removeAll(selectItems);
                showCheckBoxList(predictionList);
            }
        });

        continueList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (predictionList.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Add items to the list to continue", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intentList = new Intent(FoodItemList.this, ShowRecipes.class);
                    intentList.putStringArrayListExtra("predictionList", predictionList);
                    startActivity(intentList);
                }



            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> manualList = data.getStringArrayListExtra("manualList");
                predictionList.addAll(manualList);
                showCheckBoxList(predictionList);
            }
        }
    }

    private String formatList(ArrayList<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String item : list) {
            builder.append("\u2022 ").append(item).append("\n");
        }
        return builder.toString();
    }

    private void showCheckBoxList(ArrayList<String> list){
        checkBoxLayout.removeAllViews();
        for (String item : list){
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(item);
            //set text color to black
            int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
            checkBox.setButtonDrawable(id);
            checkBox.setTextColor(Color.BLACK);
            checkBoxLayout.addView(checkBox);
        }
    }

}
