# Bắt đầu đọc tài liệu dự án Chat Android

Thư mục này dành cho **4 thành viên** trước khi chia nhánh và trong lúc triển khai. Đây là hướng dẫn đọc tài liệu; hướng dẫn cài đặt/chạy app và khai báo sử dụng AI sẽ nằm ở `README.md` tại gốc repo khi project Android được tạo.

**Môi trường build:** theo [SPEC §2.1](SPEC.md#21-môi-trường-phát-triển-chuẩn): JDK 21, Android SDK API 36, Gradle Wrapper 8.13 và AGP 8.13.2. App viết Java với source/target 17. Mỗi người tự đặt đường dẫn JDK/SDK trên máy mình; không commit `android/local.properties`.

## Đọc theo thứ tự nào?

1. **[SPEC.md](SPEC.md)** — Đọc mục 1–2 để hiểu yêu cầu và stack; mục 4–9 để hiểu dữ liệu, gửi tin, đồng bộ, bảo mật; mục 10–12 để biết cách nghiệm thu và nộp bài. Đây là nguồn chuẩn cao nhất.
2. **[CONTRACTS.md](CONTRACTS.md)** — Đọc trước khi code song song: chữ ký Repository, API `POST /api/messages`, retry theo `clientId`, cursor, UI tokens, i18n và quy tắc PR. Không tự đổi interface hoặc payload trong nhánh riêng.
3. **[UI_SPEC.md](UI_SPEC.md)** — Đọc các mục của màn mình phụ trách. File này quy định bố cục, nội dung, thao tác, trạng thái và bản điện thoại/tablet của từng màn.
4. **Tài liệu phase hiện tại** — Xem gói việc, điểm ghép và checklist nghiệm thu của phase; chỉ làm phase sau khi phase trước đã nghiệm thu:
   - [Phase 1 — Nền móng, Auth, Danh bạ](phases/phase-1-foundation.md)
   - [Phase 2 — Chat, Outbox, Vercel, FCM](phases/phase-2-core-chat.md)
   - [Phase 3 — Media, Maps, kiểm thử, nộp bài](phases/phase-3-media-and-release.md)
5. **[ui/](ui/)** — Sáu HTML và design system xuất từ Stitch để **tham khảo hình ảnh**. Chúng thiếu một số màn, dark mode/tablet thật và có link HTML không chạy trong cấu trúc export. Không chép HTML/Tailwind vào app Android; khi khác tài liệu chính, theo `UI_SPEC.md` và `CONTRACTS.md`.
6. **[progress/](progress/README.md)** — Mẫu cập nhật và 4 nhật ký riêng TV1–TV4. Khi hoàn thành một gói việc hoặc bàn giao cho người khác, cập nhật file của mình cùng PR theo mẫu trước khi nhờ review.

## Mỗi thành viên bắt đầu từ đâu?

| Vai trò | Đọc kỹ trước khi nhận việc |
|---|---|
| **TV1 — Core, Room, Sync** | SPEC §4–5, §7, §9–10; CONTRACTS §1–4; gói TV1 trong phase hiện tại. |
| **TV2 — Auth, Contacts, Profile** | SPEC §5.1, §6.1–6.2, §8; CONTRACTS §3 và §5; UI_SPEC §2–3 và §6.1–6.2. |
| **TV3 — UI, Inbox, Chat, Group** | SPEC §6.2–6.3, §8; CONTRACTS §3–5; UI_SPEC §3–5, kể cả Empty/Offline/Failed và tablet. |
| **TV4 — Media, Maps, Vercel API** | SPEC §6.4–6.5, §8–9; CONTRACTS §1–2 và §5; UI_SPEC §4.4, §6; gói TV4 trong Phase 2–3. |

Mọi thành viên vẫn đọc phần tổng quan SPEC và tài liệu phase của mình; bảng trên chỉ là điểm bắt đầu, không thay thế các hợp đồng giao tiếp giữa nhóm.

## Khi tài liệu không khớp

Thứ tự ưu tiên: **SPEC → CONTRACTS → UI_SPEC → tài liệu phase → mẫu Stitch**. Tài liệu phase chỉ chia việc, không được thay đổi nghiệp vụ hoặc interface đã chốt. Nếu phát hiện mâu thuẫn, ghi rõ hai vị trí trong PR và sửa các file liên quan **trong cùng PR**, để người review thấy một quyết định thống nhất. Không tự áp dụng hành vi từ HTML mẫu nếu SPEC chưa có chức năng đó; ví dụ, mẫu Stitch có “Đang hoạt động” và “Quên mật khẩu” nhưng project chưa đặc tả presence hoặc đặt lại mật khẩu.

## Trước khi mở PR

- Đối chiếu chức năng với SPEC, interface/API với CONTRACTS, màn hình với UI_SPEC, và điều kiện nghiệm thu với phase hiện tại.
- Chạy test/lint phù hợp gói việc; PR ghi lệnh đã chạy và kết quả. Màn UI có ảnh điện thoại light/dark, EN/VI; Inbox/Chat có thêm ảnh tablet khi làm đến Phase 2.
- Cập nhật [progress/TV1–TV4](progress/README.md) trong cùng PR: việc đã làm, bằng chứng, kết quả kiểm thử, việc còn lại và điểm cần phối hợp. Người review đối chiếu mục này với code và checklist phase.
- Không commit `local.properties`, Firebase Admin service account, keystore, `build/` hoặc `TaiLieu/`.
- Có ít nhất **một thành viên khác review**. Người merge phải hiểu và giải thích được code; phần có dùng AI được khai báo trong README gốc hoặc báo cáo theo rubric.
