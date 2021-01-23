package liang.lollipop.lcountdown.fragment.adjustment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_adjustment_background_gradient.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.dialog.PaletteDialog
import liang.lollipop.lcountdown.drawable.GradientDrawable
import liang.lollipop.lcountdown.info.WidgetPart
import liang.lollipop.lcountdown.provider.BackgroundColorProvider
import liang.lollipop.lcountdown.util.findColor
import liang.lollipop.lcountdown.util.list.DirectionInfo
import liang.lollipop.lcountdown.util.list.ListTouchHelper
import liang.lollipop.lcountdown.util.list.MovableHolder
import liang.lollipop.lcountdown.util.list.SwipeableHolder
import liang.lollipop.lcountdown.util.toDip
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
    override val adjustmentPart: WidgetPart
        get() = WidgetPart.BackgroundColor
    override val title: Int
        get() = R.string.title_background_gradient
    override val colorId: Int
        get() = R.color.focusGradientAdjust

    companion object {
        private const val Linear = BackgroundColorProvider.Linear
        private const val Sweep = BackgroundColorProvider.Sweep
        private const val Radial = BackgroundColorProvider.Radial
    }

    private val backgroundColorProvider = BackgroundColorProviderWrapper(null)

    private val paletteDialog: PaletteDialog by lazy {
        PaletteDialog(activity as FragmentActivity) { tag, color ->
            if (tag < 0) {
                backgroundColorProvider.addColor(color)
            } else {
                backgroundColorProvider.setColor(tag, color)
            }
            adapter.notifyDataSetChanged()
            callChangeWidgetInfo()
        }
    }

    private val adapter = ColorAdapter(backgroundColorProvider, {
        changeColor(it)
    }, {
        changeColor(-1)
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
        ListTouchHelper.with(colorListView)
                .moveOrientation(DirectionInfo.VERTICAL)
                .swipeOrientation(DirectionInfo(left = true))
                .onMoved(::onItemMoved)
                .onSwiped(::onItemSwiped)
        adapter.notifyDataSetChanged()
    }

    private fun onItemMoved(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder): Boolean {
        if (target is AddColorHolder || viewHolder is AddColorHolder) {
            return false
        }
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        val color = backgroundColorProvider.getColor(fromPosition)
        backgroundColorProvider.setColor(fromPosition,
                backgroundColorProvider.getColor(toPosition))
        backgroundColorProvider.setColor(toPosition, color)
        adapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    private fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, direction: ListTouchHelper.Direction) {
        if (viewHolder is AddColorHolder) {
            return
        }
        backgroundColorProvider.removeColor(viewHolder.adapterPosition)
        adapter.notifyItemRemoved(viewHolder.adapterPosition)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachCallback<Callback>(context) {
            backgroundColorProvider.provider = it.getBackgroundColorProvider()
        }
    }

    override fun onDetach() {
        super.onDetach()
        backgroundColorProvider.provider = null
    }

    private fun changeColor(index: Int) {
        paletteDialog.show(index, if (index < 0) {
            Color.RED
        } else {
            backgroundColorProvider.getColor(index)
        })
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onWidgetInfoChange() {
        super.onWidgetInfoChange()
        adapter.notifyDataSetChanged()
    }

    private fun onSelectedTypeChange(type: Int) {
        backgroundColorProvider.gradientType = type
        callChangeWidgetInfo()
    }

    private fun onMovedChange(startX: Float, startY: Float, endX: Float, endY: Float) {
        backgroundColorProvider.startX = startX
        backgroundColorProvider.startY = startY
        backgroundColorProvider.endX = endX
        backgroundColorProvider.endY = endY
        callChangeWidgetInfo()
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
            onColorSelected.invoke(holder.adapterPosition)
        }

        private fun addColor() {
            callAddColor()
        }

    }

    private class AddColorHolder private constructor(
            view: View,
            private val onClick: () -> Unit) : RecyclerView.ViewHolder(view),
            SwipeableHolder, MovableHolder {

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

        override fun canSwipe(): DirectionInfo {
            return DirectionInfo.NONE
        }

        override fun canMove(): DirectionInfo {
            return DirectionInfo.NONE
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

        private val colorPointBtn: CardView = itemView.findViewById(R.id.colorView)

        init {
            itemView.setOnClickListener {
                onClick.invoke(this)
            }
        }

        fun onBind(color: Int) {
            colorPointBtn.setCardBackgroundColor(color)
        }

    }

    private class BackgroundColorProviderWrapper(
            var provider: BackgroundColorProvider?) : BackgroundColorProvider {
        override val colorCount: Int
            get() {
                return provider?.colorCount?:0
            }
        override var gradientType: Int
            get() {
                return provider?.gradientType?:0
            }
            set(value) {
                provider?.gradientType = value
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

        override fun removeColor(index: Int) {
            provider?.removeColor(index)
        }

        override var startX: Float
            get() {
                return provider?.startX?:0F
            }
            set(value) {
                provider?.startX = value
            }

        override var startY: Float
            get() {
                return provider?.startY?:0F
            }
            set(value) {
                provider?.startY = value
            }

        override var endX: Float
            get() {
                return provider?.endX?:0F
            }
            set(value) {
                provider?.endX = value
            }

        override var endY: Float
            get() {
                return provider?.endY?:0F
            }
            set(value) {
                provider?.endY = value
            }

    }

    interface Callback: CallChangeInfoCallback {
        fun getBackgroundColorProvider(): BackgroundColorProvider
    }

}