<script setup lang="ts">
import Card from 'primevue/card';
import Chart from 'primevue/chart';
import Image from 'primevue/image';
import {ref, onMounted} from "vue";
import krocki from '@/assets/images/Krocki.jpg'

onMounted(() => {
  chartData.value = setChartData();
  chartOptions.value = setChartOptions();
});

const chartData = ref();
const chartOptions = ref();

const setChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement);

  return {
    labels: ['June', 'July', 'August', 'September', 'October', 'November'],
    datasets: [
      {
        label: 'Inventory Value',
        data: [65, 59, 80, 81, 56, 100],
        fill: false,
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        tension: 0.4
      }
    ]
  };
};
const setChartOptions = () => {
  const documentStyle = getComputedStyle(document.documentElement);
  const textColor = documentStyle.getPropertyValue('--p-text-color');
  const textColorSecondary = documentStyle.getPropertyValue('--p-text-muted-color');
  const surfaceBorder = documentStyle.getPropertyValue('--p-content-border-color');

  return {
    maintainAspectRatio: false,
    aspectRatio: 0.6,
    plugins: {
      legend: {
        labels: {
          color: textColor
        }
      }
    },
    scales: {
      x: {
        ticks: {
          color: textColorSecondary
        },
        grid: {
          color: surfaceBorder
        }
      },
      y: {
        ticks: {
          color: textColorSecondary
        },
        grid: {
          color: surfaceBorder
        }
      }
    }
  };
}
</script>

<template>
  <main class="flexContainer">
    <Card class="portfolio">
      <template #title>Total Portfolio Value: 100 Euros</template>
      <template #content>
        <Chart type="line" :data="chartData" :options="chartOptions" class="portfolio-chart"/>
      </template>
    </Card>

    <Card style="width: 25rem; overflow: hidden" class="item">
      <template #title>Most Profitable Item</template>
      <template #content>
        <section class="item-container">
          <Image :src="krocki" alt="Krocki" width="200"/>
          <h3>Item XY With Value 167 Euros</h3>
        </section>
      </template>
    </Card>
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

.portfolio-chart {
  margin-top: 20px
}
</style>
