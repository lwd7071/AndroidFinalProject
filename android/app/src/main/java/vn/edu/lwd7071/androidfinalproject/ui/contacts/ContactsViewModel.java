package vn.edu.lwd7071.androidfinalproject.ui.contacts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import vn.edu.lwd7071.androidfinalproject.core.UiState;
import vn.edu.lwd7071.androidfinalproject.data.repository.UserRepository;
import vn.edu.lwd7071.androidfinalproject.model.Contact;

public final class ContactsViewModel extends ViewModel {
    private final MediatorLiveData<UiState<List<Contact>>> state = new MediatorLiveData<>();
    private List<Contact> contacts;
    private String query = "";
    private boolean stale;

    public ContactsViewModel(UserRepository repository) {
        state.setValue(new UiState.Loading<>());
        state.addSource(repository.observeContacts(), rows -> {
            contacts = rows == null ? Collections.emptyList() : rows;
            publish();
        });
    }

    public LiveData<UiState<List<Contact>>> state() {
        return state;
    }

    public void setQuery(String value) {
        query = value == null ? "" : value;
        publish();
    }

    public void setOffline(boolean offline) {
        stale = offline;
        publish();
    }

    private void publish() {
        if (contacts == null) return;
        List<Contact> filtered = filter(contacts, query);
        state.setValue(stale ? new UiState.Stale<>(filtered, "OFFLINE")
                : new UiState.Content<>(filtered));
    }

    public static List<Contact> filter(List<Contact> contacts, String query) {
        String needle = query.trim().toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) return contacts;
        List<Contact> matches = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.user().displayName().toLowerCase(Locale.ROOT).contains(needle)
                    || contact.user().phone().contains(needle)) {
                matches.add(contact);
            }
        }
        return matches;
    }
}
