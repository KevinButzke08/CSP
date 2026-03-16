<script setup lang="ts">
import Card from 'primevue/card';
import Image from 'primevue/image';
import {onMounted} from "vue";
import krocki from '@/assets/images/Krocki.jpg'
import {usePortfolioStore} from "@/stores/portfolio.ts";

onMounted(() => {
  portfolioStore.getPortfolio();
});

const portfolioStore = usePortfolioStore();
</script>

<template>
  <main class="flexContainer" v-if="portfolioStore.portfolio">
    <Card class="portfolio">
      <template #title>Current Portfolio Value: {{ portfolioStore.portfolio.currentValue }} €
      </template>
      <template #content>
        <ul>
          <li>Amount Of Items: {{ portfolioStore.portfolio.itemList.length }}</li>
        </ul>
        <ul>
          <li>Total Purchase Price: {{ portfolioStore.portfolio.totalPurchasePrice }} €</li>
        </ul>
        <ul>
          <li>Change Percentage: {{ portfolioStore.portfolio.changePercentage }} %</li>
        </ul>
      </template>
    </Card>

    <Card style="width: 25rem; overflow: hidden" class="item">
      <template #title>Most Profitable Item (Placeholder)</template>
      <template #content>
        <section class="item-container">
          <Image :src="krocki" alt="Krocki" width="200"/>
          <h3>Item XY With Value 167 Euros</h3>
        </section>
      </template>
    </Card>
  </main>
  <main v-else>
    <p>No Portfolio found.</p>
  </main>
</template>

<style scoped>
.flexContainer {
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  align-items: start;
  gap: 20px;
  margin-top: 50px;
  margin-right: 20px;
  margin-left: 20px;
}

.portfolio {
  flex: 2;
  margin-left: 10px;
}

.item {
  flex: 1;
  margin-right: 10px;
}

.item-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: 20px
}
</style>
