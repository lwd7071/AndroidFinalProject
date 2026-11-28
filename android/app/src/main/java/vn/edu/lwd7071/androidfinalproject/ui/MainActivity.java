package vn.edu.lwd7071.androidfinalproject.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import vn.edu.lwd7071.androidfinalproject.R;
import vn.edu.lwd7071.androidfinalproject.databinding.ActivityMainBinding;

public final class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return windowInsets;
        });

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);
        if (host == null) throw new IllegalStateException("Navigation host is missing");
        NavController navigation = host.getNavController();
        NavigationUI.setupWithNavController(binding.toolbar, navigation,
                new AppBarConfiguration.Builder(R.id.inboxFragment, R.id.contactsFragment,
                        R.id.profileFragment).build());
        NavigationUI.setupWithNavController(binding.bottomNavigation, navigation);
        navigation.addOnDestinationChangedListener((controller, destination, arguments) ->
                binding.bottomNavigation.setVisibility(
                        destination.getId() == R.id.inboxFragment
                                || destination.getId() == R.id.contactsFragment
                                || destination.getId() == R.id.profileFragment
                                ? View.VISIBLE : View.GONE));
    }
}
