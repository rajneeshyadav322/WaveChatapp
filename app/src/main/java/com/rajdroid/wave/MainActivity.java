package com.rajdroid.wave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.rajdroid.wave.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding  mainBinding;
    FirebaseAuth firebaseAuth;

    String countryCode;
    String phoneNumber;

    ProgressDialog progressDialog;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codeSent;

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");

        countryCode = mainBinding.countryCodePicker.getSelectedCountryCodeWithPlus();

        mainBinding.countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = mainBinding.countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });


        mainBinding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mainBinding.getMobileNumber.getText().toString();
                if(number.length()<10) {
                    Toast.makeText(MainActivity.this,"Please enter a valid Number", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.show();
                    phoneNumber = countryCode + number;

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                                                .setPhoneNumber(phoneNumber)
                                                .setTimeout(60L, TimeUnit.SECONDS)
                                                .setActivity(MainActivity.this)
                                                .setCallbacks(mCallbacks)
                                                .build();

                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                codeSent = s;
                Intent intent = new Intent(MainActivity.this, OtpAuthenticate.class);
                intent.putExtra("otp", codeSent);
                startActivity(intent);
            }
        };
    }

}