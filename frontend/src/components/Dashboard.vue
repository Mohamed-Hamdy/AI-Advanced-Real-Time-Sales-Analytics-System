<template>
  <div class="dashboard">
    <!-- Header -->
    <div class="dashboard-header">
      <h1 class="dashboard-title">AI Sales Analytics Dashboard</h1>
      <div class="connection-status">
        <div class="status-indicator" :class="{ 'connected': isConnected, 'disconnected': !isConnected }"></div>
        <span class="status-text">{{ isConnected ? 'Connected' : 'Disconnected' }}</span>
      </div>
    </div>

    <!-- Main Content -->
    <div class="dashboard-content">
      <!-- Analytics Cards -->
      <div class="analytics-grid">
        <div class="analytics-card revenue-card">
          <div class="card-header">
            <h3 class="card-title">Total Revenue</h3>
            <div class="card-icon">💰</div>
          </div>
          <div class="card-value">${{ formatCurrency(analytics.totalRevenue) }}</div>
          <div class="card-change" :class="{ 'positive': analytics.revenueChange > 0, 'negative': analytics.revenueChange < 0 }">
            {{ analytics.revenueChange > 0 ? '+' : '' }}{{ analytics.revenueChange.toFixed(1) }}%
          </div>
        </div>

        <div class="analytics-card orders-card">
          <div class="card-header">
            <h3 class="card-title">Total Orders</h3>
            <div class="card-icon">📦</div>
          </div>
          <div class="card-value">{{ analytics.totalOrders }}</div>
          <div class="card-subtitle">{{ analytics.ordersInLastMinute }} in last minute</div>
        </div>

        <div class="analytics-card products-card">
          <div class="card-header">
            <h3 class="card-title">Top Products</h3>
            <div class="card-icon">⭐</div>
          </div>
          <div class="top-products-list">
            <div v-for="product in analytics.topProducts.slice(0, 3)" :key="product.name" class="product-item">
              <span class="product-name">{{ product.name }}</span>
              <span class="product-sales">${{ formatCurrency(product.totalSales) }}</span>
            </div>
          </div>
        </div>

        <div class="analytics-card chart-card">
          <div class="card-header">
            <h3 class="card-title">Revenue Chart</h3>
            <div class="card-icon">📈</div>
          </div>
          <div class="chart-container">
            <RevenueChart :data="chartData" />
          </div>
        </div>
      </div>

      <!-- Recent Orders & Recommendations -->
      <div class="bottom-section">
        <div class="recent-orders-section">
          <h3 class="section-title">Recent Orders</h3>
          <div class="orders-list">
            <div v-for="order in analytics.recentOrders" :key="order.id" class="order-item">
              <div class="order-info">
                <span class="order-product">{{ order.productName }}</span>
                <span class="order-quantity">Qty: {{ order.quantity }}</span>
              </div>
              <div class="order-value">${{ formatCurrency(order.price * order.quantity) }}</div>
            </div>
          </div>
        </div>

        <div class="recommendations-section">
          <h3 class="section-title">AI Recommendations</h3>
          <div class="recommendations-list">
            <div v-for="rec in recommendations" :key="rec.id" class="recommendation-item" :class="rec.priority">
              <div class="recommendation-header">
                <span class="recommendation-title">{{ rec.title }}</span>
                <span class="recommendation-priority">{{ rec.priority }}</span>
              </div>
              <p class="recommendation-description">{{ rec.description }}</p>
              <div class="recommendation-impact">{{ rec.impact }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Order Form Modal -->
    <div v-if="showOrderForm" class="modal-overlay" @click="closeOrderForm">
      <div class="modal-content" @click.stop>
        <OrderForm @close="closeOrderForm" @order-added="handleOrderAdded" />
      </div>
    </div>

    <!-- Add Order Button -->
    <button class="add-order-btn" @click="showOrderForm = true">
      <span class="btn-icon">+</span>
      Add Order
    </button>

    <!-- Notification -->
    <div v-if="notification" class="notification" :class="notification.type">
      {{ notification.message }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { apiService } from '../services/api';
import { webSocketService } from '../services/websocket';
import type { Analytics, Recommendation, Order, ChartData } from '../types';
import RevenueChart from './RevenueChart.vue';
import OrderForm from './OrderForm.vue';

// Reactive state
const analytics = ref<Analytics>({
  totalRevenue: 0,
  totalOrders: 0,
  topProducts: [],
  recentOrders: [],
  revenueChange: 0,
  ordersInLastMinute: 0
});

const recommendations = ref<Recommendation[]>([]);
const isConnected = ref(false);
const showOrderForm = ref(false);
const notification = ref<{ message: string; type: 'success' | 'error' } | null>(null);

// Chart data
const chartData = ref<ChartData>({
  labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  values: [2400, 1398, 9800, 3908, 4800, 3800, 4300],
  colors: ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#F97316', '#06B6D4']
});

// Methods
const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount);
};

const loadAnalytics = async () => {
  try {
    const data = await apiService.getAnalytics();
    analytics.value = data;
  } catch (error) {
    console.error('Failed to load analytics:', error);
  }
};

const loadRecommendations = async () => {
  try {
    const data = await apiService.getRecommendations();
    recommendations.value = data;
  } catch (error) {
    console.error('Failed to load recommendations:', error);
  }
};

const closeOrderForm = () => {
  showOrderForm.value = false;
};

