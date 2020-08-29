package liang.lollipop.lcountdown.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.util.CurtainDialog
import liang.lollipop.lcountdown.util.parseColor
import liang.lollipop.lcountdown.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author lollipop
 * @date 8/26/20 00:12
 */
class PaletteDialog(
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
        satValPalette.onHSVChange { _, color, _ ->
            selectedColor = color and 0x00FFFFFF or (selectedColor and 0xFF000000.toInt())
            colorToValue(colorValueView)
            colorPointBtn.setStatusColor(selectedColor)
        }
        transparencyPalette.onTransparencyChange { _, alphaI, _ ->
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
            dismiss()
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