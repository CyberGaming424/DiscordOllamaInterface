# ChatAI

## Ollama Setup

1. Download ollama from https://ollama.com/
2. So long as the ollama instance is running on the same machine as the bot you will be able to use it.

## Discord Setup

1. Create a discord bot and get the token.
2. Rename the file named `.env.example` in the root directory of the project to .env.local and add the following line:

```
DISCORD_TOKEN=<your-discord-bot-token>
```
3. Install the discord bot in your server.

4. Run the project with `mvn exec:java`.

## Usage

To use the bot, simply type `!ollama` followed by your question. The bot will respond with the answer.

## Limitations

The bot is currently limited to 2000 characters per message. This is due to the limitations of the discord API.

## Contributing

Contributions are welcome! If you have any suggestions or improvements, please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.