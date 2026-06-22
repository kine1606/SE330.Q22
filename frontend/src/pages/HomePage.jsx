import { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import ProductCard from '../components/ProductCard';

const HomePage = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { token } = useAuth();
  const { addToCart } = useCart();

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/products', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setProducts(response.data);
      } catch (err) {
        setError('Không thể tải danh sách sản phẩm. Bạn vui lòng thử lại sau.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    if (token) {
      fetchProducts();
    }
  }, [token]);

  const handleAddToCart = (product) => {
    addToCart(product);
    alert(`Đã thêm ${product.name} vào giỏ hàng!`);
  };

  return (
    <div className="animate-fade-in">
      <div style={{ marginBottom: '2rem', textAlign: 'center' }}>
        <h1 style={{ fontSize: '2.5rem', fontWeight: '700', marginBottom: '0.5rem' }}>Khám phá Sản Phẩm</h1>
        <p style={{ color: 'var(--text-secondary)' }}>Những mặt hàng công nghệ mới nhất dành cho bạn.</p>
      </div>

      {loading ? (
        <div className="flex-center" style={{ minHeight: '40vh' }}>
          <p style={{ fontSize: '1.25rem', color: 'var(--text-secondary)' }}>Đang tải dữ liệu...</p>
        </div>
      ) : error ? (
        <div style={{ color: 'var(--error)', backgroundColor: 'rgba(239, 68, 68, 0.1)', padding: '1rem', borderRadius: '0.5rem', textAlign: 'center' }}>
          {error}
        </div>
      ) : (
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', 
          gap: '2rem' 
        }}>
          {products.length > 0 ? (
            products.map(product => (
              <ProductCard key={product.id} product={product} onAddToCart={handleAddToCart} />
            ))
          ) : (
            <p style={{ textAlign: 'center', gridColumn: '1 / -1', color: 'var(--text-secondary)' }}>Không có sản phẩm nào.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default HomePage;
