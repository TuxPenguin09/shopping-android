package com.meisan.shopping;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.meisan.shopping.utils.CartManager;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        mAuth = FirebaseAuth.getInstance();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Set up AppBar configuration - exclude login from having back button
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.loginFragment, R.id.itemListFragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check authentication state when activity starts
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            CartManager.getInstance(); // Initialize CartManager for authenticated user
            // Only navigate to shop if we're currently on login screen
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == R.id.loginFragment) {
                navController.navigate(R.id.itemListFragment);
            }
        } else {
            // User is not logged in, navigate to login
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() != R.id.loginFragment) {
                navController.navigate(R.id.loginFragment);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Handle back button press from itemListFragment (shop screen)
        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == R.id.itemListFragment) {
            // Exit the app instead of going to login
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}