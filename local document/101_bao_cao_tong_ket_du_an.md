# 101. Báo cáo Tổng kết Toàn bộ Quá trình Phát triển Dự án SE330.Q22

Báo cáo này là tài liệu tổng hợp toàn diện nhất, ghi lại chi tiết toàn bộ các công việc, thay đổi kiến trúc, cơ chế hoạt động, cũng như cách các thành phần trong dự án tương tác với nhau từ ngày đầu tiên bắt tay vào việc cho đến hiện tại.

---

## Phần 1. Tổng quan Kiến trúc Hệ thống (Architecture Overview)

Hệ thống được thiết kế theo mô hình **Microservices Architecture** kết hợp với **Single Page Application (SPA)** ở phía Frontend.

### 1.1. Backend (Spring Boot Microservices)
Thay vì sử dụng kiến trúc Monolithic (nguyên khối), Backend được chia nhỏ thành các dịch vụ độc lập, chạy trên các cổng (port) khác nhau:
- **User Service (`:8084`)**: Quản lý thông tin người dùng, xác thực (Authentication) và cấp phát JWT Token.
- **Product Service (`:8081`)**: Quản lý thông tin, danh mục và giá cả của sản phẩm.
- **Inventory Service (`:8083`)**: Quản lý số lượng tồn kho của sản phẩm.
- **Order Service (`:8082`)**: Xử lý logic đặt hàng, liên kết với các service khác để lấy giá và trừ kho.
- **API Gateway (`:8080`)**: Điểm vào duy nhất (Single Entry Point) của toàn bộ hệ thống Backend. Tất cả request từ Frontend đều phải đi qua đây. Hệ thống **không dùng Eureka Discovery Server** mà sử dụng **Direct URL Routing** được cấu hình thẳng bằng code Java (`RouteConfig.java`) giúp giảm bớt tài nguyên khởi chạy cục bộ.

### 1.2. Frontend (ReactJS + Vite)
Đóng vai trò là Client, giao tiếp với Backend hoàn toàn thông qua RESTful API qua cổng `:8080` của API Gateway.

---

## Phần 2. Chi tiết Các Công việc Đã Thực hiện và Ý nghĩa

### 2.1. Giải quyết Bài toán Bảo mật và Định tuyến (API Gateway)
**Công việc:**
- Chuyển đổi cấu hình Route từ file `application.yml` sang file cấu hình Java thuần (`RouteConfig.java`) để đảm bảo tính ổn định và dễ gỡ lỗi.
- Thiết lập cơ chế **CORS (Cross-Origin Resource Sharing)** tập trung tại API Gateway để cho phép Frontend (`http://localhost:5173`) gọi API mà không bị trình duyệt chặn.
- Viết lại `AuthenticationGatewayFilterFactory`: Đóng vai trò là "Người gác cổng". Khi người dùng gửi request vào các route yêu cầu bảo mật (như `/api/order`), Filter này sẽ:
  1. Kiểm tra header `Authorization` có chứa JWT Token hay không.
  2. Xác thực tính hợp lệ của Token.
  3. Giải mã Token để lấy `userId`, sau đó **đính kèm ngầm** (inject) vào Header mới mang tên `X-Auth-User-Id`.
  4. Chuyển tiếp request đến các service bên dưới (downstream).
**Tác dụng:** Các service như `order-service` không cần tự mình xử lý giải mã JWT phức tạp nữa, mà chỉ cần đọc header `X-Auth-User-Id` là biết ngay ai đang đặt hàng. Cách làm này chuẩn hóa mô hình Microservices bảo mật.

### 2.2. Xây dựng Nền tảng Giao diện (Frontend Foundation)
**Công việc:**
- Thiết lập React Router (`App.jsx`) để định tuyến các trang: Home, Login, Register, Cart, Checkout, Order Success.
- Thiết lập **`AuthContext.jsx`**: Global State quản lý trạng thái đăng nhập. JWT Token sau khi được cấp từ Backend (lúc Login) sẽ được Context này lưu trữ an toàn vào `LocalStorage` của trình duyệt. Mỗi khi gọi axios, token này tự động được lôi ra gắn vào Header.
- Xây dựng Component tái sử dụng như `Navbar` và `ProductCard`.

