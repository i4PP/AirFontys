<template>
  <div>
    <div v-if="isLoading" class="text-center">
      <p>Loading data...</p>
    </div>
    <div v-else>
      <p class="text-xl font-semibold">Data from WebSocket: {{ wsData }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { WebSocketService } from '../WebSocketService';

const isLoading = ref(true);
const wsData = ref(null);
const webSocketService = new WebSocketService('ws://localhost:8082/ws/flights');

function handleWebSocketMessage(data: any) {
  wsData.value = data; // Process data as required
  isLoading.value = false;
}

function handleWebSocketError(error: Event) {
  console.error('WebSocket error:', error);
  isLoading.value = false; // Optionally stop loading on error
}

onMounted(() => {
  isLoading.value = true;
  webSocketService.connectWebSocket(handleWebSocketMessage, handleWebSocketError, () => {
    console.log('WebSocket opened');
  });
});

onBeforeUnmount(() => {
  webSocketService.closeWebSocket();
});
</script>
