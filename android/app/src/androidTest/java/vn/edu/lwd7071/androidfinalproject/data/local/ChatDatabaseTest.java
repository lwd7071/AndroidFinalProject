package vn.edu.lwd7071.androidfinalproject.data.local;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import vn.edu.lwd7071.androidfinalproject.data.local.entity.ContactEntity;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.ConversationEntity;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.MessageEntity;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.UserEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public final class ChatDatabaseTest {
    private ChatDatabase db;

    @Before
    public void setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                ChatDatabase.class).build();
    }

    @After
    public void close() {
        db.close();
    }

    @Test
    public void joinsCachedContactsAndClearsDataAtSessionBoundary() {
        db.users().upsert(new UserEntity("peer", "private@example.com", "0901234567",
                "Bao", null, 8));
        db.contacts().upsert(new ContactEntity("peer", 10));
        db.users().upsert(new UserEntity("peer", "private@example.com", "0912345678",
                "Bảo", null, 11));

        var row = db.contacts().list().get(0);
        assertEquals("Bảo", row.user().displayName());
        assertEquals("0912345678", row.user().phone());
        assertEquals(10, row.addedAt());
        assertNull(row.user().email());

        db.clearAllTables();
        assertNull(db.users().find("peer"));
        assertTrue(db.contacts().list().isEmpty());
    }

    @Test
    public void duplicateClientIdDoesNotReplaceStoredMessageAndRoomDeleteCascades() {
        db.conversations().insertIfAbsent(new ConversationEntity("direct_a_b", "DIRECT", null,
                null, "b", null, null, null, 0, null, 0, false));
        MessageEntity message = new MessageEntity("client", "direct_a_b", "a", "An", "TEXT",
                "Xin chào", null, null, null, null, null, null, null, null, 1, null, "SENDING", 0);
        db.messages().insertIfAbsent(message);
        assertEquals(-1, db.messages().insertIfAbsent(message));
        assertNotNull(db.messages().find("client"));

        db.conversations().delete("direct_a_b");
        assertNull(db.messages().find("client"));
    }
}
