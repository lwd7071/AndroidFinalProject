package vn.edu.lwd7071.androidfinalproject.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "conversation_members", primaryKeys = {"conversationId", "userId"},
        foreignKeys = @ForeignKey(entity = ConversationEntity.class, parentColumns = "id",
                childColumns = "conversationId", onDelete = ForeignKey.CASCADE))
public record ConversationMemberEntity(@NonNull String conversationId, @NonNull String userId,
        @NonNull String role, @NonNull String displayName, @Nullable String avatarUrl) {}
