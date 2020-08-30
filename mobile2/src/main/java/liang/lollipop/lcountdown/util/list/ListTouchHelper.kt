package liang.lollipop.lcountdown.util.list

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author lollipop
 * @date 8/29/20 22:48
 */
class ListTouchHelper private constructor(
        private var moveFlag: Int,
        private var swipeFlag: Int): ItemTouchHelper.Callback() {

    companion object {

        const val NONE = 0
        const val UP = ItemTouchHelper.UP
        const val DOWN = ItemTouchHelper.DOWN
        const val LEFT = ItemTouchHelper.LEFT
        const val RIGHT = ItemTouchHelper.RIGHT
        const val START = ItemTouchHelper.START
        const val END = ItemTouchHelper.END
        const val VERTICAL = UP and DOWN
        const val HORIZONTAL = LEFT and RIGHT and START and END

        val DEFAULT_DIRECTION = DirectionInfo(
                up = true, down = true,
                left = true, right = true,
                start = true, end = true)

        fun with(recyclerView: RecyclerView): ListTouchHelper {
            val layoutManager = recyclerView.layoutManager
            var moveFlag = NONE
            var swipeFlag = NONE
            if (layoutManager is LinearLayoutManager) {
                if (layoutManager.orientation == RecyclerView.VERTICAL) {
                    moveFlag = VERTICAL
                    swipeFlag = HORIZONTAL
                } else {
                    moveFlag = HORIZONTAL
                    swipeFlag = VERTICAL
                }
            }
            val listTouchHelper = ListTouchHelper(moveFlag, swipeFlag)
            val itemTouchHelper = ItemTouchHelper(listTouchHelper)
            listTouchHelper.touchHelper = itemTouchHelper
            itemTouchHelper.attachToRecyclerView(recyclerView)
            return listTouchHelper
        }
    }

    var touchHelper: ItemTouchHelper? = null
        private set

    private var onMovedCallback: ((recyclerView: RecyclerView,
                                   viewHolder: RecyclerView.ViewHolder,
                                   target: RecyclerView.ViewHolder) -> Boolean)? = null

    private var onSwipedCallback: ((viewHolder: RecyclerView.ViewHolder,
                                    direction: Direction) -> Unit)? = null

    fun moveOrientation(direction: DirectionInfo): ListTouchHelper {
        moveFlag = direction.flag
        return this
    }

    fun swipeOrientation(direction: DirectionInfo): ListTouchHelper {
        swipeFlag = direction.flag
        return this
    }

    fun onMoved(callback: ((recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder) -> Boolean)?): ListTouchHelper {
        onMovedCallback = callback
        return this
    }

    fun onSwiped(callback: ((viewHolder: RecyclerView.ViewHolder,
                             direction: Direction) -> Unit)?): ListTouchHelper {
        onSwipedCallback = callback
        return this
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val drag = if (viewHolder is MovableHolder) {
            viewHolder.canMove()
        } else {
            DEFAULT_DIRECTION
        }.flag.and(moveFlag)

        val swipe = if (viewHolder is SwipeableHolder) {
            viewHolder.canSwipe()
        } else {
            DEFAULT_DIRECTION
        }.flag.and(swipeFlag)
        return makeMovementFlags(drag, swipe)
    }

    override fun onMove(recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        return onMovedCallback?.invoke(recyclerView, viewHolder, target)?:false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val d = when (direction) {
            ItemTouchHelper.UP -> Direction.UP
            ItemTouchHelper.DOWN -> Direction.DOWN
            ItemTouchHelper.LEFT -> Direction.LEFT
            ItemTouchHelper.RIGHT -> Direction.RIGHT
            ItemTouchHelper.START -> Direction.START
            ItemTouchHelper.END -> Direction.END
            else -> Direction.UNKNOWN
        }
        onSwipedCallback?.invoke(viewHolder, d)
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT, START, END, UNKNOWN
    }

}