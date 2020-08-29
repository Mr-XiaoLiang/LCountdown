package liang.lollipop.lcountdown.util.list

/**
 * @author lollipop
 * @date 8/29/20 23:13
 * 可移动的Holder
 */
interface MovableHolder {

    /**
     * 是否可以移动
     */
    fun canMove(): DirectionInfo

}