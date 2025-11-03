import { useState, useEffect } from 'react';
import { Music, Plus, Trash2, Edit2, Save, X, GripVertical } from 'lucide-react';
import songService, { Song } from '../../services/song.service';
import toast from 'react-hot-toast';

interface SongManagerProps {
    albumId: number | string;
  onSongsChange?: (songs: Song[]) => void;
}

const SongManager = ({ albumId, onSongsChange }: SongManagerProps) => {
  const [songs, setSongs] = useState<Song[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [newSong, setNewSong] = useState({ title: '', durationSeconds: 0 });
  const [editForm, setEditForm] = useState({ title: '', durationSeconds: 0 });

  useEffect(() => {
    loadSongs();
  }, [albumId]);

  const loadSongs = async () => {
    try {
        const numericAlbumId = typeof albumId === 'string' ? parseInt(albumId) : albumId;
        const data = await songService.getSongsByAlbum(numericAlbumId);
      setSongs(data.sort((a, b) => (a.trackNumber || 0) - (b.trackNumber || 0)));
      onSongsChange?.(data);
    } catch (error) {
      console.error('Error loading songs:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async () => {
    if (!newSong.title.trim()) {
      toast.error('El título es obligatorio');
      return;
    }

    try {
      const nextTrack = songs.length > 0 ? Math.max(...songs.map(s => s.trackNumber || 0)) + 1 : 1;
      const numericAlbumId = typeof albumId === 'string' ? parseInt(albumId) : albumId;
      await songService.createSong({
        albumId: numericAlbumId,
        title: newSong.title,
        durationSeconds: newSong.durationSeconds,
        trackNumber: nextTrack
      });
      toast.success('Canción agregada');
      setNewSong({ title: '', durationSeconds: 0 });
      loadSongs();
    } catch (error) {
      toast.error('Error al crear canción');
    }
  };

  const handleUpdate = async (id: number) => {
    try {
      await songService.updateSong(id, editForm);
      toast.success('Canción actualizada');
      setEditingId(null);
      loadSongs();
    } catch (error) {
      toast.error('Error al actualizar');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('¿Eliminar esta canción?')) return;
    
    try {
      await songService.deleteSong(id);
      toast.success('Canción eliminada');
      loadSongs();
    } catch (error) {
      toast.error('Error al eliminar');
    }
  };

  const startEdit = (song: Song) => {
    setEditingId(song.id);
    setEditForm({
      title: song.title,
      durationSeconds: song.durationSeconds
    });
  };

  const formatDuration = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  if (loading) {
    return <div className="animate-pulse h-40 bg-gray-100 rounded-lg"></div>;
  }

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
          <Music className="h-5 w-5 text-primary-900" />
          Canciones del Álbum ({songs.length})
        </h3>
      </div>

      {/* Lista de canciones */}
      <div className="space-y-2 mb-4">
        {songs.map((song) => (
          <div
            key={song.id}
            className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition"
          >
            <GripVertical className="h-4 w-4 text-gray-400 cursor-move" />
            <span className="text-sm font-medium text-gray-500 w-8">
              {song.trackNumber}
            </span>

            {editingId === song.id ? (
              <>
                <input
                  type="text"
                  value={editForm.title}
                  onChange={(e) => setEditForm({ ...editForm, title: e.target.value })}
                  className="flex-1 px-3 py-1.5 border rounded-lg"
                  placeholder="Título"
                />
                <input
                  type="number"
                  value={editForm.durationSeconds}
                  onChange={(e) => setEditForm({ ...editForm, durationSeconds: parseInt(e.target.value) })}
                  className="w-24 px-3 py-1.5 border rounded-lg"
                  placeholder="Seg."
                />
                <button
                  onClick={() => handleUpdate(song.id)}
                  className="p-2 text-green-600 hover:bg-green-50 rounded-lg"
                >
                  <Save className="h-4 w-4" />
                </button>
                <button
                  onClick={() => setEditingId(null)}
                  className="p-2 text-gray-600 hover:bg-gray-200 rounded-lg"
                >
                  <X className="h-4 w-4" />
                </button>
              </>
            ) : (
              <>
                <div className="flex-1">
                  <p className="font-medium text-gray-900">{song.title}</p>
                  <p className="text-xs text-gray-500">{formatDuration(song.durationSeconds)}</p>
                </div>
                <button
                  onClick={() => startEdit(song)}
                  className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg"
                >
                  <Edit2 className="h-4 w-4" />
                </button>
                <button
                  onClick={() => handleDelete(song.id)}
                  className="p-2 text-red-600 hover:bg-red-50 rounded-lg"
                >
                  <Trash2 className="h-4 w-4" />
                </button>
              </>
            )}
          </div>
        ))}
      </div>

      {/* Formulario nueva canción */}
      <div className="flex gap-2 p-4 bg-blue-50 rounded-lg border-2 border-dashed border-blue-200">
        <input
          type="text"
          value={newSong.title}
          onChange={(e) => setNewSong({ ...newSong, title: e.target.value })}
          placeholder="Título de la canción"
          className="flex-1 px-3 py-2 border rounded-lg"
          onKeyPress={(e) => e.key === 'Enter' && handleCreate()}
        />
        <input
          type="number"
          value={newSong.durationSeconds || ''}
          onChange={(e) => setNewSong({ ...newSong, durationSeconds: parseInt(e.target.value) || 0 })}
          placeholder="Duración (seg)"
          className="w-32 px-3 py-2 border rounded-lg"
        />
        <button
          onClick={handleCreate}
          className="bg-primary-900 text-white px-4 py-2 rounded-lg hover:bg-primary-800 flex items-center gap-2"
        >
          <Plus className="h-4 w-4" />
          Agregar
        </button>
      </div>

      <p className="text-xs text-gray-500 mt-3">
        💡 Tip: Arrastra las canciones para reordenarlas
      </p>
    </div>
  );
};

export default SongManager;