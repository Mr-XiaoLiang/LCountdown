package liang.lollipop.lcountdown.service

import android.graphics.Point
import android.graphics.PointF
import android.os.SystemClock
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import liang.lollipop.lcountdown.utils.log
import kotlin.math.abs
import kotlin.math.min

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
    private var zoomListener: ((view: View, zoom: PointF) -> Unit)? = null
    private var updateListener: ((view: View, offsetX: Int, offsetY: Int) -> Unit)? = null

    private val startOffset = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, START_OFFSET_DP, view.resources.displayMetrics)
    private var downTime = 0L
    private val moveOffset = PointF()
    private val lastPoint = PointF()
    private val downPoint = PointF()
    private val tempPoint = Point()
    private var activePointId = -1
    private var allowClick = false
    private var startDrag = false
    private var zoomMode = false

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v != view) {
            return false
        }
        when(event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 保存并记录手指id
                event.updatePointId()
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
                // 是否落在缩放区域
                checkZoomMode()
            }
            MotionEvent.ACTION_MOVE -> {
                log("x: ${event.x}, y: ${event.y}, activeX: ${event.activeX()}, activeY: ${event.activeY()}")
                onMove(event)
            }
            MotionEvent.ACTION_UP -> {
                val now = SystemClock.uptimeMillis()
                if (allowClick && now - downTime <= SINGLE_TAP_TIME) {
                    view.performClick()
                }
            }
//            MotionEvent.ACTION_POINTER_UP -> {
//                if (event.updatePointId()) {
//                    lastPoint.set(event.activeX(), event.activeY())
//                }
//            }
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
        if (zoomMode) {
            zoomView(x, y)
        } else {
            dragView(x, y)
        }
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
        updateListener?.invoke(view, realX, realY)
    }

    private fun zoomView(x: Float, y: Float) {
        // TODO
    }

    private fun checkZoomMode() {
        val viewWidth = view.width
        val viewHeight = view.height
        val left = 0
        val top = 0
        val right = left + viewWidth
        val bottom = top + viewHeight
        val range = min(viewWidth, viewHeight) / 2
        zoomMode = downPoint.x >= left && downPoint.x <= right
                && downPoint.y <= bottom
                && (downPoint.x < range + left || downPoint.x > right - range)
                && downPoint.y > bottom - range
    }

    private fun MotionEvent.activeX(): Float {
        val index = findPointerIndex(activePointId)
        if (index < 0) {
            return lastPoint.x
        }
        return this.getX(index)
    }

    private fun MotionEvent.activeY(): Float {
        val index = findPointerIndex(activePointId)
        if (index < 0) {
            return lastPoint.y
        }
        return this.getY(index)
    }

    private fun MotionEvent.updatePointId(): Boolean {
        val index = findPointerIndex(activePointId)
        if (index < 0) {
            activePointId = getPointerId(0)
            return true
        }
        return false
    }

    fun onLocationUpdate(listener: ((view: View, offsetX: Int, offsetY: Int) -> Unit)?) {
        updateListener = listener
    }

}