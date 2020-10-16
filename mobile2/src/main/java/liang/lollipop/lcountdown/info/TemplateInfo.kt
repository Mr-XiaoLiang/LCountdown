package liang.lollipop.lcountdown.info

import org.json.JSONObject

/**
 * @author lollipop
 * @date 10/15/20 16:21
 */
class TemplateInfo(obj: JSONObject = JSONObject()): JsonInfo(obj) {

    companion object {
        fun createBy(info: String): TemplateInfo {
            return try {
                TemplateInfo(JSONObject(info))
            } catch (e: Throwable) {
                TemplateInfo()
            }
        }
    }

    var name: String by StringDelegate(this)

    val widgetInfo: WidgetInfo by JsonInfoDelegate(this) {
        it.convertTo()
    }

}