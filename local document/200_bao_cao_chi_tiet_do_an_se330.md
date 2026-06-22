# BÁO CÁO ĐỒ ÁN MÔN HỌC JAVA
**ĐỀ TÀI: XÂY DỰNG HỆ THỐNG CỬA HÀNG TRỰC TUYẾN QUY MÔ NHỎ (MICRO-SHOP) BẰNG KIẾN TRÚC MICROSERVICES**

**Giảng viên hướng dẫn:** Huỳnh Ngọc Tín  
**Nhóm sinh viên thực hiện (Nhóm 3):**  
- Sơn Hồ Thiên Bảo  
- Nguyễn Trung Kiên  

---

## 1. LỜI MỞ ĐẦU
Trong bối cảnh công nghệ phần mềm ngày càng phát triển, sự gia tăng đột biến về lượng người dùng và yêu cầu nghiệp vụ phức tạp đã khiến kiến trúc nguyên khối (Monolithic) bộc lộ những hạn chế rõ rệt trong việc mở rộng, bảo trì và tích hợp liên tục (CI/CD). Kiến trúc dịch vụ vi mô (Microservices Architecture) đã trở thành một giải pháp ưu việt, cho phép chia tách hệ thống thành các khối độc lập có thể triển khai và mở rộng riêng biệt.

Đồ án này tập trung xây dựng một mô hình cửa hàng trực tuyến quy mô nhỏ (Mini E-Store) mang mã dự án SE330.Q22 nhằm ứng dụng và thực nghiệm kiến trúc Microservices sử dụng nền tảng Spring Boot (Java). Hệ thống được thiết kế hướng tới tính đóng gói, khả năng chịu lỗi (fault tolerance), bảo mật không trạng thái (stateless security) và khả năng dễ dàng mở rộng theo chiều ngang (horizontal scaling).

---

## 2. KIẾN TRÚC HỆ THỐNG TỔNG QUAN
Hệ thống tuân thủ nghiêm ngặt mô hình Microservices, bẻ gãy (decouple) các nghiệp vụ E-commerce cốt lõi thành các tiểu dịch vụ có vòng đời độc lập. Kiến trúc tổng thể sở hữu những đặc điểm kỹ thuật nổi bật sau:

- **Database per Service (Một CSDL cho một Dịch vụ):** Để loại bỏ hoàn toàn rủi ro thắt cổ chai ở tầng dữ liệu và đảm bảo tính đóng gói tuyệt đối, mỗi service sở hữu một cơ sở dữ liệu PostgreSQL riêng biệt chạy cô lập trên nền tảng Docker. Tuyệt đối không có tình trạng chia sẻ bảng (No Shared Database) giữa các ứng dụng.
- **API Gateway làm Cửa ngõ Định tuyến (Single Entry Point):** Toàn bộ Request từ Client (Frontend) sẽ không gọi trực tiếp vào các service nghiệp vụ mà phải đi qua API Gateway tại cổng `8080`. Gateway thực hiện định tuyến động (Direct URL Routing) và kiểm tra tính hợp lệ của người dùng trước khi cho phép dữ liệu đi sâu vào hệ thống.
- **Giao tiếp Nội bộ (Inter-service Communication):** Hệ thống ứng dụng `Spring Cloud OpenFeign` để giải quyết bài toán giao tiếp giữa các service. Khi một service cần dữ liệu từ service khác, nó sẽ tạo ra các Rest Client giả lập để gọi HTTP nội bộ một cách đồng bộ (Synchronous).
- **Cơ chế Bảo mật Không Trạng thái (Stateless Security):** Toàn bộ quy trình xác thực dựa trên chuẩn JSON Web Token (JWT). Backend hoàn toàn không lưu trữ phiên đăng nhập (Session) hay sử dụng Cookie truyền thống, giúp tối ưu hóa dung lượng RAM và cho phép các instances có thể phân tán thoải mái mà không lo mất đồng bộ phiên người dùng.

---

## 3. CHI TIẾT CÁC MICROSERVICES CỐT LÕI (BACKEND)
Dựa trên phương pháp Phân rã theo Lĩnh vực (Domain-Driven Design - DDD), Backend được chia thành 5 dịch vụ cốt lõi:

### 3.1. API Gateway (Port 8080)
Đóng vai trò là "lính gác" cho toàn bộ cụm Server.
- **Định tuyến (Routing):** Chuyển tiếp Request từ `/api/product/**` xuống `product-service`, `/api/user/**` xuống `user-service`, v.v.
- **Xử lý CORS Tập trung:** Cấu hình Global CORS để cho phép Domain của Frontend (localhost:5173) thực hiện các giao thức Cross-Origin an toàn mà không bị trình duyệt ngăn chặn.
- **Bảo mật (Authentication Filter):** Đây là điểm nhấn của hệ thống. Thay vì để từng service tự giải mã JWT gây lặp code, Gateway triển khai một custom Filter. Filter này bóc tách Header `Authorization`, xác thực JWT bằng Secret Key, trích xuất `userId` và đính kèm (inject) ngầm vào header nội bộ mang tên `X-Auth-User-Id`. Các service phía sau chỉ việc lấy `userId` này để xử lý nghiệp vụ mà không cần biết JWT là gì.

