package liang.lollipop.lcountdown.util.list

/**
 * @author lollipop
 * @date 8/29/20 23:37
 * 方向的信息
 */
data class DirectionInfo(val up: Boolean = true, val down: Boolean = true,
                         val left: Boolean = true, val right: Boolean = true,
                         val start: Boolean = true, val end: Boolean = true) {

    val flag = getFlag(this)

    companion object {
        private fun getFlag(direction: DirectionInfo): Int {
            var flag = 0
            if (direction.down) {
                flag = flag or ListTouchHelper.DOWN
            }
            if (direction.up) {
                flag = flag or ListTouchHelper.UP
            }
            if (direction.left) {
                flag = flag or ListTouchHelper.LEFT
            }
            if (direction.right) {
                flag = flag or ListTouchHelper.RIGHT
            }
            if (direction.start) {
                flag = flag or ListTouchHelper.START
            }
            if (direction.end) {
                flag = flag or ListTouchHelper.END
            }
            return flag
        }
    }

}