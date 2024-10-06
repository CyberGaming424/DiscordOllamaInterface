package com.cybergaming424;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

public class ChatController extends ListenerAdapter {

    String token;
    // Mapping from GuildID to AIInterFace
    HashMap<String, AIInterFace> guildAI;

    // Constructor loads the Discord token and initializes the guildAI map
    public ChatController(){
        loadDiscordToken();
        guildAI = new HashMap<>();
        start();
    }

    // Starts the Discord bot using JDA
    public void start(){
        JDABuilder.createDefault(token)
                .addEventListeners(this)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
    }

    // Loads the Discord token from a properties file
    private void loadDiscordToken(){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(".env.local"));
            token = prop.getProperty("DISCORD_TOKEN");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Handles received messages and interacts with AIInterFace
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();

        // Check if the message starts with the "!ollama" command
        if (message.getContentRaw().startsWith("!ollama")) {
            // Check if the guild has an associated AIInterFace; if not, create one
            if(!guildAI.containsKey(event.getGuild().getId())){
                guildAI.put(event.getGuild().getId(), new AIInterFace());
            }
            // Retrieve the AIInterFace associated with the guild
            AIInterFace ai = guildAI.get(event.getGuild().getId());
            // Generate a response based on the command content
            String response = ai.Generate(message.getContentRaw().substring("!ollama".length()).trim());
            // Send the response in chunks to adhere to Discord's message size limits
            sendMessageInChunks(response, event);
        }
    }

    // Sends a message in chunks to avoid exceeding Discord's message size limit
    private void sendMessageInChunks(String message, MessageReceivedEvent event) {
        final int MAX_CHUNK_SIZE = 2000;
        int start = 0;

        while (start < message.length()) {
            int end = Math.min(start + MAX_CHUNK_SIZE, message.length());
            event.getChannel().sendMessage(message.substring(start, end)).queue();
            start = end;
        }
    }
}