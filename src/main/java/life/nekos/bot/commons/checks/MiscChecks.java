package life.nekos.bot.commons.checks;

import life.nekos.bot.NekoBot;
import life.nekos.bot.commons.db.Models;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.stream.LongStream;

import static life.nekos.bot.commons.Constants.OWNERS;

public class MiscChecks {

    public static boolean isOwner(Message msg) {
        NekoBot.log.info(
                MessageFormat.format(
                        "Owner com used by {0}#{1}({2}) on {3} was {4}",
                        msg.getAuthor().getName(),
                        msg.getAuthor().getDiscriminator(),
                        msg.getAuthor().getId(),
                        msg.getChannel().getName(),
                        LongStream.of(OWNERS).anyMatch(x -> x == msg.getAuthor().getIdLong())));
        return LongStream.of(OWNERS).anyMatch(x -> x == msg.getAuthor().getIdLong());
    }

    public static boolean twoWeeks(Message message) {
        long twoWeeksAgo =
                ((System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000)) - MiscUtil.DISCORD_EPOCH)
                        << MiscUtil.TIMESTAMP_OFFSET;
        return MiscUtil.parseSnowflake(message.getId()) < twoWeeksAgo;
    }

    public static boolean isSpam(Message message) {
        return message.getJDA().getSelfUser() == message.getAuthor()
                || message.getContentDisplay().startsWith(Models.getPrefix(message));
    }

    public static boolean isMagicDay(Calendar cal){
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            return true;
        }
        if (cal.get(Calendar.MONTH) == Calendar.JANUARY
                && cal.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }
        if (cal.get(Calendar.MONTH) == Calendar.DECEMBER
                && cal.get(Calendar.DAY_OF_MONTH) == 25) {
            return true;
        }

        if (cal.get(Calendar.MONTH) == Calendar.JULY
                && cal.get(Calendar.DAY_OF_MONTH) == 4) {
            return true;
        }
        if (cal.get(Calendar.MONTH) == Calendar.NOVEMBER
                && cal.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 4
                && cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            return true;
        }
        if (cal.get(Calendar.MONTH) == Calendar.MAY
                && cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                && cal.get(Calendar.DAY_OF_MONTH) > (31 - 7) ) {
            return true;
        }
        if (cal.get(Calendar.MONTH) == Calendar.SEPTEMBER
                && cal.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 1
                && cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return true;
        }
        if (cal.get(Calendar.MONTH) == Calendar.FEBRUARY
                && cal.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 3
                && cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return true;
        }
        if (cal.get(Calendar.MONTH) == Calendar.NOVEMBER
                && cal.get(Calendar.DAY_OF_MONTH) == 11) {
            return true;
        }
        if (cal.get(Calendar.MONTH) == Calendar.JANUARY
                && cal.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 3
                && cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return true;
        }
        if (cal.get(Calendar.MONTH) == Calendar.APRIL
                && cal.get(Calendar.DAY_OF_MONTH) == 20) {
            return true;
        }
        return false;
    }


}
