package vn.edu.lwd7071.androidfinalproject.data.repository;

import androidx.lifecycle.LiveData;

import vn.edu.lwd7071.androidfinalproject.model.User;

public interface AuthRepository {
    enum SessionState { CHECKING_PROFILE, SIGNED_OUT, NEEDS_PROFILE, READY }

    LiveData<SessionState> observeSession();
    void register(String email, String password, String displayName, String phone,
            ResultCallback<User> callback);
    void signIn(String email, String password, ResultCallback<User> callback);
    void completeProfile(String displayName, String phone, ResultCallback<User> callback);
    void changePassword(String currentPassword, String newPassword, ResultCallback<Void> callback);
    void signOut(ResultCallback<Void> callback);
}
