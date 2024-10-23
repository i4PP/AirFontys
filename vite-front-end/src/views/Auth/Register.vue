<script setup lang="ts">
import { ref, computed } from 'vue'
import user from '../../localStorage/userStorage.ts'
import router from '../../router'
import {createApi} from "../../AxiosInstance.ts";

// Form fields
const firstName = ref('')
const lastName = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const api = createApi(import.meta.env.VITE_AUTH_API_URL)

// Error and loading states
const error = ref<string | null>(null)
const loading = ref(false)

// Helper function to validate password strength
const isValidPassword = (pwd: string): boolean => {
  const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/
  return regex.test(pwd)
}

// Computed property to check the validity of the password live
const passwordValidationMessage = computed(() => {
  if (password.value.length === 0) return ''
  return isValidPassword(password.value)
      ? 'Strong password'
      : 'Password must contain at least 8 characters, 1 uppercase, 1 lowercase, 1 number, and 1 special character.'
})

// Computed property to check if passwords match live
const doPasswordsMatch = computed(() => {
  if (confirmPassword.value.length === 0) return ''
  return password.value === confirmPassword.value ? 'Passwords match' : 'Passwords do not match'
})

// Registration logic
const register = async () => {
  error.value = null
  loading.value = true

  // Final password checks before submission
  if (!isValidPassword(password.value)) {
    error.value = 'Password does not meet the required strength.'
    loading.value = false
    return
  }

  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match.'
    loading.value = false
    return
  }

  api.post<user>('/auth/register', {
    firstName: firstName.value,
    lastName: lastName.value,
    email: email.value,
    password: password.value
  })
      .then(response => {
        user.value = response.data
        router.push('/')
      })
      .catch(err => {
        console.error(err)
        if (err.response.status === 409) {
          error.value = 'User with this email already exists.'
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
  <div class="flex flex-col justify-center px-6 py-7 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-sm">
      <img
          class="mx-auto h-24 w-auto"
          src="https://cdn.freebiesupply.com/logos/large/2x/fontys-39-logo-svg-vector.svg"
          alt="Fontys"
      />
      <h2 class="mt-5 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
        Register to Fontys Air
      </h2>
    </div>

    <div class="mt-3 sm:mx-auto sm:w-full sm:max-w-sm">
      <form @submit.prevent="register" class="space-y-6">
        <!-- First Name Input -->
        <div class="flex-row flex items-end w-full">
          <div class="w-full pr-3">
            <label for="firstname" class="block text-sm font-medium leading-6 text-gray-900">First name</label>
            <div class="mt-2">
              <input
                  v-model="firstName"
                  type="text"
                  required
                  class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 p-2.5"
              />
            </div>
          </div>
          <!-- Last Name Input -->
          <div class="w-full">
            <label for="lastname" class="block text-sm font-medium leading-6 text-gray-900">Last name</label>
            <div class="mt-1">
              <input
                  v-model="lastName"
                  type="text"
                  required
                  class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 p-2.5"
              />
            </div>
          </div>
        </div>

        <!-- Email Input -->
        <div>
          <label for="email" class="block text-sm font-medium leading-6 text-gray-900">Email address</label>
          <div class="mt-1">
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

        <!-- Password Input with Live Validation -->
        <div>
          <label for="password" class="block text-sm font-medium leading-6 text-gray-900">Password</label>
          <div class="mt-1">
            <input
                v-model="password"
                name="password"
                type="password"
                required
                class="p-2.5 block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
            />
          </div>
          <p class="text-sm text-gray-600">{{ passwordValidationMessage }}</p>
        </div>

        <!-- Confirm Password Input with Live Validation -->
        <div>
          <label for="confirmPassword" class="block text-sm font-medium leading-6 text-gray-900">Confirm password</label>
          <div class="mt-1">
            <input
                v-model="confirmPassword"
                type="password"
                required
                class="p-2.5 block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
            />
          </div>
          <p class="text-sm text-gray-600">{{ doPasswordsMatch }}</p>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="text-red-500 text-sm">
          {{ error }}
        </div>

        <!-- Submit Button -->
        <div>
          <button
              type="submit"
              :disabled="loading"
              class="flex w-full justify-center rounded-md bgPrimary-color-hover bgPrimary-color px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm "
          >
            <span v-if="loading">Loading...</span>
            <span v-else>Register</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
