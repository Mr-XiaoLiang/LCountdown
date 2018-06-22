package liang.lollipop.lcountdown.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_countdown_font_size.*
import liang.lollipop.lbaselib.base.BaseFragment
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean

/**
 * @date: 2018/6/21 21:28
 * @author: lollipop
 *
 * 字体大小设置的Fragment
 */
class CountdownFontSizeFragment: BaseFragment(),SeekBar.OnSeekBarChangeListener {

    private lateinit var callback: Callback

    private var isReady = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_countdown_font_size,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefixFontSizeBar.setOnSeekBarChangeListener(this)
        nameFontSizeBar.setOnSeekBarChangeListener(this)
        suffixFontSizeBar.setOnSeekBarChangeListener(this)
        dayFontSizeBar.setOnSeekBarChangeListener(this)
        dayUnitFontSizeBar.setOnSeekBarChangeListener(this)
        hourFontSizeBar.setOnSeekBarChangeListener(this)
        hourUnitFontSizeBar.setOnSeekBarChangeListener(this)
        timeFontSizeBar.setOnSeekBarChangeListener(this)
        signFontSizeBar.setOnSeekBarChangeListener(this)
        isReady = true
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        isReady = false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is Callback){
            callback = context
        }else{
            throw RuntimeException("Can't find CountdownFontSizeFragment.Callback")
        }
    }

    companion object {

        private const val ARG_PREFIX_FONT_SIZE = "ARG_PREFIX_FONT_SIZE"
        private const val ARG_NAME_FONT_SIZE = "ARG_NAME_FONT_SIZE"
        private const val ARG_SUFFIX_FONT_SIZE = "ARG_SUFFIX_FONT_SIZE"
        private const val ARG_DAY_FONT_SIZE = "ARG_DAY_FONT_SIZE"
        private const val ARG_DAY_UNIT_FONT_SIZE = "ARG_DAY_UNIT_FONT_SIZE"
        private const val ARG_HOUR_FONT_SIZE = "ARG_HOUR_FONT_SIZE"
        private const val ARG_HOUR_UNIT_FONT_SIZE = "ARG_HOUR_UNIT_FONT_SIZE"
        private const val ARG_TIME_FONT_SIZE = "ARG_TIME_FONT_SIZE"
        private const val ARG_SIGN_FONT_SIZE = "ARG_SIGN_FONT_SIZE"

    }

    fun reset(widgetBean: WidgetBean){

        arguments = (arguments?:Bundle()).apply {

            putInt(ARG_PREFIX_FONT_SIZE,widgetBean.prefixFontSize)
            putInt(ARG_NAME_FONT_SIZE,widgetBean.nameFontSize)
            putInt(ARG_SUFFIX_FONT_SIZE,widgetBean.suffixFontSize)
            putInt(ARG_DAY_FONT_SIZE,widgetBean.dayFontSize)
            putInt(ARG_DAY_UNIT_FONT_SIZE,widgetBean.dayUnitFontSize)
            putInt(ARG_HOUR_FONT_SIZE,widgetBean.hourFontSize)
            putInt(ARG_HOUR_UNIT_FONT_SIZE,widgetBean.hourUnitFontSize)
            putInt(ARG_TIME_FONT_SIZE,widgetBean.timeFontSize)
            putInt(ARG_SIGN_FONT_SIZE,widgetBean.signFontSize)

        }

        initView()

    }

    private fun initView(){
        if(!isReady){
            return
        }

        arguments?.run {

            prefixFontSizeBar.progress = getInt(ARG_PREFIX_FONT_SIZE,0)
            nameFontSizeBar.progress = getInt(ARG_NAME_FONT_SIZE,0)
            suffixFontSizeBar.progress = getInt(ARG_SUFFIX_FONT_SIZE,0)
            dayFontSizeBar.progress = getInt(ARG_DAY_FONT_SIZE,0)
            dayUnitFontSizeBar.progress = getInt(ARG_DAY_UNIT_FONT_SIZE,0)
            hourFontSizeBar.progress = getInt(ARG_HOUR_FONT_SIZE,0)
            hourUnitFontSizeBar.progress = getInt(ARG_HOUR_UNIT_FONT_SIZE,0)
            timeFontSizeBar.progress = getInt(ARG_TIME_FONT_SIZE,0)
            signFontSizeBar.progress = getInt(ARG_SIGN_FONT_SIZE,0)

            prefixFontSizeBar.callProgressChanged()
            nameFontSizeBar.callProgressChanged()
            suffixFontSizeBar.callProgressChanged()
            dayFontSizeBar.callProgressChanged()
            dayUnitFontSizeBar.callProgressChanged()
            hourFontSizeBar.callProgressChanged()
            hourUnitFontSizeBar.callProgressChanged()
            timeFontSizeBar.callProgressChanged()
            signFontSizeBar.callProgressChanged()
        }

    }

    private fun SeekBar.callProgressChanged(){
        onProgressChanged(this,progress,false)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        when(seekBar){

            prefixFontSizeBar -> {
                prefixFontSizeView.text = String.format(getString(R.string.prefix_font_size),progress)
                callback.onPrefixFontSizeChange(progress)
            }

            nameFontSizeBar -> {
                nameFontSizeView.text = String.format(getString(R.string.name_font_size),progress)
                callback.onNameFontSizeChange(progress)
            }

            suffixFontSizeBar -> {
                suffixFontSizeView.text = String.format(getString(R.string.suffix_font_size),progress)
                callback.onSuffixFontSizeChange(progress)
            }

            dayFontSizeBar -> {
                dayFontSizeView.text = String.format(getString(R.string.day_font_size),progress)
                callback.onDayFontSizeChange(progress)
            }

            dayUnitFontSizeBar -> {
                dayUnitFontSizeView.text = String.format(getString(R.string.day_unit_font_size),progress)
                callback.onDayUnitFontSizeChange(progress)
            }

            hourFontSizeBar -> {
                hourFontSizeView.text = String.format(getString(R.string.hour_font_size),progress)
                callback.onHourFontSizeChange(progress)
            }

            hourUnitFontSizeBar -> {
                hourUnitFontSizeView.text = String.format(getString(R.string.hour_unit_font_size),progress)
                callback.onHourUnitFontSizeChange(progress)
            }

            timeFontSizeBar -> {
                timeFontSizeView.text = String.format(getString(R.string.time_font_size),progress)
                callback.onTimeFontSizeChange(progress)
            }

            signFontSizeBar -> {
                signFontSizeView.text = String.format(getString(R.string.sign_font_size),progress)
                callback.onSignFontSizeChange(progress)
            }

        }

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    interface Callback{

        /**
         * 前缀名的字体大小
         */
        fun onPrefixFontSizeChange(sizeDip: Int)

        /**
         * 倒计时名的字体大小
         */
        fun onNameFontSizeChange(sizeDip: Int)

        /**
         * 后缀名字体大小
         */
        fun onSuffixFontSizeChange(sizeDip: Int)

        /**
         * 天的字体大小
         */
        fun onDayFontSizeChange(sizeDip: Int)

        /**
         * 天的单位的字体大小
         */
        fun onDayUnitFontSizeChange(sizeDip: Int)

        /**
         * 小时的字体大小
         */
        fun onHourFontSizeChange(sizeDip: Int)

        /**
         * 小时的单位的字体大小
         */
        fun onHourUnitFontSizeChange(sizeDip: Int)

        /**
         * 时间的字体大小
         */
        fun onTimeFontSizeChange(sizeDip: Int)

        /**
         * 签名的字体大小
         */
        fun onSignFontSizeChange(sizeDip: Int)

    }

}