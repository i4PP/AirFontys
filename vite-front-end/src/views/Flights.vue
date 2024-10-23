
<script setup lang="ts">
import { Form, Field, ErrorMessage } from 'vee-validate';
import { ref, onMounted } from 'vue'
import {useRoute} from "vue-router";
import router from "../router";
import {createApi} from "../AxiosInstance.ts";


const depAirport = ref( '');
const arrAirport = ref('');
const depDate = ref('');
const flights = ref<FlightData[]>([]);
const error = ref(null);
const loading = ref(false);
const api = createApi(import.meta.env.VITE_FLIGHTS_API_URL);

const fetchFlights = async () => {
    loading.value = true;
    error.value = null;
api.get<FlightData>('/flights', {
  params: {
    airportDep: depAirport.value,
    airportArrv: arrAirport.value,
    date: depDate.value,
  },
})
  .then((response) => {
    flights.value = response.data;
    console.log(response.data);
  })
  .catch((err) => {
    console.error(err);
    error.value = err.response.data;
  })
  .finally(() => {
    loading.value = false;
  });
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

    await fetchFlights();
  };

onMounted(() => {
  const route = useRoute();
  console.log(route.query + "route");
  console.log(route.query.depAirport);
  depAirport.value = route.query.depAirport as string;
  arrAirport.value = route.query.arrAirport as string;
  depDate.value = route.query.depDate as string;

  if (depAirport.value && arrAirport.value && depDate.value) {
    fetchFlights();
  }
});
</script>


<style scoped>
/* Custom styles for loading and error messages */
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>




<template>

  <div class="max-w-4xl mx-auto mt-5">
    <h1 class="text-4xl font-bold mb-3 self-start">Flights</h1>
    <div class="flex flex-col items-center ">
      <Form @submit="onSubmit"  class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="flex flex-col">
          <label for="depAirport" class="mb-2 font-medium text-gray-700">Departure Airport</label>
          <Field
              type="text"
              name="depAirport"
              v-model="depAirport"
              class="border border-gray-300 rounded-md px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
              placeholder="Enter departure airport"
          />
          <Error-message name="depAirport" class="text-red-500 h-0" />
        </div>
        <div class="flex flex-col">
          <label for="arrAirport" class="mb-2 font-medium text-gray-700">Arrival Airport</label>
          <Field
              type="text"
              name="arrAirport"
              v-model="arrAirport"
              class="border border-gray-300 rounded-md px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
              placeholder="Enter arrival airport"
          />
          <Error-message name="arrAirport" class="text-red-500 h-0" />
        </div>
        <div class="flex flex-col">
          <label for="depDate" class="mb-2 font-medium text-gray-700">Departure Date</label>
          <Field
              type="date"
              name="depDate"
              v-model="depDate"
              :min="new Date(new Date().setDate(new Date().getDate() + 40)).toISOString().split('T')[0]"
              class="border border-gray-300 rounded-md px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
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

  <!-- Loading State -->
  <div v-if="loading" class="flex items-center justify-center mt-24">
    <div class="mx-auto" role="status">
      <p class="text-gray-500 animate-pulse">Fetching flights...</p>
    </div>
  </div>
  <!-- Error Handling -->
  <div v-else-if="error">
    <!-- 404 Error (No Flights Found) -->
    <div v-if="error.status === 404" class="mx-auto bg-yellow-50 border border-yellow-300 text-yellow-600 rounded-md p-4 w-full max-w-lg">
      <div class="flex items-start">
        <svg class="h-5 w-5 text-yellow-600 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
        <div>
          <h3 class="font-semibold">No flights available for the selected Airports and date.</h3>
          <ul class="mt-1 list-disc list-inside text-sm">
            <li>Please verify the departure and arrival airports and try again.</li>
          </ul>
        </div>
      </div>
    </div>
    <!-- Other Errors -->
    <div v-else class="mx-auto mt-16 bg-red-50 border border-red-300 text-red-600 rounded-md p-4 w-full max-w-lg">
      <div class="flex items-start">
        <svg class="h-5 w-5 text-red-600 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
        <div>
          <h3 class="font-semibold">Error while fetching flights (Error: {{ error.status }})</h3>
          <p class="mt-1">{{ error.message || 'An unknown error occurred. Please try again later.' }}</p>
          <ul v-if="error.details" class="mt-1 list-disc list-inside text-sm">
            <li v-for="detail in error.details" :key="detail">{{ detail }}</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
  <!-- Display Flights -->
  <div v-else >
    <div class="max-w-6xl mx-auto mt-5" v-if="flights.length" >
      <h1 class="text-4xl font-bold mb-8 self-start">Available flights</h1>
      <div class="max-w-6xl mx-auto p-6 bg-white shadow-lg rounded-lg">
        <ul>
          <li v-for="flight in flights" :key="flight.flight.number" class="border-b py-4">
            <div class="flex justify-between items-center">
              <div>
                <p class="text-xl font-semibold Primary-color ">Flight Number: {{ flight.flight.iataNumber.toUpperCase() }}</p>
                <p class="text-gray-600">Aircraft: {{ flight.aircraft.modelText }}</p>
              </div>
              <div class="text-right">
                <p class="text-lg font-bold text-gray-800">{{ flight.flight.price }} USD</p>
                <p class="text-sm text-gray-500">Distance: {{ flight.flight.distance }} km</p>
              </div>
            </div>

            <div class="mt-4 grid grid-cols-2 gap-6">
              <!-- Departure Info -->
              <div class="p-4 bg-purple-50 rounded-lg">
                <h3 class="text-md font-bold text-gray-700">Departure</h3>
                <p class="text-gray-600">Airport: {{ flight.departure.iataCode.toUpperCase() }} ({{ flight.departure.icaoCode.toUpperCase() }})</p>
                <p class="text-gray-600">Terminal: {{ flight.departure.terminal }}</p>
                <p class="text-gray-600">Gate: {{ flight.departure.gate }}</p>
                <p class="text-gray-600">Time: {{ flight.departure.scheduledTime }}</p>
              </div>

              <!-- Arrival Info -->
              <div class="p-4 bg-gray-100 rounded-lg">
                <h3 class="text-md font-bold text-gray-700">Arrival</h3>
                <p class="text-gray-600">Airport: {{ flight.arrival.iataCode.toUpperCase() }} ({{ flight.arrival.icaoCode.toUpperCase() }})</p>
                <p class="text-gray-600">Scheduled Arrival: {{ flight.arrival.scheduledTime }}</p>
              </div>
            </div>

            <div class="mt-4 text-center">
              <button class="text-white py-2 px-4 rounded-lg bgPrimary-color bgPrimary-color-hover">
                Buy ticket
              </button>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>





