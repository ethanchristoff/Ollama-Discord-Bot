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
    private final String prompt;

    public OllamaContent(String prompt) {
        this.prompt = prompt;
    }

    public String sendRequest() throws IOException {
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);  // Corrected from setOutput to setDoOutput

        // Constructing the JSON input
        String modelName = "ollama3.1";
        String jsonInputString = String.format(
                "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\": false}", modelName, prompt
        );

        // Sending the JSON request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Getting the response code
        int code = conn.getResponseCode();
        System.out.println("Response code: " + code);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null)
            response.append(line);
        in.close();

        // Parsing the JSON response
        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("response");
    }
}
