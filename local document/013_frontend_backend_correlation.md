# Đánh giá sự tương quan giữa các chức năng Frontend và Backend Services hiện tại

Bản đánh giá này phân tích sự tương thích và cách luồng dữ liệu (Data Flow) di chuyển giữa các chức năng dự kiến trên Frontend (như trong Frontend Plan) với kiến trúc Microservices Backend hiện tại của bạn.

---

### 1. Tính năng "Hiển thị và Duyệt Sản phẩm" (Trang Chủ)
- **Chức năng Frontend:** Lấy danh sách sản phẩm hiển thị dưới dạng dạng lưới (grid), hiển thị hình ảnh, tên, giá cả và SKU.
- **Tương quan Backend:** 
  - Gọi API `GET /api/product` tới **`product-service`** (thông qua API Gateway `http://localhost:8080/api/product`).
  - Dữ liệu trả về hoàn toàn trùng khớp với cấu trúc `ProductResponse` mà Backend đang cung cấp.
  - *Mở rộng tương lai:* Frontend có thể gọi thêm `GET /api/inventory/sku/{skuCode}` tới **`inventory-service`** để hiển thị linh hoạt nhãn "Hết hàng" nếu `quantityAvailable == 0`.

### 2. Tính năng "Quản lý Giỏ hàng" (Trang Cart)
- **Chức năng Frontend:** Lưu trữ danh sách các món hàng muốn mua, thay đổi số lượng, xóa khỏi giỏ và tính toán tổng tiền.
- **Tương quan Backend:** 
  - **Không có backend service tương ứng.** Trong hệ thống của bạn hiện tại chưa có `cart-service`.
  - Do đó, toàn bộ dữ liệu giỏ hàng sẽ được Frontend xử lý cục bộ bằng `React Context` và lưu trữ bền vững tại `LocalStorage` của trình duyệt. Việc này giúp tiết kiệm tài nguyên máy chủ và tối ưu hóa tốc độ trải nghiệm.
  - Khi người dùng điều chỉnh số lượng, Frontend chỉ tính toán tại chỗ mà không cần "hỏi" Backend (sẽ kiểm tra kho hàng thực tế ở bước sau).

### 3. Tính năng "Thanh toán và Đặt hàng" (Trang Checkout)
- **Chức năng Frontend:** Gom toàn bộ danh sách sản phẩm đang có trong Giỏ hàng (chỉ cần lấy `skuCode` và `quantity`) đóng gói thành một payload `OrderRequest` và gửi đi.
- **Tương quan Backend:** 
  - Gọi API `POST /api/order` tới **`order-service`**.
  - **Dữ liệu khớp 100%:** Frontend sẽ gửi dữ liệu dưới dạng mảng `items: [{skuCode, quantity}]`. Backend không yêu cầu truyền giá (`price`), giúp bảo vệ hệ thống khỏi việc người dùng tự ý can thiệp thay đổi giá sản phẩm trên trình duyệt.
  - **Luồng nội bộ Backend (Choreography):**
    1. **`order-service`** nhận request và gọi sang **`product-service`** (thông qua `ProductClient`) để lấy chính xác giá (`price`) hiện tại của món hàng theo `skuCode`.
    2. **`order-service`** tiếp tục gọi sang **`inventory-service`** (thông qua `InventoryClient`) để tiến hành "giữ hàng" (Reserve Stock). Nếu số lượng yêu cầu vượt quá kho, giao dịch sẽ bị từ chối ngay lập tức, Frontend sẽ nhận được lỗi và báo cho khách hàng biết.
    3. Cuối cùng, **`order-service`** lưu đơn hàng xuống Database với trạng thái `PENDING`.
  - **Bảo mật:** Nhờ bộ lọc Custom ở API Gateway, `userId` được trích xuất an toàn từ Token và truyền ngầm dưới dạng Header `X-Auth-User-Id` cho `order-service`.

### 4. Tính năng "Xác thực và Cấp quyền" (Trang Login/Register)
- **Chức năng Frontend:** Đăng nhập, đăng xuất, lưu trữ JWT Token.
- **Tương quan Backend:** 
  - Gọi các API `/api/user/login` và `/api/user/register` tới **`user-service`**.
  - Token JWT lấy được từ Backend sẽ được Frontend đính kèm vào phần Header `Authorization: Bearer <token>` trong tất cả các request gửi đến API Gateway sau đó (như đặt hàng, xem hồ sơ) để Gateway xác thực danh tính.

---
**Kết luận:**
Kế hoạch tái cấu trúc Frontend hoàn toàn "ăn khớp" (perfect match) với hệ thống Microservices hiện hành. Luồng dữ liệu (Data Payload) được thiết kế khép kín, an toàn (không dựa vào Client để quyết định giá), và không đòi hỏi bất kỳ sự thay đổi kiến trúc nào từ phía Backend (ngoại trừ việc sửa một lỗi đánh máy sai đường dẫn ở `ProductClient` đã được giải quyết).
