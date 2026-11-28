package vn.edu.lwd7071.androidfinalproject.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

import vn.edu.lwd7071.androidfinalproject.core.ApiResult;
import vn.edu.lwd7071.androidfinalproject.data.local.ChatDatabase;
import vn.edu.lwd7071.androidfinalproject.data.local.dao.ContactDao;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.UserEntity;
import vn.edu.lwd7071.androidfinalproject.model.Contact;
import vn.edu.lwd7071.androidfinalproject.model.User;

public final class CachedUserRepository implements UserRepository {
    private final ChatDatabase database;

    public CachedUserRepository(ChatDatabase database) {
        this.database = database;
    }

    private static User user(UserEntity row) {
        return new User(row.uid(), null, row.phone(), row.displayName(),
                row.avatarUrl(), row.updatedAt());
    }

    @Override
    public LiveData<User> observeUser(String uid) {
        MediatorLiveData<User> result = new MediatorLiveData<>();
        result.addSource(database.users().observe(uid),
                row -> result.setValue(row == null ? null : user(row)));
        return result;
    }

    @Override
    public LiveData<List<Contact>> observeContacts() {
        MediatorLiveData<List<Contact>> result = new MediatorLiveData<>();
        result.addSource(database.contacts().observeAll(), rows -> {
            List<Contact> contacts = new ArrayList<>();
            if (rows != null) {
                for (ContactDao.ContactRow row : rows) {
                    contacts.add(new Contact(user(row.user()), row.addedAt()));
                }
            }
            result.setValue(contacts);
        });
        return result;
    }

    // shortcut: reads Room only until TV2 supplies Firebase writes and sync.
    private <T> void unavailable(ResultCallback<T> callback) {
        callback.onResult(new ApiResult.Failure<>("NOT_READY", "Account repository is not connected yet"));
    }

    @Override
    public void findByPhone(String normalizedPhone, ResultCallback<User> callback) {
        unavailable(callback);
    }

    @Override
    public void searchByNamePrefix(String query, ResultCallback<List<User>> callback) {
        unavailable(callback);
    }

    @Override
    public void addContact(String contactUid, ResultCallback<Void> callback) {
        unavailable(callback);
    }

    @Override
    public void updateProfile(String displayName, String normalizedPhone, ResultCallback<Void> callback) {
        unavailable(callback);
    }

    @Override
    public void updateAvatar(String mediaUrl, ResultCallback<Void> callback) {
        unavailable(callback);
    }
}
