package liang.lollipop.lcountdown.engine

import android.graphics.Canvas

/**
 * @author lollipop
 * @date 9/30/20 00:36
 * 渲染引擎
 */
abstract class RenderEngine {

    /**
     * 绘制的方法
     */
    abstract fun draw(canvas: Canvas)

}