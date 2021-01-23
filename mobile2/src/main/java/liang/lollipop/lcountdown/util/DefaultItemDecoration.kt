package liang.lollipop.lcountdown.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @author lollipop
 * @date 1/23/21 16:53
 */
class DefaultItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        when (parent.layoutManager) {
            is LinearLayoutManager -> {
                getItemOffsetsByLinearLayout(outRect, view, parent)
            }
            is GridLayoutManager -> {
                getItemOffsetsByGridLayout(outRect, view, parent)
            }
            is StaggeredGridLayoutManager -> {
                getItemOffsetsByStaggeredGridLayout(outRect, view, parent)
            }
        }
    }

    private fun getItemOffsetsByLinearLayout(
            outRect: Rect, view: View, parent: RecyclerView) {
        val layoutManager = parent.layoutManager as LinearLayoutManager

        outRect.left = space
        outRect.right = space
        outRect.top = space / 2
        outRect.bottom = space / 2

        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.top = space
        }

        if (layoutManager.orientation == RecyclerView.HORIZONTAL) {
            val top = outRect.left
            val bottom = outRect.right
            val left = outRect.top
            val right = outRect.bottom
            outRect.set(left, top, right, bottom)
        }
    }

    private fun getItemOffsetsByGridLayout(
            outRect: Rect, view: View, parent: RecyclerView) {

        val layoutManager = parent.layoutManager as GridLayoutManager
        val spanCount = layoutManager.spanCount
        val params = view.layoutParams
        if (params is GridLayoutManager.LayoutParams) {
            when {
                params.spanIndex % spanCount == 0 -> {
                    // 左边
                    outRect.left = space
                    outRect.right = space / 2
                }
                params.spanIndex % spanCount == spanCount - 1 -> {
                    // 右边
                    outRect.left = space / 2
                    outRect.right = space
                }
                else -> {
                    // 中间
                    outRect.left = space / 2
                    outRect.right = space / 2
                }
            }
        }
        outRect.top = space / 2
        outRect.bottom = space / 2

        val position = parent.getChildAdapterPosition(view)
        if (position / spanCount == 0) {
            outRect.top = space
        }

        if (layoutManager.orientation == RecyclerView.HORIZONTAL) {
            val top = outRect.left
            val bottom = outRect.right
            val left = outRect.top
            val right = outRect.bottom
            outRect.set(left, top, right, bottom)
        }

    }

    private fun getItemOffsetsByStaggeredGridLayout(
            outRect: Rect, view: View, parent: RecyclerView) {
        val layoutManager = parent.layoutManager as StaggeredGridLayoutManager
        val spanCount = layoutManager.spanCount
        val params = view.layoutParams
        if (params is StaggeredGridLayoutManager.LayoutParams) {
            when {
                params.spanIndex % spanCount == 0 -> {
                    // 左边
                    outRect.left = space
                    outRect.right = space / 2
                }
                params.spanIndex % spanCount == spanCount - 1 -> {
                    // 右边
                    outRect.left = space / 2
                    outRect.right = space
                }
                else -> {
                    // 中间
                    outRect.left = space / 2
                    outRect.right = space / 2
                }
            }
        }
        outRect.top = space / 2
        outRect.bottom = space / 2

        val position = parent.getChildAdapterPosition(view)
        if (position / spanCount == 0) {
            outRect.top = space
        }

        if (layoutManager.orientation == RecyclerView.HORIZONTAL) {
            val top = outRect.left
            val bottom = outRect.right
            val left = outRect.top
            val right = outRect.bottom
            outRect.set(left, top, right, bottom)
        }
    }

}