import { Link } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';

const OrderSuccessPage = () => {
  return (
    <div className="flex-center animate-fade-in" style={{ minHeight: '60vh', flexDirection: 'column', textAlign: 'center' }}>
      <CheckCircle size={80} color="var(--success)" style={{ marginBottom: '1.5rem' }} />
      <h2 style={{ fontSize: '2.5rem', fontWeight: '700', marginBottom: '1rem' }}>Đặt hàng thành công!</h2>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem', fontSize: '1.125rem', maxWidth: '500px' }}>
        Cảm ơn bạn đã mua sắm tại Q22 Shop. Đơn hàng của bạn đã được ghi nhận và đang trong quá trình xử lý.
      </p>
      <div style={{ display: 'flex', gap: '1rem' }}>
        <Link to="/" className="btn btn-outline">Tiếp tục mua sắm</Link>
      </div>
    </div>
  );
};

export default OrderSuccessPage;
