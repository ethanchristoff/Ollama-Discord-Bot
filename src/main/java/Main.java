import org.json.JSONObject;

import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) {
        final String token = System.getenv("token");
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);

        JDA jda = jdaBuilder
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(new interactionEventListener())
                .build();


        jda.upsertCommand("hello-llama","Say hello to me!").setGuildOnly(true).queue();
        // The setGuildOnly option is to sync changes locally instead of globally which tends to take longer to update the commands functionality
        jda.upsertCommand("ask-llama", "ask me something")
                .addOption(OptionType.STRING,"prompt","drop in your questions",true)
                .setGuildOnly(true)
                .queue();
        jda.upsertCommand("set-constraints", "Set limits to llama so that he doesn't say something offensive")
                .addOption(OptionType.STRING,"constraint","Make an internal prompt",true)
                .setGuildOnly(true)
                .queue();
    }
}