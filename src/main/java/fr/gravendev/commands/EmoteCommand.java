package fr.gravendev.commands;

import fr.gravendev.polls.Poll;
import fr.gravendev.polls.PollManager;
import fr.gravendev.utils.EmojiUtils;
import fr.neutronstars.nbot.api.NBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class EmoteCommand extends APollCommand {

    EmoteCommand(PollManager pollManager) {
        super(pollManager);
    }

    @Override
    protected String getName() {
        return "emote";
    }

    @Override
    protected String getUsage(String prefix) {
        return "**Changer un emoji:** `" + prefix + "poll emote <1-9> <emoji>`";
    }

    @Override
    boolean execute(Message message, User user, Poll poll, String[] args) {
        MessageChannel channel = message.getChannel();
        if (args.length != 2 || !args[0].matches("[0-9]+") || !EmojiUtils.containsEmoji(args[1])) {
            sendTemporaryMessage("Utilisation: " +
                    NBot.get().getCommandManager().getPrefix() +
                    "poll emote <1-9> <emoji>", channel);
            return false;
        }

        if (poll.isUsed(args[1])) {
            sendTemporaryMessage("Erreur: L'emoji " + args[1] + " est déjà utilisé.", channel);
            return false;
        }

        poll.setEmote(args);
        return false;
    }
}
