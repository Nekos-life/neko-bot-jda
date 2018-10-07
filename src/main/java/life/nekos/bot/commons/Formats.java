package life.nekos.bot.commons;

import com.google.common.base.CharMatcher;
import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.checks.BotChecks;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.text.MessageFormat;
import java.util.Random;

import static life.nekos.bot.commons.Misc.now;

/**
 * Created by Tom on 10/5/2017.
 */
public class Formats {
    public static final String httpRx =
            "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)";
    private static final String[] CATS = {
            "｡＾･ｪ･＾｡",
            "( ͒ ु- •̫̮ – ू ͒)",
            "( ^..^)ﾉ",
            "( =①ω①=)",
            "( =ω=)..nyaa",
            "( =ノωヽ=)",
            "(´; ω ;｀)",
            "(^-人-^)",
            "(^･o･^)ﾉ”",
            "(^・ω・^ )",
            "(^._.^)ﾉ",
            "(^人^)",
            "(・∀・)",
            "(,,◕　⋏　◕,,)",
            "(.=^・ェ・^=)",
            "(｡･ω･｡)",
            "((≡^⚲͜^≡))",
            "((ΦωΦ))",
            "(*^ω^*)",
            "(*✧×✧*)",
            "(*ΦωΦ*)",
            "(⁎˃ᆺ˂)",
            "(ٛ⁎꒪̕ॢ ˙̫ ꒪ٛ̕ॢ⁎)",
            "₍˄·͈༝·͈˄₎◞ ̑̑ෆ⃛",
            "₍˄·͈༝·͈˄₎ฅ˒˒",
            "₍˄ุ.͡˳̫.˄ุ₎ฅ˒˒",
            "(=｀ω´=)",
            "(=｀ェ´=)",
            "（=´∇｀=）",
            "(=^ ◡ ^=)",
            "(=^-ω-^=)",
            "(=^･^=)",
            "(=^･ω･^)y＝",
            "(=^･ω･^=)",
            "(=^･ｪ･^=)",
            "(=^‥^=)",
            "(=･ω･=)",
            "(=;ω;=)",
            "(=;ェ;=)",
            "(=；ェ；=)",
            "(=;ェ;=)",
            "(=；ェ；=)",
            "(=；ｪ；=)",
            "(=‘ｘ‘=)",
            "(=⌒‿‿⌒=)",
            "(=ↀωↀ=)",
            "(=ↀωↀ=)✧",
            "(=①ω①=)",
            "(=ＴェＴ=)",
            "(=ｘェｘ=)",
            "(=ΦｴΦ=)",
            "(ٛ₌டுͩ ˑ̭ டுͩٛ₌)ฅ",
            "(≚ᄌ≚)ℒℴѵℯ❤",
            "(≚ᄌ≚)ƶƵ",
            "(○｀ω´○)",
            "(●ↀωↀ●)",
            "(●ↀωↀ●)✧",
            "(✦థ ｪ థ)",
            "(ↀДↀ)",
            "(ↀДↀ)⁼³₌₃",
            "(ↀДↀ)✧",
            "(๑•ω•́ฅ✧",
            "(๑ↀᆺↀ๑)☄",
            "(๑ↀᆺↀ๑)✧",
            "(p`･ω･´) q",
            "(p`ω´) q",
            "(Φ∇Φ)",
            "(ΦεΦ)",
            "(ΦωΦ)",
            "(ΦёΦ)",
            "(ΦзΦ)",
            "(ฅ`･ω･´)っ=",
            "(ฅ`ω´ฅ)",
            "(ฅ’ω’ฅ)",
            "(ะ`♔´ะ)",
            "(ะ☫ω☫ะ)",
            "(ㅇㅅㅇ❀)",
            "(ノω<。)",
            "(ꀄꀾꀄ)",
            "（三ФÅФ三）",
            "[ΦωΦ]",
            "] ‘͇̂•̩̫’͇̂ ͒)ฅ ﾆｬ❣",
            "＼(=^‥^)/’`",
            "<(*ΦωΦ*)>",
            "<ΦωΦ>",
            "|ΦωΦ|",
            "|ｪ･`｡)･･･　　",
            "~(=^‥^)",
            "~(=^‥^)_旦~",
            "~(=^‥^)/",
            "~(=^‥^)ノ",
            "~□Pヘ(^･ω･^=)~",
            "⊱ฅ•ω•ฅ⊰",
            "└(=^‥^=)┐",
            "✩⃛( ͒ ु•·̫• ू ͒)",
            "❤(´ω｀*)",
            "ヽ(^‥^=ゞ)",
            "ヾ(*ΦωΦ)ﾉ",
            "ヾ(*ФωФ)βyё βyё☆彡",
            "ヾ(=｀ω´=)ノ”",
            "ヽ(=^･ω･^=)丿",
            "ヾ(=ﾟ･ﾟ=)ﾉ",
            "0( =^･_･^)=〇",
            "٩(ↀДↀ)۶",
            "b(=^‥^=)o",
            "d(=^･ω･^=)b",
            "o(^・x・^)o",
            "o(=・ω・=o)",
            "V(=^･ω･^=)v",
            "ლ(=ↀωↀ=)ლ",
            "ლ(●ↀωↀ●)ლ",
            "ฅ ̂⋒ิ ˑ̫ ⋒ิ ̂ฅ",
            "ฅ( ᵕ ω ᵕ )ฅ",
            "ฅ(´-ω-`)ฅ",
            "ฅ(´・ω・｀)ฅ",
            "ฅ(^ω^ฅ)",
            "ฅ(≚ᄌ≚)",
            "ฅ(⌯͒• ɪ •⌯͒)ฅ❣",
            "ฅ⃛(⌯͒꒪ั ˑ̫ ꒪ั ⌯͒) ﾆｬｯ❣",
            "ฅ(●´ω｀●)ฅ",
            "ฅ*•ω•*ฅ♡",
            "ฅ•ω•ฅ",
            "ฅ⊱*•ω•*⊰ฅ",
            "ㅇㅅㅇ",
            "ミ๏ｖ๏彡",
            "ミ◕ฺｖ◕ฺ彡",
            "=＾● ⋏ ●＾=",
            "ฅ^•ﻌ•^ฅ",
            "₍ᵔ·͈༝·͈ᵔ₎",
            "ฅ(⌯͒•̩̩̩́ ˑ̫ •̩̩̩̀⌯͒)ฅ",
            "₍˄·͈༝·͈˄*₎◞ ̑̑",
            "ଲ( ⓛ ω ⓛ *)ଲ",
            "=^._.^= ∫",
            "ଲ(⁃̗̀̂❍⃓ˑ̫❍⃓⁃̠́̂)ଲ"
    };
    public static String NEKO_BOOT_BANNER =
            "\\    /\\\r\n" + " )  ( \')\r\n" + "(  /  )\r\n" + " \\(__)|\r\n";
    public static String ON_EMOTE = "<:ON:443810942350786560>";
    public static String WEW_EMOTE = "<:wew:443818214292717598>";
    public static String STRE_EMOTE = "<:STRE:443810942124294145>";
    public static String NO_EMOTE = "<:no:443810942099390464>";
    public static String IDLE_EMOTE = "<:IDLE:443810941784817675>";
    public static String U__EMOTE = "<a:cl:444214538322313216>";
    public static String YES_EMOTE = "<:yes:443810942221025280>";
    public static String OFF_EMOTE = "<:OFF:443810942090739722>";
    public static String GCAT_EMOTE = "<:gcat:443818213718097931>";
    public static String DND_EMOTE = "<:DND:443818213839470592>";
    public static String HUN_EMOTE = "<a:100:443818214007504921>";
    public static String MAYBE_EMOTE = "<:maybe:443810942145396736>";

