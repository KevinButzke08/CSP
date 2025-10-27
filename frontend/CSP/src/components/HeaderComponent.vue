<script setup lang="ts">
import Menubar from 'primevue/menubar';
import Image from 'primevue/image';
import krocki from '@/assets/images/Krocki.jpg'
import {ref} from "vue";

const items = ref([
  {
    label: 'Home',
    icon: 'pi pi-home'
  },
  {
    label: 'Inventory',
    icon: 'pi pi-chart-line'
  },
  {
    label: 'Search',
    icon: 'pi pi-search'
  },
  {
    label: 'Favorites',
    icon: 'pi pi-star'
  },
  {
    label: 'Account',
    icon: 'pi pi-user'
  },
  {
    label: 'Feedback',
    icon: 'pi pi-comment'
  },
]);

</script>

<template>
  <Menubar :model="items">
    <template #start>
      <Image :src="krocki" alt="Krocki" width="45"/>
    </template>
  </Menubar>

</template>

<style scoped>
</style>