const handleOrderAdded = (order: Order) => {
  showNotification('Order added successfully!', 'success');
  loadAnalytics(); // Refresh analytics
  closeOrderForm();
};

const showNotification = (message: string, type: 'success' | 'error') => {
  notification.value = { message, type };
  setTimeout(() => {
    notification.value = null;
  }, 3000);
};

// WebSocket event handlers
const handleNewOrder = (order: Order) => {
  console.log('New order received:', order);
  showNotification(`New order: ${order.productName}`, 'success');
  loadAnalytics(); // Refresh analytics
};

const handleAnalyticsUpdate = (data: Analytics) => {
  console.log('Analytics updated:', data);
  analytics.value = data;
};

const handleConnectionStatus = (connected: boolean) => {
  isConnected.value = connected;
};

// Lifecycle hooks
onMounted(async () => {
  // Load initial data
  await Promise.all([
    loadAnalytics(),
    loadRecommendations()
  ]);

  // Setup WebSocket
  webSocketService.on('connected', handleConnectionStatus);
  webSocketService.on('new_order', handleNewOrder);
  webSocketService.on('analytics_update', handleAnalyticsUpdate);
  webSocketService.connect();

  // Refresh data periodically
  setInterval(loadAnalytics, 30000); // Every 30 seconds
});

onUnmounted(() => {
  webSocketService.off('connected', handleConnectionStatus);
  webSocketService.off('new_order', handleNewOrder);
  webSocketService.off('analytics_update', handleAnalyticsUpdate);
  webSocketService.disconnect();
});
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.dashboard-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: white;
  margin: 0;
}

.connection-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 2rem;
  backdrop-filter: blur(10px);
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transition: background-color 0.3s;
}

.status-indicator.connected {
  background-color: #10B981;
}

.status-indicator.disconnected {
  background-color: #EF4444;
}

.status-text {
  color: white;
  font-size: 0.875rem;
  font-weight: 500;
}

.dashboard-content {
  background: white;
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.analytics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.analytics-card {
  background: white;
  border-radius: 0.75rem;
  padding: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  transition: transform 0.2s, box-shadow 0.2s;
}

.analytics-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.card-title {
  font-size: 1rem;
  font-weight: 600;
  color: #4B5563;
  margin: 0;
}

.card-icon {
  font-size: 1.5rem;
}

.card-value {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 0.5rem;
}

.card-change {
  font-size: 0.875rem;
  font-weight: 500;
}

.card-change.positive {
  color: #10B981;
}

.card-change.negative {
  color: #EF4444;
}

.card-subtitle {
  font-size: 0.875rem;
  color: #6B7280;
}

.top-products-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.product-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f3f4f6;
}

.product-name {
  font-weight: 500;
  color: #1F2937;
}

.product-sales {
  font-weight: 600;
  color: #3B82F6;
}

.chart-container {
  height: 200px;
  width: 100%;
}

.bottom-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 1rem;
}

.orders-list, .recommendations-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #F9FAFB;
  border-radius: 0.5rem;
  border: 1px solid #e5e7eb;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.order-product {
  font-weight: 500;
  color: #1F2937;
}

.order-quantity {
  font-size: 0.875rem;
  color: #6B7280;
}

.order-value {
  font-weight: 600;
  color: #3B82F6;
}

.recommendation-item {
  padding: 1rem;
  border-radius: 0.5rem;
  border: 1px solid #e5e7eb;
  background: #F9FAFB;
}

.recommendation-item.high {
  border-left: 4px solid #EF4444;
}

.recommendation-item.medium {
  border-left: 4px solid #F59E0B;
}

.recommendation-item.low {
  border-left: 4px solid #10B981;
}

.recommendation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.recommendation-title {
  font-weight: 600;
  color: #1F2937;
}

.recommendation-priority {
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  font-weight: 500;
  text-transform: uppercase;
}

.recommendation-item.high .recommendation-priority {
  background: #FEE2E2;
  color: #DC2626;
}

.recommendation-item.medium .recommendation-priority {
  background: #FEF3C7;
  color: #D97706;
}

.recommendation-item.low .recommendation-priority {
  background: #D1FAE5;
  color: #059669;
}

.recommendation-description {
  color: #6B7280;
  margin: 0 0 0.5rem 0;
  line-height: 1.5;
}

.recommendation-impact {
  font-size: 0.875rem;
  font-weight: 500;
  color: #3B82F6;
}

.add-order-btn {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  background: #3B82F6;
  color: white;
  border: none;
  border-radius: 3rem;
  padding: 1rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.add-order-btn:hover {
  background: #2563EB;
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.btn-icon {
  font-size: 1.25rem;
  font-weight: 700;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 0.75rem;
  padding: 0;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.notification {
  position: fixed;
  top: 2rem;
  right: 2rem;
  padding: 1rem 1.5rem;
  border-radius: 0.5rem;
  color: white;
  font-weight: 500;
  z-index: 1001;
  animation: slideIn 0.3s ease-out;
}

.notification.success {
  background: #10B981;
}

.notification.error {
  background: #EF4444;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 1rem;
  }
  
  .dashboard-title {
    font-size: 2rem;
  }
  
  .analytics-grid {
    grid-template-columns: 1fr;
  }
  
  .bottom-section {
    grid-template-columns: 1fr;
  }
  
  .add-order-btn {
    bottom: 1rem;
    right: 1rem;
  }
}
</style>