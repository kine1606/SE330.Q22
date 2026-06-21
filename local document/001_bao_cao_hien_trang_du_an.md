# Báo Cáo Hiện Trạng Dự Án

## 1. Tổng quan
- Dự án là một hệ thống được thiết kế theo kiến trúc Microservices.
- Ngôn ngữ & Framework chính: Java (Sử dụng Maven để quản lý thư viện và build project, dựa trên sự xuất hiện của `pom.xml` và `mvnw` trong các service).

## 2. Cấu trúc các Service
Hiện tại dự án đang bao gồm 3 microservices chính đã được khởi tạo:
- `inventory-service`: Quản lý kho hàng.
- `order-service`: Quản lý đơn hàng.
- `product-service`: Quản lý sản phẩm.

Mỗi service đều có cấu trúc thư mục của một project Spring Boot/Java cơ bản với `pom.xml`, thư mục `src/`, và các file cấu hình maven (`mvnw`, `.mvn/`).

## 3. Hạ tầng và Database
- Dự án sử dụng Docker Compose (`docker-compose.yml`) để thiết lập cơ sở dữ liệu.
- Loại Database: PostgreSQL (phiên bản 16).
- **Trạng thái cấu hình Docker Compose:**
  - Service `inventory-postgres` đang được active và ánh xạ port `5434:5432`.
  - Các database dành cho `user-postgres`, `product-postgres`, `order-postgres`, và `payment-postgres` đang bị comment lại (vô hiệu hóa tạm thời).
  - Điều này cho thấy định hướng tương lai sẽ có thêm các service như User và Payment, nhưng hiện tại chỉ có Inventory Database là đang được khởi chạy thực sự trong docker-compose.

## 4. Trạng thái mã nguồn
- Hệ thống Git đã được khởi tạo (`.git` folder).
- Đã thêm thư mục `local document/` vào `.gitignore` để đảm bảo các báo cáo nội bộ không bị đẩy lên GitHub như yêu cầu.
- Không có bất kỳ file cấu hình CI/CD nào được phát hiện trong thư mục gốc.

**Kết luận:** Dự án đang ở giai đoạn phát triển ban đầu hoặc đang được tái cấu trúc, với việc định hình rõ các ranh giới service nhưng phần thiết lập môi trường (database) qua docker mới chỉ chạy một phần (inventory).
