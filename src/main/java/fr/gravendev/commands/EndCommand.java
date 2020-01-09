package fr.gravendev.commands;

import fr.gravendev.polls.Poll;
import fr.gravendev.polls.PollManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public class EndCommand extends APollCommand {

    EndCommand(PollManager pollManager) {
        super(pollManager);
    }

    @Override
    protected String getName() {
        return "end";
    }

    @Override
    protected String getUsage(String prefix) {
        return "**Terminer un sondage:** `" + prefix + "poll end` (2 choix minimums)";
    }

    @Override
    boolean execute(Message message, User user, Poll poll, String[] args) {
        MessageChannel channel = message.getChannel();
        if (poll.getQuestion().replace(" ", "").isEmpty()) {
            sendTemporaryMessage("Erreur: La question ne peut pas être vide.", channel);
            return false;
        }

        if (poll.getAnswers().size() < 2) {
            sendTemporaryMessage("Erreur: 2 choix doivent au moins être présents.", channel);
            return false;
        }

        Message pollMessage = poll.getMessage();
        PrivateChannel privateChannel = pollMessage.getPrivateChannel();
        pollMessage = privateChannel.retrieveMessageById(pollMessage.getId()).complete();

        pollManager.sendPoll(pollMessage, true);
        pollManager.deletePoll(user);

        channel.sendMessage("Sondage envoyé !").queue();
        return false;
    }
}
