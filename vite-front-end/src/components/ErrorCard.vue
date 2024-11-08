<script setup lang="ts">
import { defineProps } from 'vue'
import { Error } from '../entities/Error.ts'


defineProps<{ error: Error }>()

</script>

<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-red-100 border border-red-300 rounded-lg shadow-md">
    <h2 class="text-2xl font-semibold text-red-700 mb-2">Oops! Something went wrong</h2>
    <p class="text-gray-700 mb-4">
      We're having trouble processing your request. Please see the details below and try again.
    </p>

    <!-- Displaying error details -->
    <div class="bg-red-200 p-4 rounded-md">
      <p class="text-sm text-red-800"><span class="font-semibold">Status:</span> {{ error.response.status }}</p>
      <p class="text-sm text-red-800"><span class="font-semibold">Message:</span> {{ error.response.data.message }}</p>
    </div>
  </div>
</template>

<style scoped>

</style>