    public static String LEWD_EMOTE = "<:toolewd:379045728397295617>";
    public static String AOWO_EMOTE = "<a:owo:433176856397873182>";
    public static String NEXT_EMOTE = "<:skip:380530132202422272>";
    public static String BACK_EMOTE = "<:previous:380530177404305408>";
    public static String MUTE_EMOTE = "<:nmute:380050563187867648>";
    public static String LOOO_ALL_EMOTE = "<:loopall:380050052896391168>";
    public static String LOOP_EMOTE = "<:nloop:380050068880752650>";
    public static String PAUSE_EMOTE = "<:pause:380046310650282014>";
    public static String PLAYLIST_EMOTE = "<:playlistplay:380046359769645057>";
    public static String PLAY_EMOTE = "<:play:380046334649958400>";
    public static String SHUFFLE_EMOTE = "<:shuffle:380050031262171136>";
    public static String STOP_EMOTE = "<:stop:380046422294396948>";
    public static String VOL_D_EMOTE = "<:voldown:380046401910079488>";
    public static String VOL_OFF_EMOTE = "<:voloff:380046463691915294>";
    public static String VOL_UP_EMOTE = "<:volup:380046386298880000>";
    public static String USER_EMOTE = "<:profile:380022660278779905>";
    public static String DISCORD_EMOTE = "<:dis:377619781072715787>";
    public static String BOT_EMOTE = "<:bot:377619784826617856>";
    public static String LINK_EMOTE = "\uD83D\uDD17";
    public static String PATRON_EMOTE = "<:pa:377619785787244555> ";
    public static String PAYPAL_EMOTE = "<:paypal:377619788291375124>";
    public static String TWTTER_EMOTE = "<:tw:377619783790624769>";
    public static String TWITCH_EMOTE = "<:th:377619787075026954>";
    public static String NEKO_AVA_EMOTE = "<:neko3:342750480507731968>";
    public static String NEKO_V_EMOTE = "<:neko1:342750455513874442>";
    public static String NEKO_C_EMOTE = "<:neko2:342750457472483328>";
    public static String NEKO_T_EMOTE = "<:neko:342728872883912705>";
    public static String MAGIC_EMOTE = "✨";
    public static String LEVEL_EMOTE = "<:lvl:380022567676805122>";
    public static String DATE_EMOTE = "\uD83D\uDCC5";
    public static String INFO_EMOTE = "<:info:380250960951246858>";
    public static String PATREON_EMOTE = "<:p_:475801484282429450>";
    public static String LING_MSG =
            MessageFormat.format(
                    "\n{0} **Server**: [https://invite.nekos.life](https://invite.nekos.life)"
                            + "\n{1} **Bot**: [https://bot.nekos.life](https://bot.nekos.life)"
                            + "\n{2} **Website**: [https://nekos.life](https://nekos.life)"
                            + "\n{4} **Patreon**: [https://www.patreon.com/Nekos_life](https://www.patreon.com/Nekos_life)",
                    DISCORD_EMOTE, BOT_EMOTE, LINK_EMOTE, PAYPAL_EMOTE, PATRON_EMOTE);

