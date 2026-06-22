# Hướng dẫn Khởi động và Chạy Toàn bộ Dự án SE330.Q22

Dự án này được xây dựng theo kiến trúc **Microservices** với Backend là Spring Boot và Frontend là React (Vite). Dưới đây là hướng dẫn chi tiết từng bước để khởi chạy dự án từ đầu trên máy cá nhân (Localhost).

## 1. Yêu cầu hệ thống (Prerequisites)
Để chạy được dự án, máy của bạn cần cài đặt sẵn các phần mềm sau:
- **Java 17** (hoặc cao hơn)
- **Node.js** (khuyến nghị phiên bản 18+ hoặc 20+)
- **Docker Desktop** (để chạy database)
- **Git** (nếu cần pull code)
- Trình quản lý gói cho frontend: `npm` (đi kèm với Node.js)

## 2. Khởi động Cơ sở dữ liệu (Database)
Hệ thống sử dụng nhiều database độc lập cho từng service. Tất cả đã được cấu hình sẵn trong `docker-compose.yml`.

1. Mở terminal (CMD/PowerShell) tại thư mục gốc của dự án.
2. Chạy lệnh sau để khởi động toàn bộ các instance PostgreSQL:
   ```bash
   docker-compose up -d
   ```
3. *(Tùy chọn)* Kiểm tra xem 5 container (user, product, inventory, order, payment) đã chạy ổn định chưa thông qua Docker Desktop hoặc lệnh `docker ps`.

## 3. Khởi động các Microservices (Backend)
Vì đây là hệ thống Microservices, bạn bắt buộc phải khởi động **Discovery Server** trước, sau đó là các service nghiệp vụ, và cuối cùng là **API Gateway**.

Bạn cần mở **6 tab Terminal riêng biệt** (hoặc dùng tính năng Split Terminal trong VSCode), cd vào từng thư mục và chạy lệnh Maven tương ứng:

**Tab 1: Khởi động Discovery Server (Phải chạy ĐẦU TIÊN)**
```bash
cd discovery-server
.\mvnw.cmd clean spring-boot:run
```
*(Chờ khoảng 10-15 giây cho đến khi terminal báo Started...)*

**Tab 2: Khởi động User Service**
```bash
cd user-service
.\mvnw.cmd clean spring-boot:run
```

**Tab 3: Khởi động Product Service**
```bash
cd product-service
.\mvnw.cmd clean spring-boot:run
```

**Tab 4: Khởi động Inventory Service**
```bash
cd inventory-service
.\mvnw.cmd clean spring-boot:run
```

**Tab 5: Khởi động Order Service**
```bash
cd order-service
.\mvnw.cmd clean spring-boot:run
```

**Tab 6: Khởi động API Gateway (Chạy SAU CÙNG)**
```bash
cd api-gateway
.\mvnw.cmd clean spring-boot:run
```
*(Lưu ý: Đợi khoảng 1-2 phút để tất cả các service tự động đăng ký với Discovery Server. Bạn có thể kiểm tra trạng thái các service bằng cách truy cập `http://localhost:8761` trên trình duyệt).*

## 4. Khởi động Giao diện Người dùng (Frontend)
Frontend được viết bằng React và build bằng Vite. 

1. Mở thêm một **Tab Terminal thứ 7**, đi tới thư mục `frontend`:
   ```bash
   cd frontend
   ```
2. Cài đặt các gói thư viện (Chỉ cần làm 1 lần duy nhất ở lần chạy đầu tiên):
   ```bash
   npm install
   ```
3. Khởi chạy máy chủ phát triển (Dev Server):
   ```bash
   npm run dev
   ```
4. Terminal sẽ hiển thị đường link local (thường là `http://localhost:5173/`). Nhấn `Ctrl + Click` vào link đó để mở trang web.

---

## 5. Dữ liệu Test & Tài khoản mặc định
Hệ thống Backend đã được tích hợp sẵn **Seeder** (tự động nạp dữ liệu mẫu vào database khi khởi động). 
- **Sản phẩm & Kho hàng:** Đã có sẵn dữ liệu của các sản phẩm như MacBook, Bàn phím cơ...
- **Tài khoản test:** Đã có sẵn User trong DB để bạn đăng nhập ngay lập tức.
  - **Email:** `test@gmail.com`
  - **Mật khẩu:** `123`

## 6. Luồng Kiểm thử Mẫu (Happy Path)
1. Truy cập Frontend `http://localhost:5173`.
2. Bấm "Đăng nhập" ở góc trên cùng bên phải, sử dụng tài khoản `test@gmail.com` / `123`.
3. Bấm biểu tượng "Thêm vào giỏ" ở dưới các thẻ sản phẩm. Chấm đỏ hiển thị số lượng giỏ hàng sẽ tăng lên.
4. Bấm vào icon Giỏ hàng trên thanh điều hướng để tới trang Giỏ hàng chi tiết. Tại đây bạn có thể tăng/giảm số lượng món hàng.
5. Bấm "Tiến hành thanh toán".
6. Kiểm tra lại hóa đơn và bấm "Xác nhận đặt hàng". Nếu thành công, bạn sẽ được chuyển sang trang Thông báo Thành công.

Chúc các bạn phát triển dự án vui vẻ! 🚀
