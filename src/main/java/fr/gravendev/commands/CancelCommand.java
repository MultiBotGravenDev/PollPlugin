package fr.gravendev.commands;

import fr.gravendev.polls.Poll;
import fr.gravendev.polls.PollManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class CancelCommand extends APollCommand {

    CancelCommand(PollManager pollManager) {
        super(pollManager);
    }

    @Override
    protected String getName() {
        return "cancel";
    }

    @Override
    protected String getUsage(String prefix) {
        return "**Annuler le sondage:** `" + prefix + "poll cancel`";
    }

    @Override
    boolean execute(Message message, User user, Poll poll, String[] args) {
        message.getChannel().sendMessage("Sondage annulé").queue();
        pollManager.deletePoll(user);
        return false;
    }
}
