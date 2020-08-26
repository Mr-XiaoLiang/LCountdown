package liang.lollipop.lcountdown.fragment.adjustment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_adjustment_background_gradient.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.drawable.GradientDrawable
import liang.lollipop.lcountdown.provider.BackgroundColorProvider
import liang.lollipop.lcountdown.util.findColor
import liang.lollipop.lcountdown.util.toDip
import liang.lollipop.lcountdown.view.CirclePointView
import liang.lollipop.lcountdown.view.ShapeView

/**
 * @author lollipop
 * @date 7/29/20 23:39
 * 背景梯度渐变渲染的Fragment
 */
class BackgroundGradientAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_background_gradient
    override val icon: Int
        get() = R.drawable.ic_baseline_gradient_24
    override val title: Int
        get() = R.string.title_background_gradient
    override val colorId: Int
        get() = R.color.focusGradientAdjust

    companion object {
        private const val Linear = 0
        private const val Sweep = 1
        private const val Radial = 2
    }

    private val backgroundColorProvider = BackgroundColorProviderWrapper(null)

    private var backgroundChangeCallback: (() -> Unit)? = null

    private val adapter = ColorAdapter(backgroundColorProvider, {
        // TODO
    }, {
        // TODO
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ShapeView.CheckedGroup()
                .add(linearShapeBtn, sweepShapeBtn, radialShapeBtn)
                .foreach {
                    initShapeBtn(it)
                }
                .select(linearShapeBtn)
                .onCheckedChange {
                    when (it) {
                        linearShapeBtn -> {
                            onSelectedTypeChange(Linear)
                        }
                        sweepShapeBtn -> {
                            onSelectedTypeChange(Sweep)
                        }
                        radialShapeBtn -> {
                            onSelectedTypeChange(Radial)
                        }
                    }
        }
        lineView.color = R.color.linePlatBackground.findColor(lineView)
        lineView.pointColor = R.color.colorPrimary.findColor(lineView)
        lineView.pointRadius = 10.toDip(lineView)
        lineView.touchRadius = 20.toDip(lineView)
        lineView.onMoved { startX, startY, endX, endY ->
            onMovedChange(startX, startY, endX, endY)
        }

        colorListView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        colorListView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            backgroundColorProvider.provider = context.getBackgroundColorProvider()
            backgroundChangeCallback = {
                context.onBackgroundColorChange()
            }
        } else {
            parentFragment?.let { parent ->
                if (parent is Callback) {
                    backgroundColorProvider.provider = parent.getBackgroundColorProvider()
                    backgroundChangeCallback = {
                        parent.onBackgroundColorChange()
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        backgroundColorProvider.provider = null
        backgroundChangeCallback = null
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    private fun onSelectedTypeChange(type: Int) {
        // TODO
    }

    private fun onMovedChange(startX: Float, startY: Float, endX: Float, endY: Float) {
        // TODO
    }

    private fun initShapeBtn(btn: ShapeView) {
        btn.setCorner(10.toDip(btn))
        btn.changeColor(R.color.colorAccent.findColor(btn), R.color.colorPrimary.findColor(btn))
        when (btn) {
            linearShapeBtn -> {
                btn.setType(GradientDrawable.Type.Linear)
                btn.changeStart(0F, 0F)
                btn.changeEnd(1F, 1F)
            }
            sweepShapeBtn -> {
                btn.setType(GradientDrawable.Type.Sweep)
                btn.changeStart(0.5F, 0.5F)
                btn.changeEnd(1F, 0.5F)
            }
            radialShapeBtn -> {
                btn.setType(GradientDrawable.Type.Radial)
                btn.changeStart(0.5F, 0.5F)
                btn.changeEnd(1F, 0.5F)
            }
        }
    }

    private class ColorAdapter(
            private val colorList: BackgroundColorProvider,
            private val onColorSelected: (Int) -> Unit,
            private val callAddColor: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == 0) {
                return ColorHolder.create(parent, ::itemClick)
            }
            return AddColorHolder.create(parent, ::addColor)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ColorHolder) {
                holder.onBind(colorList.getColor(position))
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (position < colorList.colorCount) { 0 } else { 1 }
        }

        override fun getItemCount(): Int {
            return colorList.colorCount + 1
        }

        private fun itemClick(holder: ColorHolder) {
            onColorSelected.invoke(colorList.getColor(holder.adapterPosition))
        }

        private fun addColor() {
            callAddColor()
        }

    }

    private class AddColorHolder private constructor(
            view: View,
            private val onClick: () -> Unit) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup, onClick: () -> Unit): AddColorHolder {
                return AddColorHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_gradient_color_add, parent, false), onClick)
            }
        }

        init {
            itemView.setOnClickListener {
                onClick.invoke()
            }
        }

    }

    private class ColorHolder private constructor(
            view: View,
            private val onClick: (ColorHolder) -> Unit) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup, onClick: (ColorHolder) -> Unit): ColorHolder {
                return ColorHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_gradient_color, parent, false), onClick)
            }
        }

        private val colorPointBtn: CirclePointView = itemView.findViewById(R.id.colorView)

        init {
            itemView.setOnClickListener {
                onClick.invoke(this)
            }
        }

        fun onBind(color: Int) {
            colorPointBtn.setStatusColor(color)
        }

    }

    private class BackgroundColorProviderWrapper(
            var provider: BackgroundColorProvider?) : BackgroundColorProvider {
        override val colorCount: Int
            get() {
                return provider?.colorCount?:0
            }

        override fun getColor(index: Int): Int {
            return provider?.getColor(index)?:0
        }

        override fun setColor(index: Int, color: Int) {
            provider?.setColor(index, color)
        }

        override fun addColor(color: Int) {
            provider?.addColor(color)
        }

    }

    interface Callback {
        fun onBackgroundColorChange()
        fun getBackgroundColorProvider(): BackgroundColorProvider
    }

}