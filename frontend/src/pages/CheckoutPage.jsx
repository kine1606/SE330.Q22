import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { CheckCircle, AlertCircle } from 'lucide-react';

const CheckoutPage = () => {
  const { cartItems, getCartTotal, clearCart } = useCart();
  const { token } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('MOMO');

  if (cartItems.length === 0) {
    return (
        <div className="flex-center animate-fade-in" style={{ minHeight: '60vh', flexDirection: 'column', textAlign: 'center' }}>
          <h2 style={{ fontSize: '2rem', fontWeight: '700', marginBottom: '1rem' }}>Không có sản phẩm để thanh toán</h2>
          <Link to="/" className="btn btn-primary">Quay lại mua sắm</Link>
        </div>
    );
  }

  const handlePlaceOrder = async () => {
    setLoading(true);
    setError('');

    try {
      // BƯỚC 1: Tạo cấu trúc các item trong giỏ hàng
      const orderItems = cartItems.map(item => ({
        skuCode: item.skuCode,
        quantity: item.quantity
      }));

      // BƯỚC 2: Gọi API tạo đơn hàng (Order Service qua Gateway)
      const orderResponse = await axios.post('http://localhost:8080/api/orders', {
        items: orderItems
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });

      // Lấy orderId từ kết quả trả về của Order Service
      // (Cậu kiểm tra lại xem DTO của con OrderService trả về trường 'id' hay 'orderId' nhé)
      const orderId = orderResponse.data.id || orderResponse.data.orderId;

      if (!orderId) {
        throw new Error('Không tìm thấy mã đơn hàng từ hệ thống.');
      }

      if (paymentMethod === 'MOMO') {
        // BƯỚC 3: Gọi API thanh toán MoMo (Payment Service qua Gateway)
        const paymentResponse = await axios.post('http://localhost:8080/api/payments', {
          orderId: orderId,
          paymentMethod: "MOMO"
        }, {
          headers: { Authorization: `Bearer ${token}` }
        });

        // BƯỚC 4: Lấy payUrl và điều hướng người dùng sang cổng MoMo để quét QR
        const payUrl = paymentResponse.data.payUrl;

        if (payUrl) {
          clearCart(); // Xóa giỏ hàng trước khi chuyển đi
          window.location.href = payUrl; // Điều hướng trực tiếp sang trang hiển thị QR của MoMo
        } else {
          throw new Error('Không nhận được link thanh toán từ MoMo.');
        }
      } else {
        // BƯỚC 3 (COD): Không gọi API thanh toán MoMo, chuyển thẳng tới trang thành công
        clearCart();
        navigate('/order-success');
      }

    } catch (err) {
      console.error('Order & Payment error:', err);
      setError(err.response?.data?.message || 'Đặt hàng hoặc khởi tạo thanh toán thất bại. Vui lòng thử lại sau.');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="animate-fade-in" style={{ maxWidth: '800px', margin: '0 auto' }}>
        <h2 style={{ fontSize: '2rem', fontWeight: '700', marginBottom: '2rem' }}>Thanh toán</h2>

        {error && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--error)', backgroundColor: 'rgba(239, 68, 68, 0.1)', padding: '1rem', borderRadius: '0.5rem', marginBottom: '2rem' }}>
              <AlertCircle size={20} />
              {error}
            </div>
        )}

        <div className="glass" style={{ padding: '2rem', borderRadius: '1rem' }}>
          <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '1.5rem', borderBottom: '1px solid var(--border)', paddingBottom: '1rem' }}>Tóm tắt đơn hàng</h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginBottom: '2rem' }}>
            {cartItems.map(item => (
                <div key={item.skuCode} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <span style={{ fontWeight: '500' }}>{item.name}</span>
                    <span style={{ color: 'var(--text-secondary)', marginLeft: '0.5rem' }}>x {item.quantity}</span>
                  </div>
                  <div style={{ fontWeight: '600' }}>{(item.price * item.quantity).toLocaleString('vi-VN')} VNĐ</div>
                </div>
            ))}
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid var(--border)', paddingTop: '1.5rem', marginBottom: '2rem' }}>
            <span style={{ fontSize: '1.25rem', fontWeight: '500' }}>Tổng thanh toán:</span>
            <span style={{ fontSize: '1.75rem', fontWeight: '700', color: 'var(--accent-primary)' }}>{getCartTotal().toLocaleString('vi-VN')} VNĐ</span>
          </div>

          <div style={{ marginBottom: '2rem' }}>
            <h4 style={{ fontWeight: '600', marginBottom: '1rem' }}>Phương thức thanh toán</h4>
            <div style={{ display: 'flex', gap: '1rem' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', padding: '1rem', border: `2px solid ${paymentMethod === 'MOMO' ? 'var(--accent-primary)' : 'var(--border)'}`, borderRadius: '0.5rem', flex: 1 }}>
                <input type="radio" name="paymentMethod" value="MOMO" checked={paymentMethod === 'MOMO'} onChange={() => setPaymentMethod('MOMO')} style={{ cursor: 'pointer', width: '1.2rem', height: '1.2rem' }} />
                <span style={{ fontWeight: paymentMethod === 'MOMO' ? '600' : '400' }}>Thanh toán qua Ví MoMo</span>
              </label>
              <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', padding: '1rem', border: `2px solid ${paymentMethod === 'CASH' ? 'var(--accent-primary)' : 'var(--border)'}`, borderRadius: '0.5rem', flex: 1 }}>
                <input type="radio" name="paymentMethod" value="CASH" checked={paymentMethod === 'CASH'} onChange={() => setPaymentMethod('CASH')} style={{ cursor: 'pointer', width: '1.2rem', height: '1.2rem' }} />
                <span style={{ fontWeight: paymentMethod === 'CASH' ? '600' : '400' }}>Thanh toán khi nhận hàng (COD)</span>
              </label>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '1rem' }}>
            <Link to="/cart" className="btn btn-outline" style={{ flex: 1, textAlign: 'center' }}>Quay lại giỏ hàng</Link>
            <button
                onClick={handlePlaceOrder}
                className="btn btn-primary"
                style={{ flex: 2, display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '0.5rem' }}
                disabled={loading}
            >
              {loading ? 'Đang xử lý...' : (
                  <>
                    <CheckCircle size={20} /> Xác nhận đặt hàng
                  </>
              )}
            </button>
          </div>
        </div>
      </div>
  );
};

export default CheckoutPage;