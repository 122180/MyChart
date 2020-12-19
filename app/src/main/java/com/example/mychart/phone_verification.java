package com.example.mychart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mychart.Login.Login;
import com.example.mychart.common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class phone_verification extends AppCompatActivity {
   private  DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
         rootRef=FirebaseDatabase.getInstance().getReference();
    }

    public void goToHomeFromOTP(View view) {
        startActivity(new Intent(this, Login.class));
        finish();

    }

    public void callNextScreenFromOTP(View view) {

        rootRef.child(Common.PHONE);

    }
}