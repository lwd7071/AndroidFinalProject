package vn.edu.lwd7071.androidfinalproject.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public record User(@NonNull String uid, @Nullable String email, @NonNull String phone,
        @NonNull String displayName, @Nullable String avatarUrl, long updatedAt) {}
