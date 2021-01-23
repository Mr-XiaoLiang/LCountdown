package liang.lollipop.lcountdown.fragment.adjustment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_adjustment_card.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.info.WidgetPart
import liang.lollipop.lcountdown.provider.BackgroundCardProvider
import liang.lollipop.lcountdown.view.LSeekBar
import liang.lollipop.lpunch.utils.StringToColorUtil

/**
 * @author lollipop
 * @date 8/1/20 22:14
 * 卡片样式的调整
 */
class CardAdjustmentFragment : BaseAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_card
    override val icon: Int
        get() = R.drawable.ic_baseline_crop_24
    override val adjustmentPart: WidgetPart
        get() = WidgetPart.Card
    override val title: Int
        get() = R.string.title_card
    override val colorId: Int
        get() = R.color.focusCardAdjust

    private val cardProvider = BackgroundCardProviderWrapper()

    private val optionList = ArrayList<Option>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backgroundSwitch.setOnCheckedChangeListener { _, isChecked ->
            recycleView.visibility = if (isChecked) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }
        initBtn()
        recycleView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recycleView.adapter = OptionAdapter(optionList)
        recycleView.adapter?.notifyDataSetChanged()
    }

    private fun initBtn() {
        val heightPixels = resources.displayMetrics.heightPixels
        val widthPixels = resources.displayMetrics.widthPixels
        addOption(R.string.card_corner, 0F, 100F,
                { cardProvider.corner },
                { cardProvider.corner = it })

        addOption(R.string.card_elevation, 0F, 100F,
                { cardProvider.elevation },
                { cardProvider.elevation = it })

        addOption(R.string.card_width, 1F, widthPixels.toFloat(),
                { cardProvider.width },
                { cardProvider.width = it })

        addOption(R.string.card_height, 1F, heightPixels.toFloat(),
                { cardProvider.height },
                { cardProvider.height = it })

        addOption(R.string.card_left, 0F, 100F,
                { cardProvider.marginLeft },
                { cardProvider.marginLeft = it })

        addOption(R.string.card_top, 0F, 100F,
                { cardProvider.marginTop },
                { cardProvider.marginTop = it })

        addOption(R.string.card_right, 0F, 100F,
                { cardProvider.marginRight },
                { cardProvider.marginRight = it })

        addOption(R.string.card_bottom, 0F, 100F,
                { cardProvider.marginBottom },
                { cardProvider.marginBottom = it })

    }

    private fun addOption(nameId: Int, min: Float, max: Float,
                          provider: () -> Float,
                          callback: (Float) -> Unit) {
        val name = getString(nameId)
        val color = StringToColorUtil.format(name)
        val index = optionList.size
        optionList.add(Option(index, name, color, min, max, provider, {
            callback.invoke(it)
            onCardInfoChange()
        }))
    }

    private fun onCardInfoChange() {
        callChangeWidgetInfo()
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onWidgetInfoChange() {
        super.onWidgetInfoChange()
        update()
    }

    private fun update() {
        recycleView.adapter?.notifyDataSetChanged()
        backgroundSwitch.isChecked = cardProvider.isShow
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachCallback<Callback>(context) {
            cardProvider.provider = it.getBackgroundCardProvider()
        }
    }

    override fun onDetach() {
        super.onDetach()
        cardProvider.provider = null
    }

    private class OptionAdapter(private val optionList: ArrayList<Option>)
        : RecyclerView.Adapter<OptionHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionHolder {
            return OptionHolder.create(parent)
        }

        override fun onBindViewHolder(holder: OptionHolder, position: Int) {
            holder.bind(optionList[position])
        }

        override fun getItemCount(): Int {
            return optionList.size
        }

    }

    private class OptionHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup): OptionHolder {
                return OptionHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_card_parameter, parent, false))
            }
        }

        private var option: Option? = null

        private val nameView: TextView = itemView.findViewById(R.id.textView)
        private val sizeValue: TextView = itemView.findViewById(R.id.sizeValue)
        private val seekBar: LSeekBar = itemView.findViewById<LSeekBar>(R.id.seekBar).apply {
            onProgressChange { _, progress ->
                onValueChange(progress)
                option?.progressCallback?.invoke(progress)
            }
        }

        fun bind(option: Option) {
            this.option = option
            seekBar.setProgress(option.valueProvider(), false)
            seekBar.setTheme(option.color)
            seekBar.max = option.max
            seekBar.min = option.min
            nameView.text = option.name
            onValueChange(seekBar.progress)
        }

        private fun onValueChange(value: Float) {
            sizeValue.text = value.toInt().toString()
        }

    }

    private data class Option(
            val action: Int,
            val name: String,
            val color: Int,
            val min: Float,
            val max: Float,
            val valueProvider: () -> Float,
            val progressCallback: (progress: Float) -> Unit)

    private class BackgroundCardProviderWrapper(
            var provider: BackgroundCardProvider? = null) : BackgroundCardProvider {

        override var isShow: Boolean
            get() = provider?.isShow?:false
            set(value) {
                provider?.isShow = value
            }
        override var corner: Float
            get() = provider?.corner?:0F
            set(value) {
                provider?.corner = value
            }
        override var marginLeft: Float
            get() = provider?.marginLeft?:0F
            set(value) {
                provider?.marginLeft = value
            }
        override var marginTop: Float
            get() = provider?.marginTop?:0F
            set(value) {
                provider?.marginTop = value
            }
        override var marginRight: Float
            get() = provider?.marginRight?:0F
            set(value) {
                provider?.marginRight = value
            }
        override var marginBottom: Float
            get() = provider?.marginBottom?:0F
            set(value) {
                provider?.marginBottom = value
            }
        override var elevation: Float
            get() = provider?.elevation?:0F
            set(value) {
                provider?.elevation = value
            }
        override var width: Float
            get() = provider?.width?:0F
            set(value) {
                provider?.width = value
            }
        override var height: Float
            get() = provider?.height?:0F
            set(value) {
                provider?.height = value
            }

    }

    interface Callback: CallChangeInfoCallback {
        fun getBackgroundCardProvider(): BackgroundCardProvider
    }

}