package liang.lollipop.lcountdown.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.databinding.FragmentCountdownFontSizeBinding
import liang.lollipop.lcountdown.utils.lazyBind

/**
 * @date: 2018/6/21 21:28
 * @author: lollipop
 *
 * 字体大小设置的Fragment
 */
class CountdownFontSizeFragment : LTabFragment(), SeekBar.OnSeekBarChangeListener {

    private lateinit var callback: Callback

    private var isReady = false

    private val binding: FragmentCountdownFontSizeBinding by lazyBind()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun getTitleId(): Int {
        return R.string.title_size_fragment
    }

    override fun getIconId(): Int {
        return R.drawable.ic_format_size_black_24dp
    }

    override fun getSelectedColorId(): Int {
        return R.color.fontTabSelected
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.prefixFontSizeBar.setOnSeekBarChangeListener(this)
        binding.nameFontSizeBar.setOnSeekBarChangeListener(this)
        binding.suffixFontSizeBar.setOnSeekBarChangeListener(this)
        binding.dayFontSizeBar.setOnSeekBarChangeListener(this)
        binding.dayUnitFontSizeBar.setOnSeekBarChangeListener(this)
        binding.timeFontSizeBar.setOnSeekBarChangeListener(this)
        binding.signFontSizeBar.setOnSeekBarChangeListener(this)
        isReady = true
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        isReady = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            callback = context
        } else {
            throw RuntimeException("Can't find CountdownFontSizeFragment.Callback")
        }
    }

    companion object {

        private const val ARG_PREFIX_FONT_SIZE = "ARG_PREFIX_FONT_SIZE"
        private const val ARG_NAME_FONT_SIZE = "ARG_NAME_FONT_SIZE"
        private const val ARG_SUFFIX_FONT_SIZE = "ARG_SUFFIX_FONT_SIZE"
        private const val ARG_DAY_FONT_SIZE = "ARG_DAY_FONT_SIZE"
        private const val ARG_DAY_UNIT_FONT_SIZE = "ARG_DAY_UNIT_FONT_SIZE"
        private const val ARG_TIME_FONT_SIZE = "ARG_TIME_FONT_SIZE"
        private const val ARG_SIGN_FONT_SIZE = "ARG_SIGN_FONT_SIZE"

    }

    fun reset(widgetBean: WidgetBean) {

        arguments = (arguments ?: Bundle()).apply {

            putInt(ARG_PREFIX_FONT_SIZE, widgetBean.prefixFontSize)
            putInt(ARG_NAME_FONT_SIZE, widgetBean.nameFontSize)
            putInt(ARG_SUFFIX_FONT_SIZE, widgetBean.suffixFontSize)
            putInt(ARG_DAY_FONT_SIZE, widgetBean.dayFontSize)
            putInt(ARG_DAY_UNIT_FONT_SIZE, widgetBean.dayUnitFontSize)
            putInt(ARG_TIME_FONT_SIZE, widgetBean.timeFontSize)
            putInt(ARG_SIGN_FONT_SIZE, widgetBean.signFontSize)

        }

        initView()

    }

    private fun initView() {
        if (!isReady) {
            return
        }

        arguments?.run {

            binding.prefixFontSizeBar.progress = getInt(ARG_PREFIX_FONT_SIZE, 0)
            binding.nameFontSizeBar.progress = getInt(ARG_NAME_FONT_SIZE, 0)
            binding.suffixFontSizeBar.progress = getInt(ARG_SUFFIX_FONT_SIZE, 0)
            binding.dayFontSizeBar.progress = getInt(ARG_DAY_FONT_SIZE, 0)
            binding.dayUnitFontSizeBar.progress = getInt(ARG_DAY_UNIT_FONT_SIZE, 0)
            binding.timeFontSizeBar.progress = getInt(ARG_TIME_FONT_SIZE, 0)
            binding.signFontSizeBar.progress = getInt(ARG_SIGN_FONT_SIZE, 0)

            binding.prefixFontSizeBar.callProgressChanged()
            binding.nameFontSizeBar.callProgressChanged()
            binding.suffixFontSizeBar.callProgressChanged()
            binding.dayFontSizeBar.callProgressChanged()
            binding.dayUnitFontSizeBar.callProgressChanged()
            binding.timeFontSizeBar.callProgressChanged()
            binding.signFontSizeBar.callProgressChanged()
        }

    }

    private fun SeekBar.callProgressChanged() {
        onProgressChanged(this, progress, false)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        when (seekBar) {

            binding.prefixFontSizeBar -> {
                binding.prefixFontSizeView.text =
                    String.format(getString(R.string.prefix_font_size), progress)
                callback.onPrefixFontSizeChange(progress)
            }

            binding.nameFontSizeBar -> {
                binding.nameFontSizeView.text =
                    String.format(getString(R.string.name_font_size), progress)
                callback.onNameFontSizeChange(progress)
            }

            binding.suffixFontSizeBar -> {
                binding.suffixFontSizeView.text =
                    String.format(getString(R.string.suffix_font_size), progress)
                callback.onSuffixFontSizeChange(progress)
            }

            binding.dayFontSizeBar -> {
                binding.dayFontSizeView.text =
                    String.format(getString(R.string.day_font_size), progress)
                callback.onDayFontSizeChange(progress)
            }

            binding.dayUnitFontSizeBar -> {
                binding.dayUnitFontSizeView.text =
                    String.format(getString(R.string.day_unit_font_size), progress)
                callback.onDayUnitFontSizeChange(progress)
            }

            binding.timeFontSizeBar -> {
                binding.timeFontSizeView.text =
                    String.format(getString(R.string.time_font_size), progress)
                callback.onTimeFontSizeChange(progress)
            }

            binding.signFontSizeBar -> {
                binding.signFontSizeView.text =
                    String.format(getString(R.string.sign_font_size), progress)
                callback.onSignFontSizeChange(progress)
            }

        }

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    interface Callback {

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
         * 时间的字体大小
         */
        fun onTimeFontSizeChange(sizeDip: Int)

        /**
         * 签名的字体大小
         */
        fun onSignFontSizeChange(sizeDip: Int)

    }

}