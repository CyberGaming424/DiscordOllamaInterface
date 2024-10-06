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
    // GuildID -> AI
    HashMap<String, AIInterFace> guildAI;
    public ChatController(){
        loadDiscordToken();
        guildAI = new HashMap<>();
        start();
    }

    public void start(){
        JDABuilder.createDefault(token)
                .addEventListeners(this)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
    }

    private void loadDiscordToken(){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(".env.local"));
            token = prop.getProperty("DISCORD_TOKEN");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();

        if (message.getContentRaw().startsWith("!ollama")) {
            if(!guildAI.containsKey(event.getGuild().getId())){
                guildAI.put(event.getGuild().getId(), new AIInterFace());
            }
            AIInterFace ai = guildAI.get(event.getGuild().getId());
            String response = ai.Generate(message.getContentRaw().substring("!ollama".length()).trim());
            sendMessageInChunks(response, event);
        }
    }

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
