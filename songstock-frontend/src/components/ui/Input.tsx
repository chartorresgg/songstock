import React from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
}

const Input: React.FC<InputProps> = ({
  label,
  error,
  helperText,
  style = {},
  ...props
}) => {
  const inputStyles = {
    width: '100%',
    padding: '0.5rem 0.75rem',
    border: error ? '1px solid #DC2626' : '1px solid #D1D5DB',
    borderRadius: '0.375rem',
    fontSize: '0.875rem',
    ...style,
  };

  const labelStyles = {
    display: 'block',
    fontSize: '0.875rem',
    fontWeight: '500',
    color: '#374151',
    marginBottom: '0.25rem',
  };

  const errorStyles = {
    marginTop: '0.25rem',
    fontSize: '0.875rem',
    color: '#DC2626',
  };

  const helperStyles = {
    marginTop: '0.25rem',
    fontSize: '0.875rem',
    color: '#6B7280',
  };

  return (
    <div style={{ width: '100%' }}>
      {label && (
        <label style={labelStyles}>
          {label}
        </label>
      )}
      <input
        style={inputStyles}
        {...props}
      />
      {error && (
        <p style={errorStyles}>{error}</p>
      )}
      {helperText && !error && (
        <p style={helperStyles}>{helperText}</p>
      )}
    </div>
  );
};

export default Input;