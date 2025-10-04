/** @type {import('tailwindcss').Config} */
export default {
    content: [
      "./index.html",
      "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
      extend: {
        colors: {
          'kibu': {
            'primary': '#B8860B',
            'secondary': '#F5F5DC',
            'dark': '#2C2C2C',
            'gray': '#6B7280',
            'light-gray': '#F9FAFB',
            'white': '#FFFFFF',
            'accent': '#D4A574',
          }
        },
        fontFamily: {
          'kibu': ['Inter', 'system-ui', 'sans-serif'],
        },
      },
    },
    plugins: [],
  }
  