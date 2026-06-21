# Báo Cáo: Tích Hợp User Service & Xác Thực JWT

## 1. Tổng Quan
Theo thống nhất (Hướng 2), hệ thống đã được tích hợp cơ chế xác thực dựa trên JSON Web Token (JWT) thông qua Spring Security, kết hợp với một Microservice mới là `user-service`. Vai trò kiểm duyệt và trích xuất thông tin người dùng được giao cho `api-gateway`.

## 2. Chi Tiết Các Thay Đổi

### 2.1 Khởi tạo `user-service`
- **Mục đích**: Quản lý thông tin đăng ký, đăng nhập và cấp phát JWT Token.
- **Thành phần cốt lõi**:
  - `User`, `Role`: Entity quản lý thông tin tài khoản và quyền.
  - `UserRepository`: Tương tác với cơ sở dữ liệu `user_service_db` (PostgreSQL).
  - `AuthRequest`, `AuthResponse`, `RegisterRequest`: Data Transfer Objects (DTOs) để trao đổi dữ liệu.
  - `JwtUtils`: Lớp tiện ích sử dụng thuật toán HS256 để ký và sinh JWT chứa `userId` và `role`.
  - `CustomUserDetailsService` & `SecurityConfig`: Cấu hình Spring Security với trạng thái Stateless (vô hiệu hóa Session) và mở công khai các endpoint đăng nhập/đăng ký.
  - `AuthService` & `AuthController`: Expose 2 API chính:
    - `POST /api/user/register`
    - `POST /api/user/login`

### 2.2 Cập nhật `api-gateway`
- **Mục đích**: Chặn và kiểm tra JWT của mọi request trước khi forward tới các service bên dưới.
- **Thành phần thay đổi**:
  - `pom.xml`: Bổ sung thư viện `jjwt` (api, impl, jackson).
  - `JwtUtil`: Lớp tiện ích chung secret key với `user-service` để giải mã token.
  - `RouteValidator`: Định nghĩa các API được phép truy cập tự do (hiện tại là `/api/user/register` và `/api/user/login`).
  - `AuthenticationFilter`: Một Gateway Filter đảm nhiệm việc:
    - Đảm bảo header `Authorization` có chứa token hợp lệ.
    - Giải mã Token, trích xuất thuộc tính `userId`.
    - Gắn `userId` vào request header nội bộ (`X-Auth-User-Id`) rồi chuyển tiếp xuống các service sau nó.
  - `application.yml`: Khai báo routing cho `user-service` và ghim `AuthenticationFilter` vào các route của `product-service`, `inventory-service` và `order-service`.

### 2.3 Cập nhật `order-service`
- **Mục đích**: Loại bỏ yêu cầu client phải truyền `userId` từ Body, thay vào đó lấy một cách đáng tin cậy từ Gateway thông qua Header `X-Auth-User-Id`.
- **Thành phần thay đổi**:
  - `OrderController`: Endpoint `POST /api/order` được bổ sung tham số `@RequestHeader("X-Auth-User-Id") String userIdStr` để lấy userId. Thêm endpoint `GET /api/order/user/{userId}` để lấy danh sách đơn hàng theo user.
  - `OrderService`: `createOrder` được cập nhật tham số để trực tiếp nhận `userId` và gán vào Object `Order`.
  - `Order`, `OrderRequest`, `OrderResponse`: Uncomment trường `userId` (Kiểu `Long`) đã bị đóng băng trước đó.
  - `OrderRepository`: Bổ sung hàm `findByUserId`.

## 3. Cấu Trúc Kiểm Thử (End-to-End)
Quá trình kiểm thử đã được thiết lập qua 3 bước, bao phủ toàn bộ luồng request:
1. Đăng ký thông qua `POST /api/user/register`
2. Lấy JWT từ `POST /api/user/login`
3. Đặt hàng qua `POST /api/order` với Header chứa JWT. Gateway bóc tách `userId` truyền cho `order-service` xử lý, `order-service` lưu thành công đơn hàng có đính kèm ID người dùng.

Mọi module đều đã biên dịch thành công và mã nguồn đã được commit lên Git an toàn.
