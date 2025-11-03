export interface Song {
      id: number;
      albumId: number;
      albumTitle: string;
      artistName: string;
      trackNumber: number;
      title: string;
      durationSeconds?: number;
    }
    
    export interface Compilation {
      id: number;
      userId: number;
      creatorUsername?: string;
      name: string;
      description?: string;
      isPublic: boolean;
      songCount: number;
      songs?: Song[];
      createdAt: string;
      updatedAt: string;
    }