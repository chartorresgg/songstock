import { useState, useEffect } from 'react';
import { X, Plus, Music2, Loader2 } from 'lucide-react';
import compilationService from '../../services/compilation.service';
import { Compilation, Song } from '../../types/compilation.types';
import toast from 'react-hot-toast';

interface CompilationSelectorProps {
  song: Song;
  onClose: () => void;
}

const CompilationSelector = ({ song, onClose }: CompilationSelectorProps) => {
  const [compilations, setCompilations] = useState<Compilation[]>([]);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState<number | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [newCompilationName, setNewCompilationName] = useState('');
  const [creating, setCreating] = useState(false);

  useEffect(() => {
    loadCompilations();
  }, []);

  const loadCompilations = async () => {
    try {
      setLoading(true);
      const data = await compilationService.getMyCompilations();
      setCompilations(data);
    } catch (error) {
      console.error('Error loading compilations:', error);
      toast.error('Error al cargar recopilaciones');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCompilation = async (compilationId: number) => {
    try {
      setAdding(compilationId);
      await compilationService.addSongToCompilation(compilationId, song.id);
      toast.success('Canción añadida a la recopilación');
      onClose();
    } catch (error: any) {
      const message = error.response?.data?.message || 'Error al añadir canción';
      toast.error(message);
    } finally {
      setAdding(null);
    }
  };

  const handleCreateCompilation = async () => {
    if (!newCompilationName.trim()) {
      toast.error('Ingresa un nombre para la recopilación');
      return;
    }

    try {
      setCreating(true);
      const newCompilation = await compilationService.createCompilation({
        name: newCompilationName,
        isPublic: false,
      });
      
      await compilationService.addSongToCompilation(newCompilation.id, song.id);
      toast.success('Recopilación creada y canción añadida');
      onClose();
    } catch (error: any) {
      const message = error.response?.data?.message || 'Error al crear recopilación';
      toast.error(message);
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg max-w-md w-full max-h-[80vh] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="p-6 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-xl font-bold text-gray-900">Añadir a Recopilación</h3>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 transition"
            >
              <X className="h-6 w-6" />
            </button>
          </div>
          <p className="text-sm text-gray-600 mt-2">
            {song.title} - {song.artistName}
          </p>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {loading ? (
            <div className="flex justify-center py-8">
              <Loader2 className="h-8 w-8 animate-spin text-primary-900" />
            </div>
          ) : (
            <>
              {/* Compilations List */}
              {compilations.length > 0 ? (
                <div className="space-y-2 mb-4">
                  {compilations.map((compilation) => (
                    <button
                      key={compilation.id}
                      onClick={() => handleAddToCompilation(compilation.id)}
                      disabled={adding === compilation.id}
                      className="w-full flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:border-primary-900 hover:bg-primary-50 transition disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      <div className="flex items-center space-x-3">
                        <Music2 className="h-5 w-5 text-primary-900" />
                        <div className="text-left">
                          <p className="font-medium text-gray-900">{compilation.name}</p>
                          <p className="text-sm text-gray-500">
                            {compilation.songCount} {compilation.songCount === 1 ? 'canción' : 'canciones'}
                          </p>
                        </div>
                      </div>
                      {adding === compilation.id && (
                        <Loader2 className="h-5 w-5 animate-spin text-primary-900" />
                      )}
                    </button>
                  ))}
                </div>
              ) : (
                <p className="text-center text-gray-500 py-4">
                  No tienes recopilaciones aún
                </p>
              )}

              {/* Create New Compilation */}
              {!showCreateForm ? (
                <button
                  onClick={() => setShowCreateForm(true)}
                  className="w-full flex items-center justify-center space-x-2 p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-900 hover:bg-primary-50 transition text-gray-600 hover:text-primary-900"
                >
                  <Plus className="h-5 w-5" />
                  <span className="font-medium">Crear nueva recopilación</span>
                </button>
              ) : (
                <div className="border-2 border-primary-900 rounded-lg p-4 space-y-3">
                  <input
                    type="text"
                    value={newCompilationName}
                    onChange={(e) => setNewCompilationName(e.target.value)}
                    placeholder="Nombre de la recopilación"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-900 focus:border-transparent"
                    autoFocus
                    maxLength={100}
                  />
                  <div className="flex space-x-2">
                    <button
                      onClick={handleCreateCompilation}
                      disabled={creating || !newCompilationName.trim()}
                      className="flex-1 bg-primary-900 text-white py-2 px-4 rounded-lg hover:bg-primary-800 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
                    >
                      {creating ? (
                        <>
                          <Loader2 className="h-4 w-4 animate-spin" />
                          <span>Creando...</span>
                        </>
                      ) : (
                        <>
                          <Plus className="h-4 w-4" />
                          <span>Crear y añadir</span>
                        </>
                      )}
                    </button>
                    <button
                      onClick={() => {
                        setShowCreateForm(false);
                        setNewCompilationName('');
                      }}
                      className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition"
                    >
                      Cancelar
                    </button>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default CompilationSelector;