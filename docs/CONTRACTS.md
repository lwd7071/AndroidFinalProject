# CONTRACTS.md — Hợp đồng chung cho nhóm 4 người

`docs/SPEC.md` là nguồn chuẩn. File này chốt ranh giới giao tiếp để TV1–TV4 làm song song; đổi chữ ký hoặc JSON phải cập nhật file này cùng PR.

## 1. Quy tắc dữ liệu và luồng gửi

- Fragment chỉ gọi ViewModel; ViewModel gọi Repository interface; UI quan sát Room bằng `LiveData`. FirebaseAuth được quan sát riêng để điều hướng phiên đăng nhập.
- `clientId` là UUIDv4 do Android sinh **một lần** khi đưa tin vào Room. Retry, kể cả sau khi khởi động lại app, giữ nguyên ID đó.
- `SendMessageWorker` tải Cloudinary trước nếu cần, lưu `mediaUrl` vào Room, rồi gọi Vercel API. HTTP 2xx nghĩa là message đã có trên Firestore; FCM thất bại không làm tin `FAILED`.
- `senderId`, `senderName`, `createdAt`, `lastMessageAt` và trạng thái FCM do server quyết định. Android không gửi hoặc ghi trực tiếp các field này.
- Với chat 1-1 mới, `getOrCreateDirectConversation` tạo dòng Room trước khi điều hướng. API Vercel tạo document Firestore cha và message đầu trong một transaction.

## 2. API Vercel `POST /api/messages`

`Content-Type: application/json`, `Authorization: Bearer <Firebase ID token>`. Base URL lấy từ `MESSAGE_API_BASE_URL`; không chứa private key trong Android. `conversationId` của DIRECT phải bằng `direct_{min(uid,peerUserId)}_{max(uid,peerUserId)}`; `peerUserId` chỉ bắt buộc cho DIRECT chưa có trên server. GROUP phải tồn tại và người gửi phải còn là thành viên.

```json
{
  "clientId": "UUIDv4",
  "conversationId": "direct_uidA_uidB",
  "peerUserId": "uidB",
  "type": "TEXT",
  "text": "Xin chào",
  "mediaUrl": null,
  "fileName": null,
  "fileSize": null,
  "mimeType": null,
  "durationMs": null,
  "latitude": null,
  "longitude": null
}
```

- `type` thuộc `TEXT|IMAGE|VIDEO|AUDIO|FILE|LOCATION`. TEXT cần `text` từ 1–4000 ký tự sau trim; media cần HTTPS `mediaUrl` thuộc `res.cloudinary.com/<CLOUDINARY_CLOUD_NAME>/`, `fileName` nếu có tối đa 255 ký tự; LOCATION cần `latitude` trong [-90,90] và `longitude` trong [-180,180]. `fileSize`/`durationMs` nếu có phải không âm. Server chỉ lưu các field phù hợp với loại tin; `peerUserId` phải tồn tại và khác người gửi.
- Thành công trả HTTP `200` với `{ "clientId": "...", "conversationId": "...", "createdAtEpochMs": 0, "created": true }`. Lặp lại cùng `(conversationId, clientId)` và cùng nội dung trả `200`, `created: false`, cùng timestamp; không ghi thêm message, không cập nhật header hay gửi lại FCM.
- Lỗi trả `{ "code": "...", "message": "..." }`: `400 INVALID_PAYLOAD`, `401 UNAUTHENTICATED`, `403 NOT_PARTICIPANT`, `409 CLIENT_ID_CONFLICT` (ID đã có nhưng nội dung khác), `503 TEMPORARY_UNAVAILABLE`. Worker chỉ retry `503`, timeout và lỗi mạng; gặp `401` thì lấy Firebase ID token mới và thử lại **một lần**, sau đó dừng nếu vẫn `401`. `400/403/409` là lỗi dừng, UI báo rõ.
- Server xác thực token bằng Firebase Admin, đọc `users/{uid}` và membership bằng Admin SDK. Trong transaction, tạo message theo `clientId`, cập nhật `lastMessage*` và `lastMessageAt` bằng thời gian server; với DIRECT mới tạo document cha. Sau commit, đọc token của thành viên khác và gửi FCM data `{conversationId, clientId}`. Lỗi FCM được ghi log nhưng response vẫn `200`; push là *best effort*.
- Vercel giữ `FIREBASE_SERVICE_ACCOUNT_JSON`, `FIREBASE_PROJECT_ID` và `CLOUDINARY_CLOUD_NAME` trong environment variables. Không commit giá trị thật. Deploy từ `backend/`; Vercel Function được gọi bằng HTTP, không phải Firestore trigger.

