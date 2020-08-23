package liang.lollipop.lcountdown.fragment.adjustment

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_adjustment_background_gradient.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.drawable.GradientDrawable
import liang.lollipop.lcountdown.util.CurtainDialog
import liang.lollipop.lcountdown.util.findColor
import liang.lollipop.lcountdown.util.parseColor
import liang.lollipop.lcountdown.util.toDip
import liang.lollipop.lcountdown.view.*
import java.util.*
import kotlin.collections.ArrayList

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

    private class PaletteProvider(
            activity: Activity,
            private val onColorSelectedListener: (tag: Int, color: Int) -> Unit): InnerDialogProvider() {
        override val layoutId = R.layout.dialog_adjustment_color
        private val curtainDialog = CurtainDialog.with(activity)

        private var tag: Int = -1
        private var selectedColor: Int = Color.WHITE

        private val historyColorList = ArrayList<Int>()
        private val adapter = ColorAdapter(historyColorList, ::onHistoricalColorClick)

        override fun onViewCreated(view: View) {
            super.onViewCreated(view)
            val huePalette: HuePaletteView = view.findViewById(R.id.huePalette)
            val satValPalette: SatValPaletteView = view.findViewById(R.id.satValPalette)
            val transparencyPalette: TransparencyPaletteView = view.findViewById(R.id.transparencyPalette)
            val colorValueView: TextInputEditText = view.findViewById(R.id.colorValueView)
            val colorPointBtn: CirclePointView = view.findViewById(R.id.colorPointBtn)
            val recycleView: RecyclerView = view.findViewById(R.id.recycleView)

            huePalette.onHueChange { hue, _ ->
                satValPalette.onHueChange(hue.toFloat())
            }
            satValPalette.onHSVChange { _, color, isUser ->
                selectedColor = color and 0x00FFFFFF or (selectedColor and 0xFF000000.toInt())
                colorToValue(colorValueView)
                colorPointBtn.setStatusColor(selectedColor)
            }
            transparencyPalette.onTransparencyChange { _, alphaI, isUser ->
                selectedColor = selectedColor and 0x00FFFFFF or (alphaI shl 24)
                colorToValue(colorValueView)
                colorPointBtn.setStatusColor(selectedColor)
            }
            colorValueView.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    val colorValue = colorValueView.text?.toString()?:""
                    val color = colorValue.parseColor()
                    selectedColor = color
                    transparencyPalette.parser(Color.alpha(color))
                    val hsv = FloatArray(3)
                    Color.colorToHSV(color, hsv)
                    satValPalette.parser(hsv[1], hsv[2])
                    huePalette.parser(hsv[0])
                    colorToValue(colorValueView)
                    colorPointBtn.setStatusColor(selectedColor)
                    true
                } else {
                    false
                }
            }
            colorPointBtn.setOnClickListener {
                onColorSelectedListener.invoke(tag, selectedColor)
            }

            recycleView.adapter = adapter
            recycleView.layoutManager = GridLayoutManager(recycleView.context,
                    4, RecyclerView.HORIZONTAL, false)
            adapter.notifyDataSetChanged()
        }

        override fun onStart() {
            super.onStart()
            switchColor()
        }

        private fun onHistoricalColorClick(color: Int) {
            selectedColor = color
            switchColor()
        }

        private fun switchColor() {
            val color = selectedColor
            val huePalette = find<HuePaletteView>(R.id.huePalette)
            val satValPalette = find<SatValPaletteView>(R.id.satValPalette)
            val transparencyPalette = find<TransparencyPaletteView>(R.id.transparencyPalette)
            val colorValueView = find<TextInputEditText>(R.id.colorValueView)
            val colorPointBtn = find<CirclePointView>(R.id.colorPointBtn)
            transparencyPalette?.parser(Color.alpha(color))
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            satValPalette?.parser(hsv[1], hsv[2])
            huePalette?.parser(hsv[0])
            colorValueView?.let { colorToValue(it) }
            colorPointBtn?.setStatusColor(selectedColor)
        }

        @SuppressLint("SetTextI18n")
        private fun colorToValue(textView: TextView) {
            val alpha = Color.alpha(selectedColor).format()
            val red = Color.red(selectedColor).format()
            val green = Color.green(selectedColor).format()
            val blue = Color.blue(selectedColor).format()
            textView.text = "${alpha}${red}${green}${blue}"
        }

        private fun Int.format(): String {
            return this.toString(16).toUpperCase(Locale.US).let {
                if (it.length < 2) {
                    "0$it"
                } else {
                    it
                }
            }
        }

        fun show(index: Int, color: Int) {
            tag = index
            selectedColor = color
            curtainDialog.bindProvider(this)
            curtainDialog.show()
        }

    }

    private class ColorAdapter(
            private val colorList: ArrayList<Int>,
            private val onColorSelected: (Int) -> Unit) : RecyclerView.Adapter<ColorHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
            return ColorHolder.create(parent, ::itemClick)
        }

        override fun onBindViewHolder(holder: ColorHolder, position: Int) {
            holder.onBind(colorList[position])
        }

        override fun getItemCount(): Int {
            return colorList.size
        }

        private fun itemClick(holder: ColorHolder) {
            onColorSelected.invoke(colorList[holder.adapterPosition])
        }

    }

    private class ColorHolder private constructor(
            view: View,
            private val onClick: (ColorHolder) -> Unit) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup, onClick: (ColorHolder) -> Unit): ColorHolder {
                return ColorHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_color_point, parent, false), onClick)
            }
        }

        private val colorPointBtn: CirclePointView = itemView.findViewById(R.id.colorPointBtn)

        init {
            itemView.setOnClickListener {
                onClick.invoke(this)
            }
        }

        fun onBind(color: Int) {
            colorPointBtn.setStatusColor(color)
        }

    }

}