package vn.edu.lwd7071.androidfinalproject.data.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import vn.edu.lwd7071.androidfinalproject.model.Contact;
import vn.edu.lwd7071.androidfinalproject.model.User;

public interface UserRepository {
    LiveData<User> observeUser(String uid);
    LiveData<List<Contact>> observeContacts();
    void findByPhone(String normalizedPhone, ResultCallback<User> callback);
    void searchByNamePrefix(String query, ResultCallback<List<User>> callback);
    void addContact(String contactUid, ResultCallback<Void> callback);
    void updateProfile(String displayName, String normalizedPhone, ResultCallback<Void> callback);
    void updateAvatar(String mediaUrl, ResultCallback<Void> callback);
}
