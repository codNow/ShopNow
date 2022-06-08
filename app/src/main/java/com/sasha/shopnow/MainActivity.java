package com.sasha.shopnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sasha.shopnow.uploads.UploadSliderImagesActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageSlider imageSlider;
    private ArrayList<SlideModel> slideModelArrayList;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        imageSlider = findViewById(R.id.imageSlider);
        progressBar = findViewById(R.id.progressBar);

        coordinatorLayout = findViewById(R.id.coordinator);

        getNavigationView();

        getImageSlider();
    }

    private void getImageSlider() {

        progressBar.setVisibility(View.VISIBLE);

        slideModelArrayList = new ArrayList<>();
        coordinatorLayout.setVisibility(View.INVISIBLE);

        final DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference().child("Slider");

        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    slideModelArrayList.add(new SlideModel(dataSnapshot.getValue().toString(),
                            ScaleTypes.FIT));

                    imageSlider.setImageList(slideModelArrayList, ScaleTypes.FIT);
                    imageSlider.startSliding(3000);

                    progressBar.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.VISIBLE);

                    imageSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                            if (i == 0){
                                startActivity(new Intent());
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNavigationView() {

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setSelected(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.home_menu:
                        startActivity(new Intent(getIntent()));
                        break;
                    case R.id.upload_slider:
                        startActivity(new Intent(MainActivity.this, UploadSliderImagesActivity.class));
                        break;
                }
                return false;
            }
        });

        View headerView = navigationView.getHeaderView(0);
        ImageView closeBtn = headerView.findViewById(R.id.close_button);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }


}