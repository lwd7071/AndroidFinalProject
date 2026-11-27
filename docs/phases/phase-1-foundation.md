# Phase 1 — Nền móng, Auth, Hồ sơ chữ và Danh bạ

Nguồn chuẩn: `docs/SPEC.md`, chữ ký chung: `docs/CONTRACTS.md`, từng màn: `docs/UI_SPEC.md` §2–3. Mục tiêu nghiệm thu: hai tài khoản có thể đăng ký, đăng nhập, tìm nhau bằng SĐT, thêm danh bạ, xem hồ sơ và đổi EN/VI; chưa cần gửi tin hay upload avatar.

| Người | Gói việc chính | Bàn giao cho phase sau |
|---|---|---|
| TV1 | Dựng Gradle Wrapper 8.13 + AGP 8.13.2 trên JDK 21, đặt Java source/target 17 theo SPEC §2.1; tạo `ChatApplication`, `AppContainer`, Room entities/DAOs, schema export, Firebase config, Rules cho users/phone_index/contacts và cấu trúc conversations/devices. Viết `ValidatorsTest` hỗ trợ TV2 và DAO test nền. | DB + repository factory hoạt động; file mẫu `local.properties.example` gồm `MESSAGE_API_BASE_URL`. |
| TV2 | Auth email/password, transaction `phone_index`, xử lý đăng ký dở dang, đăng xuất xóa Room/listeners, Profile/EditProfile dạng chữ, đổi mật khẩu, EN/VI, tìm và thêm liên hệ bằng SĐT. | `AuthRepository`, `UserRepository`, FakeRepository, test auth/phone. |
| TV3 | `MainActivity`/NavHost, màn danh bạ và hồ sơ người khác qua `userId`, adapter `ListAdapter`, trạng thái Loading/Content/Stale/Error, light/dark theme theo UI contract. Nút Nhắn tin điều hướng placeholder cho Phase 2. | UI shell và layout dùng được khi xoay màn hình. |
| TV4 | `PermissionRequester`, các style `Widget.Chat.*`, color roles, spacing/typography dùng chung theo `CONTRACTS.md` §5; kiểm tra accessibility và layout cơ sở `layout-land`/`sw600dp`. | Theme light/dark, component styles và helper Android để Phase 2–3 tái sử dụng. |

## Thứ tự ghép và nghiệm thu

1. TV1 chốt package name, `AppContainer`, entities và Repository interfaces; các TV còn lại dùng FakeRepository để dựng màn hình trước khi remote sẵn sàng.
2. TV1 + TV2 thử đăng ký trùng SĐT, mất mạng giữa Auth và Firestore transaction, đăng nhập lại tài khoản dở dang; không để tài khoản khác nhìn dữ liệu Room của phiên trước.
3. TV2 + TV3 thử tìm theo SĐT, thêm liên hệ, đăng xuất/đăng nhập, EN/VI, light/dark, font scale 1.3, xoay màn hình. Các màn có cache vẫn giữ danh sách khi mất mạng; chụp ảnh hai theme để so với UI contract.
4. Chạy `testDebugUnitTest`, DAO test trên emulator và `lint`. Phase 1 **không nghiệm thu sửa avatar**; chỉ hiển thị avatar mặc định hoặc URL đã có.

Không đưa Firebase Admin private key vào Android/repo. Firebase Spark dùng cho Auth và Firestore; Vercel API triển khai ở Phase 2.
