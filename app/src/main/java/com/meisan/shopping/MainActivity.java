package com.meisan.shopping;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
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
    private NavController.OnDestinationChangedListener authListener;

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

        // Handle back button press using OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() == R.id.itemListFragment) {
                    // Exit the app instead of going to login
                    finish();
                } else {
                    // Default back behavior for other screens
                    if (!navController.navigateUp()) {
                        finish();
                    }
                }
            }
        });

        // Create a listener reference to remove it later
        authListener = (controller, destination, arguments) -> {
            // Remove this listener after first navigation to avoid loops
            navController.removeOnDestinationChangedListener(authListener);

            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                CartManager.getInstance(); // Initialize CartManager for authenticated user
                // Navigate to shop if user is logged in and currently on login screen
                if (destination.getId() == R.id.loginFragment) {
                    navController.navigate(R.id.itemListFragment);
                }
            } else {
                // User is not logged in, navigate to login if not already there
                if (destination.getId() != R.id.loginFragment) {
                    navController.navigate(R.id.loginFragment);
                }
            }
        };

        // Check authentication state and navigate after NavController is ready
        navController.addOnDestinationChangedListener(authListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Authentication check is now handled in onCreate after NavController is ready
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}