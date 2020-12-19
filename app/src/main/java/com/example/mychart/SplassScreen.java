package com.example.mychart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mychart.Login.Login;

public class SplassScreen extends AppCompatActivity {
private ImageView mImageView;
private TextView mTextView;
private Animation mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splass_screen);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();
        mImageView=findViewById(R.id.imageView);
        mTextView=findViewById(R.id.textView3);
        mAnimation= AnimationUtils.loadAnimation(this,R.anim.custom_animation);

        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplassScreen.this, Login.class));
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mImageView.startAnimation(mAnimation);
        mTextView.startAnimation(mAnimation);

    }
}