package vn.edu.lwd7071.androidfinalproject.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import vn.edu.lwd7071.androidfinalproject.ChatApplication;
import vn.edu.lwd7071.androidfinalproject.core.UiState;
import vn.edu.lwd7071.androidfinalproject.data.repository.UserRepository;
import vn.edu.lwd7071.androidfinalproject.databinding.FragmentUserProfileBinding;
import vn.edu.lwd7071.androidfinalproject.model.User;

public final class UserProfileFragment extends Fragment {
    private FragmentUserProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String userId = UserProfileFragmentArgs.fromBundle(requireArguments()).getUserId();
        UserRepository repository = ((ChatApplication) requireActivity().getApplication())
                .container().userRepository();
        UserProfileViewModel model = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return modelClass.cast(new UserProfileViewModel(repository, userId));
            }
        }).get(UserProfileViewModel.class);
        binding.buttonBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        binding.buttonMessage.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(UserProfileFragmentDirections.actionUserProfileToChat()));
        model.state().observe(getViewLifecycleOwner(), this::render);
    }

    private void render(UiState<User> state) {
        binding.progressProfile.setVisibility(state instanceof UiState.Loading ? View.VISIBLE : View.GONE);
        binding.profileError.setVisibility(state instanceof UiState.Error ? View.VISIBLE : View.GONE);
        binding.profileContent.setVisibility(state instanceof UiState.Content ? View.VISIBLE : View.GONE);
        if (state instanceof UiState.Content<?> content) {
            User user = (User) content.data();
            String name = user.displayName();
            binding.textProfileAvatar.setText(name.isEmpty() ? "?" : name.substring(0, 1));
            binding.textProfileName.setText(name);
            binding.textProfilePhone.setText(user.phone());
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
