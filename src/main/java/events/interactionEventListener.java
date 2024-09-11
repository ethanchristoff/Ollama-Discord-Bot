package events;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class interactionEventListener extends ListenerAdapter {
    private SlashCommandInteractionEvent event;

    public void output_msg_private(String value) {
        event.reply(value).setEphemeral(true).queue();
    }

    public void output_msg_public(String value) {
        event.reply(value).setEphemeral(true).queue();
    }

    // Log interactions to a text file
    private void logInteraction(String username, String prompt, String response) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("interaction_log.txt", true))) {
            writer.write("User: " + username + "\n");
            writer.write("Prompt: " + prompt + "\n");
            writer.write("Response: " + response + "\n");
            writer.write("-----------------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        this.event = event;
        String slashName = event.getName();
        System.out.println("Interaction made with the bot");

        switch (slashName) {
            case "hello-llama":
                User user = event.getUser();
                OllamaContent olc_hello = new OllamaContent(user.getName() + " says hello!");
                try {
                    // Defer the reply to acknowledge the command, allowing time for processing
                    event.deferReply(true).queue();

                    // Fetch the response from the Ollama API
                    String response = olc_hello.sendRequest();

                    // Log interaction to the file
                    logInteraction(user.getName(), "hello-llama", response);

                    // Edit the original message with the API response
                    event.getHook().editOriginal(response).queue();
                } catch (IOException e) {
                    event.getHook().editOriginal("Failed to get a response from Ollama.").queue();
                    e.printStackTrace();
                }
                break;

            case "ask-llama":
                String prompt = Objects.requireNonNull(event.getOption("prompt")).getAsString();
                OllamaContent olc_ask = new OllamaContent(prompt);
                try {
                    User username = event.getUser();

                    // Defer the reply to acknowledge the command, allowing time for processing
                    event.deferReply(true).queue();

                    // Fetch the response from the Ollama API
                    String response = olc_ask.sendRequest();

                    // Log interaction to the file
                    logInteraction(username.getName(), prompt, response);

                    // Edit the original message with the API response
                    event.getHook().editOriginal(response).queue();
                } catch (IOException e) {
                    event.getHook().editOriginal("Failed to get a response from Ollama.").queue();
                    e.printStackTrace();
                }
                break;
        }
    }
}
