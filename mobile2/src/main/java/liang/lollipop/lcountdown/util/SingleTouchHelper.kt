package liang.lollipop.lcountdown.util

import android.view.MotionEvent
import android.view.View

/**
 * @author lollipop
 * @date 11/26/20 22:51
 */
class SingleTouchHelper(
        private val touchCallback: ((view: View, type: Event, x: Float, y: Float) -> Boolean)) {

    private var focusId = 0

    fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                focusId = event.getPointerId(0)
                return doSingleTouch(view, Event.Down, getX(event), getY(event))
            }
            MotionEvent.ACTION_MOVE -> {
                return doSingleTouch(view,Event.Move, getX(event), getY(event))
            }
            MotionEvent.ACTION_UP -> {
                return doSingleTouch(view,Event.Up, getX(event), getY(event))
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val oldId = focusId
                getPointerIndex(event)
                val newId = focusId
                if (oldId != newId) {
                    return doSingleTouch(view,Event.Change, getX(event), getY(event))
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                return doSingleTouch(view,Event.Cancel, getX(event), getY(event))
            }
        }
        return true
    }

    private fun getX(event: MotionEvent): Float {
        return event.getX(getPointerIndex(event))
    }

    private fun getY(event: MotionEvent): Float {
        return event.getY(getPointerIndex(event))
    }

    private fun getPointerIndex(event: MotionEvent): Int {
        val index = event.findPointerIndex(focusId)
        if (index < 0) {
            focusId = event.getPointerId(0)
            return 0
        }
        return index
    }

    private fun doSingleTouch(view: View, event: Event, x: Float, y: Float): Boolean {
        return touchCallback.invoke(view, event, x, y)
    }

    enum class Event {
        Down, Move, Up, Change, Cancel
    }

}