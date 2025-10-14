import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, Music2, Trash2, Loader2, Clock } from 'lucide-react';
import compilationService from '../../services/compilation.service';
import { Compilation } from '../../types/compilation.types';
import toast from 'react-hot-toast';

const CompilationDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [compilation, setCompilation] = useState<Compilation | null>(null);
  const [loading, setLoading] = useState(true);
  const [removingSongId, setRemovingSongId] = useState<number | null>(null);

  useEffect(() => {
    if (id) {
      loadCompilation(parseInt(id));
    }
  }, [id]);

  const loadCompilation = async (compilationId: number) => {
    try {
      setLoading(true);
      const data = await compilationService.getCompilationById(compilationId);
      setCompilation(data);
    } catch (error: any) {
      console.error('Error loading compilation:', error);
      toast.error('Error al cargar la recopilación');
      navigate('/compilations');
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveSong = async (songId: number, songTitle: string) => {
    if (!compilation) return;
    
    if (!window.confirm(`¿Eliminar "${songTitle}" de esta recopilación?`)) {
      return;
    }

    try {
      setRemovingSongId(songId);
      await compilationService.removeSongFromCompilation(compilation.id, songId);
      toast.success('Canción eliminada');
      loadCompilation(compilation.id);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Error al eliminar canción';
      toast.error(message);
    } finally {
      setRemovingSongId(null);
    }
  };

  const formatDuration = (seconds?: number) => {
    if (!seconds) return '';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
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

  if (!compilation) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-gradient-to-r from-primary-900 to-primary-800 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <button
            onClick={() => navigate('/compilations')}
            className="flex items-center text-white hover:text-gray-200 mb-4 transition"
          >
            <ArrowLeft className="h-5 w-5 mr-2" />
            Volver a recopilaciones
          </button>
          
          <div className="flex items-start space-x-4">
            <div className="bg-white/20 backdrop-blur-sm rounded-lg p-4">
              <Music2 className="h-12 w-12" />
            </div>
            <div className="flex-1">
              <h1 className="text-3xl font-bold mb-2">{compilation.name}</h1>
              {compilation.description && (
                <p className="text-gray-200 mb-2">{compilation.description}</p>
              )}
              <div className="flex items-center space-x-4 text-sm text-gray-200">
                <span>
                  {compilation.songCount}{' '}
                  {compilation.songCount === 1 ? 'canción' : 'canciones'}
                </span>
                <span>•</span>
                <span>Creada el {formatDate(compilation.createdAt)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Songs List */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          {compilation.songs && compilation.songs.length > 0 ? (
            <div className="divide-y divide-gray-200">
              {compilation.songs.map((song, index) => (
                <div
                  key={song.id}
                  className="p-4 hover:bg-gray-50 transition flex items-center justify-between"
                >
                  <div className="flex items-center space-x-4 flex-1">
                    <span className="text-gray-500 font-medium w-8 text-center">
                      {index + 1}
                    </span>
                    <div className="flex-1">
                      <p className="font-semibold text-gray-900">{song.title}</p>
                      <p className="text-sm text-gray-600">
                        {song.artistName} • {song.albumTitle}
                      </p>
                    </div>
                    {song.durationSeconds && (
                      <div className="flex items-center text-gray-500 text-sm">
                        <Clock className="h-4 w-4 mr-1" />
                        {formatDuration(song.durationSeconds)}
                      </div>
                    )}
                  </div>
                  <button
                    onClick={() => handleRemoveSong(song.id, song.title)}
                    disabled={removingSongId === song.id}
                    className="ml-4 text-red-600 hover:text-red-800 transition disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {removingSongId === song.id ? (
                      <Loader2 className="h-5 w-5 animate-spin" />
                    ) : (
                      <Trash2 className="h-5 w-5" />
                    )}
                  </button>
                </div>
              ))}
            </div>
          ) : (
            <div className="p-12 text-center">
              <Music2 className="h-16 w-16 text-gray-300 mx-auto mb-4" />
              <h3 className="text-xl font-bold text-gray-900 mb-2">
                No hay canciones aún
              </h3>
              <p className="text-gray-600 mb-6">
                Explora el catálogo y añade canciones a esta recopilación
              </p>
              <Link
                to="/catalog"
                className="inline-block bg-primary-900 text-white px-6 py-3 rounded-lg font-semibold hover:bg-primary-800 transition"
              >
                Ir al catálogo
              </Link>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CompilationDetail;