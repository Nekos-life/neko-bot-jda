package life.nekos.bot;

import com.github.rainestormee.jdacommand.Command;
import life.nekos.bot.commands.audio.*;
import life.nekos.bot.commands.bot.*;
import life.nekos.bot.commands.fun.*;
import life.nekos.bot.commands.guild.NsfwToggleCommand;
import life.nekos.bot.commands.guild.RecolorCommand;
import life.nekos.bot.commands.guild.RolesCommand;
import life.nekos.bot.commands.guild.SettingsCommand;
import life.nekos.bot.commands.mod.BanCommand;
import life.nekos.bot.commands.mod.KickCommand;
import life.nekos.bot.commands.neko.*;
import life.nekos.bot.commands.nsfw.AnalCommand;
import life.nekos.bot.commands.nsfw.KuniCommand;
import life.nekos.bot.commands.nsfw.PussyCommand;
import life.nekos.bot.commands.owner.CoinCommand;
import life.nekos.bot.commands.owner.EvalCommand;
import life.nekos.bot.commands.owner.SSHCommand;
import life.nekos.bot.commands.owner.SetAvatarCommand;
import life.nekos.bot.commands.user.LbCommand;
import life.nekos.bot.commands.user.ProfileCommand;
import life.nekos.bot.commands.user.ReleaseCommand;
import life.nekos.bot.commands.user.SendCommand;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Repulser https://github.com/Repulser
 */
class CommandRegistry {
    private static final Set<Command> commands = new HashSet<>();

    CommandRegistry() {
        register(
                new RecolorCommand(),
                new RolesCommand(),
                new BanCommand(),
                new KickCommand(),
                new StatsCommand(),
                // nsfw
                new AnalCommand(),
                new KuniCommand(),
                new PussyCommand(),
                // neko
                new NyaCommand(),
                new LewdSildeShowCommand(),
                new SildeShowCommand(),
                new NekoCommand(),
                new LewdCommand(),
                new FoxCommand(),
                new SumFukCommand(),
                new MemeCommand(),
                // fun
                new DidyoumeanCommand(),
                new ColorCommand(),
                new CoffeeCommand(),
                new BallCommand(),
                new WhyCommand(),
                new HugCommand(),
                new KissCommand(),
                new CuddleCommand(),
                new PatCommand(),
                new ProfileCommand(),
                new AvatarCommand(),
                new SumFukCommand(),
                new ShipCommand(),
                // user
                new SendCommand(),
                new LbCommand(),
                new ReleaseCommand(),
                new ProfileCommand(),
                // Bot
                new NsfwToggleCommand(),
                new PingCommand(),
                new SettingsCommand(),
                new HelpCommand(),
                new InviteCommand(),
                new CleanCommand(),
                // Owner
                new SSHCommand(),
                new SetAvatarCommand(),
                //new Test1Command(),
                new EvalCommand(),
                new CoinCommand(),
                // MUSIC
                new PlaylistCommand(),
                new PlayCommand(),
                new StopCommand(),
                new QueueCommand(),
                new ShuffleCommand(),
                new VolumeCommand(),
                new NowPlayingCommand(),
                new SkipCommand(),
                new BackCommand(),
                new RepeatCommand());
    }

    private void register(Command... cmds) {
        commands.addAll(Arrays.asList(cmds));
    }

    Set<Command> getCommands() {
        return commands;
    }
}
