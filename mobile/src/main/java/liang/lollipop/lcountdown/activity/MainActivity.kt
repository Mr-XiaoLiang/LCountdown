package liang.lollipop.lcountdown.activity

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.widget_countdown.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.base.BaseActivity
import liang.lollipop.lcountdown.bean.PhotoInfo
import liang.lollipop.lcountdown.bean.RepeatType
import liang.lollipop.lcountdown.bean.WidgetBean
import liang.lollipop.lcountdown.bean.WidgetStyle
import liang.lollipop.lcountdown.fragment.*
import liang.lollipop.lcountdown.utils.*
import liang.lollipop.lcountdown.widget.CountdownWidget
import liang.lollipop.ltabview.LTabHelper
import liang.lollipop.ltabview.LTabView
import kotlin.math.abs

/**
 * 小部件的参数设置Activity
 * @author Lollipop
 */
class MainActivity : BaseActivity(),
        CountdownInfoFragment.Callback,
        CountdownUnitFragment.Callback,
        CountdownFontSizeFragment.Callback,
        CountdownLocationFragment.OnLocationChangeListener,
        CountdownLocationFragment.LocationInfoProvider,
        CountdownColorFragment.Callback,
        CountdownColorFragment.Provider,
        CountdownImageFragment.Callback {
    companion object {

        private const val WHAT_UPDATE = 99

        private const val DELAYED = WidgetUtil.UPDATE_TIME

    }

    private var widgetBean = WidgetBean()

    private var isCreateModel = false

    private val countdownInfoFragment = CountdownInfoFragment()

    private val countdownUnitFragment = CountdownUnitFragment()

    private val countdownFontSizeFragment = CountdownFontSizeFragment()

    private val countdownLocationFragment = CountdownLocationFragment()

    private val countdownColorFragment = CountdownColorFragment()

    private val countdownImageFragment = CountdownImageFragment()

    private val fragments: Array<LTabFragment> = arrayOf(
            countdownInfoFragment, countdownUnitFragment,
            countdownFontSizeFragment, countdownLocationFragment,
            countdownColorFragment, countdownImageFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()

        WidgetUtil.alarmUpdate(this)
    }

    private lateinit var sheetHelper: BottomSheetHelper

    private var selectedView: View? = null

    private fun initView() {
        widgetBean.widgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)

        sheetHelper = BottomSheetHelper(sheetGroup, widgetFrame, resources.getDimension(R.dimen.peekHeight))
        sheetHelper.onStateChange {
            updateButton(it)
        }
        sheetBtn.setOnClickListener {
            sheetHelper.reverse()
        }

        updateBtn.setOnClickListener {
            updateWidget()
        }

        viewPager.offscreenPageLimit = fragments.size
        viewPager.adapter = Adapter(supportFragmentManager, fragments, this)
        val tabViewBuilder = LTabHelper.withExpandItem(tabLayout)
        tabViewBuilder.let { build ->
            val tabUnselectedColor = ContextCompat.getColor(this@MainActivity, R.color.tabUnselectedColor)
            fragments.forEach { fragment ->
                val selectedColor = if (fragment.getSelectedColorId() == 0) {
                    fragment.getSelectedColor()
                } else {
                    ContextCompat.getColor(this@MainActivity, fragment.getSelectedColorId())
                }
                var title = fragment.getTitle()
                if (TextUtils.isEmpty(title) && fragment.getTitleId() != 0) {
                    title = getString(fragment.getTitleId())
                }
                var icon = fragment.getIcon()
                if (icon == null) {
                    icon = getDrawable(fragment.getIconId())
                }
                if (icon != null) {
                    build.addItem {
                        this.text = title
                        this.icon = icon
                        this.selectedIconColor = selectedColor
                        this.unselectedIconColor = tabUnselectedColor
                        this.textColor = selectedColor
                        this.expandColor = selectedColor.and(0x40FFFFFF)
                    }
                }
            }
            build.setupWithViewPager(viewPager)
        }
        tabViewBuilder.onSelected { index ->
            changeViewFocus(index == fragments.indexOf(countdownLocationFragment))
        }
        tabLayout.style = LTabView.Style.Start
        viewPager.adapter?.notifyDataSetChanged()

        isCreateModel = intent.getIntExtra(CountdownWidget.WIDGET_SHOW, 0) < 1
        if (isCreateModel) {
            sheetHelper.expand(false)
        } else {
            sheetHelper.close(false)
        }
    }

    private fun updateButton(newState: BottomSheetHelper.State) {
        if (BottomSheetHelper.State.EXPANDED == newState) {
            updateBtn.show()
            sheetBtn.animate().let {
                it.cancel()
                it.rotation(180F).start()
            }
            changeViewFocus(viewPager.currentItem == fragments.indexOf(countdownLocationFragment))
        } else {
            updateBtn.hide()
            sheetBtn.animate().let {
                it.cancel()
                it.rotation(0F).start()
            }
            changeViewFocus(false)
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        viewPager.requestLayout()
    }

    private fun initData() {
        doAsync {
            if (isCreateModel) {
                widgetBean.endTime = System.currentTimeMillis()
                widgetBean.countdownName = getString(R.string.app_name)
                widgetBean.widgetStyle = WidgetStyle.LIGHT
                widgetBean.signValue = ""
                widgetBean.prefixName = getString(R.string.left_until)
                widgetBean.suffixName = getString(R.string.the_end)
                widgetBean.dayUnit = getString(R.string.day)
            } else {
                WidgetDBUtil.read(this@MainActivity).get(widgetBean).close()
            }
            rootGroup.post {
                syncData()
            }
        }
    }

    private fun checkClipboard() {
       val value = ClipboardHelper.get(this)
        if (TextUtils.isEmpty(value)) {
            return
        }
        try {
            val times = ClipboardHelper.decodeTimestamp(value)
            alert().setMessage(R.string.found_copy_times)
                    .setPositiveButton(R.string.use) { dialog, _ ->
                        widgetBean.endTime = times
                        rootGroup.post {
                            syncData()
                        }
                        dialog.dismiss()
                    }
                    .setNeutralButton(R.string.clear) { dialog, _ ->
                        ClipboardHelper.clear(this)
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.use_and_clear) { dialog, _ ->
                        widgetBean.endTime = times
                        rootGroup.post {
                            syncData()
                        }
                        ClipboardHelper.clear(this)
                        dialog.dismiss()
                    }
                    .show()
        } catch (e: Throwable) { }
    }

    private fun syncData() {
        countdownInfoFragment.reset(widgetBean)
        countdownUnitFragment.reset(widgetBean)
        countdownFontSizeFragment.reset(widgetBean)

        onNameInfoChange(widgetBean.countdownName)
        onSignInfoChange(widgetBean.signValue)
        onTimeTypeChange(widgetBean.noTime)
        onTimeInfoChange(widgetBean.endTime)
        onStyleInfoChange(widgetBean.widgetStyle)

        onPrefixNameChange(widgetBean.prefixName)
        onSuffixNameChange(widgetBean.suffixName)
        onDayUnitChange(widgetBean.dayUnit)

        onPrefixFontSizeChange(widgetBean.prefixFontSize)
        onNameFontSizeChange(widgetBean.nameFontSize)
        onSuffixFontSizeChange(widgetBean.suffixFontSize)
        onDayFontSizeChange(widgetBean.dayFontSize)
        onDayUnitFontSizeChange(widgetBean.dayUnitFontSize)
        onTimeFontSizeChange(widgetBean.timeFontSize)
        onSignFontSizeChange(widgetBean.signFontSize)

        if (isCreateModel) {
            WidgetUtil.Target.forEach {
                resetViewLocation(it, false)
            }
        } else {
            WidgetUtil.Target.forEach {
                updateViewLocation(it, false)
            }
        }

        WidgetUtil.Target.forEach {
            onColorChange(it, getColorByTarget(it))
        }

        updateImage()
    }

    override fun onHandler(message: Message) {
        when (message.what) {
            WHAT_UPDATE -> {
                countdown()
                handler.sendEmptyMessageDelayed(WHAT_UPDATE, DELAYED)
            }
        }
    }

    override fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        super.onInsetsChange(root, left, 0, right, bottom)
    }

    private fun updateWidget() {
        if (isCreateModel) {
            WidgetDBUtil.write(this).add(widgetBean).close()
        } else {
            WidgetDBUtil.write(this).update(widgetBean).close()
        }

        val appWidgetManager = AppWidgetManager.getInstance(this)

        WidgetUtil.update(this, widgetBean, appWidgetManager)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetBean.widgetId)
        setResult(Activity.RESULT_OK, resultValue)
        isCreateModel = false
        onBackPressed()
    }

    private fun countdown() {
        val bean = widgetBean.getTimerInfo()
        dayView.text = bean.days
        timeView.text = bean.time
        if (widgetBean.repeatType == RepeatType.Day && dayGroup.visibility != View.GONE) {
            dayGroup.visibility = View.GONE
        } else if (widgetBean.repeatType != RepeatType.Day && dayGroup.visibility != View.VISIBLE) {
            dayGroup.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeMessages(WHAT_UPDATE)
        sheetHelper.end()
    }

    override fun onStart() {
        super.onStart()
        val delayed = (System.currentTimeMillis() / DELAYED + 1) * DELAYED
        handler.sendEmptyMessageDelayed(WHAT_UPDATE, delayed)
        rootGroup.post {
            checkClipboard()
        }
    }

    override fun onNameInfoChange(name: CharSequence) {
        widgetBean.countdownName = name.toString()
        nameView.text = name
    }

    override fun onSignInfoChange(sign: CharSequence) {
        widgetBean.signValue = sign.toString()
        signView.text = sign
    }

    override fun onTimeTypeChange(noTime: Boolean) {
        widgetBean.noTime = noTime
        timeView.visibility = if (noTime) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onTimeInfoChange(time: Long) {
        widgetBean.endTime = time
        countdown()
    }

    override fun onTimingTypeChange(noCountdown: Boolean) {
        widgetBean.noCountdown = noCountdown
        countdown()
    }

    override fun onRepeatTypeChange(repeatType: RepeatType) {
        widgetBean.repeatType = repeatType
        countdown()
    }

    override fun onStyleInfoChange(style: WidgetStyle) {
        val isDark = (style == WidgetStyle.WHITE || style == WidgetStyle.DARK)

        val textColor = if (isDark) {
            0xFF333333.toInt()
        } else {
            Color.WHITE
        }
        val bgColor = if (isDark) {
            Color.WHITE
        } else {
            Color.BLACK
        }

        widgetFrame.setCardBackgroundColor(bgColor)
        changeTextViewColor(widgetFrame, textColor)
        widgetBean.widgetStyle = style
    }

    override fun onPrefixNameChange(name: CharSequence) {
        widgetBean.prefixName = name.toString()
        nameFrontView.text = name
    }

    override fun onSuffixNameChange(name: CharSequence) {
        widgetBean.suffixName = name.toString()
        nameBehindView.text = name
    }

    override fun onDayUnitChange(name: CharSequence) {
        widgetBean.dayUnit = name.toString()
        dayUnitView.text = name
    }

    override fun onPrefixFontSizeChange(sizeDip: Int) {
        widgetBean.prefixFontSize = sizeDip
        nameFrontView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDip.toFloat())
    }

    override fun onNameFontSizeChange(sizeDip: Int) {
        widgetBean.nameFontSize = sizeDip
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDip.toFloat())
    }

    override fun onSuffixFontSizeChange(sizeDip: Int) {
        widgetBean.suffixFontSize = sizeDip
        nameBehindView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDip.toFloat())
    }

    override fun onDayFontSizeChange(sizeDip: Int) {
        widgetBean.dayFontSize = sizeDip
        dayView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDip.toFloat())
    }

    override fun onDayUnitFontSizeChange(sizeDip: Int) {
        widgetBean.dayUnitFontSize = sizeDip
        dayUnitView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDip.toFloat())
    }

    override fun onTimeFontSizeChange(sizeDip: Int) {
        widgetBean.timeFontSize = sizeDip
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDip.toFloat())
    }

    override fun onSignFontSizeChange(sizeDip: Int) {
        widgetBean.signFontSize = sizeDip
        signView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeDip.toFloat())
    }

    override fun onLocationChange(target: WidgetUtil.Target, gravity: Int,
                                  verticalMargin: Float, horizontalMargin: Float) {

        if (gravity == Gravity.NO_GRAVITY) {
            resetViewLocation(target)
        } else {
            getLocationInfo(target)?.let {
                it.gravity = gravity
                it.verticalMargin = verticalMargin
                it.horizontalMargin = horizontalMargin
            }
            updateViewLocation(target)
        }
    }

    override fun getLocationInfo(target: WidgetUtil.Target): WidgetBean.Location? {
        return WidgetUtil.getLocationInfo(widgetBean, target)
    }

    private fun updateViewLocation(target: WidgetUtil.Target, isShowBorder: Boolean = true) {
        val view = when (target) {
            WidgetUtil.Target.Name -> nameGroup
            WidgetUtil.Target.Prefix -> nameFrontView
            WidgetUtil.Target.Suffix -> nameBehindView
            WidgetUtil.Target.Days -> dayGroup
            WidgetUtil.Target.Unit -> dayUnitView
            WidgetUtil.Target.Time -> timeView
            WidgetUtil.Target.Inscription -> signView
            else -> null
        }
        val locationGroup = when (target) {
            WidgetUtil.Target.Name -> nameLocationGroup
            WidgetUtil.Target.Prefix -> prefixLocationGroup
            WidgetUtil.Target.Suffix -> suffixLocationGroup
            WidgetUtil.Target.Days -> dayLocationGroup
            WidgetUtil.Target.Unit -> unitLocationGroup
            WidgetUtil.Target.Time -> timeLocationGroup
            WidgetUtil.Target.Inscription -> signLocationGroup
            else -> null
        }
        if (selectedView != view) {
            selectedView?.background = null
            selectedView = view
        }
        if (isShowBorder) {
            selectedView?.setBackgroundResource(R.drawable.border_selected)
        }
        val location = getLocationInfo(target) ?: WidgetBean.EMPTY_LOCATION
        if (location.gravity == Gravity.NO_GRAVITY) {
            WidgetUtil.resetLocation(widgetBean, target, resources)
        }
        val verticalMargin = location.verticalMargin
        val horizontalMargin = location.horizontalMargin
        val gravity = location.gravity
        val vertical = dp(abs(verticalMargin)).toInt()
        val horizontal = dp(abs(horizontalMargin)).toInt()
        locationGroup?.apply {
            this.gravity = gravity
            val l: Int
            val t: Int
            val r: Int
            val b: Int
            if (verticalMargin < 0) {
                t = 0
                b = vertical
            } else {
                t = vertical
                b = 0
            }
            if (horizontalMargin < 0) {
                l = 0
                r = horizontal
            } else {
                l = horizontal
                r = 0
            }
            setPadding(l, t, r, b)
        }
    }

    override fun onColorChange(target: WidgetUtil.Target, color: Int) {
        val view: TextView? = when (target) {
            WidgetUtil.Target.Name -> {
                widgetBean.nameColor = color
                nameView
            }
            WidgetUtil.Target.Prefix -> {
                widgetBean.prefixColor = color
                nameFrontView
            }
            WidgetUtil.Target.Suffix -> {
                widgetBean.suffixColor = color
                nameBehindView
            }
            WidgetUtil.Target.Days -> {
                widgetBean.daysColor = color
                dayView
            }
            WidgetUtil.Target.Unit -> {
                widgetBean.unitColor = color
                dayUnitView
            }
            WidgetUtil.Target.Time -> {
                widgetBean.timeColor = color
                timeView
            }
            WidgetUtil.Target.Inscription -> {
                widgetBean.inscriptionColor = color
                signView
            }
            else -> null
        }
        view?.setTextColor(color)
    }

    override fun getColorByTarget(target: WidgetUtil.Target): Int {
        return when (target) {
            WidgetUtil.Target.Name -> widgetBean.nameColor
            WidgetUtil.Target.Prefix -> widgetBean.prefixColor
            WidgetUtil.Target.Suffix -> widgetBean.suffixColor
            WidgetUtil.Target.Days -> widgetBean.daysColor
            WidgetUtil.Target.Unit -> widgetBean.unitColor
            WidgetUtil.Target.Time -> widgetBean.timeColor
            WidgetUtil.Target.Inscription -> widgetBean.inscriptionColor
            else -> Color.WHITE
        }
    }

    override fun onImageSelected(info: PhotoInfo) {
        info.loadTo(backgroundImage)
        if (info == PhotoInfo.Empty) {
            FileUtil.removeWidgetImage(this, widgetBean.widgetId)
            info.loadTo(fullBgView)
            return
        }
        doAsync {
            FileUtil.copyWidget(this, info.path, widgetBean.widgetId)
            onUI{
                updateImage()
            }
        }
    }

    private fun updateImage() {
        FileUtil.loadWidgetImage(backgroundImage, widgetBean.widgetId)
        FileUtil.loadWidgetImage(fullBgView, widgetBean.widgetId, true)
    }

    override fun onDestroy() {
        if (isCreateModel) {
            FileUtil.removeWidgetImage(this, widgetBean.widgetId)
        }
        super.onDestroy()
    }

    private fun resetViewLocation(target: WidgetUtil.Target, isShowBorder: Boolean = true) {
        WidgetUtil.resetLocation(widgetBean, target, resources)
        updateViewLocation(target, isShowBorder)
    }

    private fun changeTextViewColor(viewGroup: ViewGroup, color: Int) {
        for (index in 0 until viewGroup.childCount) {

            val child = viewGroup.getChildAt(index)

            if (child is ViewGroup) {
                changeTextViewColor(child, color)
            } else if (child is TextView) {
                child.setTextColor(color)
            } else {
                continue
            }

        }
    }

    private fun changeViewFocus(isSelected: Boolean) {
        if (isSelected) {
            countdownLocationFragment.requestFocus()
        } else {
            onLocationChange(WidgetUtil.Target.Nothing, 1, 0F, 0F)
        }
    }

    private fun dp(value: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)
    }

    @SuppressLint("WrongConstant")
    private class Adapter(fragmentManager: FragmentManager,
                          private val fragments: Array<LTabFragment>,
                          private val context: Context)
        : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            val title = fragments[position].getTitle()
            if (!TextUtils.isEmpty(title)) {
                return title
            }
            val titleId = fragments[position].getTitleId()
            if (titleId != 0) {
                return context.getString(titleId)
            }
            return ""
        }

    }

    private class BottomSheetHelper(private val sheetView: View,
                                    private val contentView: View,
                                    private val peekHeight: Float) :
            ValueAnimator.AnimatorUpdateListener,
            Animator.AnimatorListener {

        private val valueAnimator = ValueAnimator().apply {
            addUpdateListener(this@BottomSheetHelper)
            addListener(this@BottomSheetHelper)
        }

        companion object {
            private const val MIN = 0F
            private const val MAX = 1F
            private const val DURATION = 300L
        }

        enum class State {
            NOTHING,
            EXPANDED,
            COLLAPSED,
            SCROLLING
        }

        private var progress = 0F
        var state = State.NOTHING
            private set
        private var isOpen = false

        private var stateListener: ((State) -> Unit)? = null

        fun onStateChange(listener: (State) -> Unit) {
            this.stateListener = listener
        }

        private fun changeState(s: State) {
            if (s != state) {
                state = s
                stateListener?.invoke(s)
            }
        }

        fun close(isAnimator: Boolean = true) {
            isOpen = false
            post {
                if (!isAnimator) {
                    changeState(State.COLLAPSED)
                    onProgressChange(MIN)
                } else {
                    changeState(State.SCROLLING)
                    valueAnimator.cancel()
                    valueAnimator.setFloatValues(progress, MIN)
                    valueAnimator.duration = ((progress - MIN) / (MAX - MIN) * DURATION).toLong()
                    valueAnimator.start()
                }
            }
        }

        fun expand(isAnimator: Boolean = true) {
            isOpen = true
            post {
                if (!isAnimator) {
                    changeState(State.EXPANDED)
                    onProgressChange(MAX)
                } else {
                    changeState(State.SCROLLING)
                    valueAnimator.cancel()
                    valueAnimator.setFloatValues(progress, MAX)
                    valueAnimator.duration = ((MAX - progress) / (MAX - MIN) * DURATION).toLong()
                    valueAnimator.start()
                }
            }
        }

        fun reverse(isAnimator: Boolean = true) {
            if (isOpen) {
                close(isAnimator)
            } else {
                expand(isAnimator)
            }
        }

        fun end() {
            if (valueAnimator.isRunning) {
                valueAnimator.end()
            }
        }

        private fun post(run: () -> Unit) {
            sheetView.post(run)
        }

        private fun onProgressChange(value: Float) {
            progress = value
            val offsetMax = sheetView.height - peekHeight
            sheetView.translationY = offsetMax * (1 - value)
            contentView.translationY = offsetMax * value * -0.5F
        }

        override fun onAnimationUpdate(animation: ValueAnimator?) {
            if (animation == valueAnimator) {
                onProgressChange(animation.animatedValue as Float)
            }
        }

        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            if (animation == valueAnimator) {
                changeState(if (isOpen) {
                    State.EXPANDED
                } else {
                    State.COLLAPSED
                })
            }
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            if (animation == valueAnimator) {
                changeState(State.SCROLLING)
            }
        }
    }

}
