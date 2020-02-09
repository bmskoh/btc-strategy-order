# Strategic order for BTCMarkets

**Under refactoring as of 04/02/2020**
**REST controller will only have access to IStrategyOrderService so retrieving/updating from/to database**
**and updating triggering rules in memory can be done together in thread safe manner**

`btc-strategy-order` is a `Spring boot` application to provide strategic order functionalities using BTCMarket's market ticker data.
This application consists of a number of components.

1. REST interface to provide CRUD actions for strategic order.
2. Streaming down market ticker data from BTCMarkets through websocket.
3. Process market ticker data and determine if triggering order is required.
4. Place order to BTCMarkets when the triggering conditions met.

As of 14/10/2019, `Trailing Stop Order` strategy part of 1, 2 and 3 are implemented.

#### Next things to do.

1. Implementing Stop loss order logic in line with trailing logic.
2. RESTful interface for CRUD of triggering rules (with authorization).
   1. Basic functionalities for `Trailing Stop Order` are implemented.
   2. Need to add authorization.
   3. Changes on `Trailing Stop Order` rule list should be updated in `OrderProcessorManager` on runtime.
3. Placing real order to BTCMarkets as a triggering action and capture all history in database.
