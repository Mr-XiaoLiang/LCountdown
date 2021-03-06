package liang.lollipop.lcountdown.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Created by lollipop on 2018/2/2.
 * @author Lollipop
 * Tint用的工具类
 */
object TintUtil {

    fun tintText(view:TextView,vararg beans: TintBean){
        TintTextBuilder.with(view).addAll(*beans).tint()
    }

    fun tintWith(view: TextView): TintTextBuilder {
        return TintTextBuilder.with(view)
    }

    class TintBean(private val str:CharSequence,private val color:Int){

        fun value(): CharSequence{
            return str
        }

        fun color(): Int{
            return color
        }

        fun length():Int{
            return str.length
        }

    }

    fun tintDrawable(drawable: Drawable): TintDrawableBuilder {
        return TintDrawableBuilder(drawable)
    }

    fun tintDrawable(context: Context,resId:Int): TintDrawableBuilder {
        return TintDrawableBuilder.whitResId(context, resId)
    }

    fun tintDrawable(context: Context,bitmap: Bitmap): TintDrawableBuilder {
        return TintDrawableBuilder.whitBitmap(context, bitmap)
    }

    class TintTextBuilder private constructor(private val view: TextView){

        private val tintBranArray = ArrayList<TintBean>()

        companion object {
            fun with(view: TextView): TintTextBuilder {
                return TintTextBuilder(view)
            }
        }

        fun add(bean: TintBean): TintTextBuilder {
            tintBranArray.add(bean)
            return this
        }

        fun add(value:CharSequence,color: Int): TintTextBuilder {
            return add(TintBean(value, color))
        }

        fun add(value:String,color: Int): TintTextBuilder {
            return add(TintBean(value, color))
        }

        fun addAll(vararg beans: TintBean): TintTextBuilder {
            tintBranArray.addAll(beans)
            return this
        }

        fun tint(){
            if(tintBranArray.isEmpty()){
                view.text = ""
                return
            }
            val strBuilder = StringBuilder()
            for(str in tintBranArray){
                strBuilder.append(str.value())
            }
            val spannable = SpannableStringBuilder(strBuilder.toString())
            var index = 0
            for(color in tintBranArray){
                if(color.length() < 1){
                    continue
                }
                spannable.setSpan(ForegroundColorSpan(color.color()),index,index+color.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                index += color.length()
            }

            view.text = spannable
        }

    }

    /**
     * 对资源进行渲染的工具类
     */
    class TintDrawableBuilder(private val drawable: Drawable){

        private var colors: ColorStateList = ColorStateList.valueOf(Color.BLACK)

        companion object {

            fun whitResId(context: Context,resId:Int): TintDrawableBuilder {
                val wrappedDrawable = ContextCompat.getDrawable(context,resId) ?: throw RuntimeException("Drawable is null")
                return TintDrawableBuilder(wrappedDrawable)
            }

            fun whitBitmap(context: Context,bitmap: Bitmap): TintDrawableBuilder {
                val wrappedDrawable = BitmapDrawable(context.resources, bitmap)
                return TintDrawableBuilder(wrappedDrawable)
            }

        }

        fun mutate(): TintDrawableBuilder {
            drawable.mutate()
            return this
        }

        fun setColor(color: Int): TintDrawableBuilder {
            colors = ColorStateList.valueOf(color)
            return this
        }

        fun setColor(color: ColorStateList): TintDrawableBuilder {
            colors = color
            return this
        }

        fun tint(): Drawable{
            DrawableCompat.setTintList(drawable, colors)
            return drawable
        }

        fun into(imageView: ImageView) {
            imageView.setImageDrawable(tint())
        }

    }

}