    public static String getReadyFormat(JDA jda, Guild HOME) {
        return MessageFormat.format(
                "Logging in Neko\r\n{0}\r\n"
                        + "Oauth link:\r\n{1}\r\n"
                        + "JDA Version:\r\n{13}\n\r"
                        + "Docs halp:\r\nhttp://home.dv8tion.net:8080/job/JDA/javadoc/\r\n"
                        + "Logged in as:\r\n{2}({3})\r\n"
                        + "Guilds:\r\n{4}\r\n"
                        + "Shards:\r\n{5}\r\n"
                        + "Users:\r\n{6}\r\n"
                        + "Bots:\r\n{7}\r\n"
                        + "Total Users:\r\n{8}\r\n"
                        + "Home Guild:\r\n{9}\r\n"
                        + "Users:\r\n{10}\r\n"
                        + "Bots:\r\n{11}\r\n"
                        + "Total Users:\r\n{12}",
                Formats.NEKO_BOOT_BANNER,
                jda.asBot().getInviteUrl(Permission.ADMINISTRATOR),
                jda.getSelfUser().getName(),
                jda.getSelfUser().getId(),
                jda.asBot().getShardManager().getGuilds().toArray().length,
                jda.asBot().getShardManager().getShardsTotal(),
                jda.asBot().getShardManager().getUsers().parallelStream().filter(user -> !user.isBot()).toArray().length,
                jda.asBot().getShardManager().getUsers().parallelStream().filter(User::isBot).toArray().length,
                jda.asBot().getShardManager().getUsers().toArray().length,
                HOME.getName(),
                HOME.getMembers().stream().filter(user -> !user.getUser().isBot()).toArray().length,
                HOME.getMembers().stream().filter(user -> user.getUser().isBot()).toArray().length,
                HOME.getMembers().toArray().length,
                JDAInfo.VERSION);
    }

    public static String getCat() {
        Random random = new Random();
        return CATS[random.nextInt(CATS.length)];
    }

    public static String codeBox(String text, String lang) {
        return MessageFormat.format("```{0}\n{1}\n```", lang, text);
    }

    public static String bold(String text) {
        return MessageFormat.format("**{0}**", text);
    }

    public static String inline(String text) {
        return MessageFormat.format("`{0}`", text);
    }

    public static String italics(String text) {
        return MessageFormat.format("*{0}*", text);
    }

    public static String error(String text) {
        return MessageFormat.format("\uD83D\uDEAB {0}", text);
    }

    public static String warning(String text) {
        return MessageFormat.format("\u26A0 {0}", text);
    }

    public static String info(String text) {
        return MessageFormat.format("{1}  {0}", text, INFO_EMOTE);
    }

    public static String clean(String text) {
        return text.replace("@everyone", "@\u200beveryone").replace("@here", "@\u200bhere");
    }

    public static String getFullName(net.dv8tion.jda.core.entities.Message msg) {
        return MessageFormat.format(
                "{0}#{1}", msg.getAuthor().getName(), msg.getAuthor().getDiscriminator());
    }

    public static void logCommand(net.dv8tion.jda.core.entities.Message message) {
        if (BotChecks.isDm(message)) {
            String msg =
                    MessageFormat.format(
                            "{4}: {0} Used {1} on Channel: {2}({3})",
                            Formats.getFullName(message),
                            message.getContentRaw(),
                            message.getChannel().getName(),
                            message.getChannel().getId(),
                            now());
            NekoBot.log.info(msg);
        } else {
            String msg =
                    MessageFormat.format(
                            "{6}: {0} Used {1} on Guild:{4}({5}) in Channel: {2}({3})",
                            Formats.getFullName(message),
                            message.getContentRaw(),
                            message.getChannel().getName(),
                            message.getChannel().getId(),
                            message.getGuild().getName(),
                            message.getGuild().getId(),
                            now());
            NekoBot.log.info(msg);
        }
    }

    public static Long getEmoteID(String text) {
        String ID = CharMatcher.digit().retainFrom(text);
        return Long.parseLong(ID);
    }

    public static String getVolEmote(int vol) {
        if (vol == 0) return VOL_OFF_EMOTE;
        if (vol <= 15) return MUTE_EMOTE;
        if (vol > 16 && vol < 60) return VOL_D_EMOTE;
        if (vol >= 60) return VOL_UP_EMOTE;
        else return VOL_UP_EMOTE;
    }
}
