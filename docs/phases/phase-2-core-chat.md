# Phase 2 — Chat realtime, offline outbox, Vercel API và FCM

Đầu vào: Phase 1 đã nghiệm thu, interfaces trong `docs/CONTRACTS.md` ổn định; màn và trạng thái theo `docs/UI_SPEC.md` §3–5. Mục tiêu: chat 1-1/nhóm, inbox và unread, gửi offline rồi đồng bộ đủ khi có mạng; máy nhận đã đóng app vẫn nhận FCM trong điều kiện thiết bị cho phép.

| Người | Gói việc chính | Kiểm thử |
|---|---|---|
| TV1 | `MessageDao`, `ConversationDao`, outbox `SendMessageWorker`, `MessageRepository`, `NetworkMonitor`, sync **mọi phòng** theo snapshot cursor, `unreadCount`; Rules cho conversation/message, indexes. | DAO, OutboxDelivery, Rules Emulator, kịch bản >50 tin offline. |
| TV2 | Đăng ký/cập nhật/xóa FCM token theo thiết bị gắn với vòng đời Auth; read marker riêng UID; Group repository cho tạo/sửa/rời nhóm và kiểm tra quyền admin ở UI. | Token đổi, đăng xuất, quyền nhóm, markAsRead. |
| TV3 | Inbox All/Unread + Search, ChatFragment text/trạng thái tin, paging 50 bằng `DocumentSnapshot`, CreateGroup/GroupInfo, `ChatMessagingService` + `MessageNotifier`, deep link và layout tablet. | ViewModel tests, Espresso inbox→chat, FCM mở đúng phòng. |
| TV4 | Vercel `POST /api/messages` bằng Firebase Admin: verify ID token, validate payload, membership, tạo DIRECT + message đầu, idempotency theo `clientId`, ghi `lastMessage*`, gửi FCM cho thiết bị người nhận; tài liệu deploy và biến môi trường. | API tests `400/401/403/409/503`, retry không trùng, FCM không gửi cho sender. |

## Điểm ghép bắt buộc

1. TV4 cung cấp API local/test URL và payload đúng `CONTRACTS.md`; TV1 tích hợp Worker. Worker chỉ đặt `SENT` khi API xác nhận message đã có trên Firestore. Retry dùng cùng `clientId`.
2. TV1 + TV3 chốt thứ tự query `createdAt`, giữ ID tin biên trong Room và lấy lại `DocumentSnapshot` sau process death. Không dùng `whereLessThan`/`whereGreaterThan` chỉ với timestamp để chia trang. Inbox/chat dùng đúng bubble, text roles, spacing và empty/loading/error states ở `CONTRACTS.md` §5.
3. TV2 + TV3 thử token mới, token cũ, từ chối `POST_NOTIFICATIONS`, người gửi đang xem phòng khác, và máy nhận đã đóng app.
4. TV1 + TV4 chạy Rules Emulator: Android không tạo/sửa message, không sửa `lastMessage*`; member không tự cấp admin hoặc xóa người khác; API Admin tự kiểm tra quyền vì Admin SDK vượt qua Rules.

## Nghiệm thu

- Hai máy chat 1-1 và nhóm realtime. Phòng DIRECT chưa có trên Firestore vẫn gửi tin đầu thành công, không lỗi FK ở Room.
- Bật máy bay gửi nhiều tin, trong đó phòng khác nhận hơn 50 tin; nối mạng lại không mất/nhân đôi tin, badge chưa đọc đúng. Nhiều tin cùng timestamp và khởi động lại app giữa các trang vẫn tải đủ.
- Gọi lại API cùng `clientId` trả cùng message; không tăng số message, không đổi header tin cuối. `FAILED` chỉ được gán sau khi kiểm tra server chưa có message.
- Máy nhận đã đóng app nhận FCM và chạm thông báo mở đúng conversation. Push là *best effort*; tin nhắn vẫn đồng bộ từ Firestore khi mở app dù push trễ hoặc không đến.
- Ảnh inbox/chat đạt UI contract ở light/dark, EN/VI, 360dp, landscape và `sw600dp`; trạng thái gửi/đã gửi/thất bại đọc được mà không dựa vào màu.