## 3. Repository interfaces và kiểu kết quả

Mọi callback hoàn tất trên main thread; IO/Firestore chạy ở executor tương ứng. `ApiResult<T>` là `Success(value)` hoặc `Failure(code, message)`; không throw lỗi mạng lên Fragment. `UiState<T>` gồm `Loading`, `Content(data)`, `Refreshing(data)`, `Stale(data, message)`, `Error(message)`; khi có cache, lỗi mạng chuyển `Stale`, không xóa danh sách.

```java
interface ResultCallback<T> { void onResult(ApiResult<T> result); }

interface AuthRepository {
    enum SessionState { CHECKING_PROFILE, SIGNED_OUT, NEEDS_PROFILE, READY }
    LiveData<SessionState> observeSession();
    void register(String email, String password, String displayName, String phone, ResultCallback<User> callback);
    void signIn(String email, String password, ResultCallback<User> callback);
    void completeProfile(String displayName, String phone, ResultCallback<User> callback);
    void changePassword(String currentPassword, String newPassword, ResultCallback<Void> callback);
    void signOut(ResultCallback<Void> callback);
}

interface UserRepository {
    LiveData<User> observeUser(String uid);
    LiveData<List<Contact>> observeContacts();
    void findByPhone(String normalizedPhone, ResultCallback<User> callback);
    void searchByNamePrefix(String query, ResultCallback<List<User>> callback);
    void addContact(String contactUid, ResultCallback<Void> callback);
    void updateProfile(String displayName, String normalizedPhone, ResultCallback<Void> callback); // tên + phone_index cùng transaction khi đổi số
    void updateAvatar(String mediaUrl, ResultCallback<Void> callback); // Phase 3
}

interface ConversationRepository {
    LiveData<List<Conversation>> observeInbox(String query, boolean unreadOnly);
    void getOrCreateDirectConversation(User peer, ResultCallback<String> callback);
    void createGroup(String title, List<String> memberUids, ResultCallback<String> callback);
    void markAsRead(String conversationId, ResultCallback<Void> callback);
    void syncAll(ResultCallback<Void> callback);
}

interface MessageRepository {
    LiveData<List<Message>> observeMessages(String conversationId);
    void send(MessageDraft draft, ResultCallback<String> callback); // lưu Room, trả clientId
    void retry(String clientId, ResultCallback<Void> callback);     // giữ nguyên clientId
    void loadOlder(String conversationId, ResultCallback<Boolean> callback); // true nếu còn trang
    void syncMissedMessages(String conversationId, ResultCallback<Void> callback);
}

interface MediaRepository {
    void prepare(Uri source, MessageType type, ResultCallback<String> callback); // localMediaPath
    void upload(String localMediaPath, MessageType type, ResultCallback<String> callback); // secure_url
}
```

`NEEDS_PROFILE` chỉ được phát khi Firebase Auth đã có phiên và server xác nhận chưa có hồ sơ. Khi khôi phục phiên chưa biết trạng thái hồ sơ, dùng `CHECKING_PROFILE` đến khi có kết quả hoặc hồ sơ cache của cùng UID. `signIn` trả `Failure("PROFILE_REQUIRED", ...)` khi cần điều hướng sang Hoàn tất hồ sơ. `completeProfile` sử dụng UID của phiên hiện tại và chỉ phát `READY` sau khi transaction thành công và Room đã nhận hồ sơ. `User.email` chỉ lấy từ Firebase Auth cho chính tài khoản đang đăng nhập. Hồ sơ người khác và kết quả danh bạ có `email = null`.

