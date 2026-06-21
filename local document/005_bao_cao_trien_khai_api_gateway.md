# Báo Cáo Triển Khai: Khởi Tạo API Gateway

Dựa trên kế hoạch đã đề ra, module API Gateway đã được code và commit thành công trên nhánh `feature/api-gateway`.

## Chi Tiết Công Việc Đã Thực Hiện:
1. **Chia Port (Cổng) Rõ Ràng:**
   - Tránh việc tranh chấp cổng 8080.
   - Set cứng `server.port=8081` cho Product Service.
   - Set cứng `server.port=8083` cho Inventory Service.
   - Order Service giữ nguyên `8082`.

2. **Dựng Khung API Gateway:**
   - Tạo thư mục `api-gateway` chứa file `pom.xml` cấu hình cho thư viện `spring-cloud-starter-gateway`.
   - Tạo class main: `ApiGatewayApplication.java`.

3. **Cấu Hình Định Tuyến Bằng YML:**
   - File `application.yml` của Gateway được set chạy ở port `8080`.
   - Các tuyến đường (Routes) đã được mapping:
     - `/api/product/**` $\rightarrow$ `http://localhost:8081`
     - `/api/order/**` $\rightarrow$ `http://localhost:8082`
     - `/api/inventory/**` $\rightarrow$ `http://localhost:8083`

## Lịch Sử Git
Các commit đã được chia nhỏ theo chuẩn "clean commit":
1. `feat: configure static ports for product and inventory services`
2. `feat: init api-gateway module with spring-cloud-starter-gateway`
3. `feat: config routes for product, order and inventory services in gateway`

## Bước Tiếp Theo Đề Xuất
Sau khi API Gateway hoàn thiện, bước tiếp theo trong quy trình kiến trúc sẽ là xử lý **User Service và Authentication (Bảo mật JWT/Keycloak)**. Khi có Auth, API Gateway sẽ được bổ sung thêm các màng lọc (Filter) để chặn các request không có Token hợp lệ.
