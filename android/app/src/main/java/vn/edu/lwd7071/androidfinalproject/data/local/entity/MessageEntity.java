package vn.edu.lwd7071.androidfinalproject.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages", indices = @Index({"conversationId", "createdAt"}),
        foreignKeys = @ForeignKey(entity = ConversationEntity.class, parentColumns = "id",
                childColumns = "conversationId", onDelete = ForeignKey.CASCADE))
public record MessageEntity(@PrimaryKey @NonNull String clientId, @NonNull String conversationId,
        @NonNull String senderId, @NonNull String senderName, @NonNull String type,
        @Nullable String text, @Nullable String mediaUrl, @Nullable String localMediaPath,
        @Nullable String fileName, @Nullable Long fileSize, @Nullable String mimeType,
        @Nullable Long durationMs, @Nullable Double latitude, @Nullable Double longitude,
        long createdAt, @Nullable Long serverCreatedAt, @NonNull String status,
        @ColumnInfo(defaultValue = "0") int retryCount) {}
