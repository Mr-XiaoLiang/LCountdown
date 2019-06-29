package liang.lollipop.lcountdown.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterViewAnimator
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_countdown_location.*
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.utils.CheckedButtonHelper

/**
 * @date: 2019-06-26 23:15
 * @author: lollipop
 * 位置调整的 Fragment
 */
class CountdownLocationFragment: LTabFragment() {
    override fun getTitleId(): Int {
        return R.string.title_loca_fragment
    }

    override fun getIconId(): Int {
        return R.drawable.ic_format_shapes_black_24dp
    }

    override fun getSelectedColorId(): Int {
        return R.color.locaTabSelected
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_countdown_location,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandBtnGroup.onSelectedBtnChange {
        }
        expandBtnGroup.onExpandStateChange { _, isOpen ->
            if (isOpen) {
                hideView(gridGroup)
                hideView(horizontalSeekBar)
                hideView(verticalSeekBar)
            } else {
                showView(gridGroup)
                showView(horizontalSeekBar)
                showView(verticalSeekBar)
            }
        }
        CheckedButtonHelper.bind(gridGroup).onChecked { checkImageView, b ->
        }
    }

    private fun hideView(view: View) {
        val animator = view.animate()
        animator.cancel()
        animator.alpha(0F)
        animator.start()
    }

    private fun showView(view: View, isAnimator: Boolean = true) {
        if (isAnimator) {
            val animator = view.animate()
            animator.cancel()
            animator.alpha(1F)
            animator.start()
        } else {
            view.alpha = 1F
        }
    }

    override fun onStop() {
        super.onStop()
        expandBtnGroup.closeAll()
        showView(gridGroup, false)
        showView(horizontalSeekBar, false)
        showView(verticalSeekBar, false)
    }

}