import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';
import { after, before, beforeEach, test } from 'node:test';
import { assertFails, assertSucceeds, initializeTestEnvironment } from '@firebase/rules-unit-testing';
import {
  collection, deleteDoc, doc, getDoc, getDocs, query, serverTimestamp,
  setDoc, Timestamp, updateDoc, where, writeBatch,
} from 'firebase/firestore';

const projectId = 'demo-android-final-project';
const firstPhone = '0901234567';
const secondPhone = '0912345678';
const unusedPhone = '0923456789';
let environment;

before(async () => {
  environment = await initializeTestEnvironment({
    projectId,
    firestore: { rules: await readFile(new URL('../firestore.rules', import.meta.url), 'utf8') },
  });
});
beforeEach(async () => { await environment.clearFirestore(); });
after(async () => { await environment?.cleanup(); });

const client = uid => environment.authenticatedContext(uid).firestore();
const user = (db, uid) => doc(db, 'users', uid);
const index = (db, phone) => doc(db, 'phone_index', phone);
const profile = (uid, phone) => ({
  uid, phone, displayName: 'An', displayNameLower: 'an', avatarUrl: null,
  createdAt: serverTimestamp(), updatedAt: serverTimestamp(),
});
const phoneIndex = (uid, phone) => ({ uid, phone, createdAt: serverTimestamp() });

function register(db, uid, phone) {
  const batch = writeBatch(db);
  batch.set(user(db, uid), profile(uid, phone));
  batch.set(index(db, phone), phoneIndex(uid, phone));
  return batch.commit();
}

function changePhone(db, uid, oldPhone, newPhone) {
  const batch = writeBatch(db);
  batch.set(index(db, newPhone), phoneIndex(uid, newPhone));
  batch.update(user(db, uid), { phone: newPhone, updatedAt: serverTimestamp() });
  batch.delete(index(db, oldPhone));
  return batch.commit();
}

test('registration requires an atomic profile and phone index write', async () => {
  const an = client('an');
  await assertFails(setDoc(user(an, 'an'), profile('an', firstPhone)));
  await assertFails(setDoc(index(an, firstPhone), phoneIndex('an', firstPhone)));
  await assertSucceeds(register(an, 'an', firstPhone));
  await assertFails(register(client('bao'), 'bao', firstPhone));
  assert.equal((await getDoc(index(an, firstPhone))).data().uid, 'an');
});

test('completing an incomplete profile can reuse the same UID index', async () => {
  await environment.withSecurityRulesDisabled(context =>
    setDoc(index(context.firestore(), firstPhone), {
      uid: 'an', phone: firstPhone, createdAt: Timestamp.fromMillis(1),
    }));
  const an = client('an');
  await assertSucceeds(setDoc(user(an, 'an'), profile('an', firstPhone)));
  assert.equal((await getDoc(index(an, firstPhone))).data().createdAt.toMillis(), 1);
});

test('changing phones releases the old number, keeps UID and rejects an occupied number', async () => {
  const an = client('an');
  await assertSucceeds(register(an, 'an', firstPhone));
  await assertSucceeds(register(client('bao'), 'bao', secondPhone));
  await assertFails(updateDoc(user(an, 'an'), { phone: unusedPhone, updatedAt: serverTimestamp() }));
  await assertFails(deleteDoc(index(an, firstPhone)));
  await assertFails(changePhone(an, 'an', firstPhone, secondPhone));
  await assertSucceeds(changePhone(an, 'an', firstPhone, unusedPhone));
  assert.equal((await getDoc(user(an, 'an'))).data().uid, 'an');
  assert.equal((await getDoc(user(an, 'an'))).data().phone, unusedPhone);
  assert.equal((await getDoc(index(an, firstPhone))).exists(), false);
});

test('public profile excludes email and a rename keeps the searchable name in sync', async () => {
  const an = client('an');
  const bao = client('bao');
  await assertSucceeds(register(an, 'an', firstPhone));
  await assertFails(updateDoc(user(an, 'an'), { email: 'an@example.com', updatedAt: serverTimestamp() }));
  await assertFails(updateDoc(user(an, 'an'), { uid: 'bao', updatedAt: serverTimestamp() }));
  await assertFails(updateDoc(user(an, 'an'), { displayName: 'An mới', updatedAt: serverTimestamp() }));
  await assertSucceeds(updateDoc(user(an, 'an'), {
    displayName: 'An mới', displayNameLower: 'an mới', updatedAt: serverTimestamp(),
  }));
  assert.equal((await assertSucceeds(getDoc(user(bao, 'an')))).data().email, undefined);
  await assertFails(getDoc(user(environment.unauthenticatedContext().firestore(), 'an')));
  await assertSucceeds(getDoc(index(bao, firstPhone)));
  await assertFails(getDocs(collection(bao, 'phone_index')));
});

test('contacts and device tokens are private to their owner', async () => {
  const an = client('an');
  const bao = client('bao');
  await assertSucceeds(register(an, 'an', firstPhone));
  await assertSucceeds(register(bao, 'bao', secondPhone));
  const contact = db => doc(db, 'users/an/contacts/bao');
  const device = db => doc(db, 'users/an/devices/phone');
  await assertSucceeds(setDoc(contact(an), { contactUid: 'bao', addedAt: serverTimestamp() }));
  await assertFails(getDoc(contact(bao)));
  await assertFails(deleteDoc(contact(bao)));
  await assertSucceeds(setDoc(device(an), { fcmToken: 'test-token', updatedAt: serverTimestamp() }));
  await assertFails(getDoc(device(bao)));
});

test('only members can read seeded conversations and client writes stay disabled in Phase 1', async () => {
  await environment.withSecurityRulesDisabled(context =>
    setDoc(doc(context.firestore(), 'conversations/room'), { participantIds: ['an', 'bao'] }));
  const an = client('an');
  const outsider = client('outsider');
  const room = db => doc(db, 'conversations/room');
  await assertSucceeds(getDoc(room(an)));
  await assertSucceeds(getDocs(query(collection(an, 'conversations'), where('participantIds', 'array-contains', 'an'))));
  await assertFails(getDoc(room(outsider)));
  await assertFails(getDocs(collection(an, 'conversations')));
  await assertFails(updateDoc(room(an), { title: 'New title' }));
  await assertFails(setDoc(doc(an, 'conversations/room/messages/sample'), { text: 'test' }));
});
