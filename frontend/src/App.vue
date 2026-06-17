<script setup lang="ts">
import {RouterView} from 'vue-router'
import HeaderComponent from "@/components/HeaderComponent.vue";

</script>

<template>
  <header>
   <HeaderComponent></HeaderComponent>
  </header>
  <RouterView/>
</template>

<style scoped>
</style>
