<template>
  <div class="bg-gray-50 rounded-xl shadow-lg max-w-2xl mb-7 mx-auto p-6 space-y-6 transition-transform  hover:shadow-xl border border-gray-200">
    <!-- Header Section with Flight Number and Price -->
    <div class="flex justify-between items-center">
      <h2 class="text-xl font-semibold text-gray-800">Flight {{ flight.flight.number }}</h2>
      <span class="text-lg font-semibold Primary-color">Price: ${{ flight.flight.price.toFixed(2) }}</span>
    </div>

    <div class="border-t border-gray-300 pt-3 flex justify-between items-center">
      <p class="text-gray-800 text-sm"><span class="font-semibold">Aircraft:</span> {{ flight.aircraft.modelText }}</p>
      <p class="text-gray-800 text-sm"><span class="font-semibold">Distance:</span> {{ flight.flight.distance }} km</p>
    </div>
    <!-- Flight Information Section -->
    <div class="grid grid-cols-2 gap-8 border-gray-300 border-b  pb-5 divide-x">
      <!-- Departure Information -->
      <div>
        <h3 class="text-gray-600 text-base font-medium ">Departure</h3>
        <div class="mt-2 space-y-1">
          <p class="text-gray-800 text-sm"><span class="font-medium">Airport:</span> {{ flight.departure.iataCode.toUpperCase() }}</p>
          <p class="text-gray-800 text-sm"><span class="font-medium">Time:</span> {{ flight.departure.scheduledTime }}</p>
          <p class="text-gray-800 text-sm"><span class="font-medium">Terminal:</span> {{ flight.departure.terminal || 'N/A' }}</p>
          <p class="text-gray-800 text-sm"><span class="font-medium">Gate:</span> {{ flight.departure.gate || 'N/A' }}</p>
        </div>
      </div>

      <!-- Arrival Information -->
      <div>
        <h3 class="text-gray-600 text-base font-medium  ">Arrival</h3>
        <div class="mt-2 space-y-1">
          <p class="text-gray-800 text-sm"><span class="font-medium">Airport:</span> {{ flight.arrival.iataCode.toUpperCase() }}</p>
          <p class="text-gray-800 text-sm"><span class="font-medium">Time:</span> {{ flight.arrival.scheduledTime }}</p>
          <p class="text-gray-800 text-sm"><span class="font-medium">Terminal:</span> {{ flight.arrival.terminal || 'N/A' }}</p>
          <p class="text-gray-800 text-sm"><span class="font-medium">Gate:</span> {{ flight.arrival.gate || 'N/A' }}</p>
        </div>
      </div>
    </div>

    <!-- Additional Information -->


    <!-- Buy Button -->
    <div class="text-center">
      <button class=" p-6 text-center bgPrimary-color text-white font-semibold py-3 rounded-lg shadow-lg bgPrimary-color-hover:hove">
        Buy Ticket
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
import { FlightData } from '../../entities/Flight/FlightData.ts'

defineProps<{ flight: FlightData }>()
</script>
