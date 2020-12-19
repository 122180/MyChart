package com.example.mychart.changepassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mychart.R;
import com.example.mychart.profile.User_Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Change_Password extends AppCompatActivity {
    private TextInputEditText mpassword,mconformPassword;
    private View progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);

        mpassword=findViewById(R.id.password);
        mconformPassword=findViewById(R.id.conformpassword);
        progressbar=findViewById(R.id.progressbarshow);
    }

    public void ChangePassword(View view) {
        String password=mpassword.getText().toString().trim();
        String Conformpassword=mconformPassword.getText().toString().trim();
        if(password.equals(""))
            mpassword.setError("can't empty");
        else if(Conformpassword.equals(""))
            mconformPassword.setError("can't empty");
        else if(!password.equals(Conformpassword))
        {
            mconformPassword.setError("password is not matched");
        }
        else {
            progressbar.setVisibility(View.VISIBLE);
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

            if(firebaseUser!=null)
            {
                firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressbar.setVisibility(View.GONE);
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Change_Password.this, "password is changed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Change_Password.this, User_Profile.class));
                        }
                        else
                        {
                            Toast.makeText(Change_Password.this, task.getException()+"", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }




    }
}