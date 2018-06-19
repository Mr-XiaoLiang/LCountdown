package liang.lollipop.lcountdown.utils

import android.content.Context
import liang.lollipop.lcountdown.bean.TimingBean

/**
 * 计时工具类
 * @author Lollipop
 */
class TimingUtil private constructor(private val dbUtil: TimingDBUtil.SqlDB) {

    companion object {

        fun read(context: Context): TimingUtil{
            return TimingUtil(TimingDBUtil.read(context))
        }

        fun write(context: Context): TimingUtil{
            return TimingUtil(TimingDBUtil.write(context))
        }

        /**
         * 添加一个计时
         */
        fun startTiming(context: Context,startTime: Long,name: String){
            write(context).startTiming(startTime,name).close()
        }

        fun startTiming(context: Context,bean: TimingBean){
            write(context).startTiming(bean).close()
        }

        /**
         * 停止一个计时
         */
        fun stopTiming(context: Context,id: Int){
            write(context).stopTiming(id).close()
        }

        /**
         * 删除一个计时
         */
        fun deleteTiming(context: Context,id: Int){
            write(context).deleteTiming(id).close()
        }

        /**
         * 获取全部
         */
        fun selectAll(context: Context,list: ArrayList<TimingBean>){
            read(context).selectAll(list).close()
        }

        /**
         * 获取一个
         */
        fun selectOne(context: Context,bean: TimingBean){
            read(context).selectOne(bean).close()
        }

    }

    fun startTiming(startTime: Long,name: String): TimingUtil{
        return startTiming(TimingBean().apply {
            this.startTime = startTime
            this.name = name
        })
    }

    fun startTiming(bean: TimingBean): TimingUtil{
        dbUtil.add(bean)
        return this
    }

    fun stopTiming(id: Int): TimingUtil{
        dbUtil.updateEndTime(id)
        return this
    }

    fun deleteTiming(id: Int): TimingUtil{
        dbUtil.delete(id)
        return this
    }

    fun selectAll(list: ArrayList<TimingBean>): TimingUtil{
        dbUtil.getAll(list)
        return this
    }

    fun selectOne(bean: TimingBean): TimingUtil{
        dbUtil.get(bean)
        return this
    }

    fun close(){
        dbUtil.close()
    }

}