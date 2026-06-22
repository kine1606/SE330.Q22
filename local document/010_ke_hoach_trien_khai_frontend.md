# Kế Hoạch Triển Khai Frontend (Giai đoạn 3)

Mục tiêu: Xây dựng một ứng dụng Web Frontend hiện đại, đẹp mắt, giao tiếp trực tiếp với hệ thống Backend Microservices thông qua API Gateway.

## Quyết định Kỹ Thuật
1. **Công nghệ (Framework):** Dùng **React + Vite** (khởi động cực nhanh, chuẩn mực hiện tại cho Single Page Application) thay vì Next.js.
2. **Styling:** Tuân thủ hệ thống CSS thuần (**Vanilla CSS**) với thiết kế cực kỳ hiện đại (Light Mode cao cấp, Glassmorphism, Micro-animations) để đảm bảo giao diện đạt đẳng cấp cao (WOW effect). Không dùng TailwindCSS.
3. **Quản lý State:** Dùng **React Context API** có sẵn để lưu trạng thái đăng nhập (Auth) và giỏ hàng (Cart), không lạm dụng Redux để code gọn gàng, dễ bảo trì.
4. **Lưu trữ Token:** Lưu JWT Token bằng `localStorage` để tiến độ được nhanh nhất.

## Kế hoạch Cụ Thể

Tạo thư mục mới `frontend` ngay tại gốc của dự án `SE330.Q22/frontend`.

### Khởi tạo & Cấu hình
- Chạy lệnh `npx -y create-vite@latest ./frontend --template react` để tạo bộ khung dự án Frontend.
- Thiết lập cấu trúc thư mục rõ ràng:
  - `src/components/`: Các thành phần tái sử dụng (Navbar, ProductCard, Button...).
  - `src/pages/`: Các trang chính (Home, Login, Register, Cart).
  - `src/services/`: Lớp chuyên trách gọi API (kết nối trực tiếp đến `http://localhost:8080` của API Gateway).
  - `src/context/`: Quản lý dữ liệu xuyên suốt ứng dụng (AuthContext, CartContext).

### Thiết kế Design System (`index.css`)
- Khai báo bộ mã màu HSL chuyên nghiệp, typography hiện đại (vd: font Inter/Roboto).
- Áp dụng các hiệu ứng bóng đổ (box-shadow) mượt mà, chuyển động (transition) mượt mà khi hover vào thẻ sản phẩm hoặc nút bấm.

### Triển khai các màn hình (Pages)
- **Authentication**:
  - `LoginPage` & `RegisterPage`: Form đăng nhập/đăng ký có hiệu ứng nổi bật, báo lỗi rõ ràng. Giao tiếp với `/api/user/*`.
- **E-Commerce Flow**:
  - `HomePage`: Lưới sản phẩm đẹp mắt gọi từ `/api/product`.
  - `Cart & Checkout Page`: Giao diện giỏ hàng. Khi bấm "Đặt hàng", FE sẽ gửi yêu cầu xuống `/api/order` kèm JWT Token trong header.

## Verification Plan
1. Khởi động các Backend Service và API Gateway.
2. Khởi động Frontend (`npm run dev`).
3. Dùng trình duyệt mở trang web.
4. Đăng ký -> Đăng nhập (thấy trạng thái người dùng thay đổi trên Navbar).
5. Duyệt sản phẩm -> Thêm vào giỏ hàng -> Thanh toán -> Nhận thông báo thành công và đơn hàng được ghi nhận chính xác dưới CSDL với `userId` trích xuất từ Token.
