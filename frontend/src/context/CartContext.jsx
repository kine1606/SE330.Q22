import { createContext, useState, useContext, useEffect } from 'react';

const CartContext = createContext(null);

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState(() => {
    const savedCart = localStorage.getItem('cartItems');
    return savedCart ? JSON.parse(savedCart) : [];
  });

  useEffect(() => {
    localStorage.setItem('cartItems', JSON.stringify(cartItems));
  }, [cartItems]);

  const addToCart = (product, quantity = 1) => {
    setCartItems(prevItems => {
      const existingItem = prevItems.find(item => item.skuCode === product.skuCode);
      if (existingItem) {
        const newQuantity = existingItem.quantity + quantity;
        if (newQuantity > product.quantityAvailable) {
          alert(`Chỉ có thể mua tối đa ${product.quantityAvailable} sản phẩm "${product.name}".`);
          return prevItems; // Không cập nhật nếu vượt quá giới hạn
        }
        return prevItems.map(item =>
          item.skuCode === product.skuCode
            ? { ...item, quantity: newQuantity }
            : item
        );
      }
      
      if (quantity > product.quantityAvailable) {
        alert(`Chỉ có thể mua tối đa ${product.quantityAvailable} sản phẩm "${product.name}".`);
        return prevItems;
      }
      
      return [...prevItems, { ...product, quantity }];
    });
  };

  const removeFromCart = (skuCode) => {
    setCartItems(prevItems => prevItems.filter(item => item.skuCode !== skuCode));
  };

  const updateQuantity = (skuCode, quantity) => {
    if (quantity < 1) return;
    setCartItems(prevItems => {
      const existingItem = prevItems.find(item => item.skuCode === skuCode);
      if (existingItem && existingItem.quantityAvailable !== undefined) {
        if (quantity > existingItem.quantityAvailable) {
          return prevItems; // Không cho phép tăng quá tồn kho
        }
      }
      return prevItems.map(item =>
        item.skuCode === skuCode ? { ...item, quantity } : item
      );
    });
  };

  const clearCart = () => {
    setCartItems([]);
  };

  const getCartTotal = () => {
    return cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
  };
  
  const getCartCount = () => {
    return cartItems.reduce((count, item) => count + item.quantity, 0);
  };

  return (
    <CartContext.Provider value={{ 
      cartItems, 
      addToCart, 
      removeFromCart, 
      updateQuantity, 
      clearCart,
      getCartTotal,
      getCartCount
    }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
