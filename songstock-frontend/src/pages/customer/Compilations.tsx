import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Music2, Plus, Trash2, Eye, Clock, Loader2, Globe, Lock } from 'lucide-react';
import compilationService from '../../services/compilation.service';
import { Compilation } from '../../types/compilation.types';
import toast from 'react-hot-toast';

const Compilations = () => {
  const navigate = useNavigate();
  const [compilations, setCompilations] = useState<Compilation[]>([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newCompilationName, setNewCompilationName] = useState('');
  const [newCompilationIsPublic, setNewCompilationIsPublic] = useState(false);
  const [newCompilationDescription, setNewCompilationDescription] = useState('');
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

  const handleCreateCompilation = async () => {
    if (!newCompilationName.trim()) {
      toast.error('El nombre es obligatorio');
      return;
    }

    try {
      setCreating(true);
      await compilationService.createCompilation({
        name: newCompilationName,
        description: newCompilationDescription,
        isPublic: newCompilationIsPublic,
      });
      toast.success('Recopilación creada exitosamente');
      setShowCreateModal(false);
      setNewCompilationName('');
      setNewCompilationDescription('');
      setNewCompilationIsPublic(false);
      loadCompilations();
    } catch (error: any) {
      const message = error.response?.data?.message || 'Error al crear recopilación';
      toast.error(message);
    } finally {
      setCreating(false);
    }
  };

  const handleDeleteCompilation = async (id: number, name: string) => {
    if (!window.confirm(`¿Estás seguro de eliminar la recopilación "${name}"?`)) {
      return;
    }

    try {
      setDeletingId(id);
      await compilationService.deleteCompilation(id);
      toast.success('Recopilación eliminada');
      loadCompilations();
    } catch (error: any) {
      const message = error.response?.data?.message || 'Error al eliminar recopilación';
      toast.error(message);
    } finally {
      setDeletingId(null);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loader2 className="h-12 w-12 animate-spin text-primary-900" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-gradient-to-r from-primary-900 to-primary-800 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold">Mis Recopilaciones</h1>
              <p className="text-gray-200 mt-2">
                Gestiona tus colecciones personalizadas de canciones
              </p>
            </div>
            <button
              onClick={() => setShowCreateModal(true)}
              className="bg-white text-primary-900 px-6 py-3 rounded-lg font-semibold hover:bg-gray-100 transition flex items-center space-x-2"
            >
              <Plus className="h-5 w-5" />
              <span>Nueva Recopilación</span>
            </button>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {compilations.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {compilations.map((compilation) => (
              <div
                key={compilation.id}
                className="bg-white rounded-lg shadow-md hover:shadow-xl transition overflow-hidden"
              >
                <div className="p-6">
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex-1">
                      <h3 className="text-lg font-bold text-gray-900 mb-1">
                        {compilation.name}
                      </h3>
                      {compilation.description && (
                        <p className="text-sm text-gray-600 line-clamp-2">
                          {compilation.description}
                        </p>
                      )}
                    </div>
                    <Music2 className="h-8 w-8 text-primary-900 flex-shrink-0 ml-2" />
                  </div>

                  <div className="flex items-center text-sm text-gray-500 mb-4">
                    <Music2 className="h-4 w-4 mr-1" />
                    <span>
                      {compilation.songCount}{' '}
                      {compilation.songCount === 1 ? 'canción' : 'canciones'}
                    </span>
                  </div>

                  <div className="flex items-center text-xs text-gray-400 mb-4">
                    <Clock className="h-3 w-3 mr-1" />
                    <span>Creada el {formatDate(compilation.createdAt)}</span>
                  </div>

                  <div className="flex space-x-2">
                    <button
                      onClick={() => navigate(`/compilations/${compilation.id}`)}
                      className="flex-1 bg-primary-900 text-white py-2 px-4 rounded-lg hover:bg-primary-800 transition flex items-center justify-center space-x-1"
                    >
                      <Eye className="h-4 w-4" />
                      <span>Ver</span>
                    </button>
                    <button
                      onClick={() => handleDeleteCompilation(compilation.id, compilation.name)}
                      disabled={deletingId === compilation.id}
                      className="bg-red-600 text-white py-2 px-4 rounded-lg hover:bg-red-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {deletingId === compilation.id ? (
                        <Loader2 className="h-4 w-4 animate-spin" />
                      ) : (
                        <Trash2 className="h-4 w-4" />
                      )}
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow-md p-12 text-center">
            <Music2 className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-xl font-bold text-gray-900 mb-2">
              No tienes recopilaciones aún
            </h3>
            <p className="text-gray-600 mb-6">
              Crea tu primera recopilación y comienza a organizar tus canciones favoritas
            </p>
            <button
              onClick={() => setShowCreateModal(true)}
              className="bg-primary-900 text-white px-6 py-3 rounded-lg font-semibold hover:bg-primary-800 transition inline-flex items-center space-x-2"
            >
              <Plus className="h-5 w-5" />
              <span>Crear Recopilación</span>
            </button>
          </div>
        )}
      </div>

      {/* Create Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-900 mb-4">
              Nueva Recopilación
            </h3>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nombre *
                </label>
                <input
                  type="text"
                  value={newCompilationName}
                  onChange={(e) => setNewCompilationName(e.target.value)}
                  placeholder="Mi recopilación favorita"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-900 focus:border-transparent"
                  maxLength={100}
                  autoFocus
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descripción (opcional)
                </label>
                <textarea
                  value={newCompilationDescription}
                  onChange={(e) => setNewCompilationDescription(e.target.value)}
                  placeholder="Describe tu recopilación..."
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-900 focus:border-transparent resize-none"
                />
              </div>
              <div className="flex items-start space-x-3 pt-2">
                <input
                  type="checkbox"
                  id="isPublic"
                  checked={newCompilationIsPublic}
                  onChange={(e) => setNewCompilationIsPublic(e.target.checked)}
                  className="mt-1 h-4 w-4 text-primary-900 border-gray-300 rounded focus:ring-primary-900"
                />
                <label htmlFor="isPublic" className="flex-1 text-sm text-gray-700 cursor-pointer">
                  <div className="flex items-center space-x-2 mb-1">
                    <Globe className="h-4 w-4 text-primary-900" />
                    <span className="font-medium">Hacer pública esta recopilación</span>
                  </div>
                  <p className="text-xs text-gray-500">Otros usuarios podrán ver y copiar tu recopilación</p>
                </label>
              </div>
            </div>

            <div className="flex space-x-3 mt-6">
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
                    <span>Crear</span>
                  </>
                )}
              </button>
              <button
                onClick={() => {
                  setShowCreateModal(false);
                  setNewCompilationName('');
                  setNewCompilationDescription('');
                  setNewCompilationIsPublic(false);
                }}
                disabled={creating}
                className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition disabled:opacity-50"
              >
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Compilations;