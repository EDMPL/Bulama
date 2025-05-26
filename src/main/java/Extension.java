import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.menu.BasicMenuItem;
import burp.api.montoya.ui.menu.Menu;
import burp.api.montoya.ui.menu.MenuItem;
import org.json.JSONObject;

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


public class Extension implements BurpExtension {
    private static final String EXTENSION_NAME = "Bulama";
    private static final String SYSTEM_PROMPT = "You are Bulama, a cybersecurity expert that can assist the user to test a system";
    public String MODEL_NAME;
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
            StringBuffer responseBody = new StringBuffer();
            while ((resp = in.readLine()) != null) {
                responseBody.append(resp);
            }
            in.close();
            con.disconnect();
            montoyaApi.logging().logToOutput("Response from server: " + responseBody.toString());

        } catch (Exception e) {
            montoyaApi.logging().logToOutput("Error happened, response code: " + status);
            //throw new RuntimeException(e);
        }


        // GENERATE RESPOMSE
        try {
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{ \"model\": \"llama3.2\", " +
                    "\"system\": \"" + SYSTEM_PROMPT + "\", " +
                    "\"prompt\": \"Hi, are you ready?\", " +
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
            montoyaApi.logging().logToOutput("Response: " + response.toString());

            conn.disconnect();

        } catch (IOException e) {
            montoyaApi.logging().logToOutput("Error happened, response code: " + status);
            //throw new RuntimeException(e);
        }

    }

}