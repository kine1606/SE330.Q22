# Kế Hoạch Triển Khai: User Service & Authentication (Giai đoạn 2)

Dựa trên tài liệu thiết kế tổng thể (`003_bao_cao_ke_hoach_fe_user_auth.md`), sau khi API Gateway đã hoạt động ổn định, dự án cần được bổ sung cơ chế quản lý người dùng và xác thực (Authentication).

## 1. Tình trạng hiện tại
- Toàn bộ các API nội bộ (`product-service`, `inventory-service`, `order-service`) đang mở hoàn toàn (public) và không có bất kỳ cơ chế bảo mật nào.
- Database cho User (`user_service_db`) đã được chuẩn bị sẵn trong `docker-compose.yml` thông qua container `user-postgres` ở cổng `5432`, nhưng chưa có code backend (User Service) nào được viết.
- Logic tạo đơn hàng trong `OrderService.java` đang phải comment lại đoạn lấy `userId` vì thiếu nguồn cung cấp ID định danh.

## 2. Hai Hướng Giải Quyết (Cần Quyết Định)

Để xây dựng hệ thống bảo mật Authentication, chúng ta có 2 hướng giải quyết chính:

### Hướng 1: Sử dụng Keycloak (Khuyên dùng)
Keycloak là một Identity & Access Management mã nguồn mở, hoạt động như một hệ thống xác thực độc lập theo chuẩn OAuth2 / OpenID Connect.
- **Cách làm:** Chạy thêm 1 container Keycloak trong Docker. Cấu hình `api-gateway` thành một `OAuth2 Resource Server` để kiểm tra Token JWT sinh ra bởi Keycloak. Cấu hình Realm/Client ngay trên giao diện web của Keycloak.
- **Ưu điểm:** Hiện đại, chuyên nghiệp, hỗ trợ sẵn nhiều tính năng (đăng nhập bằng Google/Facebook, quên mật khẩu, v.v.) mà không cần phải code.
- **Nhược điểm:** Phải cấu hình giao diện Keycloak thủ công lúc khởi tạo; nặng máy hơn một chút.

### Hướng 2: Tự code Spring Security + JWT
Cách làm truyền thống: tự cài đặt toàn bộ cơ chế bảo mật bằng code Java.
- **Cách làm:** Code thêm API `/login`, `/register` tại `user-service`. Dùng thư viện JJWT để tự tạo chuỗi mã hóa JWT sau khi user đăng nhập đúng. Cấu hình một `Global Filter` tại `api-gateway` để tự bóc tách, giải mã và xác thực chữ ký của Token trước khi cho đi qua.
- **Ưu điểm:** Nhẹ nhàng, không cần chạy thêm phần mềm bên thứ 3 nào, giúp hiểu sâu toàn bộ luồng code bảo mật.
- **Nhược điểm:** Tốn công code boilerplate, phải tự quản lý việc hết hạn token, làm mới token...

## 3. Các Bước Triển Khai Kế Tiếp

Sau khi chọn được Hướng đi ở phần 2, chúng ta sẽ thực hiện:
1. **Tạo mới `user-service`:** Chạy ở port `8084`, kết nối với `user-postgres` (5432). Viết API liên quan đến User.
2. **Cập nhật `api-gateway`:**
   - Thêm định tuyến (route): `/api/user/**` -> `http://localhost:8084`.
   - Cài đặt Filter chặn các request tới `/api/order`, `/api/inventory` (yêu cầu phải có Token hợp lệ).
3. **Cập nhật `order-service`:** Bỏ comment code gán `userId` vào `Order` và lấy thông tin ID từ header (do Gateway truyền xuống sau khi giải mã token).
4. **Kiểm thử (Postman):** Gọi API Đăng ký -> Đăng nhập lấy Token -> Gọi API Tạo đơn hàng với Token.

---
**Quyết định:** Chờ phản hồi chọn Hướng 1 hoặc Hướng 2 để bắt đầu triển khai code.
