package vn.edu.lwd7071.androidfinalproject.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = @Index("phone"))
public record UserEntity(@PrimaryKey @NonNull String uid, @Nullable String email,
        @NonNull String phone, @NonNull String displayName, @Nullable String avatarUrl,
        long updatedAt) {}
