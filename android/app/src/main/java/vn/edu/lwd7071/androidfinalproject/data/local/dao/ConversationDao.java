package vn.edu.lwd7071.androidfinalproject.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

import vn.edu.lwd7071.androidfinalproject.data.local.entity.ConversationEntity;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.ConversationMemberEntity;

@Dao
public interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertIfAbsent(ConversationEntity conversation);

    @Upsert
    void upsert(ConversationEntity conversation);

    @Upsert
    void upsertMember(ConversationMemberEntity member);

    @Query("""
            SELECT c.* FROM conversations c LEFT JOIN users u ON u.uid = c.peerUserId
            WHERE (:unreadOnly = 0 OR c.unreadCount > 0)
            AND (:query = '' OR instr(lower(COALESCE(c.title, u.displayName, '')), lower(:query)) > 0
                OR instr(lower(COALESCE(c.lastMessageText, '')), lower(:query)) > 0)
            ORDER BY c.lastMessageAt DESC, c.id
            """)
    LiveData<List<ConversationEntity>> observeInbox(String query, boolean unreadOnly);

    @Query("DELETE FROM conversations WHERE id = :id")
    void delete(String id);
}
