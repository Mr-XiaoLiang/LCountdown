package liang.lollipop.lcountdown.service

import android.graphics.Point
import android.graphics.PointF
import android.os.SystemClock
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import liang.lollipop.lcountdown.utils.log
import kotlin.math.abs

/**
 * @author lollipop
 * @date 2020/5/1 20:45
 * View的拖拽辅助类
 */
class ViewDragHelper(private val view: View): View.OnTouchListener {

    companion object {
        // 最大单击时长
        private const val SINGLE_TAP_TIME = 800L
        // 启动拖拽功能时的误差
        private const val START_OFFSET_DP = 2F

        fun bind(view: View) : ViewDragHelper {
            return ViewDragHelper(view)
        }
    }

    init {
        view.setOnTouchListener(this)
    }

    private var dragListener: ((view: View, loc: Point) -> Unit)? = null
    private var updateLocalListener: ((view: View, offsetX: Int, offsetY: Int) -> Unit)? = null

    private val startOffset = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, START_OFFSET_DP, view.resources.displayMetrics)
    private var downTime = 0L
    private val moveOffset = PointF()
    private val lastPoint = PointF()
    private val downPoint = PointF()
    private val tempPoint = Point()
    private var allowClick = false
    private var startDrag = false
    private var dragCancel = true

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v != view) {
            return false
        }
        when(event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                log("ACTION_DOWN")
                // 保存本次落点
                downPoint.set(event.activeX(), event.activeY())
                // 保存上次位置
                lastPoint.set(downPoint)
                // 更新按下时间
                downTime = SystemClock.uptimeMillis()
                // 默认为点击模式
                allowClick = true
                // 尚未开始拖拽
                startDrag = false
                // 事件是否取消？
                dragCancel = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (dragCancel) {
                    return false
                }
                onMove(event)
            }
            MotionEvent.ACTION_UP -> {
                if (dragCancel) {
                    return false
                }
                val now = SystemClock.uptimeMillis()
                if (allowClick && now - downTime <= SINGLE_TAP_TIME) {
                    view.performClick()
                }
            }
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_CANCEL -> {
                dragCancel = true
                return false
            }
        }
        return true
    }

    private fun onMove(event: MotionEvent) {
        val x = event.activeX()
        val y = event.activeY()
        // 如果当前位置距离按下位置超过了最小标准，
        // 那么开始拖拽，否则将认为处于点击模式
        // 但是如果按下时间过长，仍然无法触发点击
        if (abs(x - downPoint.x) > startOffset ||
                abs(y - downPoint.y) > startOffset) {
            startDrag = true
            allowClick = false
        }
        if (!startDrag) {
            return
        }
        dragView(x, y)
    }

    private fun dragView(x: Float, y: Float) {
        val moveX = x - lastPoint.x + moveOffset.x
        val moveY = y - lastPoint.y + moveOffset.y
        tempPoint.x = moveX.toInt()
        tempPoint.y = moveY.toInt()
        dragListener?.invoke(view, tempPoint)
        val realX = tempPoint.x
        val realY = tempPoint.y
        moveOffset.x = moveX - realX
        moveOffset.y = moveY - realY
        lastPoint.x = x
        lastPoint.y = y
        updateLocalListener?.invoke(view, realX, realY)
    }

    private fun MotionEvent.activeX(): Float {
        return this.rawX
    }

    private fun MotionEvent.activeY(): Float {
        return this.rawY
    }

    fun onLocationUpdate(listener: ((view: View, offsetX: Int, offsetY: Int) -> Unit)?): ViewDragHelper {
        updateLocalListener = listener
        return this
    }

    fun onViewDrag(listener: ((view: View, loc: Point) -> Unit)?): ViewDragHelper {
        dragListener = listener
        return this
    }

}