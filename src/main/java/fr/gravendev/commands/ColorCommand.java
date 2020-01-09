package fr.gravendev.commands;

import fr.gravendev.polls.Poll;
import fr.gravendev.polls.PollManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class ColorCommand extends APollCommand {

    ColorCommand(PollManager pollManager) {
        super(pollManager);
    }

    @Override
    protected String getName() {
        return "color";
    }

    @Override
    protected String getUsage(String prefix) {
        return "**Changer la couleur:** `" + prefix + "poll color <" + Poll.COLORS + ">`";
    }

    @Override
    boolean execute(Message message, User user, Poll poll, String[] args) {
        if (args.length == 0) {
            sendTemporaryMessage("Erreur: Insérer une couleur", message.getChannel());
            return false;
        }

        Color color = getColorByName(args[0]);
        if (color == null) {
            sendTemporaryMessage("Erreur: Couleur incorrecte", message.getChannel());
            return false;
        }

        poll.setColor(color);
        return false;
    }

    private Color getColorByName(String name) {
        try {
            return (Color) Color.class.getField(name.toUpperCase()).get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
