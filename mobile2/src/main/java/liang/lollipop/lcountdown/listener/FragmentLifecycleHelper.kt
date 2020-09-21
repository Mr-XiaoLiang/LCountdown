package liang.lollipop.lcountdown.listener

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * @author lollipop
 * @date 9/22/20 00:44
 */
class FragmentLifecycleHelper(private val target: Fragment) {

    private val listenerList = ArrayList<FragmentLifecycleListener>()

    fun addLifecycleListener(listener: FragmentLifecycleListener) {
        listenerList.add(listener)
    }

    fun removeLifecycleListener(listener: FragmentLifecycleListener) {
        listenerList.remove(listener)
    }

    fun onAttach(context: Context) {
        listenerList.forEach { it.onAttach(target, context) }
    }

    fun onDetach() {
        listenerList.forEach { it.onDetach(target) }
    }

    fun onCreate(savedInstanceState: Bundle?) {
        listenerList.forEach { it.onCreate(target, savedInstanceState) }
    }

    fun onStart() {
        listenerList.forEach { it.onStart(target) }
    }

    fun onStop() {
        listenerList.forEach { it.onStop(target) }
    }

    fun onResume() {
        listenerList.forEach { it.onResume(target) }
    }

    fun onPause() {
        listenerList.forEach { it.onPause(target) }
    }

    fun onDestroy() {
        listenerList.forEach { it.onDestroy(target) }
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listenerList.forEach { it.onViewCreated(target, view, savedInstanceState) }
    }

}