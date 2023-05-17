package com.example.fridgesnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    EditText namef, email, pass1, pass2;
    Button signUp;
    String pass1String, pass2String, emailString, nameString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title

        setContentView(R.layout.activity_sign_up);

        namef = findViewById(R.id.yourName);
        namef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                namef.setText("");
            }
        });

        email = findViewById(R.id.emailAdd);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setText("");
            }
        });

        pass1 = findViewById(R.id.passwordone);
        pass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass1.setText("");
            }
        });

        pass2 = findViewById(R.id.passwordtwo);
        pass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass2.setText("");
            }
        });

        signUp = findViewById(R.id.signuponscreenbtn);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailString = email.getText().toString();
                pass1String = pass1.getText().toString();
                pass2String = pass2.getText().toString();
                nameString = namef.getText().toString();

                if ((emailString.contains("@") && (pass1String.equals(pass2String)))){
                    startActivity(new Intent(SignUp.this, CameraUse.class));
                }

                if (!emailString.contains("@")){
                    Toast.makeText(getApplicationContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
                }

                if ((emailString == "") || (pass1String == "") || (pass2String == "") || (nameString == "")){
                    Toast.makeText(getApplicationContext(), "Please make sure all fields are filled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}