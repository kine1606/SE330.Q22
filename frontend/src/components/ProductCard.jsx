import { useState } from 'react';
import { ShoppingCart } from 'lucide-react';

const ProductCard = ({ product, onAddToCart }) => {
  const [isHovered, setIsHovered] = useState(false);

  return (
    <div 
      className="glass"
      style={{ 
        padding: '1.5rem', 
        borderRadius: '1rem',
        transition: 'transform 0.3s ease, box-shadow 0.3s ease',
        transform: isHovered ? 'translateY(-5px)' : 'none',
        boxShadow: isHovered ? 'var(--shadow-lg)' : 'var(--shadow-md)',
        display: 'flex',
        flexDirection: 'column',
        height: '100%'
      }}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div style={{ flex: 1 }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '0.5rem' }}>{product.name}</h3>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginBottom: '1rem' }}>Mã: {product.skuCode}</p>
        <p style={{ fontSize: '1.5rem', fontWeight: '700', color: 'var(--accent-primary)', marginBottom: '0.5rem' }}>
          {product.price.toLocaleString('vi-VN')} VNĐ
        </p>
        <p style={{ 
          fontSize: '0.875rem', 
          color: product.quantityAvailable > 0 ? 'var(--text-secondary)' : 'var(--error)', 
          marginBottom: '1rem',
          fontWeight: product.quantityAvailable <= 0 ? '600' : 'normal'
        }}>
          {product.quantityAvailable > 0 ? `Còn lại: ${product.quantityAvailable} sản phẩm` : 'Hết hàng'}
        </p>
      </div>
      <button 
        onClick={() => onAddToCart(product)}
        className={`btn ${product.quantityAvailable > 0 ? 'btn-primary' : ''}`}
        style={{ 
          width: '100%', 
          display: 'flex', 
          gap: '0.5rem', 
          justifyContent: 'center',
          backgroundColor: product.quantityAvailable <= 0 ? 'var(--border)' : undefined,
          color: product.quantityAvailable <= 0 ? 'var(--text-secondary)' : undefined,
          cursor: product.quantityAvailable <= 0 ? 'not-allowed' : 'pointer'
        }}
        disabled={product.quantityAvailable <= 0}
      >
        <ShoppingCart size={18} /> {product.quantityAvailable > 0 ? 'Thêm vào giỏ' : 'Tạm hết hàng'}
      </button>
    </div>
  );
};

export default ProductCard;
