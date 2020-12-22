package life.nekos.bot.apis.entities

import org.json.JSONObject

class RGB(val r: Int, val g: Int, val b: Int) {
    companion object {
        fun fromObject(data: JSONObject): RGB {
            val r = data.getInt("r")
            val g = data.getInt("g")
            val b = data.getInt("b")

            return RGB(r, g, b)
        }
    }
}
