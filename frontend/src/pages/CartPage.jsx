import { Link } from 'react-router-dom';

const CartPage = () => {
  return (
    <div className="flex-center animate-fade-in" style={{ minHeight: '60vh', flexDirection: 'column', textAlign: 'center' }}>
      <h2 style={{ fontSize: '2rem', fontWeight: '700', marginBottom: '1rem' }}>Giỏ hàng của bạn</h2>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>
        Tính năng giỏ hàng chi tiết đang được phát triển. Hiện tại bạn có thể đặt hàng trực tiếp từ thẻ sản phẩm!
      </p>
      <Link to="/" className="btn btn-primary">Quay lại mua sắm</Link>
    </div>
  );
};

export default CartPage;
