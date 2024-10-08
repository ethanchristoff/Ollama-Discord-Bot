package events;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

class OllamaContent {
    private String prompt = " ";
    private String constraints = "The following prompt is to be a constraint you must strictly follow: ";

    public OllamaContent(String prompt, boolean constraint_state){
        if (constraint_state){
            constraints += prompt;
        }else {
            this.prompt = prompt;
        }
    }

    public String sendRequest() throws IOException {
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);  // Corrected from setOutput to setDoOutput

        // Constructing the JSON input
        String modelName = "llama3.1";// Fit the model you wish to use here.
        String jsonInputString = " ";
        if (prompt == " "){
            jsonInputString = String.format(
                    "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\": false}", modelName, constraints
            );
        }else if (constraints == " "){
            jsonInputString = String.format(
                    "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\": false}", modelName, prompt
            );
        }


        // Sending the JSON request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Getting the response code
        int code = conn.getResponseCode();
        System.out.println("Response code: " + code);

        if (code == 400){
            System.out.println("There was an issue connecting to 'http://localhost:11434/api/generate'");
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null)
            response.append(line);
        in.close();

        // Parsing the JSON response
        JSONObject jsonResponse = new JSONObject(response.toString());

        // Closes the connection
        conn.disconnect();
        return jsonResponse.getString("response");
    }
}
