function App() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-kibu-primary to-kibu-accent flex items-center justify-center p-8">
      <div className="card max-w-2xl w-full text-center space-y-6">
        <h1 className="text-5xl font-bold text-kibu-primary">
          🎵 SongStock
        </h1>
        <p className="text-2xl text-kibu-dark font-semibold">
          Marketplace de Vinilos y Música Digital
        </p>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-8">
          <div className="card bg-kibu-secondary">
            <h3 className="text-lg font-bold text-kibu-dark">🎼 Catálogo</h3>
            <p className="text-sm text-kibu-gray mt-2">Miles de vinilos</p>
          </div>
          <div className="card bg-kibu-secondary">
            <h3 className="text-lg font-bold text-kibu-dark">💿 MP3</h3>
            <p className="text-sm text-kibu-gray mt-2">Descarga digital</p>
          </div>
          <div className="card bg-kibu-secondary">
            <h3 className="text-lg font-bold text-kibu-dark">🛒 Compra</h3>
            <p className="text-sm text-kibu-gray mt-2">Fácil y seguro</p>
          </div>
        </div>

        <div className="space-x-4 mt-8">
          <button className="btn-primary">
            Explorar Catálogo
          </button>
          <button className="btn-secondary">
            Vender Vinilos
          </button>
        </div>

        <div className="mt-8 p-4 bg-green-100 border border-green-400 rounded-lg">
          <p className="text-green-800 font-semibold">
            ✅ Tailwind CSS funcionando correctamente!
          </p>
        </div>
      </div>
    </div>
  )
}

export default App