package com.example.fridgesnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class ForgottenPassword extends AppCompatActivity {


    Button runCap, continuePass;
    String site_key ="6LfgsPkkAAAAAEwDawHJ41nfY5RZ2oT0tqzVpFDR";
    String secret_key = "6LfgsPkkAAAAAOXamCIGt4drJ57EAbEpByf6vgeD";
    int captchaVar = 0;
    EditText email, pass1, pass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title

        setContentView(R.layout.activity_forgotten_password);

        email = findViewById(R.id.emailForgPass);
        pass1 = findViewById(R.id.newPassOne);
        pass2 = findViewById(R.id.newPassTwo);

        runCap = (Button)findViewById(R.id.secCap);
        runCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessCaptcha();
            }
        });

        continuePass = (Button)findViewById(R.id.continueForPass);
        continuePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();
                String pass1String = pass1.getText().toString();
                String pass2String = pass2.getText().toString();
                if ((emailString.contains("@") && (pass1String.equals(pass2String)))){
                    startActivity(new Intent(ForgottenPassword.this, MainActivity.class));
                }
                if (!pass1String.equals(pass2String)){
                    Toast.makeText(getApplicationContext(), "Make sure the passwords match", Toast.LENGTH_SHORT).show();
                }

                if (!emailString.contains("@")){
                    Toast.makeText(getApplicationContext(), "Incorrect email", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void ProcessCaptcha(){
        SafetyNet.getClient(ForgottenPassword.this).verifyWithRecaptcha("6LfgsPkkAAAAAEwDawHJ41nfY5RZ2oT0tqzVpFDR")
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        String captchaToken =recaptchaTokenResponse.getTokenResult();

                        if (captchaToken != null){
                            if (!captchaToken.isEmpty()){
                                processCaptchaStep();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        captchaVar = 0;
                    }
                });
    }
    private void processCaptchaStep(){
        Toast.makeText(getApplicationContext(),"Captcha Successful", Toast.LENGTH_LONG).show();
        captchaVar = 1;
    }

}