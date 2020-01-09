package fr.gravendev.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Poll {

    public static final String COLORS = Arrays.stream(Color.class.getFields())
            .filter(field -> field.getDeclaringClass().equals(Color.class))
            .map(field -> field.getName().toUpperCase().replace("_", ""))
            .distinct()
            .collect(Collectors.joining(" | "));

    private static final String[] EMOTES = {"1\u20E3", "2\u20E3", "3\u20E3", "4\u20E3", "5\u20E3", "6\u20E3", "7\u20E3", "8\u20E3", "9\u20E3"};

    private final Map<Integer, String> answers;
    private final Map<Integer, String> emotes;
    private Color color;
    private String question;
    private Message message;

    Poll() {
        this.color = Color.GRAY;
        this.question = " ";
        this.answers = new TreeMap<>();
        this.emotes = new HashMap<>();
        this.message = null;
    }

    public Map<Integer, String> getAnswers() {
        return answers;
    }

    public String getQuestion() {
        return question;
    }

    public Message getMessage() {
        return message;
    }

    public void setColor(Color color) {
        this.color = color;
        update();
    }

    public void setQuestion(String question) {
        this.question = question.isEmpty() ? " " : question;
        update();
    }

    public void setChoice(String[] args) {
        int choiceIndex = Integer.parseInt(args[0]);
        if (args.length == 1) {
            this.answers.remove(choiceIndex);
            this.emotes.remove(choiceIndex);
            update();
            return;
        }

        String[] choiceWords = Arrays.copyOfRange(args, 1, args.length);
        String choice = String.join(" ", choiceWords);

        this.answers.put(choiceIndex, choice);
        if (!emotes.containsKey(choiceIndex)) {
            this.emotes.put(choiceIndex, EMOTES[choiceIndex - 1]);
        }
        update();
    }

    public void setEmote(String[] args) {
        if (args.length < 2) {
            return;
        }

        int emoteIndex = Integer.parseInt(args[0]);
        if (!this.emotes.containsKey(emoteIndex)) {
            return;
        }

        this.emotes.put(emoteIndex, args[1]);
        update();
    }

    public boolean isUsed(String emote) {
        return this.emotes.containsValue(emote);
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    private void update() {

        MessageEmbed messageEmbed = this.message.getEmbeds().get(0);
        EmbedBuilder embedBuilder = new EmbedBuilder(messageEmbed)
                .setTitle(this.question)
                .setColor(this.color)
                .setDescription(buildDescription());
        this.message.editMessage(embedBuilder.build()).queue();
    }

    private String buildDescription() {
        StringBuilder builder = new StringBuilder();
        this.answers.forEach((index, answer) -> builder.append(this.emotes.get(index))
                .append(" ")
                .append(answer)
                .append("\n\n"));
        return builder.toString();
    }

}
