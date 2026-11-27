# UI_SPEC.md — Đặc tả thiết kế từng màn hình Android

Tài liệu này quy định **màn hình và hành vi nhìn thấy được** của ứng dụng. Thứ tự ưu tiên khi có khác biệt: `SPEC.md` (nghiệp vụ) → `CONTRACTS.md` §5 (token, component, accessibility) → file này (bố cục từng màn) → HTML trong `docs/ui/` (ảnh tham khảo từ Stitch). HTML không phải mã Android để chép vào app và chưa bao phủ đủ màn/dark mode/tablet.

## 1. Khung chung và điều hướng

- Giao diện Android XML Views + View Binding, Material 3 DayNight. Dùng palette, typography, spacing và component styles của `CONTRACTS.md` §5; không lấy màu `light-*` trong HTML Stitch làm màu cố định cho dark mode.
- `MainActivity` chứa NavHost. Bottom navigation **Tin nhắn / Danh bạ / Cá nhân** chỉ xuất hiện ở ba màn gốc; màn form, chat, nhóm, media và map có toolbar với nút Quay lại. Khi đăng xuất, xóa back stack và về Đăng nhập.
- Điện thoại: một màn mỗi lần, nội dung cuộn trong vùng giữa toolbar và system navigation. Chat có composer bám phía trên bàn phím. Tablet `sw600dp`: màn Tin nhắn là hai pane (danh sách trái, chat phải); khi chưa chọn phòng, pane phải hiển thị lời mời chọn cuộc trò chuyện. Các màn khác dùng bề rộng đọc hợp lý, không kéo form ra toàn màn.
- Không vẽ giả status bar hoặc gesture bar trong layout Android; dùng edge-to-edge/insets thật. Không hiện trạng thái online/“Đang hoạt động” khi chưa có dữ liệu presence. Không tạo nút “Quên mật khẩu” trừ khi bổ sung nghiệp vụ đặt lại mật khẩu vào SPEC.
- Điều hướng chỉ truyền `conversationId`, `userId`, `latitude`, `longitude` qua Safe Args. Nếu ID không còn tồn tại hoặc user không còn quyền xem, hiển thị lỗi có nút Quay lại; không để màn trắng.
- Nội dung hiển thị tiếng Việt có dấu hoặc English theo cài đặt; các ví dụ bên dưới dùng tiếng Việt. Mọi chữ, plural, thông báo lỗi và `contentDescription` phải lấy từ resources. Không cắt dấu tiếng Việt; thử font scale 1.3 và tên dài.

### Quy tắc trạng thái chung

| Trạng thái | Cách hiển thị | Thao tác |
|---|---|---|
| Loading, chưa có cache | Progress nhỏ ở vùng nội dung; giữ toolbar ổn định | Không cho bấm lặp hành động đang chạy |
| Content | Dữ liệu Room là nội dung chính | Cho phép tìm/lọc/cuộn bình thường |
| Refreshing | Giữ danh sách và vị trí cuộn, hiện chỉ báo làm mới nhỏ | Không che toàn màn |
| Stale/offline | Giữ cache; banner/Snackbar “Không có kết nối mạng. Đang hiển thị dữ liệu đã lưu.” | Gửi tin vẫn vào outbox; thao tác cần server báo đang chờ hoặc lỗi rõ ràng |
| Empty | Hình/icon đơn giản, một câu mô tả và một hành động phù hợp | Không dùng lỗi mạng thay cho empty |
| Error, không cache | Icon, câu lỗi dễ hiểu và nút “Thử lại” | Có đường Quay lại khi ở màn chi tiết |

Form: label luôn hiện, lỗi nằm dưới đúng field; nút chính disabled khi dữ liệu bắt buộc thiếu hoặc đang xử lý. Tác vụ phá hủy như rời nhóm, xóa thành viên và đăng xuất có xác nhận ngắn. Snackbar báo kết quả tác vụ; không dùng Toast giả lập như mẫu HTML. Vùng chạm tối thiểu 48dp, tương phản và icon tuân `CONTRACTS.md` §5.

## 2. Màn tài khoản — Phase 1

### 2.1 Đăng nhập — `LoginFragment`

