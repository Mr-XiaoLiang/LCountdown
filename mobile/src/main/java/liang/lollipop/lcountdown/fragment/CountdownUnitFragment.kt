package liang.lollipop.lcountdown.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_countdown_unit.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean

/**
 * @date: 2018/6/21 21:02
 * @author: lollipop
 *
 * 自定义单位的Fragment
 */
class CountdownUnitFragment: LTabFragment() {

    private lateinit var callback: Callback

    private var isReady = false

    override fun getTitleId(): Int {
        return R.string.title_info_fragment
    }

    override fun getIconId(): Int {
        return R.drawable.ic_format_quote_black_24dp
    }

    override fun getSelectedColorId(): Int {
        return R.color.unitTabSelected
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_countdown_unit,container,false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Callback){
            callback = context
        }else{
            throw RuntimeException("Can't find CountdownUnitFragment.Callback")
        }
    }

    companion object {

        private const val ARG_PREFIX_NAME = "ARG_PREFIX_NAME"
        private const val ARG_SUFFIX_NAME = "ARG_SUFFIX_NAME"
        private const val ARG_DAY_UNIT = "ARG_DAY_UNIT"

    }

    fun reset(widgetBean: WidgetBean){

        arguments = (arguments?:Bundle()).apply {

            putString(ARG_PREFIX_NAME,widgetBean.prefixName)
            putString(ARG_SUFFIX_NAME,widgetBean.suffixName)
            putString(ARG_DAY_UNIT,widgetBean.dayUnit)

        }

        initView()

    }

    private fun initView(){
        if(!isReady){
            return
        }

        arguments?.let {

            prefixNameInputView.setText(it.getString(ARG_PREFIX_NAME,""))

            suffixNameInputView.setText(it.getString(ARG_SUFFIX_NAME,""))

            dayUnitInputView.setText(it.getString(ARG_DAY_UNIT,""))

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefixNameInputView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callback.onPrefixNameChange(s?:"")
            }

        })

        suffixNameInputView.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callback.onSuffixNameChange(s?:"")
            }

        })

        dayUnitInputView.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callback.onDayUnitChange(s?:"")
            }

        })

        isReady = true

        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        isReady = false
    }

    interface Callback{

        fun onPrefixNameChange(name: CharSequence)
        fun onSuffixNameChange(name: CharSequence)
        fun onDayUnitChange(name: CharSequence)

    }

}