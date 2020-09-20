package liang.lollipop.lcountdown.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.listener.TextFocusProvider
import liang.lollipop.lcountdown.view.CheckableTextView

/**
 * @author lollipop
 * @date 9/18/20 00:34
 */
class TextSelectHelper private constructor(private val recyclerView: RecyclerView) {

    private var textCountProvider: (() -> Int)? = null
    private var textValueProvider: ((Int) -> String)? = null
    private var textFocusProvider: TextFocusProvider? = null
    private var onClickCallback: ((Int) -> Unit)? = null

    companion object {
        fun with(recyclerView: RecyclerView): TextSelectHelper {
            return TextSelectHelper(recyclerView)
        }
    }

    init {
        recyclerView.layoutManager = LinearLayoutManager(
                recyclerView.context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = FocusItemAdapter(
                ::getTextSize, ::getTextValue, ::getFocusIndex, ::onClick)
    }

    private fun getTextSize(): Int {
        return textCountProvider?.invoke()?:0
    }

    private fun getTextValue(index: Int): String {
        return textValueProvider?.invoke(index)?:""
    }

    fun getFocusIndex(): Int {
        return textFocusProvider?.getSelectedIndex()?:-1
    }

    private fun onClick(index: Int) {
        onClickCallback?.invoke(index)
    }

    fun textCount(provider: () -> Int): TextSelectHelper {
        textCountProvider = provider
        return this
    }

    fun textValue(provider: (Int) -> String): TextSelectHelper {
        textValueProvider = provider
        return this
    }

    fun onAttach(fragment: Fragment) {
        val context = fragment.context
        if (context is TextFocusProvider) {
            textFocusProvider = context
        } else {
            fragment.parentFragment?.let { parent ->
                if (parent is TextFocusProvider) {
                    textFocusProvider = parent
                }
            }
        }
    }

    fun onDetach() {
        textFocusProvider = null
    }

    fun onClicked(provider: (Int) -> Unit): TextSelectHelper {
        onClickCallback = provider
        return this
    }

    private class FocusItemAdapter(
            private val textSize: () -> Int,
            private val textValue: (Int) -> String,
            private val focusIndex: () -> Int,
            private val onClick: (Int) -> Unit): RecyclerView.Adapter<ItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder.create(parent, onClick)
        }

        override fun getItemCount(): Int {
            return textSize()
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(textValue(position), focusIndex() == position)
        }

    }

    private class ItemHolder(
            view: View,
            private val onClick: (Int) -> Unit) : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(parent: ViewGroup,
                       onClick: (Int) -> Unit): ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_in_color_panel, parent, false),
                        onClick)
            }
        }

        init {
            itemView.setOnClickListener {
                onClick(this.adapterPosition)
            }
        }

        private val textView: CheckableTextView by lazy {
            itemView.findViewById(R.id.textView)
        }

        fun bind(info: String, isSelected: Boolean) {
            textView.text = info
            textView.isChecked = isSelected
        }
    }

}