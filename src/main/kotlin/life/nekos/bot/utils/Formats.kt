package life.nekos.bot.utils

import java.text.MessageFormat;

/**
 * Created by Tom on 10/5/2017.
 */
object Formats {
    val httpRx = "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)"
    val cats = listOf(
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
    )
    val NEKO_BOOT_BANNER = "\\    /\\\r\n" + " )  ( \')\r\n" + "(  /  )\r\n" + " \\(__)|\r\n"
    val ON_EMOTE = "<:ON:443810942350786560>"
    val WEW_EMOTE = "<:wew:443818214292717598>"
    val STRE_EMOTE = "<:STRE:443810942124294145>"
    val NO_EMOTE = "<:no:443810942099390464>"
    val IDLE_EMOTE = "<:IDLE:443810941784817675>"
    val U__EMOTE = "<a:cl:444214538322313216>"
    val YES_EMOTE = "<:yes:443810942221025280>"
    val OFF_EMOTE = "<:OFF:443810942090739722>"
    val GCAT_EMOTE = "<:gcat:443818213718097931>"
    val DND_EMOTE = "<:DND:443818213839470592>"
    val HUN_EMOTE = "<a:100:443818214007504921>"
    val MAYBE_EMOTE = "<:maybe:443810942145396736>"

    val LEWD_EMOTE = "<:toolewd:379045728397295617>"
    val AOWO_EMOTE = "<a:owo:433176856397873182>"
    val NEXT_EMOTE = "<:skip:380530132202422272>"
    val BACK_EMOTE = "<:previous:380530177404305408>"
    val MUTE_EMOTE = "<:nmute:380050563187867648>"
    val LOOO_ALL_EMOTE = "<:loopall:380050052896391168>"
    val LOOP_EMOTE = "<:nloop:380050068880752650>"
    val PAUSE_EMOTE = "<:pause:380046310650282014>"
    val PLAYLIST_EMOTE = "<:playlistplay:380046359769645057>"
    val PLAY_EMOTE = "<:play:380046334649958400>"
    val SHUFFLE_EMOTE = "<:shuffle:380050031262171136>"
    val STOP_EMOTE = "<:stop:380046422294396948>"
    val VOL_D_EMOTE = "<:voldown:380046401910079488>"
    val VOL_OFF_EMOTE = "<:voloff:380046463691915294>"
    val VOL_UP_EMOTE = "<:volup:380046386298880000>"
    val USER_EMOTE = "<:profile:380022660278779905>"
    val DISCORD_EMOTE = "<:dis:377619781072715787>"
    val BOT_EMOTE = "<:bot:377619784826617856>"
    val LINK_EMOTE = "\uD83D\uDD17"
    val PATRON_EMOTE = "<:pa:377619785787244555> "
    val PAYPAL_EMOTE = "<:paypal:377619788291375124>"
    val TWTTER_EMOTE = "<:tw:377619783790624769>"
    val TWITCH_EMOTE = "<:th:377619787075026954>"
    val NEKO_AVA_EMOTE = "<:neko3:342750480507731968>"
    val NEKO_V_EMOTE = "<:neko1:342750455513874442>"
    val NEKO_C_EMOTE = "<:neko2:342750457472483328>"
    val NEKO_T_EMOTE = "<:neko:342728872883912705>"
    val MAGIC_EMOTE = "✨"
    val LEVEL_EMOTE = "<:lvl:380022567676805122>"
    val DATE_EMOTE = "\uD83D\uDCC5"
    val INFO_EMOTE = "<:info:380250960951246858>"
    val PATREON_EMOTE = "<:p_:475801484282429450>"
    val LING_MSG = MessageFormat.format(
        "\n{0} **Server**: [https://invite.nekos.life](https://invite.nekos.life)"
                + "\n{1} **Bot**: [https://bot.nekos.life](https://bot.nekos.life)"
                + "\n{2} **Website**: [https://nekos.life](https://nekos.life)"
                + "\n{4} **Patreon**: [https://www.patreon.com/Nekos_life](https://www.patreon.com/Nekos_life)",
        DISCORD_EMOTE, BOT_EMOTE, LINK_EMOTE, PAYPAL_EMOTE, PATRON_EMOTE
    )

    fun codeBox(text: String, lang: String): String {
        return MessageFormat.format("```{0}\n{1}\n```", lang, text)
    }

    fun bold(text: String): String {
        return MessageFormat.format("**{0}**", text)
    }

    fun inline(text: String): String {
        return MessageFormat.format("`{0}`", text)
    }

    fun italics(text: String): String {
        return MessageFormat.format("*{0}*", text)
    }

    fun error(text: String): String {
        return MessageFormat.format("\uD83D\uDEAB {0}", text)
    }

    fun warning(text: String): String {
        return MessageFormat.format("\u26A0 {0}", text)
    }

    fun info(text: String): String {
        return MessageFormat.format("{1}  {0}", text, INFO_EMOTE)
    }

    fun clean(text: String): String {
        return text.replace("@everyone", "@\u200beveryone").replace("@here", "@\u200bhere")
    }
}
