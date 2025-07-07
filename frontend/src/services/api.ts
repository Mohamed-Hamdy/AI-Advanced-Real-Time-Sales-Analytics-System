import axios from 'axios';
import type { Order, Analytics, Recommendation } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with base configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for logging
apiClient.interceptors.request.use(
  (config) => {
    console.log(`Making ${config.method?.toUpperCase()} request to ${config.url}`);
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => {
    console.log(`Response received from ${response.config.url}:`, response.data);
    return response;
  },
  (error) => {
    console.error('Response error:', error);
    if (error.response?.status === 404) {
      console.error('API endpoint not found');
    } else if (error.response?.status >= 500) {
      console.error('Server error occurred');
    } else if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
      console.error('Backend server is not running or not accessible');
    }
    return Promise.reject(error);
  }
);

export const apiService = {
  // Get analytics data
  async getAnalytics(): Promise<Analytics> {
    try {
      const response = await apiClient.get<Analytics>('/analytics');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch analytics from backend:', error);
      throw new Error('Unable to fetch analytics data. Please ensure the backend server is running on http://localhost:8080');
    }
  },

  // Get recommendations
  async getRecommendations(): Promise<Recommendation[]> {
    try {
      const response = await apiClient.get<Recommendation[]>('/recommendations');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch recommendations from backend:', error);
      throw new Error('Unable to fetch recommendations. Please ensure the backend server is running on http://localhost:8080');
    }
  },

  // Add new order
  async addOrder(order: Omit<Order, 'id'>): Promise<Order> {
    try {
      const response = await apiClient.post<Order>('/orders', order);
      return response.data;
    } catch (error) {
      console.error('Failed to add order to backend:', error);
      throw new Error('Unable to add order. Please ensure the backend server is running on http://localhost:8080');
    }
  }
};