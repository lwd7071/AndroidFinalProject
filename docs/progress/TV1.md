# Tiến độ TV1 — Core, Room, Sync

## 2026-11-27 — Phase 1 — Nền Android, Room và Firestore Rules

- **Trạng thái:** Chờ review.
- **Đã làm:** Dựng Gradle Wrapper 8.13 và app Java, `ChatApplication`, `AppContainer`, Room v1 và DAO nền. Chốt chữ ký Auth/User cho phiên đăng nhập và hoàn tất hồ sơ. Thêm cấu hình Firebase Emulator, Firestore Rules và quy tắc giữ email đăng nhập trong Firebase Auth.
- **Bằng chứng:** Nhánh cục bộ `feat/phase-1-foundation`. APK debug tại `android/app/build/outputs/apk/debug/app-debug.apk`. Xem [README](../../README.md) để dựng và chạy lại kiểm thử.
- **Kiểm thử:** `assembleDebug`, `testDebugUnitTest` (3/3) và `lintDebug` thành công. Cài và mở APK trên Android Emulator. `connectedDebugAndroidTest -PuseFirebaseEmulators=true` qua 3/3 bài, gồm hai bài Room và một bài kết nối Auth/Firestore. `npm test` qua 6/6 bài Firestore Rules. JVM test và Firebase CLI chạy qua đường dẫn ASCII theo README để tránh lỗi Java trên Windows.
- **Còn lại / vướng mắc:** Chưa cấu hình dự án Firebase thật. Luồng đăng ký, hoàn tất hồ sơ, danh bạ, EN/VI và demo hai tài khoản cần tích hợp với phần việc TV2–TV4.
- **Bàn giao / cần phối hợp:** TV2 nối Auth/User repository vào `AppContainer` và gọi xóa dữ liệu Room khi kết thúc phiên. TV3 dùng các interface đã chốt và fake repository khi dựng màn. Nhóm xác nhận `applicationId` hiện tại trước khi đăng ký Android app trên Firebase thật.
