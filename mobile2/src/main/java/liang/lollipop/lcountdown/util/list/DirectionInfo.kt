package liang.lollipop.lcountdown.util.list

/**
 * @author lollipop
 * @date 8/29/20 23:37
 * 方向的信息
 */
data class DirectionInfo(val up: Boolean = false, val down: Boolean = false,
                         val left: Boolean = false, val right: Boolean = false,
                         val start: Boolean = false, val end: Boolean = false) {

    val flag = getFlag(this)

    companion object {

        val VERTICAL = DirectionInfo(
                up = true, down = true,
                left = false, right = false,
                start = false, end = false)

        val HORIZONTAL = DirectionInfo(
                up = false, down = false,
                left = true, right = true,
                start = true, end = true)

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