# CSP
COUNTER-STRIKE PORTFOLIO 

Project to build a CS Portfolio manager, which shows you directly what the items in your Steam inventory are worth (On 3-party sites and steam market).
-  Graphs to visualize and track your investment progress
-  Price updates on items
-  Add items
-  Sell items and track the payout

## API research:
### Steam market API:
#### GET /market/itemorderhistogram:
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
#### GET /market/priceoverview:
https://steamcommunity.com/market/priceoverview/?country=DE&currency=3&appid=730&market_hash_name=Horizon%20Case
{"success":true,"lowest_price":"1,66€","volume":"5,638","median_price":"1,63€"}
- Best API, delivers without clutter information
- But rate limited: 20 / per minute, 1000 / per day (But do we have so many requests?)
### Skinport API:
-  https://docs.skinport.com/items
-  API Key simpel durch eigenen Account, aber braucht man nicht? (Wahrscheinlich nur, wenn man Profil Requests wie Transaktionen aufgeben möchte?)
### SkinBaron API:
-  API Key nach Antrag erhältlich
## Technical Build Plan:
- Backend: Spring Boot Java
- - Dependencies: SQLite, Spring Web, HTTP Client, Reactive HTTP Client, Spring Data JPA
- Save user inputs of items: Postgres oder Embedded DB (H2)
- Frontend: Vue :3
- UI Component Framework: PrimeVue
- Cross-Platform-Desktop-App Framework: Electron
- Produce a fat jar and use jpackage to bundle it to a installer
- - Static files von Vue generieren (npm run build) in Spring Static resources kopieren und JDK bauen
 
## Class-diagramm:
<img width="790" height="330" alt="CSP Class diagram" src="documents/CSP Class diagram.png"/>


