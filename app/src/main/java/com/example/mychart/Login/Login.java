package com.example.mychart.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mychart.MainActivity;
import com.example.mychart.NetworkMessage;
import com.example.mychart.R;
import com.example.mychart.ResetPassword.Reset_password;
import com.example.mychart.common.Util;
import com.example.mychart.signup.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;

import java.io.File;

public class Login extends AppCompatActivity {

    private TextInputEditText username,password;

   private LinearLayout mLinearLayout;
   private FirebaseAuth mFirebaseAuth;
   private  View progressbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        progressbar=findViewById(R.id.progressbarshow);

        mLinearLayout=findViewById(R.id.linearlayout);
        mFirebaseAuth=FirebaseAuth.getInstance();
        getSupportActionBar().hide();

         mLinearLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(Login.this, SignUp.class));
                 Toast.makeText(Login.this, "go to sign up activity", Toast.LENGTH_SHORT).show();
             }
         });


    }


    public void setLogin(View view) {
    if(Util.ConnectionAvailable(this)) {


        String userName = username.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "filled can't be empty", Toast.LENGTH_SHORT).show();
        } else {
            progressbar.setVisibility(View.VISIBLE);
            mFirebaseAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressbar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                        Toast.makeText(Login.this, "Login suceessfull", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(Login.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    else
    {
        startActivity(new Intent(Login.this, NetworkMessage.class));
    }
    }

    public void ForgetPassword(View view) {
        startActivity(new Intent(Login.this, Reset_password.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=mFirebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(new OnSuccessListener<InstallationTokenResult>() {
                @Override
                public void onSuccess(InstallationTokenResult installationTokenResult) {

                    Util.updateDeviseToken(Login.this,installationTokenResult.getToken());


                }
            });
            startActivity(new Intent(Login.this,MainActivity.class));
            finish();

        }



    }
}