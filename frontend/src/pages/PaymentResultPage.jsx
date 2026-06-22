import { Link, useSearchParams } from 'react-router-dom';
import { CheckCircle, AlertCircle } from 'lucide-react';

const PaymentResultPage = () => {
  const [searchParams] = useSearchParams();
  const resultCode = searchParams.get('resultCode');
  const message = searchParams.get('message') || 'Không rõ trạng thái giao dịch.';
  const orderId = searchParams.get('orderId');

  const isSuccess = resultCode === '0';

  return (
    <div className="flex-center animate-fade-in" style={{ minHeight: '60vh', flexDirection: 'column', textAlign: 'center' }}>
      {isSuccess ? (
        <CheckCircle size={80} color="var(--success)" style={{ marginBottom: '1.5rem' }} />
      ) : (
        <AlertCircle size={80} color="var(--error)" style={{ marginBottom: '1.5rem' }} />
      )}
      
      <h2 style={{ fontSize: '2.5rem', fontWeight: '700', marginBottom: '1rem', color: isSuccess ? 'var(--success)' : 'var(--error)' }}>
        {isSuccess ? 'Thanh toán thành công!' : 'Thanh toán thất bại / Hủy bỏ'}
      </h2>
      
      <p style={{ color: 'var(--text-secondary)', marginBottom: '0.5rem', fontSize: '1.125rem', maxWidth: '500px' }}>
        <strong>Trạng thái từ MoMo:</strong> {decodeURIComponent(message)}
      </p>
      
      {orderId && (
        <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem', fontSize: '1.125rem' }}>
          <strong>Mã đơn hàng:</strong> {orderId}
        </p>
      )}

      <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
        <Link to="/" className="btn btn-outline">Về Trang Chủ</Link>
      </div>
    </div>
  );
};

export default PaymentResultPage;
