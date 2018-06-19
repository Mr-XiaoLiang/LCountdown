package liang.lollipop.lcountdown.bean

import android.graphics.Color
import android.text.TextUtils
import liang.lollipop.lbaselib.base.BaseBean
import liang.lollipop.lcountdown.utils.StringToColorUtil

/**
 * 计时的bean
 * @author Lollipop
 */
class TimingBean constructor(var id: Int = 0): BaseBean() {

    var name = ""

    var startTime = 0L

    var endTime = 0L

    private var lastName = name

    private var lastStartTime = startTime

    private var lastColor = getBeanColor()

    private fun getBeanColor(): Int{

        val value = if(TextUtils.isEmpty(name)){ "$startTime" }else{ name }
        return StringToColorUtil.format(value)

    }

    val color: Int
        get() {

            if(name != lastName || lastStartTime != startTime){
                lastColor = getBeanColor()
            }

            return lastColor
        }

}