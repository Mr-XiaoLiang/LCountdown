package liang.lollipop.lcountdown.fragment.adjustment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_adjustment_text.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.provider.TextInfoProvider
import liang.lollipop.lcountdown.util.CurtainDialog
import liang.lollipop.lcountdown.util.TextFormat
import liang.lollipop.lcountdown.util.closeBoard
import liang.lollipop.lcountdown.view.InnerDialogProvider

/**
 * @author lollipop
 * @date 2020/6/20 20:55
 * 文字调整的碎片页
 */
class TextAdjustmentFragment: CardAdjustmentFragment() {
    override val layoutId = R.layout.fragment_adjustment_text

    override val icon = R.drawable.ic_baseline_short_text_24

    override val title = R.string.title_text

    override val colorId = R.color.focusTextAdjust

    private val textInfoProvider: TextInfoProviderWrapper = TextInfoProviderWrapper(null)

    private var textChangeCallback: (() -> Unit)? = null

    private val adapter = TextItemAdapter(textInfoProvider, { _, it ->
        adjustmentProvider.show(textInfoProvider.getText(it), it)
    }, { a, it ->
        val lastCount = textInfoProvider.textCount
        textInfoProvider.removeText(it)
        val newCount = textInfoProvider.textCount
        if (newCount == lastCount - 1) {
            a.notifyItemRemoved(it)
        } else {
            a.notifyDataSetChanged()
        }
        textChangeCallback?.invoke()
    })

    private val onTextChangeListener: ((String, Int) -> Unit) = { value, index ->
        if (index == AdjustmentProvider.ID_NONE) {
            val lastCount = textInfoProvider.textCount
            textInfoProvider.addText(value)
            val newCount = textInfoProvider.textCount
            if (lastCount == newCount - 1) {
                adapter.notifyItemInserted(newCount - 1)
            } else {
                adapter.notifyDataSetChanged()
            }
        } else {
            textInfoProvider.setText(index, value)
            adapter.notifyItemChanged(index)
        }
        textChangeCallback?.invoke()
    }

    private val adjustmentProvider: AdjustmentProvider by lazy {
        AdjustmentProvider(activity as FragmentActivity, onTextChangeListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addTextBtn.setOnClickListener {
            addText()
        }
        recycleView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recycleView.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            textInfoProvider.parent = context.getTextInfoProvider()
        } else {
            parentFragment?.let { parent ->
                if (parent is Callback) {
                    textInfoProvider.parent = parent.getTextInfoProvider()
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        textInfoProvider.parent = null
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    private fun addText() {
        adjustmentProvider.show("", AdjustmentProvider.ID_NONE)
    }

    private class TextItemAdapter(
            private val data: TextInfoProvider,
            private val clickListener: (TextItemAdapter, Int) -> Unit,
            private val deleteListener: (TextItemAdapter, Int) -> Unit
    ): RecyclerView.Adapter<TextItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemHolder {
            return TextItemHolder.create(parent, {
                clickListener(this, it)
            }, {
                deleteListener(this, it)
            })
        }

        override fun getItemCount(): Int {
            return data.textCount
        }

        override fun onBindViewHolder(holder: TextItemHolder, position: Int) {
            holder.bind(data.getText(position))
        }

    }

    private class TextItemHolder
        private constructor(
                view: View,
                private val clickListener: (Int) -> Unit,
                private val deleteListener: (Int) -> Unit): RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup,
                       clickListener: (Int) -> Unit,
                       deleteListener: (Int) -> Unit): TextItemHolder {
                return TextItemHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_widget_text, parent, false),
                        clickListener,
                        deleteListener)
            }
        }

        private val nameView: TextView by lazy {
            itemView.findViewById<TextView>(R.id.textView)
        }

        init {
            itemView.setOnClickListener {
                clickListener(this.adapterPosition)
            }
            itemView.findViewById<View>(R.id.deleteBtn)?.setOnClickListener {
                deleteListener(this.adapterPosition)
            }
        }

        fun bind(text: String) {
            nameView.text = text
        }

    }

    private class AdjustmentProvider(
            activity: FragmentActivity,
            private val onTextChangeListener: ((value: String, index: Int) -> Unit)):
            InnerDialogProvider() {

        companion object {
            const val ID_NONE = -1
        }

        override val layoutId = R.layout.dialog_adjustment_text

        private val curtainDialog = CurtainDialog.with(activity)

        private var index = ID_NONE
        private var pendingValue = ""

        override fun onViewCreated(view: View) {
            super.onViewCreated(view)
            view.findViewById<View>(R.id.enterBtn)?.setOnClickListener {
                val value = find<TextView>(R.id.inputView)?.text?.toString()?:""
                onTextChangeListener(value, index)
                dismiss()
            }

            find<RecyclerView>(R.id.recycleView)?.let { recyclerView ->
                val adapter = TimeKeyAdapter { key ->
                    find<EditText>(R.id.inputView)?.append(key)
                }

                recyclerView.layoutManager = FlexboxLayoutManager(recyclerView.context,
                        FlexDirection.ROW)

                recyclerView.adapter = adapter

                adapter.notifyDataSetChanged()
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
            find<TextView>(R.id.inputView)?.let { view ->
                view.text = pendingValue
            }
        }

        override fun onStop() {
            find<EditText>(R.id.inputView)?.closeBoard()
            super.onStop()
        }

    }

    private class TimeKeyAdapter(private val clickListener: (String) -> Unit):
            RecyclerView.Adapter<TimeKeyHolder>() {

        private val data = TextFormat.KEYS

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeKeyHolder {
            return TimeKeyHolder.create(parent) { clickListener(data[it].value) }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: TimeKeyHolder, position: Int) {
            val key = data[position]
            holder.bind(key.name, key.value)
        }

    }

    private class TimeKeyHolder
        private constructor(
                view: View,
                private val clickListener: (Int) -> Unit): RecyclerView.ViewHolder(view) {
        companion object {
            fun create(parent: ViewGroup,
                       clickListener: (Int) -> Unit): TimeKeyHolder {
                return TimeKeyHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_time_key, parent, false),
                        clickListener)
            }
        }

        private val nameView: TextView by lazy {
            itemView.findViewById<TextView>(R.id.keyNameView)
        }

        private val valueView: TextView by lazy {
            itemView.findViewById<TextView>(R.id.keyValueView)
        }

        init {
            itemView.setOnClickListener {
                clickListener(this.adapterPosition)
            }
        }

        fun bind(name: Int, value: String) {
            nameView.setText(name)
            valueView.text = value
        }

    }

    private class TextInfoProviderWrapper(var parent: TextInfoProvider? = null): TextInfoProvider {
        override val textCount: Int
            get() {
                return parent?.textCount?:0
            }

        override fun getText(index: Int): String {
            return parent?.getText(index)?:""
        }

        override fun setText(index: Int, value: String) {
            parent?.setText(index, value)
        }

        override fun addText(value: String) {
            parent?.addText(value)
        }

        override fun removeText(index: Int) {
            parent?.removeText(index)
        }

    }

    interface Callback {
        fun getTextInfoProvider(): TextInfoProvider
        fun onTextInfoChange()
    }

}