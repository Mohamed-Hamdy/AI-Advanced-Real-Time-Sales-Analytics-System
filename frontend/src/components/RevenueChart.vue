<template>
  <div class="chart-wrapper">
    <canvas ref="chartCanvas" class="chart-canvas"></canvas>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue';
import type { ChartData } from '../types';

// Props
interface Props {
  data: ChartData;
  width?: number;
  height?: number;
}

const props = withDefaults(defineProps<Props>(), {
  width: 400,
  height: 200
});

// Refs
const chartCanvas = ref<HTMLCanvasElement | null>(null);
let animationId: number | null = null;

// Chart drawing logic
const drawChart = () => {
  if (!chartCanvas.value) return;

  const canvas = chartCanvas.value;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  // Set canvas size
  const rect = canvas.getBoundingClientRect();
  canvas.width = rect.width * devicePixelRatio;
  canvas.height = rect.height * devicePixelRatio;
  ctx.scale(devicePixelRatio, devicePixelRatio);

  const width = rect.width;
  const height = rect.height;

  // Clear canvas
  ctx.clearRect(0, 0, width, height);

  // Chart configuration
  const padding = 40;
  const chartWidth = width - 2 * padding;
  const chartHeight = height - 2 * padding;

  // Data preparation
  const { labels, values } = props.data;
  const maxValue = Math.max(...values);
  const minValue = Math.min(...values);
  const valueRange = maxValue - minValue || 1;

  // Draw grid lines
  ctx.strokeStyle = '#e5e7eb';
  ctx.lineWidth = 1;
  ctx.setLineDash([5, 5]);

  // Horizontal grid lines
  for (let i = 0; i <= 5; i++) {
    const y = padding + (chartHeight / 5) * i;
    ctx.beginPath();
    ctx.moveTo(padding, y);
    ctx.lineTo(width - padding, y);
    ctx.stroke();
  }

  // Vertical grid lines
  for (let i = 0; i <= labels.length - 1; i++) {
    const x = padding + (chartWidth / (labels.length - 1)) * i;
    ctx.beginPath();
    ctx.moveTo(x, padding);
    ctx.lineTo(x, height - padding);
    ctx.stroke();
  }

  ctx.setLineDash([]);

  // Draw area under curve
  ctx.beginPath();
  ctx.moveTo(padding, height - padding);

  for (let i = 0; i < values.length; i++) {
    const x = padding + (chartWidth / (values.length - 1)) * i;
    const normalizedValue = (values[i] - minValue) / valueRange;
    const y = height - padding - normalizedValue * chartHeight;
    
    if (i === 0) {
      ctx.lineTo(x, y);
    } else {
      ctx.lineTo(x, y);
    }
  }

  ctx.lineTo(width - padding, height - padding);
  ctx.closePath();

  // Gradient fill
  const gradient = ctx.createLinearGradient(0, padding, 0, height - padding);
  gradient.addColorStop(0, 'rgba(59, 130, 246, 0.3)');
  gradient.addColorStop(1, 'rgba(59, 130, 246, 0.05)');
  ctx.fillStyle = gradient;
  ctx.fill();

  // Draw line
  ctx.beginPath();
  ctx.strokeStyle = '#3B82F6';
  ctx.lineWidth = 3;
  ctx.lineJoin = 'round';
  ctx.lineCap = 'round';

  for (let i = 0; i < values.length; i++) {
    const x = padding + (chartWidth / (values.length - 1)) * i;
    const normalizedValue = (values[i] - minValue) / valueRange;
    const y = height - padding - normalizedValue * chartHeight;
    
    if (i === 0) {
      ctx.moveTo(x, y);
    } else {
      ctx.lineTo(x, y);
    }
  }

  ctx.stroke();

  // Draw data points
  ctx.fillStyle = '#3B82F6';
  for (let i = 0; i < values.length; i++) {
    const x = padding + (chartWidth / (values.length - 1)) * i;
    const normalizedValue = (values[i] - minValue) / valueRange;
    const y = height - padding - normalizedValue * chartHeight;
    
    ctx.beginPath();
    ctx.arc(x, y, 4, 0, 2 * Math.PI);
    ctx.fill();
    
    // Add white center
    ctx.beginPath();
    ctx.arc(x, y, 2, 0, 2 * Math.PI);
    ctx.fillStyle = 'white';
    ctx.fill();
    ctx.fillStyle = '#3B82F6';
  }

  // Draw labels
  ctx.fillStyle = '#6B7280';
  ctx.font = '12px Inter, system-ui, sans-serif';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'top';

  for (let i = 0; i < labels.length; i++) {
    const x = padding + (chartWidth / (labels.length - 1)) * i;
    const y = height - padding + 10;
    ctx.fillText(labels[i], x, y);
  }

  // Draw y-axis labels
  ctx.textAlign = 'right';
  ctx.textBaseline = 'middle';

  for (let i = 0; i <= 5; i++) {
    const value = minValue + (valueRange / 5) * (5 - i);
    const y = padding + (chartHeight / 5) * i;
    ctx.fillText(value.toFixed(0), padding - 10, y);
  }
};

// Resize observer
let resizeObserver: ResizeObserver | null = null;

// Lifecycle hooks
onMounted(() => {
  if (chartCanvas.value) {
    // Initial draw
    drawChart();

    // Setup resize observer
    resizeObserver = new ResizeObserver(() => {
      drawChart();
    });
    resizeObserver.observe(chartCanvas.value);
  }
});

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  if (resizeObserver) {
    resizeObserver.disconnect();
  }
});

// Watch for data changes
watch(() => props.data, () => {
  drawChart();
}, { deep: true });
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}

.chart-canvas {
  width: 100%;
  height: 100%;
  display: block;
}
</style>