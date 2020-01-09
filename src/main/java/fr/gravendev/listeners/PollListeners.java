package fr.gravendev.listeners;

import fr.gravendev.polls.PollManager;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class PollListeners extends ListenerAdapter {

    private final TextChannel verifyPolls;
    private final PollManager pollManager;

    public PollListeners(PollManager pollManager) {
        this.pollManager = pollManager;
        this.verifyPolls = pollManager.getPlugin().getVerifyPolls();
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        if (member.getUser().isBot() || !channel.getId().equals(verifyPolls.getId())) {
            return;
        }

        String messageId = event.getMessageId();
        Message message = verifyPolls.retrieveMessageById(messageId).complete();

        User sender = message.getMentionedUsers().get(0);
        String validationText = sender.getAsMention() + "\n\n";

        switch (event.getReactionEmote().getName()) {
            case "\u2705":
                validationText += ":white_check_mark: accepté ";
                this.pollManager.sendPoll(message, false);
                sendPrivateMessage(sender, "Votre sondage a été accepté");
                break;

            case "\u274C":
                validationText += ":x: refusé ";
                sendPrivateMessage(sender, "Votre sondage a été refusé");
                break;

            default:
                event.getReaction().removeReaction(event.getUser()).queue();
                return;
        }

        validationText += "par " + event.getMember().getAsMention();

        message.clearReactions().queue();
        message.editMessage(new MessageBuilder(message)
                .setContent(validationText)
                .build()).queue();
    }

    private void sendPrivateMessage(User user, String text) {
        user.openPrivateChannel()
                .queue(privateChannel ->
                        privateChannel.sendMessage(text).queue());
    }
}