`MessageDraft` chứa `conversationId`, `type`, các field nội dung phù hợp, `localMediaPath` tùy chọn; không chứa `senderId`, `clientId` hay server time. `send()` tự sinh ID và chỉ enqueue Worker sau khi ghi Room thành công. `retry()` kiểm tra lại message trên Firestore trước khi enqueue; đã tồn tại thì cập nhật `SENT`.

## 4. Đồng bộ, cursor và chưa đọc

- `loadOlder`: `orderBy(createdAt, DESC)`, trang 50, `startAfter(DocumentSnapshot cuối trang)`. Sau process death, lấy document theo `clientId` của tin server cũ nhất trong Room để khôi phục snapshot. Không dùng mỗi `createdAt` làm cursor.
- `syncMissedMessages`: lấy document theo `clientId` của tin server mới nhất trong Room, query `orderBy(createdAt, ASC).startAfter(snapshot).limit(50)` lặp đến hết; phòng chưa có cache bắt đầu từ trang đầu. `syncAll()` gọi hàm này cho mọi phòng còn tham gia sau đăng nhập/mở app/kết nối lại.
- Room là nguồn hiển thị. Sau khi tải đủ tin mới hơn read marker, DAO tính `unreadCount` từ message server của người khác sau `myLastReadAt`; `markAsRead` cập nhật đúng key của user trên Firestore và phản chiếu vào Room. `SENDING`/`FAILED` của chính mình không tính chưa đọc.

## 5. UI contract: màu, chữ và layout

Hướng thiết kế: Material 3 gọn, ưu tiên nội dung trò chuyện. Dùng **palette cố định** để hai máy demo có cùng nhận diện; chưa bật Dynamic Color theo hình nền. `Theme.Material3.DayNight.NoActionBar` tự chọn bộ light/dark. Màu hex chỉ xuất hiện trong `res/values/colors.xml` và `res/values-night/colors.xml`; `themes.xml` ánh xạ chúng sang Material color roles, còn layout/component chỉ dùng `?attr/...` hoặc style chung. Không đặt màu trực tiếp trong Fragment, adapter hay layout. **Bố cục và hành vi từng màn xem `docs/UI_SPEC.md`**; HTML `docs/ui/` chỉ là mẫu Stitch.

| Material color role | Light | Dark | Dùng cho |
|---|---|---|---|
| `colorPrimary` / `colorOnPrimary` | `#2458A5` / `#FFFFFF` | `#A9C7FF` / `#00315F` | CTA, FAB, trạng thái được chọn |
| `colorPrimaryContainer` / `colorOnPrimaryContainer` | `#D9E7FF` / `#0D2F60` | `#17477F` / `#D9E7FF` | Bong bóng tin mình gửi, chip chọn |
| `colorSurface` / `colorOnSurface` | `#FAFBFF` / `#191C20` | `#111318` / `#E2E2E9` | Nền màn, toolbar, chữ chính |
| `colorSurfaceVariant` / `colorOnSurfaceVariant` | `#E0E4EC` / `#424750` | `#424750` / `#C2C6D0` | Bong bóng tin nhận, chữ phụ |
| `colorOutline` | `#727781` | `#8C9099` | Viền input, divider |
| `colorError` / `colorOnError` | `#BA1A1A` / `#FFFFFF` | `#FFB4AB` / `#690005` | Lỗi form, gửi thất bại |

