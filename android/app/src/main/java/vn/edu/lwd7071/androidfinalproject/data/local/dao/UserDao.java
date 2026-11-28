package vn.edu.lwd7071.androidfinalproject.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import vn.edu.lwd7071.androidfinalproject.data.local.entity.UserEntity;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    UserEntity find(String uid);

    @Query("SELECT * FROM users WHERE uid = :uid")
    LiveData<UserEntity> observe(String uid);

    @Upsert
    void upsert(UserEntity user);
}
