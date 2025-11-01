import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { Product } from '../types/product.types';
import { toast } from 'react-hot-toast';


interface CartItem {
  product: Product;
  quantity: number;
}

interface CartContextType {
  items: CartItem[];
  itemCount: number;
  total: number;
  addItem: (product: Product, quantity?: number) => void;
  removeItem: (productId: number) => void;
  updateQuantity: (productId: number, quantity: number) => void;
  clearCart: () => void;
  isInCart: (productId: number) => boolean;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
  const [items, setItems] = useState<CartItem[]>([]);
  const [lastAction, setLastAction] = useState<{type: string; message: string} | null>(null);

  // Cargar carrito del localStorage al iniciar
  useEffect(() => {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      try {
        setItems(JSON.parse(savedCart));
      } catch (error) {
        console.error('Error loading cart:', error);
      }
    }
  }, []);

  // Guardar carrito en localStorage cuando cambie
  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(items));
  }, [items]);

  // Mostrar toast cuando cambia lastAction
  useEffect(() => {
    if (lastAction) {
      toast[lastAction.type === 'error' ? 'error' : 'success'](lastAction.message);
      setLastAction(null);
    }
  }, [lastAction]);


  const addItem = (product: Product, quantity: number = 1) => {
    setItems((currentItems) => {
      const existingItem = currentItems.find((item) => item.product.id === product.id);

      if (existingItem) {
        // Si ya existe, actualizar cantidad
        const newQuantity = existingItem.quantity + quantity;
        
        // Verificar stock
        if (newQuantity > product.stockQuantity) {
          setLastAction({type: 'error', message: `Solo hay ${product.stockQuantity} unidades disponibles`});
          return currentItems;
        }

        setLastAction({type: 'success', message: 'Cantidad actualizada en el carrito'});
        return currentItems.map((item) =>
          item.product.id === product.id
            ? { ...item, quantity: newQuantity }
            : item
        );
      } else {
        // Si no existe, agregarlo
        setLastAction({type: 'success', message: `${product.albumTitle} agregado al carrito`});
        return [...currentItems, { product, quantity }];
      }
    });
  };

  const removeItem = (productId: number) => {
    setItems((currentItems) => {
      const item = currentItems.find((i) => i.product.id === productId);
      if (item) setLastAction({type: 'success', message: `${item.product.albumTitle} eliminado del carrito`});
      return currentItems.filter((item) => item.product.id !== productId);
    });
  };

  const updateQuantity = (productId: number, quantity: number) => {
    if (quantity < 1) {
      removeItem(productId);
      return;
    }

    setItems((currentItems) =>
      currentItems.map((item) => {
        if (item.product.id === productId) {
          // Verificar stock
          if (quantity > item.product.stockQuantity) {
            setLastAction({type: 'error', message: `Solo hay ${item.product.stockQuantity} unidades disponibles`});
            return item;
          }
          return { ...item, quantity };
        }
        return item;
      })
    );
  };

  const clearCart = () => {
    setItems([]);
    setLastAction({type: 'success', message: 'Carrito vaciado'});
  };

  const isInCart = (productId: number): boolean => {
    return items.some((item) => item.product.id === productId);
  };

  const itemCount = items.reduce((total, item) => total + item.quantity, 0);
  const total = items.reduce(
    (sum, item) => sum + item.product.price * item.quantity,
    0
  );

  return (
    <CartContext.Provider
      value={{
        items,
        itemCount,
        total,
        addItem,
        removeItem,
        updateQuantity,
        clearCart,
        isInCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within CartProvider');
  }
  return context;
};