package fr.gravendev;

import fr.gravendev.polls.PollManager;
import fr.neutronstars.nbot.api.NBot;
import fr.neutronstars.nbot.api.event.Listener;
import fr.neutronstars.nbot.api.event.server.NBotServerStartedEvent;
import fr.neutronstars.nbot.api.event.server.NBotServerStartingEvent;
import fr.neutronstars.nbot.api.plugin.NBotPlugin;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.Arrays;

public class PollPlugin extends NBotPlugin {

    private Guild guild;
    private TextChannel polls;
    private TextChannel verifyPolls;
    private Role pollster;

    public PollPlugin() {
        super("poll_plugin", "Poll Plugin", "1.0", "", "Nolan");
    }

    @Listener
    public void onStarting(NBotServerStartingEvent event) {
        super.getLogger().info("Starting of " + super.getName() + "...");
    }

    @Listener
    public void onStarted(NBotServerStartedEvent event) {
        ShardManager shardManager = NBot.get().getShardManager();
        initGuild(shardManager);
        this.polls = getChannel("polls");
        this.verifyPolls = getChannel("verifyPolls");
        this.pollster = getPollsterRole();
        new PollManager(this);
    }

    private void initGuild(ShardManager shardManager) {
        String guildIdentifier = getConfiguration("guild");
        this.guild = shardManager.getGuildById(guildIdentifier);
        if (this.guild == null) {
            getLogger().error("Guild " + guildIdentifier + " not found !");
        }
    }

    private Role getPollsterRole() {
        String identifier = getConfiguration("roles", "pollster");
        Role role = this.guild.getRoleById(identifier);
        if (role == null) {
            getLogger().error("Role " + "pollster" + " not found ! (" + identifier + ")");
        }
        return role;
    }

    private TextChannel getChannel(String keyChannel) {
        String identifier = getConfiguration("channels", keyChannel);
        TextChannel channel = this.guild.getTextChannelById(identifier);
        if (channel == null) {
            getLogger().error("Channel " + keyChannel + " not found ! (" + identifier + ")");
        }
        return channel;
    }

    private String getConfiguration(String... nodes) {
        String snowflake = super.getConfiguration().getOrSet("", nodes);
        if (snowflake.length() > 20 || !Helpers.isNumeric(snowflake)) {
            getLogger().error(Arrays.toString(nodes) + " is not a valid snowflake.");
        }
        return snowflake;
    }

    public TextChannel getPolls() {
        return polls;
    }

    public TextChannel getVerifyPolls() {
        return verifyPolls;
    }

    public Role getPollster() {
        return pollster;
    }
}
