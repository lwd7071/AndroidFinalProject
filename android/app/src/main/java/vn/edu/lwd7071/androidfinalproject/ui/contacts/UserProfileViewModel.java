package vn.edu.lwd7071.androidfinalproject.ui.contacts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import vn.edu.lwd7071.androidfinalproject.core.UiState;
import vn.edu.lwd7071.androidfinalproject.data.repository.UserRepository;
import vn.edu.lwd7071.androidfinalproject.model.User;

public final class UserProfileViewModel extends ViewModel {
    private final MediatorLiveData<UiState<User>> state = new MediatorLiveData<>();

    public UserProfileViewModel(UserRepository repository, String userId) {
        state.setValue(new UiState.Loading<>());
        state.addSource(repository.observeUser(userId), user -> state.setValue(
                user == null ? new UiState.Error<>("NOT_FOUND") : new UiState.Content<>(user)));
    }

    public LiveData<UiState<User>> state() {
        return state;
    }
}
