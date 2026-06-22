# Hướng Dẫn Chạy Project và Nghiệm Thu API Gateway

Dưới đây là hướng dẫn chi tiết trả lời cho 2 câu hỏi của bạn: Cách chạy toàn bộ project hiện tại và Cách nghiệm thu hiệu quả của API Gateway.

## Phần 1: Làm thế nào để chạy Project hiện tại?
Dự án được cấu trúc theo dạng nhiều module Microservices độc lập, bạn cần chạy Database trước, sau đó chạy từng service.

### Bước 1: Khởi động Database
- Mở Terminal/Command Prompt tại thư mục gốc của dự án (`d:\UIT - Third year - II\SE330 - Ngon ngu lap trinh Java\Project\SE330.Q22`).
- Chạy lệnh: `docker-compose up -d`
- **Lưu ý quan trọng (Phát hiện lỗi):** Hiện tại trong file `docker-compose.yml`, chỉ có DB của `inventory` được bật (port 5434). File `application.properties` của `product-service` cũng đang trỏ tới port `5434` nhưng tìm DB tên là `product_service_db` (DB này không tồn tại trong container của inventory). Điều này sẽ làm `product-service` bị lỗi khi khởi động. (Bạn hãy cho tôi biết nếu muốn tôi fix lại toàn bộ file docker-compose này nhé).

### Bước 2: Chạy các Service backend
Bạn có thể mở project bằng IntelliJ IDEA (Mở thư mục gốc, IDEA sẽ tự nhận diện các module Maven).
Lần lượt chạy hàm `main` trong các file Application của từng service:
1. Chạy `ProductServiceApplication` (Sẽ khởi động ở port 8081).
2. Chạy `InventoryServiceApplication` (Sẽ khởi động ở port 8083).
3. Chạy `OrderServiceApplication` (Sẽ khởi động ở port 8082).
4. Chạy `ApiGatewayApplication` (Sẽ khởi động ở port 8080).

*Hoặc chạy bằng lệnh terminal cho từng thư mục (VD: `cd product-service` rồi chạy `mvnw spring-boot:run`).*

---

## Phần 2: Cách nghiệm thu hiệu quả của API Gateway
Để biết API Gateway tôi vừa làm có thực sự hoạt động hay không, ta sẽ thực hiện bài test "Đi đường vòng".

### Bước 1: Gọi trực tiếp (Cách cũ)
Bạn mở Postman hoặc trình duyệt web, gọi thử vào API lấy danh sách sản phẩm trực tiếp bằng cổng của Product Service:
- **URL:** `GET http://localhost:8081/api/product`
- **Kết quả kỳ vọng:** Nó sẽ trả về danh sách sản phẩm (có thể là mảng rỗng `[]` nếu database chưa có data), nhưng request là thành công (HTTP 200).

### Bước 2: Gọi qua Gateway (Cách mới - Để nghiệm thu)
Thay vì gọi cổng `8081`, bạn hãy gọi vào cổng `8080` của API Gateway:
- **URL:** `GET http://localhost:8080/api/product`
- **Kết quả kỳ vọng:** API Gateway sẽ tự động nhận diện tiền tố `/api/product` và bí mật chuyển tiếp request đó sang cổng `8081`. Phản hồi trả về màn hình của bạn sẽ y hệt như kết quả ở Bước 1. 

**Kết luận nghiệm thu:** Nếu bạn gọi vào cổng `8080` (Gateway) mà lấy được data của Product (cổng 8081) hoặc Order (cổng 8082) thì có nghĩa là API Gateway đã hoạt động định tuyến chính xác. Từ nay về sau, phía Frontend chỉ cần biết tới một địa chỉ duy nhất là `http://localhost:8080`.
