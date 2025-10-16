import React, { useState } from 'react';
import TrackSearch from '../components/ui/TrackSearch';
import VinylList from '../components/ui/VinylList';
import { tracksAPI } from '../services/api';
import { useToast } from '../hooks/useToast';
import Button from '../components/ui/Button';

interface Track {
  id: number;
  title: string;
}

const TrackVinylsPage: React.FC = () => {
  const [tracks, setTracks] = useState<Track[]>([]);
  const [selected, setSelected] = useState<Track | null>(null);
  const [vinyls, setVinyls] = useState<any[]>([]);
  const { showToast } = useToast();

  const searchFn = async (q: string) => {
    try {
      const data = await tracksAPI.searchTracks(q);
      // tracksAPI devuelve response.data según helper, puede ser ApiResponse o lista directa
      const list = Array.isArray(data) ? data : data.data || [];
      setTracks(list);
      return list;
    } catch (e) {
      console.error(e);
      showToast('Error al buscar pistas. Intenta de nuevo.', 'error');
      setTracks([]);
      return [];
    }
  };

  const handleSelect = async (track: Track) => {
    setSelected(track);
    try {
      const data = await tracksAPI.getVinylsForTrack(track.id);
      const list = Array.isArray(data) ? data : data.data || [];
      setVinyls(list);
    } catch (e) {
      console.error(e);
      showToast('Error al obtener vinilos para la pista seleccionada.', 'error');
    }
  };

  // Demo helpers for testing toasts
  const demoSuccess = () => showToast('Operación completada', 'success', 4000);
  const demoError = () => showToast('Ha ocurrido un error crítico', 'error', 6000, { priority: 10 });
  const demoAction = () => showToast('Vinilo cargado parcialmente', 'warning', 8000, { action: { label: 'Reintentar', onClick: () => showToast('Reintentando...', 'info') } });

  return (
    <div className="main container">
      <h1 className="text-2xl font-semibold mb-4">Buscar pista y ver vinilos disponibles</h1>
      <div className="mb-4 flex gap-2">
        <Button size="sm" variant="primary" onClick={demoSuccess}>Demo éxito</Button>
        <Button size="sm" variant="danger" onClick={demoError}>Demo error (alta prioridad)</Button>
        <Button size="sm" variant="secondary" onClick={demoAction}>Demo con acción</Button>
      </div>
      <TrackSearch searchFn={searchFn} onSearch={setTracks} onSelect={handleSelect} />

      {tracks.length > 0 && (
        <div className="mt-4">
          <h2 className="font-semibold mb-2">Resultados</h2>
          <div className="grid md:grid-cols-2 gap-2">
            {tracks.map((t) => (
              <div key={t.id} className="card cursor-pointer" onClick={() => handleSelect(t)}>
                <div className="font-medium">{t.title}</div>
              </div>
            ))}
          </div>
        </div>
      )}

      {selected && (
        <div className="mt-6">
          <h3 className="font-semibold">Vinilos para: {selected.title}</h3>
          <VinylList products={vinyls} />
        </div>
      )}
    </div>
  );
};

export default TrackVinylsPage;
