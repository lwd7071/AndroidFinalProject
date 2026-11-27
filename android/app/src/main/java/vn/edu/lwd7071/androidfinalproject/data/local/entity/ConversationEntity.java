package vn.edu.lwd7071.androidfinalproject.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "conversations")
public record ConversationEntity(@PrimaryKey @NonNull String id, @NonNull String type,
        @Nullable String title, @Nullable String avatarUrl, @Nullable String peerUserId,
        @Nullable String lastMessageText, @Nullable String lastMessageType,
        @Nullable String lastMessageSenderId, long lastMessageAt, @Nullable Long myLastReadAt,
        int unreadCount, boolean isRemoteCreated) {}
