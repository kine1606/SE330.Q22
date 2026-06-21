# Báo Cáo Nghiệp Vụ Và Tính Năng Dự Án

## 1. Mục Đích Thực Tế Của Project
Dự án này là hệ thống **Backend** dành cho một ứng dụng/sàn **Thương mại điện tử (E-commerce)** hoặc hệ thống bán lẻ. 
Việc phân chia hệ thống thành các module nhỏ (Microservices) như Order, Product, Inventory cho thấy mục đích xây dựng một hệ thống có tính mở rộng cao (high scalability), có khả năng chịu tải tốt và các dịch vụ hoạt động độc lập (ví dụ dịch vụ sản phẩm có thể scale riêng rẽ so với dịch vụ xử lý đơn hàng).

## 2. Các Yêu Cầu Nghiệp Vụ Cơ Bản
Dựa vào mã nguồn, hệ thống đang giải quyết các yêu cầu nghiệp vụ sau:
- **Quản lý danh mục Sản phẩm:** Định danh sản phẩm qua mã duy nhất (SKU Code). Cần có nơi lưu trữ thông tin sản phẩm và giá cả độc lập.
- **Quản lý Kho (Inventory) chặt chẽ:** Tồn kho không chỉ có một con số mà được chia thành 3 trạng thái rõ ràng để tránh bán vượt quá số lượng (over-selling):
  - `Available`: Số lượng còn sẵn sàng để bán.
  - `Reserved`: Số lượng đã được khách hàng đặt nhưng chưa thanh toán/chưa hoàn tất.
  - `Sold`: Số lượng đã thực sự bán ra thành công.
- **Xử lý Đơn hàng (Order Processing) an toàn:** 
  - Khi đặt hàng, hệ thống phải đảm bảo khách hàng mua đúng giá gốc của sản phẩm (lấy từ Product Service).
  - Phải chắc chắn trong kho còn đủ hàng để bán (gọi Inventory Service để giữ chỗ - Reserve).
  - Phải có cơ chế rollback (trả lại kho) nếu quá trình tạo đơn hàng gặp trục trặc.
  - Xử lý nghiệp vụ khi đơn hàng kết thúc: xác nhận trừ hẳn kho khi thành công, hoặc trả lại kho khi thất bại/hủy.

## 3. Các Chức Năng Đã Có Hiện Tại
Hệ thống hiện tại đã hoàn thiện các chức năng ở mức cơ bản (CRUD và logic xử lý nội bộ) của 3 Service:

### A. Product Service
- **Tạo mới sản phẩm.**
- **Lấy danh sách** toàn bộ sản phẩm.
- **Lấy thông tin chi tiết** sản phẩm (theo ID hoặc theo mã SKU).
- **Cập nhật** thông tin sản phẩm.
- **Xóa** sản phẩm.

### B. Inventory Service
- **Khởi tạo thông tin kho** cho một mã SKU mới (số lượng ban đầu là 0).
- **Lấy danh sách / chi tiết** tình trạng kho.
- **Reserve Stock (Giữ chỗ):** Trừ đi số lượng ở `Available` và cộng vào `Reserved`.
- **Confirm Stock (Xác nhận xuất kho):** Trừ đi số lượng ở `Reserved` và cộng vào `Sold`.
- **Release Stock (Hoàn trả kho):** Trừ đi số lượng ở `Reserved` và cộng lại vào `Available`.
- **Restock (Nhập kho):** Cộng thêm số lượng vào `Available`.

### C. Order Service
- **Tạo đơn hàng (Create Order).**
- **Đánh dấu thành công (Mark Order Success).**
- **Đánh dấu thất bại / Hủy (Mark Order Fail).**
- **Lấy danh sách đơn / chi tiết đơn hàng.**

## 4. Các Luồng (Flows) Đã Được Triển Khai
Hệ thống hiện đang áp dụng kiến trúc giao tiếp đồng bộ (Synchronous) thông qua **FeignClient** (từ Order Service gọi sang Product và Inventory). Có 3 luồng chính đã được code:

### Luồng 1: Tạo Đơn Hàng (Create Order Flow)
1. **Tiếp nhận:** `Order Service` nhận Request chứa các mã SKU và Số lượng cần mua.
2. **Lấy thông tin giá:** `Order Service` gọi API sang `Product Service` để lấy giá chính xác và thông tin của từng mã SKU.
3. **Giữ chỗ kho:** Với mỗi item, `Order Service` gọi API sang `Inventory Service` (endpoint `/reserve`) để kiểm tra và trừ tạm thời số lượng trong kho.
4. **Lưu DB:** Nếu toàn bộ bước trên thành công, đơn hàng được tạo với trạng thái `PENDING` và lưu vào Database.
5. **Rollback (Xử lý lỗi):** Nếu có bất kỳ lỗi nào xảy ra trong quá trình lặp (VD: đến item thứ 3 thì hết hàng), hệ thống sẽ catch lỗi và gọi API `/release` sang `Inventory Service` để nhả lại số lượng của các item đã giữ chỗ trước đó.

### Luồng 2: Xác Nhận Đơn Hàng Thành Công (Success Flow)
1. Khởi chạy khi gọi API Patch sang endpoint `order-success`.
2. Kiểm tra trạng thái đơn phải là `PENDING`.
3. Gọi sang `Inventory Service` (endpoint `/confirm`) cho từng mặt hàng để chuyển số lượng từ trạng thái chờ (Reserved) sang đã bán (Sold).
4. Cập nhật trạng thái đơn hàng thành `SUCCESS`.

### Luồng 3: Hủy Đơn Hàng / Thất Bại (Fail Flow)
1. Khởi chạy khi gọi API Patch sang endpoint `order-failed`.
2. Kiểm tra trạng thái đơn phải là `PENDING`.
3. Gọi sang `Inventory Service` (endpoint `/release`) cho từng mặt hàng để nhả số lượng từ trạng thái chờ (Reserved) về lại có sẵn (Available).
4. Cập nhật trạng thái đơn hàng thành `FAIL`.
