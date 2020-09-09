package liang.lollipop.lcountdown.fragment.adjustment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.include_gravity.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.util.GravityViewHelper

/**
 * @author lollipop
 * @date 7/30/20 23:42
 */
class LocationAdjustmentFragment: BaseAdjustmentFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_adjustment_location
    override val icon: Int
        get() = R.drawable.ic_baseline_format_shapes_24
    override val title: Int
        get() = R.string.title_location
    override val colorId: Int
        get() = R.color.focusLocationAdjust

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GravityViewHelper(gridGroup)
                .viewToGravity(this::viewToGravity)
                .onGravityChange(this::onGravityChange)

    }

    private fun onGravityChange(gravity: Int) {

    }

    private fun viewToGravity(view: View): Int {
        return 0
    }

}

