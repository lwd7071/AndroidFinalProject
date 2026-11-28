package vn.edu.lwd7071.androidfinalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import vn.edu.lwd7071.androidfinalproject.R;
import vn.edu.lwd7071.androidfinalproject.databinding.FragmentPlaceholderBinding;

public final class PlaceholderFragment extends Fragment {
    private FragmentPlaceholderBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaceholderBinding.inflate(inflater, container, false);
        int destination = NavHostFragment.findNavController(this).getCurrentDestination().getId();
        binding.placeholderMessage.setText(destination == R.id.inboxFragment
                ? R.string.inbox_phase_two
                : destination == R.id.profileFragment ? R.string.profile_pending
                : R.string.screen_pending);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
