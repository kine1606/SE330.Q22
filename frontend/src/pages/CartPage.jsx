import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { Trash2, Plus, Minus, ShoppingBag } from 'lucide-react';

const CartPage = () => {
  const { cartItems, removeFromCart, updateQuantity, getCartTotal } = useCart();
  const navigate = useNavigate();

  if (cartItems.length === 0) {
    return (
      <div className="flex-center animate-fade-in" style={{ minHeight: '60vh', flexDirection: 'column', textAlign: 'center' }}>
        <ShoppingBag size={64} color="var(--text-secondary)" style={{ marginBottom: '1.5rem' }} />
        <h2 style={{ fontSize: '2rem', fontWeight: '700', marginBottom: '1rem' }}>Giỏ hàng trống</h2>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>
          Bạn chưa có sản phẩm nào trong giỏ hàng.
        </p>
        <Link to="/" className="btn btn-primary">Tiếp tục mua sắm</Link>
      </div>
    );
  }

  return (
    <div className="animate-fade-in">
      <h2 style={{ fontSize: '2rem', fontWeight: '700', marginBottom: '2rem' }}>Giỏ hàng của bạn</h2>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
        <div className="glass" style={{ borderRadius: '1rem', overflow: 'hidden' }}>
          {cartItems.map((item, index) => (
            <div 
              key={item.skuCode} 
              style={{ 
                display: 'flex', 
                alignItems: 'center', 
                padding: '1.5rem',
                borderBottom: index < cartItems.length - 1 ? '1px solid var(--border)' : 'none',
                gap: '1.5rem',
                flexWrap: 'wrap'
              }}
            >
              <div style={{ flex: '1', minWidth: '200px' }}>
                <h3 style={{ fontSize: '1.125rem', fontWeight: '600', marginBottom: '0.25rem' }}>{item.name}</h3>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>Mã: {item.skuCode}</p>
              </div>
              
              <div style={{ fontSize: '1.25rem', fontWeight: '600', color: 'var(--accent-primary)', minWidth: '100px' }}>
                {item.price.toLocaleString('vi-VN')} VNĐ
              </div>
              
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', backgroundColor: 'var(--bg-secondary)', padding: '0.25rem', borderRadius: '0.5rem' }}>
                <button 
                  onClick={() => updateQuantity(item.skuCode, item.quantity - 1)}
                  style={{ padding: '0.5rem', border: 'none', background: 'transparent', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                  disabled={item.quantity <= 1}
                >
                  <Minus size={16} />
                </button>
                <span style={{ width: '2rem', textAlign: 'center', fontWeight: '500' }}>{item.quantity}</span>
                <button 
                  onClick={() => updateQuantity(item.skuCode, item.quantity + 1)}
                  style={{ padding: '0.5rem', border: 'none', background: 'transparent', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                >
                  <Plus size={16} />
                </button>
              </div>

                <div style={{ fontSize: '1.25rem', fontWeight: '700', minWidth: '100px', textAlign: 'right' }}>
                    {(item.price * item.quantity).toLocaleString('vi-VN')} VNĐ
                </div>
              
              <button 
                onClick={() => removeFromCart(item.skuCode)}
                style={{ border: 'none', background: 'transparent', cursor: 'pointer', color: 'var(--error)', padding: '0.5rem' }}
                title="Xóa sản phẩm"
              >
                <Trash2 size={20} />
              </button>
            </div>
          ))}
        </div>
        
        <div className="glass" style={{ padding: '2rem', borderRadius: '1rem', display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '1rem' }}>
          <div style={{ display: 'flex', gap: '2rem', alignItems: 'center', fontSize: '1.25rem' }}>
            <span style={{ color: 'var(--text-secondary)', fontWeight: '500' }}>Tổng cộng:</span>
            <span style={{ fontSize: '1.75rem', fontWeight: '700', color: 'var(--accent-primary)' }}>{getCartTotal().toLocaleString('vi-VN')} VNĐ</span>
          </div>
          <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
            <Link to="/" className="btn btn-outline">Tiếp tục mua sắm</Link>
            <button onClick={() => navigate('/checkout')} className="btn btn-primary">
              Tiến hành thanh toán
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CartPage;
