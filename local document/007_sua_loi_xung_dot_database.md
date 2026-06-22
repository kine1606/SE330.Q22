# Báo Cáo: Sửa Lỗi Xung Đột Database

## 1. Vấn Đề
File `docker-compose.yml` gốc có vấn đề nghiêm trọng:
- `product-postgres` và `inventory-postgres` cùng cấu hình chiếm dụng cổng `5434:5432`. Điều này dẫn đến Docker không thể khởi động nếu mở khóa (uncomment) cả 2 container.
- File `application.properties` của `product-service` trỏ đến `localhost:5434` nhưng lại tìm Database có tên là `product_service_db`. Trong khi port 5434 đang chạy DB của Inventory (tên là `inventory_service_db`). Điều này khiến `product-service` bị crash (lỗi) ngay khi khởi động vì không tìm thấy Database.

## 2. Giải Pháp Đã Thực Hiện
Đã tạo nhánh `fix/db-port-conflict` để cô lập sửa lỗi này với các nội dung sửa đổi sau:

### Chỉnh sửa `docker-compose.yml`:
Mở khóa (uncomment) toàn bộ các database container và phân vùng lại cổng (port) sao cho mỗi service có một cổng riêng độc lập không đụng chạm nhau:
- **`user-postgres`**: Port `5432:5432` (Sẵn sàng cho User Service sau này).
- **`product-postgres`**: Port `5433:5432` (Đã sửa từ 5434 sang 5433).
- **`inventory-postgres`**: Port `5434:5432` (Giữ nguyên).
- **`order-postgres`**: Port `5435:5432` (Mở khóa).
- **`payment-postgres`**: Port `5436:5432` (Mở khóa, sẵn sàng cho tương lai).

Đồng thời mở khóa luôn toàn bộ phần `volumes` lưu trữ dữ liệu của các database.

### Chỉnh sửa `product-service`:
Sửa thông tin file cấu hình `product-service/src/main/resources/application.properties`:
- Cập nhật dòng `spring.datasource.url=jdbc:postgresql://localhost:5433/product_service_db` để trỏ đúng vào cổng `5433` của Product Database.

## 3. Lịch Sử Git
Các chỉnh sửa trên đã được commit gọn gàng:
- `fix: resolve postgres port conflicts and uncomment databases`

## 4. Kết Luận
Hiện tại, hạ tầng dữ liệu qua Docker Compose đã hoàn toàn sẵn sàng. Khi bạn chạy lệnh `docker-compose up -d`, 5 database độc lập sẽ cùng chạy song song mà không còn bị xung đột lỗi.
Đồng thời việc phát triển `product-service`, `order-service`, và tương lai là `user-service` sẽ suôn sẻ hoàn toàn.
