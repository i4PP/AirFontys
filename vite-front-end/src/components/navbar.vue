<template>
  <nav class="relative w-full bgPrimary-color shadow-lg">
    <div class="container mx-auto flex items-center justify-between py-4 px-6">
      <!-- Logo Section -->
      <a href="#" class="flex items-center">
        <img
            src="https://www.fontys.nl/static/design/FA845701-BD71-466E-9B3D-38580DFAD5B4-fsm/images/logo-inverted@2x.png"
            alt="TE Logo"
            class="h-12 transition-transform transform hover:scale-110"
            loading="lazy"
        />
      </a>

      <!-- Navigation Links -->
      <ul class="flex space-x-8 items-center">
        <!-- Conditionally display the user's name or login link -->
        <li v-if="user" class="relative group">
          <!-- User Name with Hover Dropdown -->
          <div class="text-gray-800 dark:text-gray-100 text-lg font-semibold transition-colors duration-300 cursor-pointer ">
            {{ user.firstName }} {{ user.lastName }}
          </div>
          <ul
              class="absolute right-0 mt-2 w-40 bg-white  rounded-lg shadow-lg border border-purple-800  opacity-0 group-hover:opacity-100 group-hover:translate-y-1 transition-all duration-300 transform invisible group-hover:visible z-10"
          >
            <li>
              <button
                  @click="logout"
                  class="block w-full text-left px-4 py-2 text-sm text-gray-900"
              >
                Logout
              </button>
            </li>
          </ul>
        </li>

        <li v-else>
          <router-link
              to="/login"
              class="dark:text-gray-100 text-lg font-semibold transition-colors duration-150 hover:text-blue-600"
          >
            Login
          </router-link>
        </li>
      </ul>
    </div>
  </nav>
</template>

<script setup lang="ts">
import user from "../localStorage/userStorage.ts";
import router from "../router";



const logout = () => {
  user.value = null;
  router.push('/login');
};
</script>

<style scoped>


</style>
