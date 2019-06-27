package liang.lollipop.lcountdown.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_countdown_location.*
import liang.lollipop.lcountdown.R

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
        dateModeBtn.setOnClickListener {
            Toast.makeText(context, "onClick", Toast.LENGTH_SHORT).show()
        }
    }

}