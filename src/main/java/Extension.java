import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.menu.BasicMenuItem;
import burp.api.montoya.ui.menu.Menu;
import burp.api.montoya.ui.menu.MenuItem;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burp.api.montoya.utilities.json.JsonObjectNode;


public class Extension implements BurpExtension {
    private static final String EXTENSION_NAME = "Bulama";
    private static final String SYSTEM_PROMPT = "You are Bulama, a cybersecurity expert that can assist the user to test a system.";
    public String MODEL_NAME;
    public String prompt;
    public int status;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        montoyaApi.extension().setName(EXTENSION_NAME);
        montoyaApi.extension().setName("Bulama");
        montoyaApi.logging().logToOutput("Bulama extension loaded");

        BasicMenuItem alertEventItem = BasicMenuItem.basicMenuItem("Raise critical alert").withAction(() -> montoyaApi.logging().raiseCriticalEvent("Alert from extension"));
        BasicMenuItem basicMenuItem = MenuItem.basicMenuItem("Unload extension");
        MenuItem unloadExtensionItem = basicMenuItem.withAction(() -> montoyaApi.extension().unload());

        Menu menu = Menu.menu(EXTENSION_NAME).withMenuItems(alertEventItem, unloadExtensionItem);

        montoyaApi.userInterface().menuBar().registerMenu(menu);

        montoyaApi.extension().registerUnloadingHandler(new MyExtensionUnloadingHandler(montoyaApi));

        UserInterface ui = new UserInterface();

        montoyaApi.userInterface().registerSuiteTab(EXTENSION_NAME, ui.getUI());

        // Now lets make request to Ollama API, use the following command in terminal to set the CORS policy (Reference: https://objectgraph.com/blog/ollama-cors/)
        // launchctl setenv OLLAMA_ORIGINS "*"
        // or
        // export OLLAMA_ORIGINS="*"

        // GET AVAILABLE MODEL
        try {
            URL url = new URL("http://localhost:11434/api/ps");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String resp;
            this.status = con.getResponseCode();
            StringBuffer responseBody = new StringBuffer();
            while ((resp = in.readLine()) != null) {
                responseBody.append(resp);
            }
            in.close();
            con.disconnect();
            String modelString = responseBody.toString();

            JsonObject root = JsonParser.parseString(modelString).getAsJsonObject();
            JsonArray modelArray = root.getAsJsonArray("models");

            for (JsonElement modelElement : modelArray){
                JsonObject modelObject = modelElement.getAsJsonObject();
                String modelName = modelObject.get("name").getAsString();
                montoyaApi.logging().logToOutput("MODEL USED: " + modelName);
                MODEL_NAME = modelName; // Set the model name to the instance variable
            }

        } catch (Exception e) {
            montoyaApi.logging().logToOutput("Error happened when fetching model, response code: " + status);
            //throw new RuntimeException(e);
        }

        ui.addSubmitListener(e ->{ 
            // Get the prompt from the UI
            String userPrompt = ui.getPrompt();
            if (userPrompt == null || userPrompt.isEmpty()) {
                montoyaApi.logging().logToOutput("Prompt is empty, please enter a valid prompt.");
                return;
            }
            montoyaApi.logging().logToOutput("User prompt: " + userPrompt);
            
            this.prompt = userPrompt;
            
            generateResponse(montoyaApi, userPrompt, ui);
        });

    }

    // GENERATE RESPOMSE

    public void generateResponse(MontoyaApi montoyaApi, String prompt, UserInterface ui) {
        try {
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{ \"model\": \"" + MODEL_NAME + "\", " +
                    "\"system\": \"" + SYSTEM_PROMPT + "\", " +
                    "\"prompt\": \"" + prompt + "\", " +
                    "\"stream\": false }";

            //Write JSON to request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            status = conn.getResponseCode();
            BufferedReader br;
            if (status == 200){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }
            else{
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                response.append(line);
            }
            br.close();
            String responseString = response.toString();
            String LMresp = JsonParser.parseString(responseString).getAsJsonObject().get("response").getAsString();
            ui.setResponse(LMresp);
            //montoyaApi.logging().logToOutput("Response code: " + status);
            //montoyaApi.logging().logToOutput("Response body: " + response.toString());
            //montoyaApi.logging().logToOutput("Response: " + response.toString());

            conn.disconnect();

        } catch (IOException e) {
            montoyaApi.logging().logToOutput("Error happened, response code: " + status);
            //throw new RuntimeException(e);
        }
    }

}