package fr.gravendev.commands;

import fr.gravendev.polls.Poll;
import fr.gravendev.polls.PollManager;
import fr.neutronstars.nbot.api.NBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class ChoiceCommand extends APollCommand {

    ChoiceCommand(PollManager pollManager) {
        super(pollManager);
    }

    @Override
    protected String getName() {
        return "choice";
    }

    @Override
    protected String getUsage(String prefix) {
        return "**Définir un choix:** `" + prefix + "poll choice <1-9> [choix]`";
    }

    @Override
    boolean execute(Message message, User user, Poll poll, String[] args) {
        if (args.length < 1 || !args[0].matches("[1-9]+")) {
            sendTemporaryMessage("Utilisation: " +
                    NBot.get().getCommandManager().getPrefix() +
                    "poll choice <1-9> [choix]", message.getChannel());
            return false;
        }

        poll.setChoice(args);
        return false;
    }
}
