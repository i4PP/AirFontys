<template>
  <h1 class="text-4xl font-bold mb-3 self-start">Live flight information</h1>
  <div class="max-w-2xl mx-auto mt-5 mb-7 bg-white shadow-md rounded-lg p-5">
    <div class="flex flex-col items-center ">
      <Form  @submit.prevent="onSubmit"   class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="flex flex-col">
          <label for="arrAirport" class="mb-2 font-medium text-gray-700">Flight Code</label>
          <input
              type="text"
              name="flightCode"
              v-model="flightCode"
              class="border border-gray-300 rounded-md px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
              placeholder="Enter arrival airport"
              required
          />
          <Error-message name="arrAirport" class="text-red-500 h-0" />
        </div>
        <div class="flex flex-col">
          <label for="depDate" class="mb-2 font-medium text-gray-700">Departure Date</label>
          <input
              type="date"
              name="depDate"
              v-model="depDate"
              :min="new Date(new Date().setDate(new Date().getDate() + 40)).toISOString().split('T')[0]"
              class="border border-gray-300 rounded-md px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
              required
          />
          <ErrorMessage name="depDate" class="text-red-500 h-0" />
        </div>
        <div class="flex  items-end">
          <button
              type="submit"
              class="w-full bgPrimary-color text-white font-semibold py-2 px-4 rounded-md bgPrimary-color-hover transition duration-150">
            Search
          </button>
        </div>
      </Form>
    </div>
  </div>


  <div v-if="flightData" class="bg-white shadow-lg rounded-lg p-4 w-full max-w-md mx-auto">
    <div>
      <div class="text-lg font-semibold text-gray-600">
        {{ flightData.departure.airport }} to {{ flightData.arrival.airport }}
      </div>
    </div>

    <div class="flex items-center justify-between text-gray-600 text-base font-semibold mb-2">
      <span>{{ flightData.airline.iata }}{{ flightData.flight.number }}</span>
      <div v-if="flightStatus">{{ flightStatus }}</div>
    </div>

    <div class="flex justify-between items-center mb-4">
      <!-- Departure Information -->
      <div class="text-center">
        <div class="text-xl font-bold text-black">{{ departureTime }}</div>
        <div class="text-xs text-green-500">{{ departureStatus }}</div>
        <div class="text-sm font-medium text-gray-700">{{ flightData.departure.iata }}</div>
      </div>

      <!-- Progress Bar -->
      <div class="flex-1 flex items-center justify-center px-4">
        <div class="w-full bg-gray-200 h-1 rounded">
          <div class="bg-blue-500 h-1 rounded" :style="{ width: progressBarWidth + '%' }"></div>
        </div>
      </div>

      <!-- Arrival Information -->
      <div class="text-center">
        <div class="text-xl font-bold text-black">{{ arrivalTime }}</div>
        <div class="text-xs text-green-500">{{ arrivalStatus }}</div>
        <div class="text-sm font-medium text-gray-700">{{ flightData.arrival.iata }}</div>
      </div>
    </div>

    <!-- Additional Flight Information -->
    <div class="text-gray-600 text-xs mb-2">
      <div class="flex justify-between">
        <span>Terminal: {{ flightData.departure.terminal || 'N/A' }}</span>
        <span>Time left: {{ timeLeft }}</span>
      </div>
      <div class="flex justify-between">
        <span>Altitude: {{ flightData.live.altitude ? flightData.live.altitude.toFixed(0) + ' ft' : 'N/A' }}</span>
        <span>Speed: {{ flightData.live.speed_horizontal ? flightData.live.speed_horizontal.toFixed(0) + ' km/h' : 'N/A' }}</span>
      </div>
      <div class="flex justify-between">
        <span>Latitude: {{ flightData.live.latitude.toFixed(2) }}</span>
        <span>Longitude: {{ flightData.live.longitude.toFixed(2) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount, computed } from 'vue';
import {ErrorMessage} from "vee-validate";

// Reactive states
const flightCode = ref('');
const depDate = ref('');

const flightData = ref<any>(null);
const socket = ref<WebSocket | null>(null);

// Reactive properties for computed values
const departureTime = computed(() => flightData.value?.departure.scheduled ? new Date(flightData.value.departure.scheduled).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '');
const arrivalTime = computed(() => flightData.value?.arrival.scheduled ? new Date(flightData.value.arrival.scheduled).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '');
const flightStatus = computed(() => flightData.value?.flight_status === "active" ? 'In Air' : 'Arrived');
const departureStatus = computed(() => flightData.value?.departure.actual !== flightData.value?.departure.scheduled
    ? `Delayed by ${Math.floor((flightData.value.departure.actual - flightData.value.departure.scheduled) / 60000)} mins`
    : 'On time');
const arrivalStatus = ref("On time");
const progressBarWidth = ref(0);
const timeLeft = ref('Calculating...');

function updateProgress() {
  if (!flightData.value) return;

  const now = Date.now();
  const departure = new Date(flightData.value.departure.scheduled).getTime();
  const arrival = new Date(flightData.value.arrival.scheduled).getTime();
  const totalFlightTime = arrival - departure;
  const elapsed = now - departure;

  if (elapsed >= totalFlightTime) {
    progressBarWidth.value = 100;
    timeLeft.value = 'Arrived';
    arrivalStatus.value = 'Arrived';
  } else {
    progressBarWidth.value = (elapsed / totalFlightTime) * 100;
    const remainingMinutes = Math.floor((totalFlightTime - elapsed) / 60000);
    timeLeft.value = `${Math.floor(remainingMinutes / 60)}h ${remainingMinutes % 60}m left`;
  }
}

function handleWebSocketMessage(event: MessageEvent) {
  const data = JSON.parse(event.data);
  console.log('Received flight data:', data); // Log the entire data for inspection
  flightData.value = data.data[0]; // Ensure that you're setting the correct flight data
  updateProgress();
}

function connectWebSocket(code: string, date: string) {
  socket.value = new WebSocket('ws://localhost:8082/ws/flight-tracking');

  socket.value.onopen = () => {
    console.log('WebSocket connection opened');
    // Send the flight code and departure date to the backend
    socket.value?.send(JSON.stringify({ flightCode: code, depDate: date }));
  };

  socket.value.onmessage = handleWebSocketMessage;

  socket.value.onerror = (error: Event) => {
    console.error('WebSocket error:', error);
  };

  socket.value.onclose = () => {
    console.warn('WebSocket connection closed, retrying in 5 seconds');
    setTimeout(() => connectWebSocket(code, date), 5000); // Retry connection
  };
}

function onSubmit() {
  if (socket.value) {
    socket.value.close();
  }
  connectWebSocket(flightCode.value, depDate.value);
}


// WebSocket cleanup
onBeforeUnmount(() => {
  if (socket.value) {
    socket.value.close();
    console.log('WebSocket connection closed on component unmount');
  }
});
</script>

<style scoped>
/* Add additional styling here */
</style>

