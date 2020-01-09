package fr.gravendev.commands;

import fr.gravendev.polls.Poll;
import fr.gravendev.polls.PollManager;
import fr.neutronstars.nbot.api.NBot;
import fr.neutronstars.nbot.api.command.CommandExecutor;
import fr.neutronstars.nbot.api.entity.NBotUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PollCommand implements CommandExecutor {

    private final PollManager manager;
    private final List<APollCommand> subCommands;
    private final String usage;

    public PollCommand(PollManager pollManager) {
        this.manager = pollManager;
        this.subCommands = Arrays.asList(
                new AskCommand(pollManager),
                new ColorCommand(pollManager),
                new ChoiceCommand(pollManager),
                new EmoteCommand(pollManager),
                new CancelCommand(pollManager),
                new EndCommand(pollManager));
        String prefix = NBot.get().getCommandManager().getPrefix();
        this.usage = buildUsage(prefix);
    }

    @Override
    public boolean onCommand(NBotUser nBotUser, Message message, String... args) {
        Member member = message.getMember();
        if (member == null) {
            return executeSubCommand(nBotUser, message, args);
        }

        TextChannel channel = message.getTextChannel();
        boolean hasPollster = member.getRoles().stream().anyMatch(role -> role.equals(manager.getPollster()));
        if (!hasPollster && !member.hasPermission(Permission.ADMINISTRATOR)) {
            channel.sendMessage("Vous n'avez pas la permission de créer un sondage !")
                    .queueAfter(3000, TimeUnit.MILLISECONDS,
                            sentMessage -> sentMessage.delete().queue());
            return false;
        }

        User user = member.getUser();

        channel.sendMessage(user.getAsMention() + " >> Regardez vos messages privés").queue();
        user.openPrivateChannel().queue(
                privateChannel -> startPoll(member, privateChannel),
                error -> channel.sendMessage("Impossible de vous envoyer un message, vos MP sont désactivés !").queue());

        return false;
    }

    private boolean executeSubCommand(NBotUser nBotUser, Message message, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String subCommand = args[0];
        Optional<APollCommand> commandOptional = subCommands.stream().filter(command -> command.getName().equalsIgnoreCase(subCommand)).findFirst();
        if (!commandOptional.isPresent()) {
            return false;
        }

        CommandExecutor command = commandOptional.get();
        return command.onCommand(nBotUser, message, Arrays.copyOfRange(args, 1, args.length));
    }

    private void startPoll(Member member, PrivateChannel privateChannel) {
        Poll poll = manager.createPoll(member);
        User user = member.getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.GRAY)
                .setFooter(user.getName(), user.getEffectiveAvatarUrl());

        MessageBuilder messageBuilder = new MessageBuilder()
                .setContent(user.getAsMention())
                .setEmbed(embedBuilder.build());

        messageBuilder.sendTo(privateChannel).queue(poll::setMessage);
        privateChannel.sendMessage(this.usage).queue();
    }

    private String buildUsage(String prefix) {
        return this.subCommands
                .stream()
                .map(subCommand -> subCommand.getUsage(prefix))
                .collect(Collectors.joining("\n"));
    }
}
