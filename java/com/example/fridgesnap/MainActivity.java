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

public class MainActivity extends AppCompatActivity {

    Button signUp;
    TextView forgPass;
    EditText email;
    EditText pass;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title

        setContentView(R.layout.activity_main);

        signUp = (Button)findViewById(R.id.signUpBtn);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        forgPass = (TextView)findViewById(R.id.txtForgotPass);
        forgPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ForgottenPassword.class));
            }
        });

        email = (EditText) findViewById(R.id.emailSignIn);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setText("");
            }
        });

        pass = (EditText) findViewById(R.id.passSignin);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass.setText("");
            }
        });



        signIn = (Button)findViewById(R.id.signInBtn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();
                if (emailString.contains("@")) {
                    startActivity(new Intent(MainActivity.this, CameraUse.class));
                }
                else{
                    Toast.makeText(getApplicationContext(), "Enter a valid email", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}