import axiosInstance from '../services/axios.instance';
import { ApiResponse } from '../types/api.types';

export interface Notification {
  id: number;
  type: string;
  title: string;
  message: string;
  isRead: boolean;
  orderId?: number;
  createdAt: string;
}

class NotificationService {
  async getNotifications(): Promise<Notification[]> {
    const response = await axiosInstance.get<ApiResponse<Notification[]>>('/notifications');
    return response.data.data;
  }

  async getUnreadCount(): Promise<number> {
    const response = await axiosInstance.get<ApiResponse<number>>('/notifications/unread-count');
    return response.data.data;
  }

  async markAsRead(id: number): Promise<void> {
    await axiosInstance.put(`/notifications/${id}/read`);
  }

  async markAllAsRead(notifications: Notification[]): Promise<void> {
    const unreadIds = notifications.filter(n => !n.isRead).map(n => n.id);
    await Promise.all(unreadIds.map(id => this.markAsRead(id)));
  }
}

export default new NotificationService();