- **Bố cục:** logo chữ “Chat” nhỏ, tiêu đề “Đăng nhập”, mô tả ngắn; hai `TextInputLayout` Email và Mật khẩu (nút hiện/ẩn); nút “Đăng nhập”; liên kết “Đăng ký tài khoản”. Form nằm trong vùng cuộn, rộng tối đa khoảng 480dp trên tablet.
- **Hành vi:** validate email/rỗng trước khi gửi; Enter ở mật khẩu có thể submit. Đăng nhập thành công mở Tin nhắn, xóa back stack auth. Nếu hồ sơ Firestore chưa hoàn tất, mở màn 2.3.
- **Trạng thái:** sai thông tin báo lỗi tại form; mất mạng giữ input và hiện Thử lại; nút có loading khi đang xác thực. Không hiển thị mật khẩu mẫu trong bản chạy thật.
- **Tham khảo:** `docs/ui/1._ng_nh_p.html`; bỏ nút “Quên mật khẩu” mô phỏng vì ngoài phạm vi SPEC hiện tại.

### 2.2 Đăng ký — `RegisterFragment`

- **Bố cục:** toolbar Quay lại; các field Tên hiển thị, Email, Số điện thoại Việt Nam, Mật khẩu; helper cho định dạng SĐT; nút “Tạo tài khoản”.
- **Hành vi:** trim tên/email, chuẩn hóa SĐT theo `Validators`, mật khẩu tối thiểu 6 ký tự. Thành công vào Tin nhắn; email/SĐT đã tồn tại báo ngay cạnh field tương ứng.
- **Trạng thái:** đang tạo tài khoản khóa submit; transaction lỗi/mất mạng giữ nguyên dữ liệu để thử lại. Nếu rollback Auth không hoàn tất, chỉ dẫn đăng nhập để hoàn tất hồ sơ; không nói “đăng ký thành công” khi chưa có user profile.
- **Tham khảo:** Stitch chưa có màn này; dùng cùng style form của Đăng nhập.

### 2.3 Hoàn tất hồ sơ — `CompleteProfileFragment`

- **Bố cục:** tiêu đề “Hoàn tất hồ sơ”, giải thích ngắn; Tên hiển thị, SĐT, nút “Tiếp tục”. Email đã đăng nhập hiển thị chỉ đọc.
- **Hành vi:** dành cho tài khoản Firebase Auth có thật nhưng `users/{uid}` chưa tồn tại. Chạy lại transaction hồ sơ; không mở các màn chính trước khi hoàn tất.
- **Trạng thái:** SĐT trùng và mất mạng báo rõ; không cho bỏ qua rồi vào chat.

## 3. Ba màn gốc — Phase 1–2

### 3.1 Tin nhắn — `ConversationListFragment`

- **Bố cục:** toolbar “Chat”, tìm kiếm, chip/tab “Tất cả” và “Chưa đọc”, danh sách phẳng sắp theo tin cuối mới nhất; FAB/nút “Tin nhắn mới”; bottom navigation. Mỗi dòng có avatar, tên người/nhóm, preview (nhóm có tên người gửi), thời gian, badge số chưa đọc. Dòng tối thiểu 72dp.
- **Hành vi:** tìm tên phòng/người chat hoặc preview từ Room; tab Chưa đọc chỉ lọc `unreadCount > 0`; kéo làm mới đồng bộ; chạm dòng mở chat. “Tin nhắn mới” mở Danh bạ để chọn người. Badge cập nhật sau sync mọi phòng.
- **Trạng thái:** offline giữ list và banner nhỏ; không có hội thoại hiện lời mời mở Danh bạ; không khớp tìm kiếm hiện “Không tìm thấy cuộc trò chuyện”; lỗi không cache có Thử lại. Không dùng nút giả lập online/offline trong sản phẩm.
- **Tablet:** list chiếm khoảng 320–360dp bên trái, chat ở pane phải; chọn phòng đổi pane phải và giữ list/filter.
- **Tham khảo:** `docs/ui/2._h_p_th_n.html` cho bố cục mobile light.

### 3.2 Danh bạ — `ContactsFragment`

- **Bố cục:** toolbar “Danh bạ” với nút Thêm; ô tìm kiếm; hành động “Tạo nhóm”; danh sách avatar, tên, SĐT; bottom navigation. Không dùng card bao quanh từng liên hệ.
- **Hành vi:** tìm trong danh bạ local theo tên/SĐT; Thêm mở màn 3.3; chạm người mở Hồ sơ người khác; Tạo nhóm mở 5.1.
- **Trạng thái:** danh bạ rỗng có CTA “Thêm liên hệ”; tìm không thấy có mô tả riêng; offline giữ cache, việc tra cứu SĐT mới báo cần mạng.
- **Tham khảo:** `docs/ui/3._danh_b.html`.

