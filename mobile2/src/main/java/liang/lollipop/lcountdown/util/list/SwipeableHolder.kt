package liang.lollipop.lcountdown.util.list

/**
 * @author lollipop
 * @date 8/29/20 23:16
 * 可以被滑动的Holder
 */
interface SwipeableHolder {

    /**
     * 是否可以滑动
     */
    fun canSwipe(): DirectionInfo

}