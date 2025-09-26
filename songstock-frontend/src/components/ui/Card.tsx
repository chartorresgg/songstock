import React from 'react';

interface CardProps {
  children: React.ReactNode;
  title?: string;
  className?: string;
  style?: React.CSSProperties;
}

const Card: React.FC<CardProps> = ({ 
  children, 
  title, 
  className = '', 
  style = {} 
}) => {
  const cardStyles = {
    backgroundColor: 'white',
    borderRadius: '0.5rem',
    border: '1px solid #E5E7EB',
    boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
    ...style,
  };

  const headerStyles = {
    padding: '1.5rem 1.5rem 0 1.5rem',
    borderBottom: title ? '1px solid #E5E7EB' : 'none',
    marginBottom: title ? '1.5rem' : '0',
  };

  const titleStyles = {
    fontSize: '1.125rem',
    fontWeight: '500',
    color: '#111827',
    margin: '0 0 1.5rem 0',
  };

  const contentStyles = {
    padding: '1.5rem',
  };

  return (
    <div style={cardStyles} className={className}>
      {title && (
        <div style={headerStyles}>
          <h3 style={titleStyles}>{title}</h3>
        </div>
      )}
      <div style={title ? { padding: '0 1.5rem 1.5rem 1.5rem' } : contentStyles}>
        {children}
      </div>
    </div>
  );
};

export default Card;