export type Portfolio = {
  id: number;
  itemList: Item[];
  currentValue: number;
  totalPurchasePrice: number;
  changePercentage: number;
}

export type Item = {
  id: number;
  name: string;
  quantity: number;
  currentPrice: number;
  purchasePrice: number;
}
