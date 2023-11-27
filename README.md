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
docker run -d -p 8080:8080 --env-file .env tradingview-binance-connector
```

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
  "leverage": "{{strategy.order.contracts}}",
  "position_size": x
}
```
Where x is the USD amount you want to trade with.

8. Click on create alert.
