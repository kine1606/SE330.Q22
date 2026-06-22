# 102. Tổng quan và Chi tiết toàn bộ Dự án SE330.Q22

Tài liệu này cung cấp một cái nhìn từ bao quát (Overview) đến cực kỳ chi tiết (In-depth) về toàn bộ dự án SE330.Q22. Nó phục vụ như một cuốn Bách khoa toàn thư để bất kỳ ai (kể cả những người mới vào dự án) cũng có thể đọc hiểu cặn kẽ mọi ngóc ngách của hệ thống.

---

## MỤC LỤC
1. [Giới thiệu Chung & Tech Stack](#1-giới-thiệu-chung--tech-stack)
2. [Kiến trúc Hệ thống (System Architecture)](#2-kiến-trúc-hệ-thống-system-architecture)
3. [Phân tích Chi tiết Backend Microservices](#3-phân-tích-chi-tiết-backend-microservices)
4. [Phân tích Chi tiết Frontend (ReactJS)](#4-phân-tích-chi-tiết-frontend-reactjs)
5. [Thiết kế Cơ sở dữ liệu (Database Schema)](#5-thiết-kế-cơ-sở-dữ-liệu-database-schema)
6. [Luồng Nghiệp vụ (Business Flows)](#6-luồng-nghiệp-vụ-business-flows)
7. [Cơ chế Bảo mật (Security & JWT)](#7-cơ-chế-bảo-mật-security--jwt)
8. [Định hướng Phát triển Tương lai](#8-định-hướng-phát-triển-tương-lai)

---

## 1. Giới thiệu Chung & Tech Stack
Dự án SE330.Q22 là một hệ thống **E-Commerce (Thương mại điện tử)** hiện đại được thiết kế với mục tiêu tối ưu hiệu năng, khả năng mở rộng (scalability) và khả năng bảo trì. 

**Công nghệ sử dụng (Tech Stack):**
- **Backend Core:** Java 17, Spring Boot 3
- **Microservices Tools:** Spring Cloud Gateway, OpenFeign (để gọi API nội bộ)
- **Database:** PostgreSQL (5 databases riêng biệt chạy trên Docker)
- **ORM & Mapping:** Hibernate / Spring Data JPA, MapStruct, Lombok
- **Bảo mật:** Spring Security, JSON Web Token (JWT)
- **Frontend:** React 18, Vite, React Router DOM, Axios, Lucide-React (Icon)

---

## 2. Kiến trúc Hệ thống (System Architecture)
Hệ thống tuân thủ nghiêm ngặt mô hình **Microservices**. Thay vì gom mọi thứ vào một ứng dụng to lớn (Monolithic), dự án chia nhỏ thành các "tiểu dịch vụ" có vòng đời độc lập.

- **Frontend** đóng vai trò là "Cửa hàng hiển thị" (Storefront). Nó giao tiếp với Backend qua Internet.
- **API Gateway** đóng vai trò là "Bảo vệ cổng" và "Lễ tân chỉ đường". Bất cứ ai (Frontend hay Mobile App) muốn kết nối đến Backend đều phải đi qua Gateway này ở cổng `8080`.
- Các **Core Services** (User, Product, Inventory, Order, Payment) đứng sau Gateway. Không ai từ bên ngoài có thể gọi trực tiếp đến chúng. Chúng giao tiếp với nhau bằng `OpenFeign`.
- Mỗi Service sở hữu một cơ sở dữ liệu riêng biệt để đảm bảo tính đóng gói (Encapsulation) tuyệt đối.

---

## 3. Phân tích Chi tiết Backend Microservices

### 3.1. API Gateway (`:8080`)
- **Nhiệm vụ:** Định tuyến URL (Direct URL Routing) và xác thực người dùng.
- **Cơ chế:** Nhận request từ Frontend. Nó sẽ soi URL (VD: `/api/product`) và chuyển tiếp xuống `product-service` (`:8081`).
- **CORS Config:** Cấu hình để cho phép Frontend ở `http://localhost:5173` được phép gửi request mà không bị trình duyệt chặn.
- **Authentication Filter:** Kiểm tra token JWT. Nếu token hợp lệ, nó sẽ giải mã lấy `userId` và đính kèm vào Header `X-Auth-User-Id` trước khi chuyển request đi tiếp.

### 3.2. User Service (`:8084`)
- **Nhiệm vụ:** Quản lý tài khoản (Đăng ký, Đăng nhập).
- **Cơ chế:** Trực tiếp tương tác với `user_service_db`. Khi người dùng đăng nhập thành công, nó sẽ dùng thuật toán `HS256` cùng Secret Key để tạo ra một chuỗi JWT.

### 3.3. Product Service (`:8081`)
- **Nhiệm vụ:** Quản lý hàng hóa (Tên, Mô tả, Giá tiền, Mã SKU).
- **Cơ chế:** Cung cấp API để Frontend lấy danh mục sản phẩm. Cung cấp API ngầm cho Order Service kiểm tra giá gốc của một mã SKU.

### 3.4. Inventory Service (`:8083`)
- **Nhiệm vụ:** Cầm trịch số lượng tồn kho vật lý.
- **Cơ chế:** Cung cấp API `check-stock` (kiểm tra còn hàng không) và `deduct-stock` (trừ hàng khi có người đặt mua). Đảm bảo tính nhất quán (không bị bán lố hàng).

### 3.5. Order Service (`:8082`)
- **Nhiệm vụ:** "Trái tim" của quy trình mua sắm.
- **Cơ chế:** Nhận yêu cầu mua hàng từ Frontend. Nó sẽ làm 2 việc: Gọi sang Product Service bằng `ProductClient` để lấy đúng giá gốc, và gọi sang Inventory Service bằng `InventoryClient` để trừ số lượng. Sau đó lưu đơn hàng lại với trạng thái `PENDING`.

---

## 4. Phân tích Chi tiết Frontend (ReactJS)

Cấu trúc Frontend được thiết kế theo hướng Component-based, chia tách rõ ràng logic và giao diện:
- **`src/context/`**:
  - `AuthContext.jsx`: Trái tim quản lý trạng thái Đăng nhập. Lưu giữ Token vào LocalStorage và tự động gắn Token vào mọi request gọi đi bằng Axios Interceptor.
  - `CartContext.jsx`: Trái tim quản lý Giỏ hàng. Quản lý mảng danh sách sản phẩm, tăng giảm số lượng, lưu tạm vào LocalStorage để không mất giỏ hàng khi F5 trang.
- **`src/pages/`**:
  - `HomePage.jsx`: Nơi liệt kê thẻ sản phẩm. Nút "Thêm vào giỏ" ở đây chỉ thao tác với vùng nhớ tạm của `CartContext`, không gọi API backend để đảm bảo hiệu năng.
  - `CartPage.jsx` & `CheckoutPage.jsx`: Nơi review giỏ hàng và tiến hành đẩy Order lên Backend.
- **`src/components/Navbar.jsx`**: Thanh điều hướng trên cùng, có khả năng lắng nghe và hiển thị realtime trạng thái Giỏ hàng (chấm đỏ đếm số lượng).

---

## 5. Thiết kế Cơ sở dữ liệu (Database Schema)

Hệ thống dùng 5 database PostgreSQL. Cấu trúc các bảng quan trọng (Entity):

- **DB: `user_service_db` | Table `users`**
  - `id` (PK, Long)
  - `name` (String)
  - `email` (String, Unique)
  - `password` (String, đã được mã hóa BCrypt)

- **DB: `product_service_db` | Table `products`**
  - `id` (PK, Long)
  - `skuCode` (String, Unique) - VD: "SKU01"
  - `name` (String)
  - `description` (String)
  - `price` (BigDecimal)

- **DB: `inventory_service_db` | Table `inventory`**
  - `id` (PK, Long)
  - `skuCode` (String, Unique)
  - `quantity` (Integer)

- **DB: `order_service_db` | Table `t_orders` và `t_order_line_items`**
  - **Orders**: `id` (PK), `orderNumber` (UUID), `userId` (Long), `totalAmount` (BigDecimal), `status` (String - PENDING/COMPLETED)
  - **Items**: `id` (PK), `order_id` (FK), `skuCode` (String), `quantity` (Integer), `price` (BigDecimal). Một đơn hàng có nhiều Items.

---

## 6. Luồng Nghiệp vụ (Business Flows)

### Flow 1: Người dùng truy cập và Đăng nhập
1. User nhập url `http://localhost:5173/login`.
2. Gõ email/password -> Bấm Đăng nhập.
3. React Frontend gọi Axios gửi lệnh `POST /api/user/login` (body: JSON).
4. Gateway nhận request -> Chuyển thẳng xuống `user-service` (do cấu hình route không bắt kiểm tra Token ở nhánh `/api/user/**`).
5. `user-service` kiểm tra Hash password hợp lệ -> Ký ra một Token JWT -> Trả về Client.
6. React Frontend nhận Token -> Lưu vào LocalStorage -> Chuyển hướng (Redirect) về Trang chủ.

### Flow 2: Đặt hàng (Checkout)
1. User ở trang `/checkout`, bấm "Xác nhận đặt hàng".
2. React Frontend móc mảng dữ liệu từ `CartContext`, gọt dũa lại thành format `[{skuCode, quantity}]`.
3. Gửi lệnh `POST /api/order` kèm Header `Authorization: Bearer <Token>`.
4. Gateway nhận Request. Filter bắt lại, thấy có Token -> Giải mã -> Lấy `userId` -> Chèn vào Header `X-Auth-User-Id` -> Chuyển xuống `order-service`.
5. `order-service` tiếp nhận, quét mảng Items. Dùng FeignClient gọi qua Product và Inventory (như đã nói ở mục 3.5).
6. Order lưu thành công. Trả về Frontend status HTTP 201.
7. Frontend nhận tín hiệu -> Xóa sạch Local Cart -> Đẩy user sang trang Success.

---

## 7. Cơ chế Bảo mật (Security & JWT)
Toàn bộ dự án tuân theo tiêu chuẩn **Stateless Security**.
- Backend không hề lưu trữ "Phiên đăng nhập" (Session/Cookie) như các web thập niên 2010s. Việc này giúp Backend không bị phình to RAM khi có triệu user.
- Security được bóc tách hoàn toàn lên tầng Gateway. Các Service đằng sau (như Order, Inventory) hoàn toàn là những "lớp lõi ngây thơ" chỉ việc lấy thông tin `X-Auth-User-Id` từ Gateway truyền xuống mà xử lý, không màng đến thế sự bảo mật.
- Passwords trong `user_service` được băm bằng `BCryptPasswordEncoder`. Kể cả khi Hacker dump được Database, họ cũng không thể giải mã ngược lại ra mật khẩu gốc.

---

## 8. Định hướng Phát triển Tương lai
Để đưa dự án SE330.Q22 đạt tầm cỡ "Production Ready", chúng ta có thể hướng tới:
1. **Refresh Token Lifecycle:** Xây dựng cơ chế làm mới Access Token (như đã giải thích ở Báo cáo 101) để user không bị văng ra ngoài sau khi Token hết hạn.
2. **Payment Service Integration:** Kết nối với các cổng thanh toán thật như VNPay, MoMo, Stripe để xử lý giao dịch.
3. **Message Broker (Kafka/RabbitMQ):** Thay vì để Order gọi Inventory qua FeignClient (Đồng bộ - Synchronous), có thể chuyển sang dùng Sự kiện (Asynchronous). Order cứ tạo trước, quăng 1 Event vào Kafka, Inventory nhặt Event và trừ dần kho.
4. **Caching với Redis:** Đưa danh mục sản phẩm vào Redis để tối ưu tốc độ load trang chủ Frontend.

Báo cáo hoàn tất. Đây là một nền tảng vững chắc để phát triển mạnh mẽ về sau!
