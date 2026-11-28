package vn.edu.lwd7071.androidfinalproject.di;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FieldValue;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import vn.edu.lwd7071.androidfinalproject.BuildConfig;
import vn.edu.lwd7071.androidfinalproject.ChatApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

@RunWith(AndroidJUnit4.class)
public final class FirebaseFoundationTest {
    @Test
    public void connectsToAuthAndFirestoreEmulatorsWithTheSameProject() throws Exception {
        assumeTrue("Enable local Firebase emulators to run this test", BuildConfig.USE_FIREBASE_EMULATORS);
        ChatApplication application = ApplicationProvider.getApplicationContext();
        AppContainer container = application.container();
        var auth = container.auth();
        var firestore = container.firestore();
        String email = "phase1-" + UUID.randomUUID() + "@example.test";
        String phone = "0" + ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000);
        try {
            var authResult = Tasks.await(auth.createUserWithEmailAndPassword(email, "demo-password"),
                    20, TimeUnit.SECONDS);
            var user = authResult.getUser();
            assertNotNull(user);
            String uid = user.getUid();
            var userDoc = firestore.collection("users").document(uid);
            var indexDoc = firestore.collection("phone_index").document(phone);
            var batch = firestore.batch();
            Map<String, Object> userFields = new HashMap<>();
            userFields.put("uid", uid);
            userFields.put("phone", phone);
            userFields.put("displayName", "Phase 1");
            userFields.put("displayNameLower", "phase 1");
            userFields.put("createdAt", FieldValue.serverTimestamp());
            userFields.put("updatedAt", FieldValue.serverTimestamp());
            batch.set(userDoc, userFields);
            Map<String, Object> indexFields = new HashMap<>();
            indexFields.put("uid", uid);
            indexFields.put("phone", phone);
            indexFields.put("createdAt", FieldValue.serverTimestamp());
            batch.set(indexDoc, indexFields);
            Tasks.await(batch.commit(), 20, TimeUnit.SECONDS);

            var profile = Tasks.await(userDoc.get(), 20, TimeUnit.SECONDS);
            assertEquals(uid, profile.getString("uid"));
            assertNull(profile.getString("email"));
        } finally {
            auth.signOut();
        }
    }
}
