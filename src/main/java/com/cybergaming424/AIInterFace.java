package com.cybergaming424;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AIInterFace {
    private HttpURLConnection con;
    private ArrayList<HashMap<String, String>> memory;
    public AIInterFace() {
        memory = new ArrayList<>();
    }

    public String Generate(String prompt){
        try {
            TryToConnect();
            prompt = prompt.replaceAll("\\n+", " ");
            prompt = prompt.replaceAll("\"", "\'");
            HashMap<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            memory.add(message);
            StringBuilder json = new StringBuilder();
            json.append("{\"model\": \"solar\", \"messages\": [");
            for (HashMap<String, String> map : memory) {
                String content = map.get("content");
                content = content.replaceAll("\\n+", " ");
                content = content.replaceAll("\"", "\'");
                json.append("{\"role\": \"").append(map.get("role")).append("\", \"content\": \"").append(content).append("\"},");
            }
            json.deleteCharAt(json.length() - 1);
            json.append("], \"stream\": false, \"keep_alive\": \"10m\"}");
            System.out.println(json.toString());
            con.getOutputStream().write((json.toString()).getBytes());
            con.getOutputStream().close();

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            StringBuilder responseString = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseString.append(inputLine);
            }
            in.close();

            // Parse the JSON response
            JsonObject jsonResponse = JsonParser.parseString(responseString.toString()).getAsJsonObject();
            JsonObject specificData = jsonResponse.get("message").getAsJsonObject();
            String content = specificData.get("content").getAsString();

            System.out.println("Response Data : " + content);
            HashMap<String, String> response = new HashMap<>();
            response.put("role", "assistant");
            response.put("content", content);
            memory.add(response);
            System.out.println(response);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean TryToConnect(){
        try {
            URL url = new URL("http://127.0.0.1:11434/api/chat");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
