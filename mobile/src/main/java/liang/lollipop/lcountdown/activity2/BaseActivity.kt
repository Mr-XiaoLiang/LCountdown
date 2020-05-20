package liang.lollipop.lcountdown.activity2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import liang.lollipop.lcountdown.R

/**
 * 新的Base activity
 */
class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
}
