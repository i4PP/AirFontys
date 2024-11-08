<script setup lang="ts">
import Navbar from "./components/navbar.vue";
import {useRoute} from "vue-router";
const route = useRoute()
</script>

<template>
  <navbar :user="route.meta.user" />
  <div class="text-center w-full h-fit p-8 mt-0 mb-0 pl-5 pr-5 sm:pl-36 sm:pr-36 md:pl-20 md:pr-20 lg:pl-52 lg:pr-52">
    <router-view />
  </div>
</template>

