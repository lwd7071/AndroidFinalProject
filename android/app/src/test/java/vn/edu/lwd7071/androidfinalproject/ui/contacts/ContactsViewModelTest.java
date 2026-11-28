package vn.edu.lwd7071.androidfinalproject.ui.contacts;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import vn.edu.lwd7071.androidfinalproject.model.Contact;
import vn.edu.lwd7071.androidfinalproject.model.User;

public final class ContactsViewModelTest {
    @Test
    public void searchMatchesNameOrPhone() {
        List<Contact> contacts = List.of(
                new Contact(new User("a", null, "0901234567", "Minh Anh", null, 0), 0),
                new Contact(new User("b", null, "0987654321", "Bình", null, 0), 0));

        assertEquals("a", ContactsViewModel.filter(contacts, "MINH").get(0).user().uid());
        assertEquals("b", ContactsViewModel.filter(contacts, "4321").get(0).user().uid());
        assertEquals(0, ContactsViewModel.filter(contacts, "không có").size());
    }
}
