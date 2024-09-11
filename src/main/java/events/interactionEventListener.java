package events;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.io.IOException;

public class interactionEventListener extends ListenerAdapter {
    private SlashCommandInteractionEvent event;
    public void output_msg_private(String value){event.reply(value).setEphemeral(true).queue();}
    public void output_msg_public(String value){event.reply(value).setEphemeral(true).queue();}

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        this.event = event;
        String slashName = event.getName();
        System.out.println("interaction made with the bot");

        switch (slashName) {
            case "hello-llama":
                User username = event.getUser();
                OllamaContent olc_hello = new OllamaContent(username+"says hello!");
                try {
                    // Defer the reply to acknowledge the command, allowing time for processing
                    event.deferReply(true).queue();

                    // Fetch the response from the Ollama API
                    String response = olc_hello.sendRequest();

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
                    // Defer the reply to acknowledge the command, allowing time for processing
                    event.deferReply(true).queue();

                    // Fetch the response from the Ollama API
                    String response = olc_ask.sendRequest();

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
