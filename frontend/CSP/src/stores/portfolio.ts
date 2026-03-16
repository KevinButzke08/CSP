import {defineStore} from "pinia";
import {fetcher} from "@/utils/fetcher.ts";
import type {Portfolio} from "@/types/portfolio.ts";

interface PortfolioState {
  portfolio: Portfolio | null;
}

export const usePortfolioStore = defineStore("portfolio", {
  state(): PortfolioState {
    return {
      portfolio: null
    };
  },
  actions: {
    async getPortfolio() {
      try {
        this.portfolio = await fetcher(
          "http://localhost:8080/api/portfolio",
          "GET",
          null
        );

        console.log(this.portfolio);
        return this.portfolio;
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Unknown error occurred';
        console.error("Error while fetching portfolio: ", errorMessage);
        this.portfolio = null;
        return this.portfolio;
      }
    },
  },
});
