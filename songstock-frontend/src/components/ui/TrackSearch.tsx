MNimport React, { useState, useEffect, useRef, useCallback } from 'react';
import Button from './Button';
import { useToast } from '../../hooks/useToast';

interface Track {
  id: number;
  title: string;
  trackNumber?: number;
  durationSeconds?: number;
}

interface Props {
  onSelect: (track: Track) => void;
  onSearch?: (results: Track[]) => void;
  searchFn: (q: string) => Promise<Track[]>;
}

const TrackSearch: React.FC<Props> = ({ onSelect, onSearch, searchFn }) => {
  const [q, setQ] = useState('');
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<Track[]>([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [highlight, setHighlight] = useState<number>(-1);
  const debounceRef = useRef<number | null>(null);
  const listId = `track-search-list-${Math.random().toString(36).substr(2,6)}`;
  const { showToast } = useToast();

  const handleSearch = async () => {
    if (!q.trim()) return;
    setLoading(true);
    try {
      const results = await searchFn(q.trim());
      setResults(results);
      setShowSuggestions(true);
      onSearch && onSearch(results);
    } catch (e) {
      console.error(e);
      showToast('Error al buscar pistas. Intenta de nuevo.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSelect = (t: Track) => {
    setQ(t.title);
    setShowSuggestions(false);
    onSelect(t);
  };

  // Debounced automatic search when typing
  useEffect(() => {
    // if input empty, clear suggestions
    if (!q.trim()) {
      setResults([]);
      setShowSuggestions(false);
      return;
    }

    // clear previous timer
    if (debounceRef.current) {
      window.clearTimeout(debounceRef.current);
    }
    debounceRef.current = window.setTimeout(() => {
      handleSearch().catch(() => {});
    }, 300);

    return () => {
      if (debounceRef.current) window.clearTimeout(debounceRef.current);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [q]);

  const handleKeyDown = useCallback((e: React.KeyboardEvent<HTMLInputElement>) => {
    if (!showSuggestions) return;
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setHighlight((h) => Math.min(h + 1, results.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setHighlight((h) => Math.max(h - 1, 0));
    } else if (e.key === 'Enter') {
      if (highlight >= 0 && highlight < results.length) {
        e.preventDefault();
        handleSelect(results[highlight]);
      } else {
        // No highlight: perform explicit search
        handleSearch().catch(() => {});
      }
    } else if (e.key === 'Escape') {
      setShowSuggestions(false);
    }
  }, [highlight, results, showSuggestions]);

  return (
    <div className="w-full max-w-2xl">
      <div className="flex gap-2">
        <input
          className="input"
          placeholder="Buscar pista por título..."
          value={q}
          onChange={(e) => setQ(e.target.value)}
          onKeyDown={handleKeyDown}
          aria-autocomplete="list"
          aria-controls={listId}
          aria-expanded={showSuggestions}
          aria-activedescendant={highlight >= 0 ? `${listId}-item-${highlight}` : undefined}
          onFocus={() => { if (results.length > 0) setShowSuggestions(true); }}
        />
        <Button onClick={handleSearch} loading={loading}>
          Buscar
        </Button>
      </div>

      {showSuggestions && (
        <div className="mt-2 bg-white border rounded shadow-sm max-h-56 overflow-auto" role="listbox" id={listId}>
          {results.length === 0 ? (
            <div className="p-3 text-sm text-gray-600">No se encontraron pistas.</div>
          ) : (
            results.map((r, idx) => (
              <div
                id={`${listId}-item-${idx}`}
                key={r.id}
                role="option"
                aria-selected={highlight === idx}
                className={`p-3 hover:bg-gray-100 cursor-pointer ${highlight === idx ? 'bg-gray-100' : ''}`}
                onClick={() => handleSelect(r)}
                onMouseEnter={() => setHighlight(idx)}
              >
                <div className="font-medium">{r.title}</div>
                {r.trackNumber !== undefined && (
                  <div className="text-sm text-gray-500">Nº {r.trackNumber} • {r.durationSeconds ? `${r.durationSeconds}s` : ''}</div>
                )}
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
};

export default TrackSearch;
