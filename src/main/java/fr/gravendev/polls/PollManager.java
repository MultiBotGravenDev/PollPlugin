package fr.gravendev.polls;

import fr.gravendev.PollPlugin;
import fr.gravendev.commands.PollCommand;
import fr.gravendev.listeners.PollListeners;
import fr.gravendev.utils.EmojiUtils;
import fr.neutronstars.nbot.api.NBot;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PollManager {

    private final HashMap<String, Poll> polls;
    private final PollPlugin plugin;

    public PollManager(PollPlugin plugin) {
        this.plugin = plugin;
        this.polls = new HashMap<>();
        plugin.registerCommand("poll", "command.poll.description", new PollCommand(this), plugin.getId() + ".command.poll");
        NBot.get().getShardManager().addEventListener(new PollListeners(this));
    }

    public Poll createPoll(Member member) {
        Poll poll = new Poll();
        polls.put(member.getId(), poll);
        return poll;
    }

    public Poll getPoll(User user) {
        return polls.get(user.getId());
    }

    public void deletePoll(User user) {
        polls.remove(user.getId());
    }

    public void sendPoll(Message message, boolean verification) {
        if (verification) {
            sendForVerification(message);
            return;
        }
        MessageEmbed embed = message.getEmbeds().get(0);
        send(embed);
    }

    private void sendForVerification(Message message) {
        TextChannel channel = plugin.getVerifyPolls();
        Message pollMessage = channel.sendMessage(message).complete();
        pollMessage.addReaction("\u2705").queue();
        pollMessage.addReaction("\u274C").queue();
    }

    private void send(MessageEmbed embed) {
        TextChannel channel = plugin.getPolls();
        Message pollMessage = new MessageBuilder()
                .setEmbed(embed)
                .sendTo(channel)
                .complete();

        String description = embed.getDescription();
        for (String emotes : extractEmotes(description)) {
            pollMessage.addReaction(emotes).queue();
        }
    }

    private List<String> extractEmotes(String text) {
        List<String> emotes = new ArrayList<>();
        String[] lines = text.split("\n");
        for (String line : lines) {
            String[] words = line.split(" ");
            if (words.length == 0 || !EmojiUtils.containsEmoji(words[0])) {
                continue;
            }
            emotes.add(words[0]);
        }
        return emotes;
    }

    public Role getPollster() {
        return plugin.getPollster();
    }

    public PollPlugin getPlugin() {
        return plugin;
    }
}
