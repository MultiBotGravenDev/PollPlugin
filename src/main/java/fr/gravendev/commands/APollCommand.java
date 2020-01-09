package fr.gravendev.commands;

import fr.gravendev.polls.Poll;
import fr.gravendev.polls.PollManager;
import fr.neutronstars.nbot.api.command.CommandExecutor;
import fr.neutronstars.nbot.api.entity.NBotUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.TimeUnit;

public abstract class APollCommand implements CommandExecutor {

    final PollManager pollManager;

    APollCommand(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    protected abstract String getName();

    protected abstract String getUsage(String prefix);

    @Override
    public boolean onCommand(NBotUser nBotUser, Message message, String... args) {
        User user = message.getAuthor();
        Poll poll = pollManager.getPoll(user);
        if (poll == null) {
            return false;
        }

        return execute(message, user, poll, args);
    }

    abstract boolean execute(Message message, User user, Poll poll, String[] args);

    void sendTemporaryMessage(String text, MessageChannel channel) {
        channel.sendMessage(text).queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
    }

}
