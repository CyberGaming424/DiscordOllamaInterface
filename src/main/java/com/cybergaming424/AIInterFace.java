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

    // Constructor initializes the memory as an ArrayList of HashMaps
    public AIInterFace() {
        memory = new ArrayList<>();
    }

    /**
     * Generates a response based on the provided prompt.
     * @param prompt The input string provided by the user.
     * @return The response content from the AI model or null if an exception occurs.
     */
    public String Generate(String prompt){
        try {
            TryToConnect();

            // Sanitize the prompt by replacing new lines and quotes
            prompt = prompt.replaceAll("\\n+", " ");
            prompt = prompt.replaceAll("\"", "\'");

            // Create a user message and add it to the memory
            HashMap<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            memory.add(message);

            // Build the JSON request for the API
            StringBuilder json = new StringBuilder();
            json.append("{\"model\": \"solar\", \"messages\": [");
            for (HashMap<String, String> map : memory) {
                String content = map.get("content");
                content = content.replaceAll("\\n+", " ");
                content = content.replaceAll("\"", "\'");
                json.append("{\"role\": \"").append(map.get("role")).append("\", \"content\": \"").append(content).append("\"},");
            }
            json.deleteCharAt(json.length() - 1);  // Remove the last comma
            json.append("], \"stream\": false, \"keep_alive\": \"10m\"}");

            // Send the request to the API
            con.getOutputStream().write((json.toString()).getBytes());
            con.getOutputStream().close();

            // Get the response code from the API call
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            // Read the response from the API
            StringBuilder responseString = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseString.append(inputLine);
            }
            in.close();

            // Parse the JSON response and extract the content
            JsonObject jsonResponse = JsonParser.parseString(responseString.toString()).getAsJsonObject();
            JsonObject specificData = jsonResponse.get("message").getAsJsonObject();
            String content = specificData.get("content").getAsString();

            // Create the assistant's message and add it to the memory
            HashMap<String, String> response = new HashMap<>();
            response.put("role", "assistant");
            response.put("content", content);
            memory.add(response);

            return content;
        } catch (Exception e) {
            // Print stack trace for debugging and return null if an exception occurs
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Attempts to establish a connection to the AI service.
     * @return True if the connection was successfully established, false otherwise.
     */
    private boolean TryToConnect(){
        try {
            // Set up the connection to the local AI service
            URL url = new URL("http://127.0.0.1:11434/api/chat");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
        }catch (Exception e){
            return false;  // Return false if connection setup fails
        }
        return true;  // Return true if connection is successful
    }
}