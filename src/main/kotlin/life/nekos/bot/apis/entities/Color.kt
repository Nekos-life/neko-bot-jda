package life.nekos.bot.apis.entities

import org.json.JSONObject

class Color(
    val textContrast: String,
    val brightness: Int,
    val hex: String,
    val imageUrl: String,
    val gradientImageUrl: String,
    val integer: Int,
    val name: String,
    val rgb: String,
    val rgbValues: RGB,
    val shade: List<String>,
    val tint: List<String>
) {
    companion object {
        fun fromObject(data: JSONObject): Color {
            val textContrast = data.getString("blackorwhite_text")
            val brightness = data.getInt("brightness")
            val hex = data.getString("hex")
            val imageUrl = data.getString("image")
            val gradientImageUrl = data.getString("image_gradient")
            val integer = data.getInt("int")
            val name = data.getString("name")
            val rgb = data.getString("rgb")

            val rgbValuesJson = data.getJSONObject("rgb_values")
            val rgbValues = RGB.fromObject(rgbValuesJson)

            val shade = data.getJSONArray("shade").map { it as String }.toList()
            val tint = data.getJSONArray("tint").map { it as String }.toList()

            return Color(
                textContrast,
                brightness,
                hex,
                imageUrl,
                gradientImageUrl,
                integer,
                name,
                rgb,
                rgbValues,
                shade,
                tint
            )
        }
    }
}
