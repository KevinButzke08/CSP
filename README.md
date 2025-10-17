# CSP
COUNTER-STRIKE PORTFOLIO 

Project to build a CS Portfolio manager, which shows you directly what the items in your Steam inventory are worth (On 3-party sites and steam market).
-  Graphs to visualize and track your investment progress
-  Price updates on items
-  Add items
-  Sell items and track the payout

## API FORSCHUNG:

Horizon case API example: https://steamcommunity.com/market/itemordershistogram?country=DE&language=german&currency=1&item_nameid=175999886
`{
  "success": 1,
  "sell_order_table": "<table class=\"market_commodity_orders_table\"><tr><th align=\"right\">Preis</th><th align=\"right\">Anzahl</th></tr><tr><td align=\"right\" class=\"\">$2.41</td><td align=\"right\">350</td></tr><tr><td align=\"right\" class=\"\">$2.42</td><td align=\"right\">616</td></tr><tr><td align=\"right\" class=\"\">$2.43</td><td align=\"right\">368</td></tr><tr><td align=\"right\" class=\"\">$2.44</td><td align=\"right\">702</td></tr><tr><td align=\"right\" class=\"\">$2.45</td><td align=\"right\">168</td></tr><tr><td align=\"right\" class=\"\">$2.46 oder mehr</td><td align=\"right\">19829</td></tr></table>",
  "sell_order_summary": "<span class=\"market_commodity_orders_header_promote\">22033</span> stehen ab <span class=\"market_commodity_orders_header_promote\">$2.41</span> zum Verkauf",
  "buy_order_table": "<table class=\"market_commodity_orders_table\"><tr><th align=\"right\">Preis</th><th align=\"right\">Anzahl</th></tr><tr><td align=\"right\" class=\"\">$2.32</td><td align=\"right\">18</td></tr><tr><td align=\"right\" class=\"\">$2.12</td><td align=\"right\">29</td></tr><tr><td align=\"right\" class=\"\">$2.11</td><td align=\"right\">478</td></tr><tr><td align=\"right\" class=\"\">$2.10</td><td align=\"right\">802</td></tr><tr><td align=\"right\" class=\"\">$2.09</td><td align=\"right\">169</td></tr><tr><td align=\"right\" class=\"\">$2.08 oder weniger</td><td align=\"right\">1412625</td></tr></table>",
  "buy_order_summary": "<span class=\"market_commodity_orders_header_promote\">1414121</span> Kaufaufträge für <span class=\"market_commodity_orders_header_promote\">$2.32</span> oder weniger",
  **"highest_buy_order": "232",**
  **"lowest_sell_order": "241",**
  "buy_order_graph": [
    [
      2.32,
      18,
      "18 Kaufaufträge für $2.32 oder mehr"
    ],
    ...
  ],
  "graph_max_y": 200000,
  "graph_min_x": 0.97,
  "graph_max_x": 3.56,
  "price_prefix": "$",
  "price_suffix": ""
}`

- Nice, because currency can be easily changed
- Rate limits are still to be tested

