package com.example.vendor.ui;

import android.os.Bundle;

import com.example.vendor.R;
import com.example.vendor.ui.ui.buyers.BuyersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class BottomNavigation extends AppCompatActivity {

    public static final String EXTRA_DETAIL = "detail";
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_ADDRESS = "address";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_buyers)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        ///NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}
