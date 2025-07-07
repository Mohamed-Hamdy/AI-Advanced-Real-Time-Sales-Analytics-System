<template>
  <div class="order-form">
    <div class="form-header">
      <h2 class="form-title">Add New Order</h2>
      <button class="close-btn" @click="$emit('close')">×</button>
    </div>

    <form @submit.prevent="handleSubmit" class="form">
      <div class="form-group">
        <label for="productName" class="form-label">Product Name</label>
        <input
          id="productName"
          v-model="form.productName"
          type="text"
          class="form-input"
          :class="{ 'error': errors.productName }"
          placeholder="Enter product name"
          required
        />
        <span v-if="errors.productName" class="error-message">{{ errors.productName }}</span>
      </div>

      <div class="form-group">
        <label for="quantity" class="form-label">Quantity</label>
        <input
          id="quantity"
          v-model.number="form.quantity"
          type="number"
          class="form-input"
          :class="{ 'error': errors.quantity }"
          placeholder="Enter quantity"
          min="1"
          required
        />
        <span v-if="errors.quantity" class="error-message">{{ errors.quantity }}</span>
      </div>

      <div class="form-group">
        <label for="price" class="form-label">Price ($)</label>
        <input
          id="price"
          v-model.number="form.price"
          type="number"
          class="form-input"
          :class="{ 'error': errors.price }"
          placeholder="Enter price"
          min="0"
          step="0.01"
          required
        />
        <span v-if="errors.price" class="error-message">{{ errors.price }}</span>
      </div>

      <div class="form-group">
        <label for="date" class="form-label">Date</label>
        <input
          id="date"
          v-model="form.date"
          type="datetime-local"
          class="form-input"
          :class="{ 'error': errors.date }"
          required
        />
        <span v-if="errors.date" class="error-message">{{ errors.date }}</span>
      </div>

      <div class="form-summary">
        <div class="summary-item">
          <span class="summary-label">Total Amount:</span>
          <span class="summary-value">${{ formatCurrency(form.quantity * form.price) }}</span>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn btn-secondary" @click="$emit('close')">
          Cancel
        </button>
        <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
          {{ isSubmitting ? 'Adding...' : 'Add Order' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { apiService } from '../services/api';
import type { Order } from '../types';

// Emits
const emit = defineEmits<{
  close: [];
  orderAdded: [order: Order];
}>();

// Reactive state
const form = ref({
  productName: '',
  quantity: 1,
  price: 0,
  date: ''
});

const errors = ref<Record<string, string>>({});
const isSubmitting = ref(false);

// Computed
const totalAmount = computed(() => form.value.quantity * form.value.price);

// Methods
const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount);
};

const validateForm = (): boolean => {
  errors.value = {};

  if (!form.value.productName.trim()) {
    errors.value.productName = 'Product name is required';
  }

  if (!form.value.quantity || form.value.quantity < 1) {
    errors.value.quantity = 'Quantity must be at least 1';
  }

  if (!form.value.price || form.value.price <= 0) {
    errors.value.price = 'Price must be greater than 0';
  }

  if (!form.value.date) {
    errors.value.date = 'Date is required';
  }

  return Object.keys(errors.value).length === 0;
};

const handleSubmit = async () => {
  if (!validateForm()) return;

  isSubmitting.value = true;

  try {
    const orderData = {
      productName: form.value.productName.trim(),
      quantity: form.value.quantity,
      price: form.value.price,
      date: form.value.date
    };

    const newOrder = await apiService.addOrder(orderData);
    emit('orderAdded', newOrder);
  } catch (error) {
    console.error('Failed to add order:', error);
    // Show error message to user
    errors.value.general = 'Failed to add order. Please try again.';
  } finally {
    isSubmitting.value = false;
  }
};

// Lifecycle hooks
onMounted(() => {
  // Set default date to current date/time
  const now = new Date();
  const localISOTime = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
  form.value.date = localISOTime;
});
</script>

<style scoped>
.order-form {
  background: white;
  border-radius: 0.75rem;
  overflow: hidden;
  max-width: 500px;
  width: 100%;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  background: #F9FAFB;
  border-bottom: 1px solid #E5E7EB;
}

.form-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1F2937;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6B7280;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #374151;
}

.form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.5rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #D1D5DB;
  border-radius: 0.5rem;
  font-size: 1rem;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #3B82F6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input.error {
  border-color: #EF4444;
}

.form-input.error:focus {
  border-color: #EF4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.error-message {
  display: block;
  color: #EF4444;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.form-summary {
  background: #F9FAFB;
  border-radius: 0.5rem;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-label {
  font-weight: 500;
  color: #374151;
}

.summary-value {
  font-size: 1.25rem;
  font-weight: 600;
  color: #3B82F6;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 0.5rem;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 100px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #F3F4F6;
  color: #374151;
}

.btn-secondary:hover:not(:disabled) {
  background: #E5E7EB;
}

.btn-primary {
  background: #3B82F6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563EB;
}

@media (max-width: 768px) {
  .form-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>