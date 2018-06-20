package liang.lollipop.lcountdown.utils

import android.content.ClipData
import android.content.ClipDescription
import android.view.DragEvent
import android.view.View


/**
 * @date: 2018/6/19 17:50
 * @author: lollipop
 *
 */
class DragHelper(private val callback: DragCallback): View.OnDragListener {

    override fun onDrag(v: View?, event: DragEvent?): Boolean {

        if(v == null || event == null){
            return false
        }

        when(event.action){

            DragEvent.ACTION_DRAG_STARTED -> {
                /**
                 * 只在应用程序调用startDrag()方法，
                 * 并且获得了拖拽影子后，
                 * View对象的拖拽事件监听器才接收这种事件操作。
                 */

                return callback.onDragStarted(v,event.clipDescription,event.localState,event.x,event.y)

            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                /**
                 * 当拖拽影子刚进入View对象的边框时，
                 * View对象的拖拽事件监听器会接收这种事件操作类型。
                 */

                callback.onDragEntered(v,event.clipDescription,event.localState)

                return true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                /**
                 * 在View对象收到一个ACTION_DRAG_ENTERED事件之后，
                 * 并且拖拽影子依然还在这个对象的边框之内时，
                 * 这个View对象的拖拽事件监听器会接收这种事件操作类型
                 */

                callback.onDragHold(v,event.clipDescription,event.localState,event.x,event.y)

                return true

            }

            DragEvent.ACTION_DRAG_EXITED -> {
                /**
                 * View对象收到一个ACTION_DRAG_ENTERED和至少一个ACTION_DRAG_LOCATION事件之后，
                 * 这个对象的事件监听器会接受这种操作类型。
                 */
                callback.onDragExited(v,event.clipDescription,event.localState)
                return true
            }

            DragEvent.ACTION_DROP -> {
                /**
                 * 当用户在一个View对象之上释放了拖拽影子，
                 * 这个对象的拖拽事件监听器就会收到这种操作类型。
                 * 如果这个监听器在响应ACTION_DRAG_STARTED拖拽事件中返回了true，
                 * 那么这种操作类型只会发送给一个View对象。
                 * 如果用户在没有被注册监听器的View对象上释放了拖拽影子，
                 * 或者用户没有在当前布局的任何部分释放操作影子，
                 * 这个操作类型就不会被发送。
                 * 如果View对象成功的处理放下事件，
                 * 监听器要返回true，
                 * 否则应该返回false。
                 */

                return callback.onDragDrop(v,event.clipDescription,event.localState,event.x,event.y,event.clipData)
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                /**
                 * 当系统结束拖拽操作时，
                 * View对象拖拽监听器会接收这种事件操作类型。
                 * 这种操作类型之前不一定是ACTION_DROP事件。
                 * 如果系统发送了一个ACTION_DROP事件，
                 * 那么接收ACTION_DRAG_ENDED操作类型不意味着放下操作成功了。
                 * 监听器必须调用getResult()方法来获得响应ACTION_DROP事件中的返回值。
                 * 如果ACTION_DROP事件没有被发送，
                 * 那么getResult()会返回false。
                 */

                callback.onDragEnded(v,event.clipDescription,event.localState,event.result)
                return true

            }

            else -> {
                return false
            }

        }

    }

    interface DragCallback{

        /**
         * 只在应用程序调用startDrag()方法，
         * 并且获得了拖拽影子后，
         * View对象的拖拽事件监听器才接收这种事件操作。
         */
        fun onDragStarted(view: View, clipDescription: ClipDescription?, localState: Any?, x: Float, y: Float): Boolean

        /**
         * 当拖拽影子刚进入View对象的边框时，
         * View对象的拖拽事件监听器会接收这种事件操作类型。
         */
        fun onDragEntered(view: View, clipDescription: ClipDescription?, localState: Any?)

        /**
         * 当阴影悬浮并且保持在View上时，
         * 会持续调用此方法
         */
        fun onDragHold(view: View, clipDescription: ClipDescription?, localState: Any?, x: Float, y: Float)

        /**
         * View对象收到一个ACTION_DRAG_ENTERED和至少一个ACTION_DRAG_LOCATION事件之后，
         * 这个对象的事件监听器会接受这种操作类型。
         *
         * 这个方法，表示阴影
         */
        fun onDragExited(view: View, clipDescription: ClipDescription?, localState: Any?)

        /**
         * 当用户在一个View对象之上释放了拖拽影子，
         * 这个对象的拖拽事件监听器就会收到这种操作类型。
         * 如果这个监听器在响应ACTION_DRAG_STARTED拖拽事件中返回了true，
         * 那么这种操作类型只会发送给一个View对象。
         * 如果用户在没有被注册监听器的View对象上释放了拖拽影子，
         * 或者用户没有在当前布局的任何部分释放操作影子，
         * 这个操作类型就不会被发送。
         * 如果View对象成功的处理放下事件，
         * 监听器要返回true，否则应该返回false。
         */
        fun onDragDrop(view: View, clipDescription: ClipDescription?,
                   localState: Any?, x: Float, y: Float,clipData: ClipData?): Boolean

        /**
         * 当系统结束拖拽操作时，
         * View对象拖拽监听器会接收这种事件操作类型。
         * 这种操作类型之前不一定是ACTION_DROP事件。
         * 如果系统发送了一个ACTION_DROP事件，
         * 那么接收ACTION_DRAG_ENDED操作类型不意味着放下操作成功了。
         * 监听器必须调用getResult()方法来获得响应ACTION_DROP事件中的返回值。
         * 如果ACTION_DROP事件没有被发送，
         * 那么getResult()会返回false。
         */
        fun onDragEnded(view: View, clipDescription: ClipDescription?, localState: Any?, result: Boolean)

    }

}