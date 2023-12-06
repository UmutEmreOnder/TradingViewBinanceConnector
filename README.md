# 1. Install Ngrok
1. Download the latest version of Ngrok from [here](https://ngrok.com/download).
2. Unzip the downloaded file.
3. Open a terminal and run the following command:
```
 sudo tar xvzf ~/Downloads/ngrok-v3-stable-linux-amd64.tgz -C /usr/local/bin
```

or you can use the following command to download and unzip the file:
```
 curl -s https://ngrok-agent.s3.amazonaws.com/ngrok.asc | sudo tee /etc/apt/trusted.gpg.d/ngrok.asc >/dev/null && echo "deb https://ngrok-agent.s3.amazonaws.com buster main" | sudo tee /etc/apt/sources.list.d/ngrok.list && sudo apt update && sudo apt install ngrok
```

# 2. Setup Ngrok
1. Create an account on [Ngrok](https://ngrok.com/).
2. Copy your Ngrok auth token from [here](https://dashboard.ngrok.com/get-started/your-authtoken).
3. Write the following command in the terminal:
```
ngrok config add-authtoken <your_auth_token>
```

# 3. Run App
1. Clone the repository.
2. Open a terminal and run the following command:
```
docker build -t tradingview-binance-connector . 
```
3. Create .env file with these contents:
```
BINANCE_API_KEY=<your_binance_api_key>
BINANCE_SECRET_KEY=<your_binance_secret_key>
BINANCE_WAIT_TIME=<wait_time_in_minutes_after_placing_order>
```
If an order is not filled within the wait time, it will be canceled.

4. Run the app with the following command:
```
docker run -d -p 80:8080 --env-file .env tradingview-binance-connector
```
TradingView only accepts port 80 for alerts. 


# 4. Run Ngrok
1. Open a terminal and run the following command, where 8080 is the port number of the app:
```
ngrok http 8080
```
2. Copy the forwarding link from the terminal and use it to access the app from anywhere

# 5. Set Alerts on TradingView
1. Open [TradingView](https://www.tradingview.com/).
2. Open the chart of the stock you want to trade.
3. Click on the alert button.
4. Set the alert to be triggered when the price is above the upper band or below the lower band, or use your strategy.
5. Set the alert to send a webhook.
6. Paste the forwarding link from Ngrok in the URL field, and add ```/alert``` at the end of the url.
7. Set the alert message to the following format:
```
{
  "symbol": "{{ticker}}",
  "position": "{{strategy.order.action}}",
  "signal_action": "{{strategy.market_position}}",
  "entry_price": "{{strategy.order.price}}",
  "take_profit": "{{plot_0}}",
  "leverage": "{{strategy.order.contracts}}",
  "position_size": x
}
```

Where x is the USD amount you want to trade with.

### About plot_0
TradingView does not allow sending the value of the limit price of the order in the alert message,
so we use the value of the first plot instead. nth plot can be accessed using ```{{plot_n}}```.



8. Click on create alert.


# How it works
When an alert is triggered, the app will place a limit order with the specified ```entry_price``` and ```leverage```.
The app will create a limit order at ```take_profit``` on the opposite side of the position.
If the entry order is not filled within the wait time, it will cancel both entry and take profit orders.

Binance API does not support stop loss, so for stop loss, it has to wait an alert from TradingView also.
When the ```signal_action``` is equal to ```flat```,
it will close all the open positions for that symbol with market orders and cancel all limit orders.
Take profit can be set via limit order, so before that alert the order will be filled, probably.
If not, it will be filled with market order again like in stop loss.