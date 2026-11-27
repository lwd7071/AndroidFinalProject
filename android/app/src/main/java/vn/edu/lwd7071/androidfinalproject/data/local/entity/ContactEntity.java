package vn.edu.lwd7071.androidfinalproject.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public record ContactEntity(@PrimaryKey @NonNull String contactUid, long addedAt) {}
