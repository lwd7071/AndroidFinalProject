package vn.edu.lwd7071.androidfinalproject.di;

import android.content.Context;

import androidx.room.Room;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;

import vn.edu.lwd7071.androidfinalproject.BuildConfig;
import vn.edu.lwd7071.androidfinalproject.core.AppExecutors;
import vn.edu.lwd7071.androidfinalproject.data.local.ChatDatabase;
import vn.edu.lwd7071.androidfinalproject.data.repository.CachedUserRepository;
import vn.edu.lwd7071.androidfinalproject.data.repository.UserRepository;

public final class AppContainer {
    private static final String EMULATOR_HOST = "10.0.2.2";
    private final Context context;
    private final ChatDatabase database;
    private final AppExecutors executors = new AppExecutors();
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private final UserRepository userRepository;

    public AppContainer(Context context) {
        this.context = context.getApplicationContext();
        database = Room.databaseBuilder(this.context, ChatDatabase.class, "chat.db").build();
        userRepository = new CachedUserRepository(database);
    }

    public ChatDatabase database() {
        return database;
    }

    public UserRepository userRepository() {
        return userRepository;
    }

    public AppExecutors executors() {
        return executors;
    }

    public synchronized FirebaseAuth auth() {
        initializeFirebase();
        return auth;
    }

    public synchronized FirebaseFirestore firestore() {
        initializeFirebase();
        return firestore;
    }

    private void initializeFirebase() {
        if (auth != null) {
            return;
        }
        FirebaseApp app;
        if (BuildConfig.USE_FIREBASE_EMULATORS) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setProjectId("demo-android-final-project")
                    .setApplicationId("1:1234567890:android:1234567890abcdef")
                    .setApiKey("demo-emulator-only").build();
            app = FirebaseApp.initializeApp(context, options, "local-emulators");
        } else {
            app = FirebaseApp.initializeApp(context);
            if (app == null) {
                throw new IllegalStateException("Add app/google-services.json or enable the Firebase emulators.");
            }
        }
        FirebaseAuth configuredAuth = FirebaseAuth.getInstance(app);
        FirebaseFirestore configuredFirestore = FirebaseFirestore.getInstance(app);
        if (BuildConfig.USE_FIREBASE_EMULATORS) {
            configuredAuth.useEmulator(EMULATOR_HOST, 9099);
            configuredFirestore.useEmulator(EMULATOR_HOST, 8080);
        }
        // Room is the durable offline store and is cleared at the session boundary.
        configuredFirestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder(
                configuredFirestore.getFirestoreSettings())
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build()).build());
        firestore = configuredFirestore;
        auth = configuredAuth;
    }
}