### 3.2. User Service (Port 8084)
Chịu trách nhiệm quản lý định danh, tài khoản và vòng đời xác thực.
- Cung cấp các endpoint cho quy trình Đăng ký (Register) và Đăng nhập (Login).
- Đảm bảo an toàn thông tin bằng cách băm (hashing) mật khẩu người dùng thông qua thư viện `BCryptPasswordEncoder` trước khi lưu vào DB.
- Khi xác thực thành công, service sử dụng thư viện `JJWT` với thuật toán `HMAC-SHA256` để ký và phát hành chuỗi Access Token mang định danh người dùng.

### 3.3. Product Service (Port 8081)
Đóng vai trò như một kho danh mục hàng hóa điện tử (Catalog).
- Quản lý các thuộc tính cốt lõi của sản phẩm: Mã định danh (SKU Code), Tên, Mô tả, và Giá bán niêm yết (Price).
- **Cơ chế chống gian lận (Anti-fraud):** Cung cấp API nội bộ cho Order Service để lấy giá gốc của sản phẩm dựa trên SKU. Bằng cách này, dù hacker có can thiệp sửa giá trên Frontend, hệ thống Backend vẫn tính toán dựa trên giá chuẩn từ Product Service.

### 3.4. Inventory Service (Port 8083)
Quản lý dòng chảy số lượng vật lý của hàng hóa trong kho.
- Kiểm soát trạng thái hàng hóa một cách chặt chẽ qua 3 chỉ số: `quantityAvailable` (Có thể bán), `quantityReserved` (Đang bị giữ chỗ) và `quantitySold` (Đã bán thành công).
- Cung cấp API `isInStock` giúp Frontend tra cứu để ngăn người dùng thêm vào giỏ hàng các mặt hàng đã hết.
- Đảm bảo tính toàn vẹn dữ liệu trong giao dịch mua bán: Khi có lệnh trừ kho từ Order Service, hệ thống sẽ kiểm tra logic chặt chẽ để chống lại hiện tượng Overselling (Bán lố hàng).

### 3.5. Order Service (Port 8082)
"Bộ não điều phối" của toàn bộ quy trình E-Commerce. Quá trình Checkout diễn ra như sau:
1. Nhận mảng các sản phẩm `[{skuCode, quantity}]` từ Frontend.
2. Dùng `ProductClient` (OpenFeign) gọi qua Product Service để chốt giá thực tế. Tính toán tổng tiền hóa đơn (`totalAmount`).
3. Dùng `InventoryClient` (OpenFeign) gọi qua Inventory Service để đối chiếu và giữ chỗ lượng hàng tồn kho.
4. Nếu kho xác nhận thành công, khởi tạo thực thể `Order` và lưu vào PostgreSQL với trạng thái `PENDING` kèm theo mã `orderNumber` (UUID) duy nhất.

### 3.6. Payment Service (Port 8085)
Tầng trung gian xử lý thanh toán, đảm nhận việc giao tiếp an toàn với bên thứ ba (Cổng thanh toán MoMo).
- Tiếp nhận `orderId` từ Frontend, đối chiếu tổng tiền hóa đơn.
- Xây dựng Payload và sử dụng thuật toán mã hóa `HmacSHA256` kết hợp Secret Key để tạo Chữ ký điện tử (Signature) hợp lệ.
- Giao tiếp qua HTTP POST với API của MoMo Sandbox để lấy liên kết thanh toán (`payUrl`) và trả về cho hệ thống điều hướng Client.

---

## 4. FRONTEND VÀ GIAO DIỆN NGƯỜI DÙNG
Frontend được xây dựng bằng hệ sinh thái React (ReactJS 18, Vite, React Router DOM, Axios). Kiến trúc được thiết kế theo nguyên tắc "Thin Client - Thick Server" (Client mỏng, Server dày), nhường việc tính toán nặng và bảo mật cho Backend, Frontend tập trung vào luồng UX/UI.

- **AuthContext (Quản lý Xác thực):** Là trung tâm quản lý State đăng nhập toàn cục. Khi nhận được JWT từ Backend, AuthContext lưu trữ an toàn vào `LocalStorage`. Tại các page yêu cầu gọi API bảo mật, Token được lấy trực tiếp từ State và đính kèm vào phần Headers của thư viện Axios.
- **CartContext (Giỏ hàng và Tồn kho Fail-fast):** Giỏ hàng được vận hành hoàn toàn trên bộ nhớ RAM và `LocalStorage` của trình duyệt. Điều này cho phép thao tác tăng/giảm số lượng mượt mà không có độ trễ network. 
- **Cơ chế Inventory Validation:** Điểm nổi bật là Frontend đã gọi API Inventory để tích hợp số lượng kho thực tế (`quantityAvailable`) trực tiếp lên thẻ sản phẩm. Ngăn chặn triệt để hành động (Disable Button) mua hàng đã hết, và khóa nút tăng số lượng khi chạm trần tồn kho (Fail-fast), giúp tiết kiệm tài nguyên xử lý cho Backend.

