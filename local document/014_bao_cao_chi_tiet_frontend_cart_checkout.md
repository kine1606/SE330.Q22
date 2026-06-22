# Báo cáo Chi tiết Triển khai Frontend: Luồng Giỏ hàng & Thanh toán (Cart & Checkout Flow)

## 1. Tổng quan
Báo cáo này tổng hợp toàn bộ các công việc đã thực hiện trên Frontend để xây dựng tính năng Giỏ hàng và Thanh toán, thay thế cho cơ chế cũ (gọi trực tiếp API đặt hàng ngay tại thẻ sản phẩm). Quá trình này đảm bảo tính năng hoạt động mượt mà, độc lập một phần với Backend và nâng cao trải nghiệm người dùng.

## 2. Các thay đổi và tính năng đã triển khai

### 2.1. Khởi tạo `CartContext`
**File:** `frontend/src/context/CartContext.jsx`
- **Chức năng:** Tạo ra một Global State (trạng thái toàn cục) bằng React Context để quản lý danh sách sản phẩm trong giỏ hàng.
- **Tính năng chính:**
  - `addToCart(product, quantity)`: Thêm một sản phẩm mới vào giỏ hoặc tăng số lượng nếu sản phẩm đã tồn tại (dựa trên `skuCode`).
  - `removeFromCart(skuCode)`: Xóa một sản phẩm khỏi giỏ.
  - `updateQuantity(skuCode, quantity)`: Cho phép người dùng trực tiếp điều chỉnh số lượng.
  - `clearCart()`: Xóa toàn bộ giỏ hàng (thực hiện sau khi thanh toán thành công).
  - `getCartTotal()`, `getCartCount()`: Các hàm hỗ trợ tính toán tổng tiền và tổng số lượng sản phẩm.
- **Tối ưu hóa (Data Persistence):** Sử dụng `useEffect` để liên tục đồng bộ dữ liệu `cartItems` xuống `localStorage` của trình duyệt. Điều này đảm bảo giỏ hàng không bị mất khi người dùng tải lại trang (F5) hoặc đóng/mở lại trình duyệt.

### 2.2. Tích hợp `CartContext` vào App
**File:** `frontend/src/App.jsx`
- **Chức năng:** Bao bọc (wrap) toàn bộ ứng dụng bằng `<CartProvider>`, đảm bảo mọi component từ bất kỳ đâu cũng có thể truy xuất và thao tác với giỏ hàng.
- **Cập nhật Router:** Khai báo thêm các route `/checkout` và `/order-success` vào `Routes` để điều hướng người dùng trong quá trình thanh toán.

### 2.3. Cải tiến Navbar (Thanh điều hướng)
**File:** `frontend/src/components/Navbar.jsx`
- **Chức năng:** Cập nhật icon Giỏ hàng (ShoppingCart).
- **Tính năng chính:** Thêm một Badge (vòng tròn màu đỏ) hiển thị linh động tổng số lượng sản phẩm có trong giỏ hàng, sử dụng giá trị lấy từ `getCartCount()`.

### 2.4. Refactor Trang chủ và Thẻ sản phẩm
**Files:** `frontend/src/pages/HomePage.jsx`, `frontend/src/components/ProductCard.jsx`
- **Chức năng:** Thay đổi hành vi của nút "Thêm vào giỏ".
- **Sửa đổi:** Trước đây, nút bấm gọi thẳng `axios.post('/api/order')` gây ra nhiều vấn đề về logic nghiệp vụ. Hiện tại, nút bấm này được liên kết với hàm `addToCart` từ `CartContext`, tức là chỉ lưu dữ liệu vào trình duyệt (localStorage) một cách tức thời, kèm theo một Alert thông báo thành công mà không phải chờ đợi phản hồi từ Backend.

### 2.5. Xây dựng Trang Giỏ hàng chi tiết (Cart Page)
**File:** `frontend/src/pages/CartPage.jsx`
- **Chức năng:** Giao diện trực quan để người dùng quản lý các món hàng đã chọn.
- **Thiết kế UI:** 
  - Hiển thị thông báo "Giỏ hàng trống" nếu không có sản phẩm.
  - Hiển thị dạng danh sách (List) các sản phẩm với tên, mã SKU, đơn giá.
  - Gắn các nút `+` / `-` để tăng/giảm số lượng linh hoạt (tác động trực tiếp lên LocalStorage qua Context).
  - Tự động tính tổng thành tiền của toàn bộ hóa đơn.
  - Cung cấp nút chuyển tiếp sang màn hình Thanh toán (Checkout).

### 2.6. Xây dựng Trang Thanh toán (Checkout Page)
**File:** `frontend/src/pages/CheckoutPage.jsx`
- **Chức năng:** Chốt đơn hàng và gửi dữ liệu lên Backend `order-service`.
- **Logic hoạt động:**
  1. Hiển thị bảng tóm tắt đơn hàng (tên sản phẩm, số lượng, tổng tiền) để người dùng rà soát lần cuối.
  2. Nút "Xác nhận đặt hàng" được gắn sự kiện `handlePlaceOrder()`.
  3. Lọc mảng `cartItems` chỉ lấy `skuCode` và `quantity` tạo thành mảng `orderItems` (khớp hoàn toàn với DTO Backend yêu cầu).
  4. Gọi API `POST http://localhost:8080/api/order` kèm theo header `Authorization: Bearer <token>`.
  5. Khi có phản hồi HTTP 201 Created (thành công): Gọi `clearCart()` để dọn dẹp giỏ hàng cục bộ và điều hướng người dùng sang trang `OrderSuccessPage`.
  6. Quản lý trạng thái Load (`loading = true/false`) trong lúc chờ gọi API để khóa nút bấm, tránh người dùng bấm 2 lần.

### 2.7. Xây dựng Trang Thông báo Thành công
**File:** `frontend/src/pages/OrderSuccessPage.jsx`
- **Chức năng:** Cung cấp thông điệp xác nhận rõ ràng, nâng cao trải nghiệm người mua sắm.
- Cung cấp nút điều hướng quay về trang chủ để người dùng bắt đầu một vòng lặp mua sắm mới.

## 3. Tổng kết
Việc triển khai thành công quy trình Cart & Checkout ở phía Frontend đã giải quyết triệt để lỗi thiết kế ban đầu. Hệ thống hiện tại vận hành theo một luồng E-commerce tiêu chuẩn: **Duyệt sản phẩm -> Bỏ vào giỏ (Local) -> Tinh chỉnh số lượng (Local) -> Thanh toán (Gửi Backend) -> Thành công**. Toàn bộ mã nguồn đã được tối ưu hóa theo phong cách Functional Components và Hooks hiện đại của React. Mọi thay đổi đều đã được Commit đầy đủ vào hệ thống quản lý phiên bản Git.
