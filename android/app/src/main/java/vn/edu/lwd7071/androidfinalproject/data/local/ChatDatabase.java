package vn.edu.lwd7071.androidfinalproject.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import vn.edu.lwd7071.androidfinalproject.data.local.dao.ContactDao;
import vn.edu.lwd7071.androidfinalproject.data.local.dao.ConversationDao;
import vn.edu.lwd7071.androidfinalproject.data.local.dao.MessageDao;
import vn.edu.lwd7071.androidfinalproject.data.local.dao.UserDao;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.ContactEntity;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.ConversationEntity;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.ConversationMemberEntity;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.MessageEntity;
import vn.edu.lwd7071.androidfinalproject.data.local.entity.UserEntity;

@Database(entities = {UserEntity.class, ContactEntity.class, ConversationEntity.class,
        ConversationMemberEntity.class, MessageEntity.class}, version = 1, exportSchema = true)
public abstract class ChatDatabase extends RoomDatabase {
    public abstract UserDao users();
    public abstract ContactDao contacts();
    public abstract ConversationDao conversations();
    public abstract MessageDao messages();
}
