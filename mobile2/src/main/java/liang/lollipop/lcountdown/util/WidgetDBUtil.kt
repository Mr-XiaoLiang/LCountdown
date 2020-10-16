package liang.lollipop.lcountdown.util

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import liang.lollipop.lcountdown.info.WidgetInfo

/**
 * @author lollipop
 * @date 10/15/20 11:35
 * 小部件管理的数据库对象
 */
class WidgetDBUtil private constructor(context: Context): BaseDBUtil<WidgetInfo>(
        context, DB_NAME, BASE_VERSION + VERSION) {

    companion object {

        private const val BASE_VERSION = 0
        private const val DB_NAME = "WidgetDatabase2"
        private const val VERSION = 1

        fun read(context: Context): SqlDB<WidgetInfo> {
            return SqlDB(WidgetDBUtil(context), WidgetTableProvider(), false)
        }

        fun write(context: Context): SqlDB<WidgetInfo> {
            return SqlDB(WidgetDBUtil(context), WidgetTableProvider(), true)
        }

    }

    private object WidgetTable {
        const val NAME = "Widget"
        const val ID = "ID"
        const val INFO = "INFO"

        const val CREATE_TABLE = "CREATE TABLE $NAME ( " +
                " $ID INTEGER , " +
                " $INFO VARCHAR " +
                " );"

        const val FIND_BY_ID = " select $ID, $INFO from $NAME WHERE $ID = ? ;"

        const val SELECT_ALL = " select $ID, $INFO from $NAME ;"
    }

    override val createSql: String = WidgetTable.CREATE_TABLE

    private class WidgetTableProvider: TableProvider<WidgetInfo> {
        override val tableName: String = WidgetTable.NAME

        override val idName: String = WidgetTable.ID

        override fun getSelectSql(): String {
            return WidgetTable.SELECT_ALL
        }

        override fun getSelectByIdSql(): String {
            return WidgetTable.FIND_BY_ID
        }

        override fun putData(info: WidgetInfo, contentValues: ContentValues) {
            contentValues.apply {
                put(WidgetTable.ID, info.widgetId)
                put(WidgetTable.INFO, info.toString())
            }
        }

        override fun getId(info: WidgetInfo): String {
            return "${info.widgetId}"
        }

        override fun createInfo(cursor: Cursor): WidgetInfo {
            return WidgetInfo.createBy(cursor.getString(cursor.getColumnIndex(WidgetTable.INFO)))
        }

    }

}