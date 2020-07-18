package liang.lollipop.lcountdown.listener

/**
 * @author lollipop
 * @date 7/18/20 17:04
 * 当一个文本被选中时当回调函数
 */
interface OnTextSelectedListener {

    /**
     * 文本的序号
     */
    fun onTextSelected(index: Int)

    /**
     * 获取已选中文本的序号
     */
    fun getSelectedIndex(): Int

}