- **Ba lớp token:** primitive = các hex trên; semantic = Material color roles trong theme; component = style `Widget.Chat.MessageOutgoing`, `Widget.Chat.MessageIncoming`, `Widget.Chat.PrimaryButton`, `Widget.Chat.Input` tham chiếu role. Đổi màu ở theme, không sửa từng màn.
- **Spacing:** bội số 4dp; chỉ dùng `4, 8, 12, 16, 24, 32dp`. Lề ngang màn điện thoại 16dp, khoảng giữa các nhóm nội dung 24dp, item danh sách tối thiểu 72dp, vùng chạm icon/nút tối thiểu 48×48dp. Không nhét nhiều lớp card vào inbox/chat.
- **Typography:** Roboto hệ thống, `sp` và Material text appearances: tiêu đề màn `titleLarge` (22sp), tên hội thoại `titleMedium` (16sp, medium), nội dung tin `bodyLarge` (16sp), preview/thời gian `bodyMedium` (14sp), trạng thái tin `labelSmall` (12sp). Không đặt chiều cao cố định cho text; cho phép font scale, tên dài tối đa 2 dòng rồi ellipsize.
- **Chat:** tin mình gửi căn phải dùng `colorPrimaryContainer/onPrimaryContainer`; tin nhận căn trái dùng `colorSurfaceVariant/onSurfaceVariant`. Bong bóng rộng tối đa 80% vùng chat, radius 16dp; khoảng tin cùng người 4dp, khác người 12dp. Nhóm hiện tên người gửi trên cụm tin; timestamp và `SENDING/SENT/FAILED` dùng chữ/biểu tượng, không chỉ màu. Composer bám đáy phía trên bàn phím, gửi text rỗng bị vô hiệu hóa.
- **Component:** màn chính dùng MaterialToolbar, BottomNavigationView và RecyclerView; form dùng TextInputLayout với label, helper/error text; hành động chính một nút nổi bật mỗi màn. Loading lần đầu dùng progress ở vùng nội dung; Refreshing giữ danh sách; Stale giữ cache và báo Snackbar; Error không cache có nút Thử lại; empty state có mô tả và hành động phù hợp. Không tự chế spinner/dialog khác nhau giữa màn.
- **Responsive:** điện thoại portrait/landscape giữ nguyên luồng điều hướng; từ `sw600dp` inbox + chat thành hai pane bằng SlidingPaneLayout. Nội dung không bị che bởi status/navigation bar hoặc bàn phím; kiểm tra ít nhất 360dp width, landscape và tablet. ViewModel/SavedStateHandle giữ draft, bộ lọc và vị trí phát khi xoay.
- **Accessibility:** chữ thường đạt contrast tối thiểu 4.5:1, icon/viền quan trọng tối thiểu 3:1 trên cả hai theme; icon button có `contentDescription`, decoration không đọc lặp; thứ tự focus theo thứ tự nhìn; không dùng chỉ màu để báo unread/lỗi/trạng thái. Các cặp màu chính trong bảng đã được kiểm tra đạt 4.5:1 cho chữ.
- **i18n:** key chuỗi giống nhau ở `values/strings.xml` (EN) và `values-vi/strings.xml` (VI). Không hard-code text, content description hoặc chuỗi định dạng ngày giờ; dùng `strings.xml`, plural resources và locale hiện tại. Kiểm tra cả EN/VI với tên dài và font scale 1.3.
- **XML/ViewBinding:** ID dạng `fragment_<screen>`, `button_<action>`, `text_<meaning>`, `recycler_<items>`; observe bằng `getViewLifecycleOwner()`, giải phóng binding/adapter trong `onDestroyView()`.

Nguồn quy ước role và typography: [Material 3 color roles](https://m3.material.io/styles/color/the-color-system), [Material 3 typography](https://m3.material.io/styles/typography/applying-type).

## 6. Git và thay đổi hợp đồng

- Nhánh `feat/<feature>` hoặc `fix/<issue>`; mỗi PR có ít nhất một người khác review và ghi test đã chạy.
- Thay đổi interface/API/rules hoặc UI token/component style phải cập nhật SPEC + CONTRACTS trong cùng PR; đổi bố cục/hành vi màn cập nhật `UI_SPEC.md`. PR có màn hình mới kèm ảnh light/dark và EN/VI ở kích thước điện thoại; màn chat/inbox thêm ảnh tablet.
