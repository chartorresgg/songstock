export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
    timestamp: string;
  }
  
  export interface PaginatedResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
  }

  export interface Song {
    id: number;
    title: string;
    price: number;
    format: string;
    artistName: string;
    albumTitle: string;
    available?: boolean;
    albumId?: number;
    trackNumber?: number;
    durationSeconds?: number;
  }