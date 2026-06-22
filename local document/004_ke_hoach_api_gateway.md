# Kế hoạch triển khai API Gateway

Mục tiêu: Xây dựng một cổng giao tiếp duy nhất (API Gateway) để điều phối mọi request từ client/FE đến các dịch vụ phía sau (product, inventory, order).

## Các thay đổi sẽ được thực hiện
1. **Quy hoạch lại Port:**
   - **API Gateway:** `8080` (Client chỉ gọi vào cổng này)
   - **Product Service:** `8081`
   - **Order Service:** `8082` (Đã được set trong file config hiện tại)
   - **Inventory Service:** `8083`

2. **Khởi tạo service `api-gateway`**
   - Tạo thư mục microservice `api-gateway`.
   - Setup `pom.xml` (Java 17, Spring Cloud Gateway).
   - Setup `application.yml` định tuyến các request `/api/product/**`, `/api/inventory/**`, và `/api/order/**` tới các cổng tương ứng ở trên.

3. **Cấu trúc Git & Branch**
   - Nhánh: `feature/api-gateway`.
   - Các thay đổi sẽ được chia làm nhiều commit rõ ràng để dễ theo dõi (Ví dụ: Update port -> Init service -> Config routes).
