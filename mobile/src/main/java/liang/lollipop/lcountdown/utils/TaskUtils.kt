package liang.lollipop.lcountdown.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by lollipop on 2018/2/23.
 * @author lollipop
 * 任务工具类
 */

val threadPool: Executor by lazy {
    Executors.newCachedThreadPool()
}

val mainHandler: Handler by lazy {
    Handler(Looper.getMainLooper())
}

inline fun <reified T: Any> T.doAsync(task: Task<T>) {
    threadPool.execute(task)
}

inline fun <reified T: Any> T.onUI(task: Task<T>) {
    mainHandler.post(task)
}

inline fun <reified T: Any> T.doAsync(
        noinline err: ((Throwable) -> Unit)? = null,
        noinline run: T.() -> Unit) {
    doAsync(createTask(err, run))
}

inline fun <reified T: Any> T.onUIDelay(
        delay: Long, task: Task<T>) {
    mainHandler.postDelayed(task, delay)
}

inline fun <reified T: Any> T.onUI(
        noinline err: ((Throwable) -> Unit)? = null,
        noinline run: T.() -> Unit) {
    onUI(createTask(err, run))
}

inline fun <reified T: Any> T.onUIDelay(
        delay: Long,
        noinline err: ((Throwable) -> Unit)? = null,
        noinline run: T.() -> Unit) {
    onUIDelay(delay, createTask(err, run))
}

inline fun <reified T: Any> T.removeTask(task: Task<T>) {
    mainHandler.removeCallbacks(task)
}

inline fun <reified T: Any> T.createTask(
        noinline err: ((Throwable) -> Unit)? = null,
        noinline run: T.() -> Unit): Task<T> {

    return Task(this, err, run)

}

class Task<T: Any>(
        private val context: T,
        private val err: ((Throwable) -> Unit)? = null,
        private val run: T.() -> Unit): Runnable {

    override fun run() {
        try {
            run.invoke(context)
        } catch (e: Throwable) {
            err?.invoke(e)
        }
    }

}
