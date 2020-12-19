package com.example.mychart.ResetPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.mychart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Reset_password extends AppCompatActivity {
private TextInputEditText memail;
private TextView showMessage;
private LinearLayout mLinearLayoutEmail,mLinearLayoutMessage;
private Button Retry,Close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);
         memail =findViewById(R.id.email_id);
         showMessage=findViewById(R.id.tvMessage);
         mLinearLayoutEmail=findViewById(R.id.linearlayoutEmail);
         mLinearLayoutMessage=findViewById(R.id.linearlayoutMessage);
         Retry=findViewById(R.id.retry);
         Close=findViewById(R.id.btnClose);

    }


    public  void ResetPasswordWithEmail(View view)
    {
        final String email=memail.getText().toString().trim();
        if(email.equals(""))
        {
            memail.setError("email can't be empty");
        }
        else {
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mLinearLayoutEmail.setVisibility(View.GONE);
                    mLinearLayoutMessage.setVisibility(View.VISIBLE);
                    if(task.isSuccessful())
                    {
                        showMessage.setText(getString(R.string.reset_password_instruction,email));
                        new CountDownTimer(60000,1000)
                        {

                            @Override
                            public void onTick(long millisUntilFinished) {
                              Retry.setText(getString(R.string.Retry_Timer,String.valueOf(millisUntilFinished/1000)));
                               Retry.setOnClickListener(null);
                            }

                            @Override
                            public void onFinish() {
                                    Retry.setText(getString(R.string.retry));
                                    Retry.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mLinearLayoutEmail.setVisibility(View.VISIBLE);
                                            mLinearLayoutMessage.setVisibility(View.GONE);
                                        }
                                    });
                            }
                        }.start();
                    }
                    else
                    {
                        showMessage.setText(getString(R.string.Task_is_failed)+":"+task.getException());

                        Retry.setText(getString(R.string.retry));
                        Retry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLinearLayoutEmail.setVisibility(View.VISIBLE);
                                mLinearLayoutMessage.setVisibility(View.GONE);
                            }
                        });

                    }
                }
            });

        }
    }
    public void btnClose(View view)
    {
        finish();
    }
}