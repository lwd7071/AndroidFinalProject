# Nhật ký tiến độ của 4 thành viên

Mỗi người chỉ cập nhật file của mình: [TV1](TV1.md), [TV2](TV2.md), [TV3](TV3.md), [TV4](TV4.md). Ghi một mục mới **lên đầu file** khi hoàn thành một gói việc hoặc có thay đổi quan trọng về việc bàn giao. Cập nhật cùng PR chứa phần code tương ứng, trước khi nhờ người khác review; nếu chưa có PR, ghi commit/nhánh để người khác tìm được. Mỗi phase có thể có nhiều mục. Không xóa các mục cũ.

Nhật ký giúp nhóm biết ai đã làm gì, kiểm thử đến đâu, và ai đang chờ đầu vào nào. PR/commit và kết quả test vẫn là bằng chứng chính; không ghi mật khẩu, token, key, file cấu hình riêng hay dữ liệu người dùng thật. Việc chưa xong ghi rõ ở **Còn lại / vướng mắc**, không đánh dấu hoàn thành sớm. Khi nghiệm thu phase, cả 4 file phải có mục bàn giao tương ứng hoặc ghi rõ chưa hoàn thành.

## Mẫu cho mỗi lần cập nhật

Sao chép khối dưới đây vào file của mình, thay dòng “Chưa có cập nhật” ở lần đầu; các lần sau đặt mục mới nhất ngay dưới tiêu đề file. Ngày dùng `YYYY-MM-DD` theo giờ Việt Nam. Viết ngắn, cụ thể; nếu một trường không áp dụng thì ghi `Không có`.

```md
## YYYY-MM-DD — Phase N — Tên gói việc

- **Trạng thái:** Đang làm / Chờ review / Đã merge / Bị chặn
- **Đã làm:** Chức năng hoặc phần bàn giao đã hoàn thành; nêu file/module chính nếu cần.
- **Bằng chứng:** PR #... hoặc link PR; commit `...` nếu chưa có PR.
- **Kiểm thử:** Lệnh/kịch bản đã chạy và kết quả; ghi rõ phần chưa chạy.
- **Còn lại / vướng mắc:** Việc chưa xong, lỗi đang biết hoặc `Không có`.
- **Bàn giao / cần phối hợp:** Ai cần dùng đầu ra này, interface/đường dẫn liên quan, hoặc `Không có`.
```

Khi trạng thái thay đổi sau review hoặc merge, sửa **mục vừa ghi** để phản ánh trạng thái và bằng chứng mới; không tạo mục trùng cho cùng một gói việc. Nếu phát sinh việc mới, thêm mục mới. Người review kiểm tra mục progress cùng code và tài liệu phase.
