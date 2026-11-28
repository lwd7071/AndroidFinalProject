package vn.edu.lwd7071.androidfinalproject.ui.contacts;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

import vn.edu.lwd7071.androidfinalproject.databinding.ItemContactBinding;
import vn.edu.lwd7071.androidfinalproject.model.Contact;

final class ContactsAdapter extends ListAdapter<Contact, ContactsAdapter.Holder> {
    private final Consumer<Contact> onClick;

    ContactsAdapter(Consumer<Contact> onClick) {
        super(new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
                return oldItem.user().uid().equals(newItem.user().uid());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemContactBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Contact contact = getItem(position);
        String name = contact.user().displayName();
        holder.binding.textAvatar.setText(name.isEmpty() ? "?" : name.substring(0, 1));
        holder.binding.textName.setText(name);
        holder.binding.textPhone.setText(contact.user().phone());
        holder.binding.getRoot().setOnClickListener(view -> onClick.accept(contact));
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemContactBinding binding;

        Holder(ItemContactBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
