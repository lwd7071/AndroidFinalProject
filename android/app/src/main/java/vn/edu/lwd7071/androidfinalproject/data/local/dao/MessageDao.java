package vn.edu.lwd7071.androidfinalproject.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import vn.edu.lwd7071.androidfinalproject.data.local.entity.MessageEntity;

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertIfAbsent(MessageEntity message);

    @Query("SELECT * FROM messages WHERE clientId = :clientId")
    MessageEntity find(String clientId);

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt, clientId")
    LiveData<List<MessageEntity>> observeMessages(String conversationId);
}
