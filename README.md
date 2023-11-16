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
ngrok config add-auth <your_auth_token>
```

# 3. Run App
1. Clone the repository.
2. Open it in your IDE.
3. Run the app.

# 4. Run Ngrok
1. Open a terminal and run the following command, where 8080 is the port number of the app:
```
ngrok http 8080
```
2. Copy the forwarding link from the terminal and use it to access the app from anywhere