---

## 5. THIẾT KẾ CƠ SỞ DỮ LIỆU (DATABASE SCHEMA)
Hệ thống triển khai 5 cơ sở dữ liệu riêng biệt chạy trên PostgreSQL với kiến trúc chuẩn hóa thực thể. Các bảng lõi bao gồm:

| Tên Cơ sở Dữ liệu | Bảng Thực thể (Entity) | Các Trường Dữ liệu Chính (Attributes) |
| :--- | :--- | :--- |
| **`user_service_db`** | `users` | `id` (PK), `name`, `email` (Unique), `password` (BCrypt Hash) |
| **`product_service_db`** | `products` | `id` (PK), `sku_code` (Unique), `name`, `description`, `price` |
| **`inventory_service_db`** | `inventory` | `id` (PK), `sku_code`, `quantity_available`, `quantity_reserved`, `quantity_sold` |
| **`order_service_db`** | `t_orders` | `id` (PK), `order_number` (UUID), `user_id`, `total_amount`, `status` |
| | `t_order_line_items` | `id` (PK), `order_id` (FK), `sku_code`, `quantity`, `price` |

---

## 6. KẾT LUẬN VÀ ĐỊNH HƯỚNG TƯƠNG LAI
Mặc dù là một dự án E-Store quy mô mô phỏng, SE330.Q22 đã hoàn thành xuất sắc mục tiêu hiện thực hóa một kiến trúc Microservices phân tán thực tiễn. Việc tách biệt rạch ròi biên giới nghiệp vụ (Bounded Context) giữa các service và database không chỉ đảm bảo hiệu năng trong hiện tại mà còn tạo ra một nền tảng mã nguồn vững chắc, sẵn sàng nâng cấp.

**Định hướng phát triển và tối ưu hóa trong tương lai:**
1. **Message Broker (Event-Driven Architecture):** Thay vì sử dụng OpenFeign để gọi đồng bộ giữa Order và Inventory (có thể gây ra rủi ro Single Point of Failure nếu Inventory chết), hệ thống sẽ tích hợp Apache Kafka hoặc RabbitMQ. Chuyển đổi thành kiến trúc Bất đồng bộ (Asynchronous) thông qua các Event (Sự kiện).
2. **Caching Mechanism:** Tích hợp Redis vào Product Service để lưu trữ danh mục hàng hóa (Cache), nhằm giảm thiểu độ trễ truy vấn Database và tăng tốc độ phản hồi cho trang chủ.
3. **Mở rộng Thanh toán:** Dựa trên cấu trúc Payment Service linh hoạt đã có sẵn từ MoMo, hệ thống có thể dễ dàng apply pattern Strategy để cắm thêm (plug-in) các cổng thanh toán khác như VNPay, ZaloPay hoặc Stripe.
4. **Triển khai Đám mây (Cloud Native):** Sử dụng Kubernetes (K8s) để điều phối container thay cho Docker Compose, kết hợp cấu hình CI/CD Pipeline để tự động hóa quá trình kiểm thử và triển khai.

---

## PHỤ LỤC: PHÂN CÔNG CÔNG VIỆC

| Thành viên | Nhiệm vụ đảm nhiệm |
| :--- | :--- |
| **Nguyễn Trung Kiên** | - Thiết kế cấu trúc Cơ sở dữ liệu (Database Schema) phân tán cho 5 database riêng biệt.<br>- Xây dựng các Backend Core Microservices: User, Product, Inventory và Order Service.<br>- Xây dựng Payment Service backend để xử lý thuật toán tạo chữ ký và giao tiếp với hệ thống MoMo.<br>- Trực tiếp test các dịch vụ backend nhằm đảm bảo tính ổn định của hệ thống. |
| **Sơn Hồ Thiên Bảo** | - Thiết lập và cấu hình API Gateway (Định tuyến, CORS, Authentication Filter).<br>- Xây dựng nền tảng Frontend (React Router, AuthContext, UI Component).<br>- Xây dựng luồng Giỏ hàng (CartContext) và xử lý kiểm tra tồn kho trực tiếp trên giao diện.<br>- Ghép nối Frontend với Backend xử lý luồng Checkout và khắc phục lỗi giao tiếp giữa các microservices.<br>- Tích hợp API gọi cổng thanh toán MoMo từ Frontend và bổ sung tính năng thanh toán COD. |
