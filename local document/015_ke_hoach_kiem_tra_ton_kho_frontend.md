# Triển khai tính năng kiểm tra Tồn Kho (Inventory Check) trên Frontend

Tính năng này sẽ đồng bộ số lượng tồn kho thực tế từ backend để ngăn chặn việc người dùng thêm các sản phẩm đã hết hàng vào giỏ, cũng như chặn việc tăng số lượng mua vượt quá số lượng còn lại trong kho.

## Các thay đổi dự kiến

---

### Frontend

#### [MODIFY] [HomePage.jsx](file:///d:/UIT%20-%20Third%20year%20-%20II/SE330%20-%20Ngon%20ngu%20lap%20trinh%20Java/Project/SE330.Q22/frontend/src/pages/HomePage.jsx)
- Cập nhật hàm `fetchProducts` để gọi thêm API `GET /api/inventory` (chạy song song với API products để tiết kiệm thời gian).
- Ánh xạ (Map) giá trị `quantityAvailable` từ dữ liệu trả về của API inventory vào từng object sản phẩm.

#### [MODIFY] [ProductCard.jsx](file:///d:/UIT%20-%20Third%20year%20-%20II/SE330%20-%20Ngon%20ngu%20lap%20trinh%20Java/Project/SE330.Q22/frontend/src/components/ProductCard.jsx)
- Hiển thị thêm thông tin "Còn lại: X sản phẩm" để người dùng dễ theo dõi.
- Nếu `quantityAvailable <= 0`: 
  - Đổi màu nút "Thêm vào giỏ" sang màu xám.
  - Vô hiệu hóa nút (disabled) và đổi chữ thành "Hết hàng".

#### [MODIFY] [CartContext.jsx](file:///d:/UIT%20-%20Third%20year%20-%20II/SE330%20-%20Ngon%20ngu%20lap%20trinh%20Java/Project/SE330.Q22/frontend/src/context/CartContext.jsx)
- Trong hàm `addToCart`: Kiểm tra nếu số lượng trong giỏ hàng hiện tại + số lượng chuẩn bị thêm > `quantityAvailable` thì sẽ chặn lại và cảnh báo "Chỉ còn X sản phẩm trong kho".
- Trong hàm `updateQuantity`: Chặn việc truyền số lượng lớn hơn `quantityAvailable`.

#### [MODIFY] [CartPage.jsx](file:///d:/UIT%20-%20Third%20year%20-%20II/SE330%20-%20Ngon%20ngu%20lap%20trinh%20Java/Project/SE330.Q22/frontend/src/pages/CartPage.jsx)
- Tại nút dấu cộng `+` tăng số lượng: Thêm logic `disabled={item.quantity >= item.quantityAvailable}` để nút này mờ đi khi người dùng đã đạt đến giới hạn số lượng trong kho.
- Hiển thị thông báo nhỏ "Đạt giới hạn kho" nếu cần thiết.

## Verification Plan

### Manual Verification
- F5 lại trang chủ, kiểm tra xem số lượng tồn kho có hiển thị đúng với DB không.
- Thử bấm "Thêm vào giỏ" liên tục 1 sản phẩm cho đến khi đạt giới hạn kho để xem hệ thống có chặn lại không.
- Chỉnh sửa thủ công số lượng tồn kho của "Pad chuột cũ" về 0 trong Database và kiểm tra xem nút bấm có biến thành "Hết hàng" không.
