// Type definitions for the sales analytics system
export interface Order {
  id?: string;
  productName: string;
  quantity: number;
  price: number;
  date: string;
  total?: number;
}

export interface Analytics {
  totalRevenue: number;
  totalOrders: number;
  topProducts: TopProduct[];
  recentOrders: Order[];
  revenueChange: number;
  ordersInLastMinute: number;
}

export interface TopProduct {
  name: string;
  totalSales: number;
  quantity: number;
  percentage: number;
}

export interface Recommendation {
  id: string;
  title: string;
  description: string;
  type: 'promotion' | 'pricing' | 'inventory' | 'seasonal';
  priority: 'high' | 'medium' | 'low';
  impact: string;
}

export interface WebSocketMessage {
  type: 'new_order' | 'analytics_update' | 'recommendation_update';
  data: any;
}

export interface ChartData {
  labels: string[];
  values: number[];
  colors?: string[];
}