package com.sasha.shopnow.start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sasha.shopnow.MainActivity;
import com.sasha.shopnow.R;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView cart_logo;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        cart_logo = findViewById(R.id.cart_logo);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),
        R.anim.splash_screen_animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cart_logo.startAnimation(animation);
    }
}