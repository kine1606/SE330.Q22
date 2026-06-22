import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { LogOut, ShoppingCart, User } from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useAuth();
  const { getCartCount } = useCart();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const cartCount = getCartCount();

  return (
    <nav className="glass" style={{ position: 'sticky', top: 0, zIndex: 100, padding: '1rem 0' }}>
      <div className="container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Link to="/" style={{ fontSize: '1.5rem', fontWeight: '700', color: 'var(--text-primary)' }}>
          Q22 Shop
        </Link>
        <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
          {user ? (
            <>
              <Link to="/cart" style={{ color: 'var(--text-secondary)', position: 'relative' }}>
                <ShoppingCart size={24} />
                {cartCount > 0 && (
                  <span style={{
                    position: 'absolute',
                    top: '-8px',
                    right: '-8px',
                    backgroundColor: 'var(--primary)',
                    color: 'white',
                    fontSize: '0.75rem',
                    fontWeight: 'bold',
                    width: '20px',
                    height: '20px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    borderRadius: '50%'
                  }}>
                    {cartCount}
                  </span>
                )}
              </Link>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)' }}>
                <User size={20} />
                <span style={{ fontWeight: '500' }}>User {user.userId}</span>
              </div>
              <button onClick={handleLogout} className="btn btn-outline" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.5rem 1rem' }}>
                <LogOut size={16} /> Đăng xuất
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-outline" style={{ padding: '0.5rem 1.5rem' }}>Đăng nhập</Link>
              <Link to="/register" className="btn btn-primary" style={{ padding: '0.5rem 1.5rem' }}>Đăng ký</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
