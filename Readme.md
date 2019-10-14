# Strategic order for BTCMarkets

`btc-strategy-order` is a `Spring boot` application to provide functionalities of strategic order for BTCMarkets.
This application consists of a number of components.

1. REST interface to provide CRUD actions for strategic order.
2. Streaming down market ticker data from BTCMarkets through websocket.
3. Process market ticker data and trigger order based on the configured strategy.
4. Place actual order to BTCMarkets.

As of 14/10/2019, `Trailing Stop Order` strategy part of 1, 2 and 3 are implemented.

#### Next things to do.

1. Implementing Stop loss order logic in line with trailing logic.
2. RESTful interface for CRUD of triggering rules (with authorization).
   1. Basic functionalities for `Trailing Stop Order` are implemented.
   2. Need to add authorization.
   3. Changes on `Trailing Stop Order` rule list should be updated in `OrderProcessorManager` on runtime.
3. Placing real order to BTCMarkets as a triggering action and capture all history in database.
