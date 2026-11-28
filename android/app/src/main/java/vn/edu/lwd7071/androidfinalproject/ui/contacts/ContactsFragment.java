package vn.edu.lwd7071.androidfinalproject.ui.contacts;

import android.os.Bundle;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import java.util.Collections;

import vn.edu.lwd7071.androidfinalproject.ChatApplication;
import vn.edu.lwd7071.androidfinalproject.R;
import vn.edu.lwd7071.androidfinalproject.core.UiState;
import vn.edu.lwd7071.androidfinalproject.data.repository.UserRepository;
import vn.edu.lwd7071.androidfinalproject.databinding.FragmentContactsBinding;
import vn.edu.lwd7071.androidfinalproject.model.Contact;

public final class ContactsFragment extends Fragment {
    private FragmentContactsBinding binding;
    private ContactsViewModel model;
    private ConnectivityManager connectivity;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        UserRepository repository = ((ChatApplication) requireActivity().getApplication())
                .container().userRepository();
        model = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return modelClass.cast(new ContactsViewModel(repository));
            }
        }).get(ContactsViewModel.class);
        ContactsAdapter adapter = new ContactsAdapter(contact -> NavHostFragment.findNavController(this)
                .navigate(ContactsFragmentDirections.actionContactsToUserProfile(contact.user().uid())));
        binding.recyclerContacts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerContacts.setAdapter(adapter);
        binding.buttonAddContact.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(ContactsFragmentDirections.actionContactsToAddContact()));
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                model.setQuery(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        model.state().observe(getViewLifecycleOwner(), state -> render(state, adapter));
    }

    @Override
    public void onStart() {
        super.onStart();
        connectivity = requireContext().getSystemService(ConnectivityManager.class);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override public void onAvailable(@NonNull Network network) { updateConnection(); }
            @Override public void onLost(@NonNull Network network) { updateConnection(); }
            @Override public void onCapabilitiesChanged(@NonNull Network network,
                    @NonNull NetworkCapabilities capabilities) { updateConnection(); }
        };
        connectivity.registerDefaultNetworkCallback(networkCallback);
        updateConnection();
    }

    private void updateConnection() {
        if (binding == null || connectivity == null) return;
        NetworkCapabilities capabilities = connectivity.getNetworkCapabilities(
                connectivity.getActiveNetwork());
        boolean offline = capabilities == null
                || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        binding.getRoot().post(() -> {
            if (binding != null && model != null) model.setOffline(offline);
        });
    }

    @Override
    public void onStop() {
        if (connectivity != null && networkCallback != null) {
            connectivity.unregisterNetworkCallback(networkCallback);
            networkCallback = null;
        }
        super.onStop();
    }

    private void render(UiState<List<Contact>> state, ContactsAdapter adapter) {
        boolean loading = state instanceof UiState.Loading;
        binding.progressContacts.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.textOffline.setVisibility(state instanceof UiState.Stale ? View.VISIBLE : View.GONE);
        List<Contact> rows = Collections.emptyList();
        if (state instanceof UiState.Content<?> content) {
            rows = contacts(content.data());
        } else if (state instanceof UiState.Stale<?> stale) {
            rows = contacts(stale.data());
        }
        adapter.submitList(rows);
        boolean empty = !loading && rows.isEmpty();
        binding.textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerContacts.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (empty) binding.textEmpty.setText(state instanceof UiState.Error
                ? R.string.contact_unavailable
                : binding.inputSearch.getText() != null
                && binding.inputSearch.getText().length() > 0
                ? R.string.contacts_no_matches : R.string.contacts_empty);
    }

    @SuppressWarnings("unchecked")
    private static List<Contact> contacts(Object data) {
        return (List<Contact>) data;
    }

    @Override
    public void onDestroyView() {
        binding.recyclerContacts.setAdapter(null);
        binding = null;
        super.onDestroyView();
    }
}
