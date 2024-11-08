<template>
  <nav class="bgPrimary-color shadow-lg">
    <div class="container mx-auto flex items-center justify-between py-4 px-6">
      <!-- Logo Section -->
      <router-link to="/" class="flex items-center">
        <img
            src="https://www.fontys.nl/static/design/FA845701-BD71-466E-9B3D-38580DFAD5B4-fsm/images/logo-inverted@2x.png"
            alt="TE Logo"
            class="h-14 transition-transform transform hover:scale-105"
            loading="lazy"
        />
      </router-link>

      <!-- Navigation Links -->
      <div class="flex items-center space-x-8">
        <!-- User Dropdown -->
        <div v-if="user" class="flex flex-row space-x-6" >
          <div>
            <router-link
                to="/"
                class="text-base font-semibold text-gray-700 dark:text-gray-100 hover:text-gray-300 transition-colors duration-150"
            >
              Flight search
            </router-link>
          </div>
          <div>
            <router-link
                to="/liveFlights"
                class="text-base font-semibold text-gray-700 dark:text-gray-100 hover:text-gray-300 transition-colors duration-150"
            >
              Flights tracker
            </router-link>
          </div>
          <div class="relative group">
            <button class="flex items-center text-gray-700 dark:text-gray-50 text-base font-semibold focus:outline-none group-hover:text-gray-300 transition duration-150">
              {{ user.firstName }} {{ user.lastName }}
              <svg class="ml-1 w-4 h-4 text-white hover:fill-gray-400 fill-white g transition-transform transform group-hover:rotate-180" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M5.23 7.5 10 12.27l4.77-4.77a.75.75 0 0 1 1.06 1.06l-5.3 5.3a.75.75 0 0 1-1.06 0l-5.3-5.3A.75.75 0 0 1 5.23 7.5" clip-rule="evenodd"/></svg>
            </button>

            <!-- Dropdown Menu -->
            <ul
                class="absolute right-0 mt-2 w-44 bg-white hover:border-red-200  rounded-md shadow-lg border border-gray-50 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 transform -translate-y-2 group-hover:translate-y-0"
            >
              <li>
                <button
                    @click="logout"
                    class="block w-full px-4 py-2 text-sm text-red-700 font-semibold rounded hover:bg-red-200"
                >
                  Logout
                </button>
              </li>
            </ul>
          </div>
        </div>


        <!-- Login Link -->
        <div v-else>
          <router-link
              to="/login"
              class="text-base font-semibold text-gray-700 dark:text-gray-100 hover:text-blue-500 transition-colors duration-150"
          >
            Login
          </router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import user from "../localStorage/userStorage.ts";
import router from "../router";
import { onMounted } from "vue";
import {createApi} from "../AxiosInstance.ts";

const api = createApi(import.meta.env.VITE_AUTH_API_URL);

onMounted(() => {
  console.log(user.value);
});

const logout = () => {
  user.value = null;
  api.post('/auth/logout');
  router.push('/login');
};
</script>

<style scoped>
/* Additional styles for transitions and cleaner visuals */
</style>
