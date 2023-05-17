package com.example.fridgesnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ManualItem extends AppCompatActivity {

    Button cont, submit, closeApp;
    EditText txtManual;
    private List<String> manualList = new ArrayList<>();
    TextView manTextList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_item);
        closeApp = findViewById(R.id.closeApp);
        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManualItem.this, MainActivity.class));
            }
        });
        manTextList = findViewById(R.id.itemLstManual);
        cont = findViewById(R.id.continueManual);
        submit = findViewById(R.id.submitManual);
        txtManual = findViewById(R.id.nameofitem);
        txtManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtManual.setText(" ");
            }
        });
        ArrayList<String> predictionList = getIntent().getStringArrayListExtra("predictionList");
        ArrayList<String> manualList = new ArrayList<String>();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String manText = txtManual.getText().toString();
                manualList.add(manText);
                manTextList.setText(manualList.toString());

                cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("manualList", manualList);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });







    }
}