### 3.3 Thêm liên hệ — `AddContactDialog` hoặc `AddContactFragment`

- **Bố cục:** một field “Số điện thoại Việt Nam”, ví dụ định dạng, hai nút “Hủy” và “Tìm”. Kết quả tìm hiện avatar/tên/SĐT và nút “Thêm vào danh bạ”; không thêm ngay khi chỉ bấm Tìm.
- **Hành vi:** chuẩn hóa SĐT, tra `phone_index`, không cho thêm chính mình hoặc liên hệ đã có. Thêm thành công trở về Danh bạ và hiển thị dòng mới.
- **Trạng thái:** số sai, không tìm thấy, trùng liên hệ, mất mạng là bốn thông báo khác nhau; giữ số đã nhập sau lỗi.
- **Tham khảo:** sheet Thêm liên hệ trong `docs/ui/3._danh_b.html`, nhưng tách bước Tìm và Thêm để người dùng xác nhận đúng người.

### 3.4 Cá nhân và Cài đặt — `ProfileFragment`

- **Bố cục:** avatar, tên, email, SĐT; nhóm “Tài khoản” (Chỉnh sửa hồ sơ, Đổi mật khẩu); nhóm “Giao diện & Ngôn ngữ” (Tiếng Việt/English, Sáng/Tối/Theo hệ thống); Đăng xuất ở cuối; bottom navigation.
- **Hành vi:** đổi ngôn ngữ/theme áp dụng ngay và lưu cài đặt; mở màn 6.1/6.2; Đăng xuất xác nhận rồi xóa session/cache theo SPEC. Phase 1 avatar chỉ xem; Phase 3 mới hiện nút đổi ảnh.
- **Trạng thái:** cập nhật setting có thông báo nhỏ; nếu hồ sơ cache cũ, vẫn hiển thị và báo offline.
- **Tham khảo:** `docs/ui/6._c_nh_n_c_i_t.html`; mẫu HTML chưa thực sự đổi dark/English nên cần thiết kế biến thể thật.

## 4. Hồ sơ người khác và chat — Phase 1–3

### 4.1 Hồ sơ người khác — `UserProfileFragment`

- **Bố cục:** avatar lớn, tên, SĐT; nút chính “Nhắn tin” và nút phụ “Gọi điện”. Toolbar Quay lại; không có bottom navigation.
- **Hành vi:** Nhắn tin tạo dòng DIRECT trong Room trước khi mở Chat; Gọi điện mở dialer qua `ACTION_DIAL`. Nếu máy không có dialer, báo Snackbar.
- **Trạng thái:** user không tồn tại/quyền đọc lỗi có màn lỗi và Quay lại; không hiện số điện thoại rỗng thành “null”.

### 4.2 Trò chuyện 1-1 — `ChatFragment` với `DIRECT`

- **Bố cục:** toolbar Quay lại, avatar/tên người kia, nút Gọi điện; danh sách tin có ngày ngăn nhóm; composer gồm Đính kèm, ô nhập, Gửi. Tin mình bên phải, tin nhận bên trái, max 80% chiều rộng. Không hiện “Đang hoạt động” vì chưa có presence trong schema.
- **Hành vi:** cuộn lên tải trang cũ 50 tin; chạm ảnh/video/file/vị trí mở màn tương ứng; giữ tin mở menu “Sao chép” / “Chia sẻ”; Đính kèm mở sheet 4.4. Gửi text trim, rỗng thì nút disabled. Draft giữ qua xoay màn hình.
- **Trạng thái:** `SENDING` = icon + “Đang gửi”, `SENT` = icon + “Đã gửi”, `FAILED` = icon lỗi + “Gửi thất bại · Thử lại”; retry giữ bubble và `clientId`. Offline vẫn cho nhập/gửi vào outbox; lỗi không có cache có Thử lại. Tin media chờ upload hiện preview local và tiến trình đơn giản.
- **Tham khảo:** `docs/ui/4._tr_chuy_n_1_1.html`. Nút retry trong mẫu chỉ mô phỏng chuyển ngay sang “Đã gửi”; app thật chờ kết quả Worker/API.

### 4.3 Trò chuyện nhóm — `ChatFragment` với `GROUP`

