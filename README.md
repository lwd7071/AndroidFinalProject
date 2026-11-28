# AndroidFinalProject

Ứng dụng chat Android của nhóm, viết bằng Java và dùng Room để lưu dữ liệu trên thiết bị. Đọc [hướng dẫn dự án](docs/README.md) và [kế hoạch Phase 1](docs/phases/phase-1-foundation.md) trước khi tích hợp.

## Dựng và kiểm tra Android

Cài JDK 21, Android SDK Platform 36 và Build Tools 36.0.0. Mở thư mục `android` bằng Android Studio hoặc đặt `ANDROID_HOME` khi chạy PowerShell. Gradle Wrapper tải Gradle 8.13. App dùng Java source/target 17 và `applicationId` hiện tại là `vn.edu.lwd7071.androidfinalproject`.

```powershell
Set-Location android
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

Lệnh cuối cần emulator Android hoặc thiết bị được ADB nhận diện. APK debug được tạo trong `android/app/build/outputs/apk/debug/`. Room xuất schema vào `android/app/schemas/`. Android Studio tạo `android/local.properties` theo máy. Xem các thuộc tính có thể cấu hình ở [local.properties.example](android/local.properties.example).

Nếu Windows báo `ClassNotFoundException` khi chạy JVM test hoặc Firestore Emulator đổi ký tự có dấu thành `?`, dùng đường dẫn chỉ có ký tự ASCII. Có thể tạo ổ tạm nếu `Q:` còn trống. Chạy từ thư mục gốc repo:

```powershell
subst Q: (Get-Location).Path
Set-Location Q:/android
```

Thư mục Firebase khi đó là `Q:/backend`. Sau khi dừng emulator và quay về đường dẫn gốc, gỡ ổ tạm bằng `subst Q: /d`.

## Firebase

Để kiểm thử Firestore Rules, dùng JDK 21 và Node.js 20 trở lên, rồi chạy từ thư mục `backend`:

```powershell
npm.cmd ci
npm.cmd test
```

Để tích hợp app với Auth và Firestore Emulator, chạy `npm.cmd run emulators` từ `backend`. Từ `android`, chạy `.\gradlew.bat :app:connectedDebugAndroidTest -PuseFirebaseEmulators=true` để kiểm tra kết nối. Bản debug này dùng dự án `demo-android-final-project` tại `10.0.2.2` trên Android Emulator. Với Firebase thật, đăng ký Android app theo `applicationId` của nhóm, bật Email/Password và đặt client config tại `android/app/google-services.json`. Kiểm tra Rules trên emulator trước khi nhóm deploy.

## Bàn giao nền Phase 1

TV2 có thể lấy `ChatDatabase`, `AppExecutors`, `FirebaseAuth` và `FirebaseFirestore` qua `ChatApplication.container()` để dựng triển khai repository. TV3 lấy hợp đồng từ package `data.repository` để kết nối UI và fake repository. [CONTRACTS](docs/CONTRACTS.md) quy định điều hướng phiên và cách hoàn tất hồ sơ.

## Sử dụng công cụ AI

Codex hỗ trợ thiết lập Gradle và ứng dụng nền, Room, hợp đồng repository, Firestore Rules, các bộ kiểm thử và tài liệu bàn giao Phase 1. Các thay đổi đi qua review của nhóm theo [SPEC §11.3](docs/SPEC.md#113-quy-chuẩn-git--khai-báo-ai).
