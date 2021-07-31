package com.rajdroid.wave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rajdroid.wave.databinding.ActivityOtpAuthenticateBinding;

public class OtpAuthenticate extends AppCompatActivity {

    ActivityOtpAuthenticateBinding binding;

    String enteredOtp;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpAuthenticateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();

        binding.changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtpAuthenticate.this, MainActivity.class);
                startActivity(intent);
            }
        });


        binding.verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredOtp = binding.getOTP.getText().toString();
                if(enteredOtp.isEmpty()) {
                    Toast.makeText(OtpAuthenticate.this, "Enter OTP first",Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.verifyingOtpProgressBar.setVisibility(View.VISIBLE);
                    String codeReceived = getIntent().getStringExtra("otp");
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeReceived, enteredOtp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    binding.verifyingOtpProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(OtpAuthenticate.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OtpAuthenticate.this, SetProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    binding.verifyingOtpProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(OtpAuthenticate.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}