### 2.3. Cấu trúc lại Luồng Giỏ hàng (Cart Flow)
**Vấn đề cũ:** Thiết kế ban đầu có xu hướng gọi thẳng API `POST /api/order` mỗi khi người dùng bấm "Thêm vào giỏ" ở một sản phẩm. Điều này là sai về mặt nghiệp vụ E-commerce vì một đơn hàng thường gồm nhiều món.
**Giải pháp đã triển khai:**
- Tạo **`CartContext.jsx`**: Cung cấp một Giỏ hàng ảo (Local Cart) chạy hoàn toàn trên RAM và LocalStorage của trình duyệt.
- Tác dụng: Người dùng có thể thêm món, tăng giảm số lượng, xóa món tức thời mà không tốn bất kỳ kết nối mạng nào tới Backend. Trải nghiệm mượt mà 100%.

### 2.4. Khắc phục Lỗi Nghiêm trọng tại Backend (Order Service)
**Công việc:**
- Khi tiến hành test luồng Checkout, Backend báo lỗi 404. Sau khi trace (truy vết) mã nguồn Backend, tôi đã phát hiện ra lỗi sai chính tả trong `order-service/src/main/java/com/SE330_Q22/order_service/client/ProductClient.java`.
- Feign Client này được `order-service` dùng để "gọi điện" sang `product-service`. Nhưng nó lại bị dư chữ "s" (`/api/products` thay vì `/api/product`). 
- Đã tiến hành sửa lỗi bằng file `replace_file_content`, commit lại và hướng dẫn khởi động lại service.
**Tác dụng:** Kết nối giữa các service được nối lại, giúp luồng Checkout hoàn thành trơn tru.

### 2.5. Xây dựng Luồng Đặt hàng và Thanh toán (Checkout Flow)
**Cơ chế hoạt động:**
- Khi người dùng ở trang `/checkout` và bấm "Xác nhận đặt hàng":
  1. Frontend đóng gói dữ liệu giỏ hàng thành một mảng chỉ chứa mã `skuCode` và `quantity`. 
  2. Gửi request `POST http://localhost:8080/api/order` (Kèm Token).
  3. Backend **`order-service`** nhận lệnh. Nó **không tin tưởng** giá (`price`) do Frontend gửi lên (để chống hack giá).
  4. `order-service` gọi sang **`product-service`** thông qua `ProductClient` để tra cứu giá gốc của từng `skuCode` hiện tại là bao nhiêu.
  5. Tính toán tổng tiền thực tế.
  6. `order-service` tiếp tục gọi sang **`inventory-service`** thông qua `InventoryClient` để "giữ chỗ" (Reserve) số lượng hàng đó trong kho. Tránh trường hợp 2 người cùng mua món đồ cuối cùng.
  7. Nếu kho còn hàng, Order được tạo thành công vào Database PostgreSQL với trạng thái `PENDING`.
  8. Phản hồi HTTP 201 trả về Frontend.
  9. Frontend nhận phản hồi, xóa sạch Giỏ hàng (Local Cart) và hiển thị màn hình `OrderSuccessPage`.

