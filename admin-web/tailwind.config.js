/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        obsidian: "#0a0a0f",
        surface: "#12121a",
        card: "#1a1a2e",
      },
    },
  },
  plugins: [],
};
