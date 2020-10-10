package liang.lollipop.lcountdown.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_widget_adjustment.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.engine.WidgetEngine
import liang.lollipop.lcountdown.fragment.adjustment.*
import liang.lollipop.lcountdown.info.BackgroundInfo
import liang.lollipop.lcountdown.info.TextColor
import liang.lollipop.lcountdown.info.TextInfoArray
import liang.lollipop.lcountdown.info.WidgetInfo
import liang.lollipop.lcountdown.provider.*
import liang.lollipop.lcountdown.util.BottomSheetHelper
import liang.lollipop.lcountdown.util.toDip
import liang.lollipop.lcountdown.util.zeroTo
import liang.lollipop.ltabview.LTabHelper
import liang.lollipop.ltabview.LTabView

/**
 * 小部件的调整页面
 */
class WidgetAdjustmentActivity : BaseActivity(),
        TimeAdjustmentFragment.Callback,
        TextAdjustmentFragment.Callback,
        FontAdjustmentFragment.Callback,
        ColorAdjustmentFragment.Callback,
        LocationAdjustmentFragment.Callback,
        BackgroundGradientAdjustmentFragment.Callback,
        CardAdjustmentFragment.Callback {

    private var bottomSheetHelper: BottomSheetHelper? = null

    private val fragments: Array<BaseAdjustmentFragment> = arrayOf(
            TimeAdjustmentFragment(),
            TextAdjustmentFragment(),
            FontAdjustmentFragment(),
            ColorAdjustmentFragment(),
            LocationAdjustmentFragment(),
            BackgroundGradientAdjustmentFragment(),
            BackgroundImageAdjustmentFragment(),
            CardAdjustmentFragment(),
            SettingsAdjustmentFragment()
    )

    private val widgetInfo = WidgetInfo()

    private val textInfoArray: TextInfoArray
        get() {
            return widgetInfo.textInfoArray
        }

    private val backgroundInfo: BackgroundInfo
        get() {
            return widgetInfo.backgroundInfo
        }

    private var widgetEngine: WidgetEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_adjustment)
        initRootGroup(rootGroup)
        initView()
        initData()
    }

    private fun initData() {
        // TODO("update widget info")
        widgetEngine?.updateAll(widgetInfo)
    }

    private fun initView() {
        bottomSheetHelper = BottomSheetHelper(sheetPanel, panelGrip, widgetFrame, sheetContent)
        bottomSheetHelper?.show(false)
        panelGrip.setOnClickListener {
            bottomSheetHelper?.changeStatus()
        }
        bottomSheetHelper?.onStatusChange { isShow, start ->
            if (isShow && !start) {
                saveBtn.show()
            } else if (!isShow && start) {
                saveBtn.hide()
            }
        }

        panelPager.offscreenPageLimit = fragments.size
        panelPager.adapter = Adapter(supportFragmentManager, fragments, this)
        tabView.style = LTabView.Style.Start
        val tabViewBuilder = LTabHelper.withExpandItem(tabView)
        tabViewBuilder.let { build ->
            val tabUnselectedColor = ContextCompat.getColor(
                    this@WidgetAdjustmentActivity, R.color.tabUnselectedColor)
            fragments.forEach { fragment ->
                val selectedColor = ContextCompat.getColor(
                        this@WidgetAdjustmentActivity, fragment.colorId)
                val title = getString(fragment.title)
                val icon = getDrawable(fragment.icon)
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
            build.setupWithViewPager(panelPager)
        }

        widgetEngine = WidgetEngine(widgetFrame)
    }

    override fun onInsetsChange(root: View, left: Int, top: Int, right: Int, bottom: Int) {
        tabGroup.setPadding(left, 0, right, bottom.zeroTo{ 10F.toDip(root).toInt() })
        bottomSheetHelper?.paddingBottom = bottom
    }

    override fun onStop() {
        super.onStop()
        bottomSheetHelper?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomSheetHelper?.destroy()
        widgetEngine = null
    }

    @SuppressLint("WrongConstant")
    private class Adapter(fragmentManager: FragmentManager,
                          private val fragments: Array<BaseAdjustmentFragment>,
                          private val context: Context)
        : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            val titleId = fragments[position].title
            if (titleId != 0) {
                return context.getString(titleId)
            }
            return ""
        }

    }

    override fun getTextInfoProvider(): TextInfoProvider {
        return textInfoArray
    }

    override fun onTextInfoChange() {
        widgetEngine?.updateText(textInfoArray)
    }

    override fun getFontSizeProvider(): FontSizeProvider {
        return textInfoArray
    }

    override fun onFontSizeChange(index: Int, fontSize: Float) {
        widgetEngine?.updateText(textInfoArray)
    }

    override fun getFontColorProvider(): FontColorProvider {
        return textInfoArray
    }

    override fun onFontColorChange(index: Int, fontColor: TextColor) {
        widgetEngine?.updateText(textInfoArray)
    }

    override fun onBackgroundColorChange() {
        widgetEngine?.updateBackground(backgroundInfo)
    }

    override fun getBackgroundColorProvider(): BackgroundColorProvider {
        return backgroundInfo
    }

    override fun onBackgroundCardChange() {
        widgetEngine?.updateCard(backgroundInfo)
    }

    override fun getBackgroundCardProvider(): BackgroundCardProvider {
        return backgroundInfo
    }

    override fun getTextLocationProvider(): TextLocationProvider {
        return textInfoArray
    }

    override fun onTextLocationChange(index: Int) {
        widgetEngine?.updateText(textInfoArray)
    }

    override fun getTimeInfoProvider(): TimeInfoProvider {
        return widgetInfo
    }

    override fun onTimeInfoChange() {
        widgetEngine?.updateText(textInfoArray)
    }

}

