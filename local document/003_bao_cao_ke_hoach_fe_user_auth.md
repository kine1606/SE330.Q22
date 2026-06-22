# Báo Cáo: Hiện trạng và Kế hoạch triển khai Frontend, User, Auth

## 1. Dấu hiệu (Biểu hiện) hiện tại trong dự án
Dựa trên việc rà soát toàn bộ dự án, dưới đây là tình trạng thực tế của Frontend (FE), User, và Authentication (Auth):

### A. Về User (Người dùng)
- **Đã có sự chuẩn bị, nhưng chưa code:**
  - Trong file `docker-compose.yml`, có một block database tên là `user-postgres` đang bị comment lại. Điều này chứng tỏ dự án đã dự trù một `user-service` riêng biệt.
  - Trong `OrderService.java`, ở các dòng 65 và 155, đoạn code gán `userId` vào đơn hàng (`// .userId(request.getUserId())`) và hàm tìm đơn hàng theo User (`// getOrdersByUserId`) cũng đang bị comment. Lý do là vì hiện tại chưa có User Service để cung cấp định danh người dùng.

### B. Về Auth (Xác thực và Phân quyền)
- **Hoàn toàn vắng bóng:** Hiện tại không có bất kỳ thư viện `Spring Security`, `JWT`, hay cấu hình bảo mật nào trong các service hiện có. Các API nội bộ (`product-service`, `inventory-service`, `order-service`) đều đang ở trạng thái mở hoàn toàn (public) và gọi chéo nhau không cần token.

### C. Về Frontend (FE)
- **Chưa tồn tại:** Thư mục dự án hiện tại chỉ chứa mã nguồn Java thuần túy của các Backend Microservices. Không có bất kỳ thư mục nào chứa framework FE (như React, Vue, Angular) hay HTML/CSS.

---

## 2. Đề xuất: Thứ tự và Cách thức triển khai
Để hệ thống hoàn thiện, bảo mật và kết nối được với người dùng cuối, tôi đề xuất triển khai theo thứ tự sau:

### Giai đoạn 1: Triển khai API Gateway (Rất quan trọng)
*Lý do:* Với Microservices, FE không nên gọi trực tiếp từng cổng (port) của từng service (5434, 5435...). Cần một cổng duy nhất (Single Entry Point) để FE gọi vào.
- **Cách làm:** 
  - Khởi tạo một microservice mới tên là `api-gateway` (sử dụng Spring Cloud Gateway).
  - Cấu hình định tuyến (Routing) để mọi request từ FE gọi vào Gateway đều tự động trỏ đến đúng các service (product, order...).

### Giai đoạn 2: Triển khai Auth & User Service
*Lý do:* FE cần có tài khoản để đăng nhập, và các API cần được bảo mật trước khi đưa lên giao diện.
- **Cách làm:**
  - **Mở khóa DB:** Uncomment block `user-postgres` trong file `docker-compose.yml` để chạy database cho User.
  - **Tạo `user-service`:** Quản lý thông tin cá nhân (Tên, Email, Mật khẩu đã mã hóa).
  - **Tích hợp Auth:** Có 2 hướng:
    - *Hướng 1 (Hiện đại, khuyên dùng):* Dùng **Keycloak** (chạy thêm 1 container Keycloak trong docker-compose) làm Identity Provider. Api Gateway sẽ kiểm tra token, các service sẽ lấy `userId` từ token.
    - *Hướng 2 (Thủ công):* Code Spring Security + JWT ngay tại `user-service` để sinh Token. Tại `api-gateway`, viết một filter để validate JWT Token trước khi cho phép request đi sâu vào các service như order hay inventory.
  - **Cập nhật Order Service:** Bỏ comment các đoạn code liên quan đến `userId` trong `OrderService`, lấy `userId` từ Header hoặc Token do Gateway truyền xuống để gắn vào đơn hàng.

### Giai đoạn 3: Triển khai Frontend (FE)
*Lý do:* Backend đã có cổng giao tiếp an toàn (Gateway) và Auth, sẵn sàng để ráp giao diện.
- **Cách làm:**
  - Khởi tạo một thư mục mới (ví dụ: `frontend` sử dụng Next.js hoặc React/Vite) ngang hàng với các service hoặc nằm ở repository khác.
  - **Bước 1 (Auth UI):** Làm trang Đăng nhập / Đăng ký. Gọi API Auth. Lưu Token (JWT) vào Cookie hoặc LocalStorage.
  - **Bước 2 (Trang chủ & Sản phẩm):** Gọi API qua Gateway lấy danh sách sản phẩm hiển thị lên trang chủ.
  - **Bước 3 (Giỏ hàng & Đặt hàng):** Gửi Request tạo Order (kèm JWT Token trong header) xuống `api-gateway` -> `order-service`. Xử lý hiển thị thông báo thành công / thất bại dựa trên phản hồi của backend.
