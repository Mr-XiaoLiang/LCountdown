package liang.lollipop.lcountdown.fragment.adjustment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_adjustment_card.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.view.LSeekBar

/**
 * @author lollipop
 * @date 8/1/20 22:14
 * 卡片样式的调整
 */
class CardAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_card
    override val icon: Int
        get() = R.drawable.ic_baseline_crop_24
    override val title: Int
        get() = R.string.title_card
    override val colorId: Int
        get() = R.color.focusCardAdjust

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backgroundSwitch.setOnCheckedChangeListener { _, isChecked ->
            recycleView.visibility = if (isChecked) { View.VISIBLE } else { View.INVISIBLE }
        }
    }

    private class OptionHolder
        private constructor(view: View,
         private val valueProvider: (action: Int) -> Float,
         private val onProgressChange: (action: Int, value: Float) -> Unit) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup,
                       valueProvider: (action: Int) -> Float,
                       onProgressChange: (action: Int, value: Float) -> Unit): OptionHolder {
                return OptionHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_card_parameter, parent, false),
                        valueProvider, onProgressChange)
            }
        }

        private var action: Int = -1

        private val nameView: TextView = itemView.findViewById(R.id.textView)
        private val seekBar: LSeekBar = itemView.findViewById(R.id.seekBar)

        fun bind(option: Option) {
            action = option.action
            seekBar.setProgress(valueProvider(action), false)
            seekBar.setTheme(option.color)
            nameView.text = option.name
        }

    }

    private data class Option(val action: Int, val name: String, val color: Int)

}