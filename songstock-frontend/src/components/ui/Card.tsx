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
  return (
    <div className={`bg-white rounded-lg border border-gray-200 shadow-sm ${className}`} style={style}>
      {title && (
        <div className="px-6 pt-6 pb-0 border-b border-gray-200 mb-4">
          <h3 className="text-lg font-medium text-gray-900 mb-3">{title}</h3>
        </div>
      )}
      <div className={title ? 'px-6 pb-6' : 'p-6'}>
        {children}
      </div>
    </div>
  );
};

export default Card;