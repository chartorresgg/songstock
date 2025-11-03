// src/pages/Compilations/PublicCompilationDetail.tsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, Music2, Copy, Loader2, Clock, User } from 'lucide-react';
import compilationService from '../../services/compilation.service';
import { Compilation } from '../../types/compilation.types';
import toast from 'react-hot-toast';

const PublicCompilationDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [compilation, setCompilation] = useState<Compilation | null>(null);
  const [loading, setLoading] = useState(true);
  const [cloning, setCloning] = useState(false);

  useEffect(() => {
    if (id) {
      loadCompilation(parseInt(id));
    }
  }, [id]);

  const loadCompilation = async (compilationId: number) => {
    try {
      setLoading(true);
      const data = await compilationService.getPublicCompilationById(compilationId);
      setCompilation(data);
    } catch (error: any) {
      console.error('Error loading compilation:', error);
      toast.error('Error al cargar la recopilación');
      navigate('/compilations/explore');
    } finally {
      setLoading(false);
    }
  };

  const handleClone = async () => {
    if (!compilation) return;

    try {
      setCloning(true);
      await compilationService.cloneCompilation(compilation.id);
      toast.success('¡Recopilación copiada a tu colección!');
      navigate('/compilations');
    } catch (error: any) {
      const message = error.response?.data?.message || 'Error al copiar recopilación';
      toast.error(message);
    } finally {
      setCloning(false);
    }
  };

  const formatDuration = (seconds?: number) => {
    if (!seconds) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin text-primary-900" />
      </div>
    );
  }

  if (!compilation) {
    return null;
  }

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-6">
        <button
          onClick={() => navigate('/compilations/explore')}
          className="flex items-center text-gray-600 hover:text-gray-900 mb-4 transition"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          Volver a explorar
        </button>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <div className="flex items-center space-x-3 mb-2">
                <Music2 className="h-8 w-8 text-primary-900" />
                <span className="text-xs bg-green-100 text-green-800 px-3 py-1 rounded">
                  Pública
                </span>
              </div>

              <h1 className="text-3xl font-bold text-gray-900 mb-2">
                {compilation.name}
              </h1>

              {compilation.description && (
                <p className="text-gray-600 mb-4">{compilation.description}</p>
              )}

              <div className="flex items-center space-x-4 text-sm text-gray-500">
                <div className="flex items-center space-x-2">
                  <User className="h-4 w-4" />
                  <span>
                    Creada por: <strong className="text-gray-900">{compilation.creatorUsername || 'Usuario'}</strong>
                  </span>
                </div>
                <div className="flex items-center space-x-2">
                  <Music2 className="h-4 w-4" />
                  <span>
                    <strong className="text-gray-900">{compilation.songCount || 0}</strong> canciones
                  </span>
                </div>
              </div>
            </div>

            <button
              onClick={handleClone}
              disabled={cloning}
              className="flex items-center space-x-2 px-6 py-3 bg-primary-900 text-white rounded-lg hover:bg-primary-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {cloning ? (
                <>
                  <Loader2 className="h-5 w-5 animate-spin" />
                  <span>Copiando...</span>
                </>
              ) : (
                <>
                  <Copy className="h-5 w-5" />
                  <span>Copiar a mi colección</span>
                </>
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Lista de Canciones */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">Canciones</h2>
        </div>

        {compilation.songs && compilation.songs.length > 0 ? (
          <div className="divide-y divide-gray-200">
            {compilation.songs.map((song, index) => (
              <div
                key={song.id}
                className="px-6 py-4 hover:bg-gray-50 transition"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4 flex-1">
                    <span className="text-sm font-medium text-gray-500 w-8">
                      {index + 1}
                    </span>

                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-900 truncate">
                        {song.title}
                      </p>
                      <p className="text-sm text-gray-500 truncate">
                        {song.artistName} • {song.albumTitle}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center space-x-2 text-sm text-gray-500">
                    <Clock className="h-4 w-4" />
                    <span>{formatDuration(song.durationSeconds)}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="px-6 py-12 text-center">
            <Music2 className="h-12 w-12 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-600">Esta recopilación no tiene canciones aún</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default PublicCompilationDetail;