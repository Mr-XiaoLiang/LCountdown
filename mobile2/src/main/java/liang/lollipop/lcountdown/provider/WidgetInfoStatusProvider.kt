package liang.lollipop.lcountdown.provider

import liang.lollipop.lcountdown.listener.WidgetInfoStatusListener

/**
 * @author lollipop
 * @date 1/18/21 23:24
 * 小部件配置信息变更时的回调函数状态提供者
 */
interface WidgetInfoStatusProvider {

    fun registerWidgetInfoStatusListener(listener: WidgetInfoStatusListener)

    fun unregisterWidgetInfoStatusListener(listener: WidgetInfoStatusListener)

}