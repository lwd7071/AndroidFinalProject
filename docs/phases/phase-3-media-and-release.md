# Phase 3 — Multimedia, avatar, Maps và phát hành bài nộp

Đầu vào: Phase 2 đã gửi text và FCM ổn định; UI media/hồ sơ theo `docs/UI_SPEC.md` §4–6. Mục tiêu: hoàn thiện ảnh, camera, voice, video, file, vị trí, tính năng sáng tạo; kiểm thử và đóng gói bài nộp cho nhóm 4 người.

| Người | Gói việc chính | Kiểm thử |
|---|---|---|
| TV1 | Mở rộng Room/outbox cho `localMediaPath` và `mediaUrl`, bảo đảm retry API không upload lại; kiểm tra migration/schema, build release và đóng gói. | Media outbox offline, app restart giữa upload/API, DAO + lint. |
| TV2 | Photo Picker và sửa avatar hồ sơ bằng `MediaRepository`; hoàn thiện EN/VI, dark mode, báo cáo Word và khai báo AI cùng cả nhóm. | Avatar đổi trên hai máy; đăng xuất/đăng nhập vẫn hiển thị đúng. |
| TV3 | Bubble ảnh/voice/video/file/location, preview/viewer, chia sẻ/sao chép tin, layout xoay màn hình/tablet, kịch bản demo 2 máy. | Espresso luồng chat, nội dung hiển thị EN/VI, deep link. |
| TV4 | Cloudinary `auto/upload`, `MediaFileHelper` 10/20 MB, camera `FileProvider`, `AudioRecorder`/`AudioPlayer`, Media3, `LocationMapFragment`, `ACTION_DIAL`; bàn giao helper upload cho TV2/TV3. | Audio <1s, file quá dung lượng, URI bị thu hồi, quyền camera/location bị từ chối. |

## Quy tắc tích hợp

- File được copy vào `pending_media/` trước khi enqueue. Upload thành công thì lưu `secure_url` vào Room, sau đó gửi **cùng `clientId`** qua Vercel API. API lỗi không upload lại; message đã có trên Firestore thì đánh dấu `SENT` và dọn file tạm.
- Unsigned Cloudinary preset dùng `allowed_formats` và không cho client chọn `public_id`. Mức 10 MB (ảnh/file) và 20 MB (audio/video) chỉ được kiểm tra trong Android; Cloudinary không có giới hạn dung lượng theo từng preset.
- Avatar bắt đầu được nghiệm thu ở Phase 3; ảnh nhóm có thể dùng cùng helper. Không thêm private key Vercel/Firebase Admin vào APK hoặc ZIP.

## Nghiệm thu và nộp

1. Chạy toàn bộ Unit Test, DAO/Espresso, Rules Emulator, backend API tests và Android Lint; không có test fail hoặc warning nghiêm trọng. Rà UI contract cho mọi màn với light/dark, EN/VI, font scale 1.3 và vùng chạm 48dp.
2. Demo hai thiết bị: offline outbox, text/nhóm, đủ media, Maps, gọi điện, chia sẻ, EN/VI, dark mode, xoay màn hình, tablet; máy nhận đóng app vẫn nhận push và mở đúng phòng khi chạm.
3. Nộp `.apk`, `screenshots/`, báo cáo `.docx`, mã nguồn Android + backend Vercel/Firestore và `.git/`; loại `build/`, `.gradle/`, keystore, `local.properties`, service account JSON và `TaiLieu/`.
4. File ZIP cho nhóm 4 người: `GroupXX_MSSV1_MSSV2_MSSV3_MSSV4.zip`. README giải thích cấu hình Firebase Spark, Vercel environment variables, Cloudinary preset và phần dùng AI.
