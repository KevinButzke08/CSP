<script setup lang="ts">
import Menubar from 'primevue/menubar';
import Image from 'primevue/image';
import krocki from '@/assets/images/Krocki.jpg'
import {ref} from "vue";

const items = ref([
  {
    label: 'Home',
    icon: 'pi pi-home',
    route: '/'
  },
  {
    label: 'Inventory',
    icon: 'pi pi-chart-line',
    route: '/'
  },
  {
    label: 'Search',
    icon: 'pi pi-search',
    route: '/'
  },
  {
    label: 'Favorites',
    icon: 'pi pi-star',
    route: '/'
  },
  {
    label: 'Account',
    icon: 'pi pi-user',
    route: '/'
  },
  {
    label: 'Feedback',
    icon: 'pi pi-comment',
    route: '/'
  },
]);

</script>

<template>
  <Menubar :model="items">
    <template #start>
      <Image :src="krocki" alt="Krocki" width="45"/>
    </template>
    <template #item="{ item }">
      <router-link :to="item.route" class="menu-link">
        <p :class="item.icon"/>
        <p>{{ item.label }}</p>
      </router-link>
    </template>
  </Menubar>
</template>

<style scoped>
.menu-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: inherit;
  text-decoration: none;
}

.menu-link:hover {
  color: #f18137;
}
</style>
