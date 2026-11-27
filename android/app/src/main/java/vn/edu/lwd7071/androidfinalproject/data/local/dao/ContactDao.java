package vn.edu.lwd7071.androidfinalproject.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

import vn.edu.lwd7071.androidfinalproject.data.local.entity.ContactEntity;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.UserEntity;

@Dao
public interface ContactDao {
    String SELECT_CONTACTS = """
            SELECT users.uid, NULL AS email, users.phone, users.displayName,
                   users.avatarUrl, users.updatedAt, contacts.addedAt
            FROM contacts JOIN users ON users.uid = contacts.contactUid
            ORDER BY users.displayName COLLATE NOCASE, users.uid
            """;

    @Query(SELECT_CONTACTS)
    LiveData<List<ContactRow>> observeAll();

    @Query(SELECT_CONTACTS)
    List<ContactRow> list();

    @Upsert
    void upsert(ContactEntity contact);

    record ContactRow(@Embedded UserEntity user, long addedAt) {}
}