- **Bố cục:** cùng component chat 1-1; toolbar tên nhóm, avatar nhóm, số thành viên và nút Thông tin nhóm. Tin nhận có tên/avatar ở đầu cụm tin cùng người, không lặp trên từng bubble.
- **Hành vi:** Thông tin nhóm mở 5.2; nhắn tin, media, phân trang, long-press giống 1-1. Người rời/ bị xóa khỏi nhóm không còn composer và được đưa về Inbox sau thông báo quyền.
- **Trạng thái:** giữ đúng `SENDING/SENT/FAILED`, offline và empty như chat 1-1. Không giả lập “thành viên đang hoạt động”.
- **Tham khảo:** `docs/ui/5._tr_chuy_n_nh_m.html`.

### 4.4 Sheet đính kèm — trong `ChatFragment`

- **Bố cục:** Material bottom sheet một tầng, các lựa chọn Ảnh, Camera, Ghi âm, Video, Tệp, Vị trí với icon và label; đóng bằng vuốt xuống hoặc Quay lại. Không che toàn bộ nội dung chat nếu không cần.
- **Hành vi:** mỗi lựa chọn mở Android picker/permission/recording tương ứng. Chỉ xin quyền đúng lúc bấm; từ chối quyền có giải thích và đường vào Cài đặt khi bị từ chối vĩnh viễn.
- **Trạng thái:** file quá 10/20 MB, voice <1 giây, vị trí null hoặc upload lỗi hiển thị lý do cụ thể, giữ draft text.

## 5. Tạo và quản lý nhóm — Phase 2–3

### 5.1 Tạo nhóm — `CreateGroupFragment`

- **Bố cục:** toolbar Quay lại; field Tên nhóm; avatar nhóm tùy chọn (Phase 3); tìm/chọn người từ danh bạ, chip người đã chọn; nút “Tạo nhóm” cố định trên vùng an toàn cuối màn.
- **Hành vi:** cần tên không rỗng và ít nhất **2 người khác**; nút disabled trước khi đủ. Tạo thành công mở chat nhóm. Người tạo là admin.
- **Trạng thái:** danh bạ rỗng có đường Thêm liên hệ; lỗi mạng/tạo thất bại giữ tên và người đã chọn; không tạo trùng khi bấm nhiều lần.

### 5.2 Thông tin nhóm — `GroupInfoFragment`

- **Bố cục:** avatar/tên nhóm, số thành viên; danh sách người với nhãn Admin; hành động “Thêm thành viên”, “Đổi tên nhóm”, “Đổi ảnh nhóm” (Phase 3), “Rời nhóm”. Nút xóa thành viên chỉ hiện cho admin và không áp dụng cho chính mình.
- **Hành vi:** admin sửa thông tin/quản lý thành viên; thành viên thường chỉ xem và tự rời nhóm. Rời/xóa thành viên cần dialog xác nhận nêu rõ người/nhóm.
- **Trạng thái:** quyền thay đổi giữa lúc mở màn thì cập nhật nút theo dữ liệu mới; server từ chối quyền thì báo lỗi và tải lại. Không hiện thao tác admin cho người thường.

### 5.3 Thêm thành viên — `AddMemberFragment`

- **Bố cục:** toolbar, tìm danh bạ, checkbox/avatar/tên; người đã ở trong nhóm hiện disabled hoặc ẩn; nút “Thêm” kèm số người chọn.
- **Hành vi:** chỉ admin vào được; thêm thành công quay về Thông tin nhóm và cập nhật danh sách. Không đưa cùng UID hai lần.
- **Trạng thái:** không có liên hệ phù hợp, offline, mất quyền admin đều có thông báo riêng và đường Quay lại.

## 6. Media, bản đồ và chỉnh hồ sơ — Phase 1/3

### 6.1 Chỉnh sửa hồ sơ — `EditProfileFragment`

- **Bố cục:** avatar, field Tên hiển thị, Email/SĐT chỉ đọc nếu SPEC chưa có luồng đổi; nút “Lưu”. Phase 3 thêm hành động “Đổi ảnh đại diện”.
- **Hành vi:** Phase 1 lưu tên; Phase 3 chọn ảnh, preview trước khi upload và chỉ cập nhật avatar URL khi upload thành công.
- **Trạng thái:** tên rỗng báo dưới field; lỗi upload giữ tên/ảnh hiện có; lưu đang chạy khóa nút và không mất input.

