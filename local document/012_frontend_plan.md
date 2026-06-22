# Restructure Frontend Cart and Checkout Logic

Mục tiêu: Tái cấu trúc lại luồng mua sắm trên Frontend của hệ thống Q22 Shop. Thay vì việc nhấn "Thêm vào giỏ" ở trang chủ sẽ gọi API tạo đơn hàng ngay lập tức (gây lỗi do thiếu thông tin), chúng ta sẽ xây dựng luồng chuẩn của E-commerce: **Sản phẩm -> Giỏ hàng (Local) -> Thanh toán (Checkout) -> Đặt hàng thành công**.

## User Review Required

- Giỏ hàng tạm thời sẽ được lưu vào trạng thái của React (Context API) và `localStorage` để không bị mất khi F5 trang. Bạn có đồng ý với phương pháp này không (thay vì làm 1 microservice Cart Service riêng biệt ở backend)?
- Ở phần thanh toán (Checkout), tôi sẽ thiết kế một form cơ bản để nhập "Địa chỉ giao hàng" và "Số điện thoại". Bạn có yêu cầu thêm trường thông tin nào khác không?

## Proposed Changes

### Frontend App

Thay đổi luồng UI/UX để phù hợp với kiến trúc E-commerce.

#### 1. src/context/CartContext.jsx (Tạo mới)
- Tạo `CartContext` để quản lý giỏ hàng trên toàn cục ứng dụng.
- Cung cấp các hàm: `addToCart`, `removeFromCart`, `updateQuantity`, `clearCart`.
- Đồng bộ dữ liệu giỏ hàng với `localStorage` của trình duyệt.

#### 2. src/pages/HomePage.jsx (Sửa đổi)
- Loại bỏ hàm gọi API `POST /api/order` trực tiếp.
- Gọi hàm `addToCart` từ `CartContext` khi người dùng nhấn "Thêm vào giỏ".
- Thêm thông báo toast/alert thân thiện báo "Đã thêm vào giỏ hàng".

#### 3. src/components/ProductCard.jsx (Sửa đổi)
- Cập nhật UI nút "Thêm vào giỏ" nếu cần thiết. 

#### 4. src/pages/CartPage.jsx (Sửa đổi)
- Hoàn thiện giao diện Giỏ hàng (hiện tại đang là trang "Coming Soon").
- Liệt kê danh sách sản phẩm trong giỏ, cho phép tăng/giảm số lượng.
- Hiển thị tổng tiền.
- Thêm nút "Tiến hành thanh toán" chuyển hướng sang trang `/checkout`.

#### 5. src/pages/CheckoutPage.jsx (Tạo mới)
- Tạo trang thanh toán mới.
- Khách hàng nhập thông tin giao hàng cơ bản.
- Khi người dùng nhấn "Xác nhận đặt hàng", hàm gọi API `POST http://localhost:8080/api/order` sẽ được thực thi tại đây.
- Gọi `clearCart()` sau khi đặt hàng thành công và chuyển hướng đến trang Thành công.

#### 6. src/pages/OrderSuccessPage.jsx (Tùy chọn)
- Trang thông báo "Đặt hàng thành công", cảm ơn khách hàng và có nút quay về Trang chủ hoặc Lịch sử đơn hàng.

#### 7. src/App.jsx (Sửa đổi)
- Bọc ứng dụng bằng `CartProvider`.
- Đăng ký các Route mới cho `/checkout` và `/order-success`.

## Verification Plan

- Truy cập trang chủ, nhấn "Thêm vào giỏ" và xác nhận sản phẩm được thêm vào Context/LocalStorage thay vì báo lỗi.
- Vào trang `/cart`, xác nhận hiển thị đúng sản phẩm và tổng tiền.
- Nhấn thanh toán, điền form tại `/checkout` và xác nhận việc gọi API `/api/order` thành công.
- Kiểm tra Console và Network tab để đảm bảo không có lỗi CORS và lỗi 4xx/5xx nào xảy ra.