### 2.6. Tích hợp Payment Service (MoMo) và Các Tùy chỉnh Nâng cao
**Cơ sở nền tảng:** Một thành viên của team phát triển đã xây dựng thành công `payment-service` đóng vai trò liên kết với API MoMo Sandbox, bao gồm xử lý tạo chữ ký (Signature) bằng HMAC-SHA256, gọi tới cổng thanh toán và hứng kết quả trả về.
**Các bổ sung và khắc phục lỗi quan trọng chúng ta đã làm:**
- **Sửa lỗi Timezone PostgreSQL 16 trên Windows:** Khắc phục triệt để lỗi "The JVM timezone is Asia/Saigon... but PostgreSQL 16 does not support it" bằng cách chèn cờ `?options=-c%20timezone=Asia/Ho_Chi_Minh` vào chuỗi JDBC URL của toàn bộ 5 microservices.
- **Tích hợp API MoMo vào luồng Frontend Checkout:** Tự động gọi API `/api/payments` của `payment-service` sau khi Order Service trả về `orderId`, lấy `payUrl` và tự động điều hướng người dùng quét mã.
- **Bổ sung tính năng Thanh toán khi nhận hàng (COD):** Tạo thêm giao diện Modal/Radio box chọn phương thức ngay trước khi thanh toán, cung cấp một lối tắt (bypass) hoàn hảo để kiểm thử nhanh luồng thành công mà không phải lúc nào cũng cần mở ví MoMo.

### 2.7. Cơ chế Kiểm soát Tồn kho (Inventory Validation) Toàn diện trên Frontend
**Công việc:**
- Tại Trang chủ (`HomePage`): Tích hợp gọi API `inventory-service` song song với `product-service` để lấy dữ liệu kho thực tế. Gắn trực tiếp trạng thái "Còn lại: X sản phẩm" hoặc đổi thành "Tạm hết hàng" (đồng thời mờ nút bấm) ngay trên mỗi Product Card.
- Tại Context và Giỏ hàng (`CartContext`, `CartPage`): Thêm cơ chế chốt chặn (Validation), mờ nút `+` khi chạm trần tồn kho, ngăn chặn người dùng mua lố số lượng thực tế của kho và hiển thị cảnh báo.
**Tác dụng:** Cải thiện triệt để logic nghiệp vụ E-commerce, ngăn chặn các case lỗi từ sớm (Fail-fast) ngay tại giao diện hiển thị, nâng tầm trải nghiệm UX/UI của dự án lên mức chuyên nghiệp.

---

## Phần 3. Tương quan giữa Các Thành phần (Component Correlation)

Nhìn chung, hệ thống hoạt động như một cỗ máy nhiều bánh răng:

- **Frontend đóng vai trò "Trình diễn và Gom dữ liệu"**: Nó không làm các phép tính quan trọng (như tổng tiền hóa đơn cuối cùng để trừ tiền), nó chỉ làm nhiệm vụ giao tiếp với người dùng, gom đủ thông tin (`skuCode`, `quantity`) rồi đưa cho Backend xử lý. Điều này đáp ứng tiêu chuẩn "Thin Client - Thick Server".
- **API Gateway đóng vai trò "Bảo vệ và Điều phối"**: Không ai từ bên ngoài được gọi thẳng vào `order-service` hay `user-service`. Gateway giấu kín kiến trúc bên trong, lo liệu việc kiểm tra vé (Token JWT) trước khi cho vào cửa.
- **Backend Services đóng vai trò "Chuyên môn hóa" (Choreography/Orchestration)**: Mỗi service làm đúng một việc:
  - `product-service`: Cuốn catalogue sản phẩm.
  - `inventory-service`: Nhà kho.
  - `order-service`: Thu ngân (đứng giữa gọi catalogue và nhà kho để lập hóa đơn).

## Phần 4. Kết luận
Từ một dự án có nhiều chức năng còn phân mảnh và phát sinh lỗi ngầm, chúng ta đã cùng nhau quy hoạch lại toàn bộ luồng E-commerce. Giao diện Frontend đã tương thích 100% với cấu trúc Backend Microservices, tạo ra một trải nghiệm mua sắm bảo mật, mượt mà và đúng chuẩn thiết kế hệ thống lớn. Toàn bộ code đều đã được thiết kế sẵn sàng để dễ dàng mở rộng thêm tính năng Payment (Thanh toán) trong tương lai.
