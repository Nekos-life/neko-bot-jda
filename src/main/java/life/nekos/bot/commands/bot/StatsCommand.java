package life.nekos.bot.commands.bot;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.sun.management.OperatingSystemMXBean;
import life.nekos.bot.commons.Misc;
import net.dv8tion.jda.core.entities.Message;

import java.lang.management.ManagementFactory;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;

@CommandDescription(
        name = "StatsCommand",
        triggers = {"stats"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly", value = "no"),
                @CommandAttribute(key = "dm", value = "no"),
        },
        description = "StatsCommand"
)
public class StatsCommand implements Command {
    @Override
    public void execute(Message message, String args) {

        OperatingSystemMXBean operatingSystemMXBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        DecimalFormat df = new DecimalFormat(".##");
        df.setRoundingMode(RoundingMode.CEILING);
        System.out.println(
                MessageFormat.format(
                        "{0}%/{1}%",
                        df.format(operatingSystemMXBean.getProcessCpuLoad()).replace(".", ""),
                        df.format(operatingSystemMXBean.getSystemCpuLoad()).replace(".", "")));
        System.out.println(
                "Committed mem"
                        + Misc.humanReadableByteCount(
                        operatingSystemMXBean.getCommittedVirtualMemorySize(), false));
        System.out.println(
                "free ram: "
                        + Misc.humanReadableByteCount(
                        operatingSystemMXBean.getFreePhysicalMemorySize(), false));
        System.out.println(
                "free swap: "
                        + Misc.humanReadableByteCount(operatingSystemMXBean.getFreeSwapSpaceSize(), false));
        System.out.println(
                "max ram : "
                        + Misc.humanReadableByteCount(
                        operatingSystemMXBean.getTotalPhysicalMemorySize(), false));
        System.out.println(
                "max swap: "
                        + Misc.humanReadableByteCount(operatingSystemMXBean.getTotalSwapSpaceSize(), false));
        System.out.println(operatingSystemMXBean.getSystemLoadAverage());
        System.out.println(
                "Available processors (cores): " + operatingSystemMXBean.getAvailableProcessors());
        System.out.println(ManagementFactory.getThreadMXBean().getThreadCount());

        System.out.println(
                "Free memory (bytes): "
                        + Misc.humanReadableByteCount(Runtime.getRuntime().freeMemory(), true));
        long maxMemory = Runtime.getRuntime().maxMemory();

        System.out.println("Maximum memory (bytes): " + Misc.humanReadableByteCount(maxMemory, false));

        System.out.println(
                "Total memory available to JVM (bytes): "
                        + Misc.humanReadableByteCount(Runtime.getRuntime().totalMemory(), false));
    }
}
