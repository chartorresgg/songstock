import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
}

const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  className = '',
  disabled,
  style = {},
  ...props
}) => {
  const getVariantStyles = (variant: string) => {
    switch (variant) {
      case 'primary':
        return {
          backgroundColor: '#2563EB',
          color: 'white',
          border: 'none',
        };
      case 'secondary':
        return {
          backgroundColor: '#E5E7EB',
          color: '#374151',
          border: '1px solid #D1D5DB',
        };
      case 'danger':
        return {
          backgroundColor: '#DC2626',
          color: 'white',
          border: 'none',
        };
      default:
        return {
          backgroundColor: '#2563EB',
          color: 'white',
          border: 'none',
        };
    }
  };

  const getSizeStyles = (size: string) => {
    switch (size) {
      case 'sm':
        return {
          padding: '0.375rem 0.75rem',
          fontSize: '0.875rem',
        };
      case 'lg':
        return {
          padding: '0.75rem 1.5rem',
          fontSize: '1.125rem',
        };
      default:
        return {
          padding: '0.5rem 1rem',
          fontSize: '1rem',
        };
    }
  };

  const buttonStyles = {
    fontWeight: '500',
    borderRadius: '0.375rem',
    cursor: disabled || loading ? 'not-allowed' : 'pointer',
    opacity: disabled || loading ? 0.5 : 1,
    transition: 'all 0.2s',
    ...getVariantStyles(variant),
    ...getSizeStyles(size),
    ...style,
  };

  return (
    <button
      style={buttonStyles}
      className={className}
      disabled={disabled || loading}
      {...props}
    >
      {loading ? 'Cargando...' : children}
    </button>
  );
};

export default Button;