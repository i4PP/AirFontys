<script setup lang="ts">
import Navbar from "./components/Navbar.vue";
import {useRoute} from "vue-router";
const route = useRoute()
</script>

<template>
  <navbar v-if="!route?.meta?.hideNav" />
  <div class="w-full h-fit p-8 mt-0 mb-0 pl-3 pr-3 sm:pl-16 sm:pr-16 md:pl-28 md:pr-28 lg:pl-56 lg:pr-56">
    <router-view />
  </div>
</template>

