package liang.lollipop.lcountdown.provider

/**
 * @author lollipop
 * @date 9/4/20 00:30
 */
interface BackgroundCardProvider {

    /**
     * 是否展示背景
     */
    var isShow: Boolean

    /**
     * 圆角尺寸
     */
    var corner: Float

    /**
     * 左侧边距
     */
    var marginLeft: Float

    /**
     * 顶部边距
     */
    var marginTop: Float

    /**
     * 右侧边距
     */
    var marginRight: Float

    /**
     * 底部边距
     */
    var marginBottom: Float

    /**
     * 海拔高度
     */
    var elevation: Float

    /**
     * 宽度
     */
    var width: Float

    /**
     * 高度
     */
    var height: Float

}