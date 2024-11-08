<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from "vue-router";
import router from "../../router";
import { createApi } from "../../AxiosInstance.ts";
import { FlightData } from "../../entities/Flight/FlightData.ts";
import { Error } from "../../entities/Error.ts";
import { fetchFlights } from "./FetchFlights.ts";
import FlightCard from "./FlightCard.vue";
import ErrorCard from "../../components/ErrorCard.vue";
import {ErrorMessage, Field} from "vee-validate";


const depAirport = ref('')
const arrAirport = ref('')
const depDate = ref('')
const flights = ref<FlightData[] | null>(null)
const error = ref<Error | null>(null)
const loading = ref(false)

const api = createApi(import.meta.env.VITE_FLIGHT_API_URL)

const loadFlights = async () => {
  loading.value = true;
  error.value = null;

  const result = await fetchFlights(api, {
    depAirport: depAirport.value,
    arrAirport: arrAirport.value,
    depDate: depDate.value,
  });

  flights.value = result.flights;
  error.value = result.error;
  loading.value = false;

  if (error.value) {
    console.error(error.value);
  } else {
    console.log(flights.value);
  }
}

const onSubmit = async () => {
  router.push({
    path: '/flights',
    query: {
      depAirport: depAirport.value,
      arrAirport: arrAirport.value,
      depDate: depDate.value,
    },
  });

  await loadFlights();
};

onMounted(() => {
  const route = useRoute();
  depAirport.value = route.query.depAirport as string || '';
  arrAirport.value = route.query.arrAirport as string || '';
  depDate.value = route.query.depDate as string || '';

  if (depAirport.value && arrAirport.value && depDate.value) {
    loadFlights();
  }
});
</script>

<template>

  <h1 class="text-4xl font-bold mb-3 self-start">Flights</h1>
  <div class="max-w-5xl mx-auto mt-5 mb-7 bg-white shadow-md rounded-lg p-5">
    <div class="flex flex-col items-center ">
      <Form @submit="onSubmit"  class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="flex flex-col">
          <label for="depAirport" class="mb-2 font-medium text-gray-700">Departure Airport</label>
          <input
              type="text"
              name="depAirport"
              v-model="depAirport"
              class="border border-gray-300 rounded-md px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
              placeholder="Enter departure airport"
              required
          />
          <Error-message name="depAirport" class="text-red-500 h-0" />
        </div>
        <div class="flex flex-col">
          <label for="arrAirport" class="mb-2 font-medium text-gray-700">Arrival Airport</label>
          <input
              type="text"
              name="arrAirport"
              v-model="arrAirport"
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
              class="w-full bgPrimary-color text-white font-semibold py-2 px-4 rounded-md bgPrimary-color-hover  transition duration-150">
            Search
          </button>
        </div>
      </Form>
    </div>
  </div>



    <!-- Flight Results -->
    <div v-if="loading" class="text-center flex flex-col items-center">
      <div class="spinner-border animate-spin inline-block w-8 h-8 border-4 rounded-full border-t-fuchsia-900 mb-4"></div>
      <p class="text-gray-600 text-lg">Loading flights...</p>
    </div>

    <div v-else-if="error" class="text-red-500 text-center">
      <ErrorCard :error="error" />
    </div>

    <div v-else class="">
      <h2 v-if="flights && flights.length" class="text-xl font-semibold text-center mb-4">Available Flights</h2>
      <p v-else class="text-center"> </p>

      <FlightCard v-for="flight in flights" :key="flight.flight.number" :flight="flight" />
    </div>

</template>

<style scoped>

</style>
