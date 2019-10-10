# Strategic order for BTCMarkets

`btc-strategy-order` is a `Spring boot` application to provide functionalities of strategic order for BTCMarkets.
This application consists of a number of components.
1. REST interface to provide CRUD actions for strategic order.
2. Streaming down market ticker data from BTCMarkets through websocket.
3. Process market ticker data and trigger order based on the configured strategy.
4. Place actual order to BTCMarkets.

As of 10/10/2019, `Trailing Stop Order` strategy part of 2 and 3 are implemented.