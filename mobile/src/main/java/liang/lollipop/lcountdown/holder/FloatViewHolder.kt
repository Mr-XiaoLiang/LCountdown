package liang.lollipop.lcountdown.holder

import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import liang.lollipop.lbaselib.base.SimpleHandler

/**
 * @date: 2018/6/26 14:17
 * @author: lollipop
 *
 * 悬浮的View的封装对象
 */
class FloatViewHolder(val itemView: View, private val callback: ViewUpdateCallback)
    : View.OnTouchListener,
    SimpleHandler.HandlerCallback{

    companion object {

        private const val CLICK_DELAY = 300L

        private const val WHAT_CLICK = 123321

        private const val WHAT_UPDATE = 321123

    }

    private var touchDownTime = 0L

    private var firstDownTime = 0L

    private var clickSize = 0

    private val downPoint = PointF()

    private val handler = SimpleHandler(this)

    override fun onHandler(message: Message) {

        when(message.what){

            WHAT_CLICK -> if(clickSize > 0){

                when(clickSize){

                    1 -> callback.onClick(itemView)

                    2 -> callback.onDoubleClick(itemView)

                    else -> callback.onMoreClick(itemView,clickSize)

                }

                resetClick()

            }

            WHAT_UPDATE -> {



            }

        }

    }

    fun add(windowManager: WindowManager){

        removeView()

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }

        val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS//透明状态栏
                        or WindowManager.LayoutParams.FLAG_FULLSCREEN//覆盖整个屏幕
                        or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS//绘制状态栏背景
                        or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//不拦截焦点，否则所有界面将不可用
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, //允许窗口延伸到屏幕外部
                PixelFormat.TRANSPARENT
        )

        itemView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        windowManager.addView(itemView, layoutParams)

        callback.onShow(itemView)

        update()
    }

    fun remove(){

        removeView()

        callback.onHide(itemView)

        handler.removeMessages(WHAT_UPDATE)
    }

    fun update(){

        callback.onUpdate(itemView)

        handler.sendEmptyMessageDelayed(WHAT_UPDATE,updateDelay)

    }

    private val updateDelay: Long
        get() = callback.updateDelay()

    private fun removeView(){
        if(itemView.parent != null){

            val parent = itemView.parent
            if(parent is WindowManager){
                parent.removeView(itemView)
            }else if(parent is ViewGroup){
                parent.removeView(itemView)
            }

        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when(event?.action){

            MotionEvent.ACTION_DOWN -> {

                onTouchDown(event.rawX,event.rawY)

            }

            MotionEvent.ACTION_UP -> {

                onTouchUp(event.rawX,event.rawY)

            }

            MotionEvent.ACTION_MOVE -> {

                onTouchMove(event.rawX,event.rawY)

            }

        }

        return true
    }

    private fun onTouchDown(x: Float,y: Float){

        touchDownTime = System.currentTimeMillis()

        if(firstDownTime == 0L){
            firstDownTime = touchDownTime
        }

        downPoint.set(x,y)

    }

    private fun onTouchMove(x: Float,y: Float){

        val loc = callback.onMove(itemView,downPoint.x,downPoint.y,x,y)

        itemView.translationX = downPoint.x - loc.x

        itemView.translationY = downPoint.y - loc.y

    }

    private fun onTouchUp(x: Float,y: Float){

        val now = System.currentTimeMillis()
        if(now - touchDownTime > CLICK_DELAY){
            resetClick()
        }else{
            handler.removeMessages(WHAT_CLICK)
            clickSize++
            handler.sendEmptyMessageDelayed(WHAT_CLICK,CLICK_DELAY)
        }

        onViewFreed(callback.onFreed(itemView,x,y),x,y)

    }

    private fun onViewFreed(gravity: Gravity,x: Float,y: Float){
        //TODO
    }

    private fun resetClick(){
        handler.removeMessages(WHAT_CLICK)
        clickSize = 0
        touchDownTime = 0L
        firstDownTime = 0L
    }

    interface ViewUpdateCallback{

        /**
         * 更新的方法
         */
        fun onUpdate(view: View)

        /**
         * 当被隐藏，被remove的时候
         */
        fun onHide(view: View)

        /**
         * 当被显示，被添加的时候
         */
        fun onShow(view: View)

        /**
         * 被拖拽移动的时候
         */
        fun onMove(view: View,downX: Float,downY: Float,nowX: Float,nowY: Float): Point

        /**
         * 当手指松开时
         */
        fun onFreed(view: View,nowX: Float,nowY: Float): Gravity

        /**
         * 返回更新的间隔时间
         */
        fun updateDelay(): Long

        /**
         * 被单击
         */
        fun onClick(view: View)

        /**
         * 被双击
         */
        fun onDoubleClick(view: View)

        /**
         * 多次点击
         */
        fun onMoreClick(view: View,clickSize: Int)

    }

    enum class Gravity(val value: Int){

        NO_GRAVITY(android.view.Gravity.NO_GRAVITY),
        TOP(android.view.Gravity.TOP),
        BOTTOM(android.view.Gravity.BOTTOM),
        LEFT(android.view.Gravity.LEFT),
        RIGHT(android.view.Gravity.RIGHT)

    }

}