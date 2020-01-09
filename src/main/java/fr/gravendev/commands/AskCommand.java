package fr.gravendev.commands;

import fr.gravendev.polls.Poll;
import fr.gravendev.polls.PollManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class AskCommand extends APollCommand {

    AskCommand(PollManager pollManager) {
        super(pollManager);
    }

    @Override
    protected String getName() {
        return "ask";
    }

    @Override
    protected String getUsage(String prefix) {
        return "**Changer la question:** `" + prefix + "poll ask <question>`";
    }

    @Override
    protected boolean execute(Message message, User user, Poll poll, String[] args) {
        poll.setQuestion(String.join(" ", args));
        return false;
    }
}
