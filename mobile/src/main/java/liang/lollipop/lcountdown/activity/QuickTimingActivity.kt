package liang.lollipop.lcountdown.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import kotlinx.android.synthetic.main.activity_quick_timing.*
import liang.lollipop.lbaselib.base.BaseActivity
import liang.lollipop.lcountdown.R
import liang.lollipop.lcountdown.bean.TimingBean
import liang.lollipop.lcountdown.service.FloatingService
import liang.lollipop.lcountdown.utils.TimingUtil

/**
 * 快速计时Activity
 * @author Lollipop
 */
class QuickTimingActivity : BaseActivity() {

    private val startTime = System.currentTimeMillis()

    companion object {

        const val RESULT_TIMING_ID = "RESULT_TIMING_ID"

        const val REMIND_BTN_TRANSITION = "REMIND_BTN"

        const val QUIET_BTN_TRANSITION = "QUIET_BTN"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_timing)

        init()
    }

    private fun init(){

        rootGroup.setOnClickListener(this)
        quietBtn.setOnClickListener(this)
        infoBody.setOnClickListener(this)
        remindBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when(v){

            rootGroup -> onBackPressed()

            quietBtn -> {
                saveTiming()
                rootGroup.callOnClick()
            }

            remindBtn -> {

                startFloating()
                quietBtn.callOnClick()

            }

        }

    }

    private fun startFloating(){
        val floatingIntent = Intent(this,FloatingService::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(floatingIntent)
        }else{
            startService(floatingIntent)
        }
    }

    private fun saveTiming(){
        val name = timingNameEdit.text.toString().trim()
        val bean = TimingBean().apply {
            this@apply.name = name
            this@apply.startTime = this@QuickTimingActivity.startTime
        }
        TimingUtil.startTiming(this,bean)
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(RESULT_TIMING_ID,bean.id)
        })
    }

}
