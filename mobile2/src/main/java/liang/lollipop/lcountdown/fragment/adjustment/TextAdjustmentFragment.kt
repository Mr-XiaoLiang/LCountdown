package liang.lollipop.lcountdown.fragment.adjustment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_adjustment_text.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.util.CurtainDialog
import liang.lollipop.lcountdown.view.InnerDialogProvider

/**
 * @author lollipop
 * @date 2020/6/20 20:55
 * 文字调整的碎片页
 */
class TextAdjustmentFragment: CardAdjustmentFragment() {
    override val layoutId = R.layout.fragment_adjustment_text

    override val icon = R.drawable.ic_baseline_format_size_24

    override val title = R.string.title_text

    override val colorId = R.color.focusFontAdjust

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addTextBtn.setOnClickListener {

        }
    }

    private fun addText() {

    }

    private class AdjustmentProvider(
            private val fragment: TextAdjustmentFragment,
            private val onTextChangeListener: ((value: String, index: Int) -> Unit)):
            InnerDialogProvider() {

        companion object {
            const val ID_NONE = -1
        }

        override val layoutId = R.layout.dialog_adjustment_text

        private val curtainDialog = CurtainDialog.with(fragment)

        private var index = ID_NONE
        private var pendingValue = ""

        override fun onViewCreated(view: View) {
            super.onViewCreated(view)
            view.findViewById<View>(R.id.enterBtn)?.setOnClickListener {
                val value = find<TextView>(R.id.inputView)?.text?.toString()?:""
                onTextChangeListener(value, index)
            }
        }

        fun show(value: String, key: Int) {
            curtainDialog.bindProvider(this)
            curtainDialog.show()
            pendingValue = value
            index = key
        }

        override fun onStart() {
            super.onStart()
            find<TextView>(R.id.inputView)?.text = pendingValue
        }

    }

}