import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import TrackSearch from '../TrackSearch';
import { vi } from 'vitest';

describe('TrackSearch', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
    vi.restoreAllMocks();
  });

  it('calls searchFn after debounce', async () => {
    const searchFn = vi.fn().mockResolvedValue([]);
    const onSelect = vi.fn();
    render(<TrackSearch searchFn={searchFn} onSelect={onSelect} />);

    const input = screen.getByPlaceholderText(/Buscar pista/i) as HTMLInputElement;
    fireEvent.change(input, { target: { value: 'hello' } });

    // advance debounce timer (300ms in component)
    await vi.advanceTimersByTimeAsync(300);

    await waitFor(() => {
      expect(searchFn).toHaveBeenCalledWith('hello');
    });
  });

  it('allows keyboard navigation and selection', async () => {
    const tracks = [
      { id: 1, title: 'One' },
      { id: 2, title: 'Two' },
    ];
    const searchFn = vi.fn().mockResolvedValue(tracks);
    const onSelect = vi.fn();
    render(<TrackSearch searchFn={searchFn} onSelect={onSelect} />);

    const input = screen.getByPlaceholderText(/Buscar pista/i) as HTMLInputElement;
    fireEvent.change(input, { target: { value: 'o' } });

    await vi.advanceTimersByTimeAsync(300);

    // wait for results to appear
    await waitFor(() => expect(screen.getByText('One')).toBeInTheDocument());

    // navigate and select
    fireEvent.keyDown(input, { key: 'ArrowDown' });
    fireEvent.keyDown(input, { key: 'Enter' });

    await waitFor(() => expect(onSelect).toHaveBeenCalledWith(tracks[0]));
  });
});
import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import TrackSearch from '../TrackSearch';

describe('TrackSearch', () => {
  afterEach(() => {
    vi.clearAllTimers();
    vi.restoreAllMocks();
  });

  test('debounces calls to searchFn and calls with the last value', async () => {
    vi.useFakeTimers();
    const searchFn = vi.fn(async (q: string) => {
      return [ { id: 1, title: `Result for ${q}` } ];
    });

    const onSelect = vi.fn();
    const user = userEvent.setup({ advanceTimers: vi.advanceTimersByTime });

    render(<TrackSearch onSelect={onSelect} searchFn={searchFn} />);

    const input = screen.getByPlaceholderText(/buscar pista/i);

    // type quickly several characters
    await user.type(input, 'abc');

    // advance time less than debounce - should not call
    vi.advanceTimersByTime(200);
    expect(searchFn).not.toHaveBeenCalled();

    // advance over debounce timeout (300ms)
    vi.advanceTimersByTime(200);

    // wait for the async search to resolve
    await waitFor(() => expect(searchFn).toHaveBeenCalledTimes(1));
    expect(searchFn).toHaveBeenCalledWith('abc');

    // suggestions should be shown
    expect(screen.getByRole('listbox')).toBeInTheDocument();
    expect(screen.getByText(/Result for abc/)).toBeInTheDocument();
  });

  test('keyboard navigation and selection via Enter', async () => {
    vi.useFakeTimers();
    const results = [
      { id: 1, title: 'First' },
      { id: 2, title: 'Second' },
      { id: 3, title: 'Third' },
    ];

    const searchFn = vi.fn(async (q: string) => results);
    const onSelect = vi.fn();
    const user = userEvent.setup({ advanceTimers: vi.advanceTimersByTime });

    render(<TrackSearch onSelect={onSelect} searchFn={searchFn} />);
    const input = screen.getByPlaceholderText(/buscar pista/i);

    await user.type(input, 'se');
    vi.advanceTimersByTime(350);

    // expect listbox and items
    await waitFor(() => expect(screen.getByRole('listbox')).toBeInTheDocument());

    // press ArrowDown to highlight first, ArrowDown to highlight second, then Enter
    await user.keyboard('{ArrowDown}{ArrowDown}{Enter}');

    // onSelect should be called with the second item
    await waitFor(() => expect(onSelect).toHaveBeenCalledTimes(1));
    expect(onSelect).toHaveBeenCalledWith(results[1]);
  });
});
