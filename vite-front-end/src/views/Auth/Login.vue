<script setup lang="ts">
import {onMounted, ref} from 'vue'
import user from '../../localStorage/userStorage.ts'
import router from "../../router";
import {createApi} from "../../AxiosInstance.ts";



const email = ref('')
const password = ref('')
const error = ref<string | null>(null)
const loading = ref(false)
const api = createApi(import.meta.env.VITE_AUTH_API_URL)

const login = async () => {
  error.value = null
  loading.value = true

  api.post<user>('/auth/login', {
    email: email.value,
    password: password.value
  })
      .then(response => {
        user.value = response.data
        router.push('/')
      })
      .catch(err => {
        console.error(err)
        if(err.message === 'Network Error') {
          error.value = 'A network error occurred. Please try again later.'
        } else
        if (err.response.status === 401) {
          error.value = 'Invalid email or password'
        } else {
          error.value = 'An error occurred. Please try again later.'
        }

      })
      .finally(() => {
        loading.value = false
      })
}

</script>

<template>
  <div class="flex min-h-full flex-col justify-center px-6 py-7 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-sm">
      <img
          class="mx-auto h-24 w-auto"
          src="https://cdn.freebiesupply.com/logos/large/2x/fontys-39-logo-svg-vector.svg"
          alt="Fontys"
      />
      <h2 class="mt-5 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
        Sign in to Fontys Air
      </h2>
    </div>

    <div class="mt-3 sm:mx-auto sm:w-full sm:max-w-sm">
      <form @submit.prevent="login" class="space-y-6">
        <div>
          <label for="email" class="block text-sm font-medium leading-6 text-gray-900">Email address</label>
          <div class="mt-2">
            <input
                id="email"
                name="email"
                v-model="email"
                type="email"
                autocomplete="email"
                required
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 p-2.5"
            />
          </div>
        </div>

        <div>
          <label for="password" class="block text-sm font-medium leading-6 text-gray-900">Password</label>
          <div class="mt-2">
            <input
                id="password"
                v-model="password"
                name="password"
                type="password"
                autocomplete="current-password"
                required
                class="p-2.5 block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
            />
          </div>
        </div>

        <div v-if="error" class="text-red-500 text-sm">
          {{ error }}
        </div>

        <div>
          <button
              type="submit"
              :disabled="loading"
              class="flex w-full justify-center rounded-md bgPrimary-color-hover bgPrimary-color px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm "
          >
            <span v-if="loading">Loading...</span>
            <span v-else>Sign in</span>
          </button>
        </div>

        <div class="flex justify-center items-center mt-4">
          <p class="text-sm text-gray-500">Don't have an account?</p>
          <router-link
              to="register"
              type="button"
              class="ml-2 text-sm font-semibold hovercolor transform transition-transform duration-200 "
          >
            Register
          </router-link>
        </div>

      </form>
    </div>
  </div>
</template>
