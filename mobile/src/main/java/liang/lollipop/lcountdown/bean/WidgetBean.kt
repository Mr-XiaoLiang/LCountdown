package liang.lollipop.lcountdown.bean

import liang.lollipop.lbaselib.base.BaseBean

class WidgetBean : BaseBean() {

    var index = Integer.MAX_VALUE

    var widgetId = 0
    var countdownName = ""
    var endTime = 0L

    var signValue = ""

    var widgetStyle = WidgetStyle.LIGHT

    var noTime = false

    fun parseStyle(value: Int){
        widgetStyle = when(value){

            WidgetStyle.DARK.value -> WidgetStyle.DARK

            WidgetStyle.BLACK.value -> WidgetStyle.BLACK

            WidgetStyle.WHITE.value -> WidgetStyle.WHITE

            else -> WidgetStyle.LIGHT
        }
    }

    fun copy(new: WidgetBean){
        this.index = new.index
        this.widgetId = new.widgetId
        this.countdownName = new.countdownName
        this.endTime = new.endTime
        this.signValue = new.signValue
        this.widgetStyle = new.widgetStyle
    }

}