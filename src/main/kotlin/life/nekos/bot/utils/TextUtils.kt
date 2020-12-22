package life.nekos.bot.utils

object TextUtils {
    fun split(content: String, limit: Int = 2000): Array<String> {
        val pages = ArrayList<String>()

        val lines = content.trim().split("\n").dropLastWhile { it.isEmpty() }.toTypedArray()
        var chunk = StringBuilder()

        for (line in lines) {
            if (chunk.isNotEmpty() && chunk.length + line.length > limit) {
                pages.add(chunk.toString())
                chunk = StringBuilder()
            }

            if (line.length > limit) {
                val lineChunks = line.length / limit

                for (i in 0 until lineChunks) {
                    val start = limit * i
                    val end = start + limit
                    pages.add(line.substring(start, end))
                }
            } else {
                chunk.append(line).append("\n")
            }
        }

        if (chunk.isNotEmpty())
            pages.add(chunk.toString())

        return pages.toTypedArray()
    }

    fun toTimeString(time: Long): String {
        val seconds = time / 1000 % 60
        val minutes = time / (1000 * 60) % 60
        val hours = time / (1000 * 60 * 60) % 24
        val days = time / (1000 * 60 * 60 * 24)

        return when {
            days > 0 -> String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
}
