import './assets/main.css'
import {createApp} from 'vue'
import {createPinia} from 'pinia'
import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config';
import 'primeicons/primeicons.css'
import {MyPreset} from "@/assets/ThemePreset.ts";

const app = createApp(App)

app.use(PrimeVue, {
  theme: {
    preset: MyPreset,
    options: {
      darkModeSelector: 'system'
    }
  }
});
app.use(createPinia())
app.use(router)

app.mount('#app')