### 6.2 Đổi mật khẩu — `ChangePasswordDialog`

- **Bố cục:** dialog có Mật khẩu hiện tại, Mật khẩu mới, nút Hủy/Đổi mật khẩu; mỗi field có hiện/ẩn mật khẩu.
- **Hành vi:** reauthenticate rồi updatePassword; đóng dialog và báo thành công. Không lưu mật khẩu trong draft hay log.
- **Trạng thái:** sai mật khẩu hiện tại, mật khẩu mới <6 ký tự, mất mạng có lỗi riêng; giữ mật khẩu mới khi retry nếu dialog còn mở.

### 6.3 Xem ảnh — `ImageViewerFragment`

- **Bố cục:** nền tối, ảnh fit-center giữ tỷ lệ, toolbar Quay lại và tên người gửi/thời gian gọn; có nút Chia sẻ nếu URL hợp lệ.
- **Hành vi:** tap để ẩn/hiện toolbar; ảnh luôn giữ tỷ lệ khi xoay màn hình. Không kéo ảnh méo hoặc crop mất nội dung chính.
- **Trạng thái:** loading ảnh, URL lỗi/mất mạng có thông báo và Thử lại; ảnh trong outbox có thể xem từ file local.

### 6.4 Xem video — `VideoPlayerFragment`

- **Bố cục:** PlayerView trên nền tối, nút Play/Pause, thanh tiến trình, Quay lại. Không phát tự động khi vừa mở.
- **Hành vi:** giữ vị trí phát qua xoay màn hình; dừng/giải phóng player theo lifecycle.
- **Trạng thái:** loading, không phát được/URL lỗi với Thử lại; không để màn đen không giải thích.

### 6.5 Xem vị trí — `LocationMapFragment`

- **Bố cục:** toolbar Quay lại, bản đồ chiếm vùng chính với một marker; dải dưới có tọa độ hoặc mô tả và nút “Mở bằng Bản đồ”.
- **Hành vi:** nhận `latitude/longitude` qua Safe Args, đặt camera ở marker; nút mở ứng dụng bản đồ ngoài. Nếu không có app bản đồ, báo Snackbar.
- **Trạng thái:** Maps key/tiles lỗi vẫn giữ tọa độ và nút mở ngoài; không xin quyền vị trí chỉ để xem vị trí người khác.

### 6.6 Tệp đính kèm — trong bubble chat

- **Bố cục:** icon loại tệp, tên tối đa 2 dòng, dung lượng, hành động Mở; bubble theo chiều người gửi.
- **Hành vi:** tải/mở bằng app phù hợp qua `ACTION_VIEW` + `FileProvider`; không có app xử lý thì báo rõ. Long-press vẫn có Chia sẻ/Sao chép URL khi phù hợp.
- **Trạng thái:** tệp còn trong outbox hiện “Đang gửi”; mở/tải thất bại có Thử lại. Không tự tải tệp khi chỉ cuộn qua bubble.

## 7. Kiểm tra trước khi coi là thiết kế hoàn tất

1. Có màn/biến thể cho toàn bộ mục 2–6; sáu HTML Stitch hiện tại chỉ làm mẫu cho 2.1, 3.1, 3.2, 3.4, 4.2, 4.3. File `chat_material_3_design_system.md` trong `docs/ui/` chỉ tham khảo; token chính thức nằm ở `CONTRACTS.md` §5.
2. Mỗi màn có ảnh light/dark, EN/VI, chiều rộng 360dp, landscape; Inbox + Chat có thêm tablet `sw600dp`. Kiểm tra font scale 1.3, bàn phím, status/navigation insets và tên dài.
3. Chứng minh các trạng thái Empty/Loading/Refreshing/Stale/Error trên màn có tải dữ liệu; chat có `SENDING/SENT/FAILED`, media có lỗi upload/phát/tải.
4. Đi hết luồng Đăng nhập → Inbox → Danh bạ → Hồ sơ người khác → Chat 1-1; Tạo nhóm → Chat nhóm → Thông tin nhóm; Profile → Chỉnh sửa/Đổi mật khẩu/Đổi EN/VI. Back và bottom navigation không dẫn tới màn trắng.
5. Screenshot Stitch không phải nguồn hành vi. Chức năng giả lập, link HTML hỏng, status bar vẽ trong HTML và chỉ đổi màu nút thay vì đổi theme phải được thay bằng hành vi Android thật theo SPEC.
