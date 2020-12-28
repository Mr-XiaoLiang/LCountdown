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

        fun read(context: Context): WidgetSqlDB {
            return WidgetSqlDB(WidgetDBUtil(context), false)
        }

        fun write(context: Context): WidgetSqlDB {
            return WidgetSqlDB(WidgetDBUtil(context), true)
        }

    }

    private object WidgetTable {
        const val NAME = "Widget"
        const val ID = "ID"
        const val INFO = "INFO"
        const val WIDGET = "WIDGET"
        const val REMOVE = "IS_REMOVE"

        const val CREATE_TABLE = "CREATE TABLE $NAME ( " +
                " $ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                " $WIDGET INTEGER , " +
                " $INFO VARCHAR , " +
                " $REMOVE INTEGER " +
                " );"

        private const val ALL_COLUMN = "$ID, $WIDGET, $INFO, $REMOVE"

        const val FIND_BY_ID = " select $ALL_COLUMN from $NAME WHERE $ID = ? ;"

        const val SELECT_SHOWN_WIDGET = " select $ALL_COLUMN from $NAME WHERE $WIDGET <> 0 AND $REMOVE == 0 ;"

        const val SELECT_HIDE_WIDGET = " select $ALL_COLUMN from $NAME WHERE $WIDGET == 0 AND $REMOVE == 0 ;"

        const val FIND_BY_WIDGET = " select $ALL_COLUMN from $NAME WHERE $WIDGET = ? ;"

        const val SELECT_ALL = " select $ALL_COLUMN from $NAME WHERE $REMOVE == 0 ;"

        const val SELECT_ASHCAN = " select $ALL_COLUMN from $NAME WHERE $REMOVE <> 0 ;"
    }

    override val createSql: String = WidgetTable.CREATE_TABLE

    private class WidgetTableProvider: TableProvider<WidgetInfo> {

        override val tableName: String = WidgetTable.NAME

        override val idName: String = WidgetTable.ID

        companion object {
            fun createWidgetInfo(cursor: Cursor): WidgetInfo {
                return WidgetInfo.createBy(cursor.getString(cursor.getColumnIndex(WidgetTable.INFO))).apply {
                    id = cursor.getInt(cursor.getColumnIndex(WidgetTable.ID))
                    widgetId = cursor.getInt(cursor.getColumnIndex(WidgetTable.WIDGET))
                }
            }
        }

        override fun getSelectSql(): String {
            return WidgetTable.SELECT_ALL
        }

        override fun getSelectByIdSql(): String {
            return WidgetTable.FIND_BY_ID
        }

        override fun putData(info: WidgetInfo, contentValues: ContentValues) {
            contentValues.apply {
                put(WidgetTable.WIDGET, info.widgetId)
                put(WidgetTable.INFO, info.toString())
                put(WidgetTable.REMOVE, if (info.isRemove) { 1 } else { 0 } )
            }
        }

        override fun getId(info: WidgetInfo): String {
            return "${info.id}"
        }

        override fun createInfo(cursor: Cursor): WidgetInfo {
            return createWidgetInfo(cursor)
        }

    }

    class WidgetSqlDB constructor(
            databaseHelper: WidgetDBUtil,
            isWritable: Boolean): SqlDB<WidgetInfo>(databaseHelper,
            WidgetTableProvider(), isWritable) {

        fun findByWidgetId(info: WidgetInfo): Boolean {
            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(WidgetTable.FIND_BY_WIDGET, arrayOf("${info.widgetId}"))
            var isRight = false
            if (c.moveToNext()) {
                info.copy(WidgetTableProvider.createWidgetInfo(c))
                isRight = true
            }
            c.close()
            return isRight
        }

        fun updateWidgetId(oldWidgetId: Int, newWidgetId: Int): Boolean {
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            values.put(WidgetTable.WIDGET, newWidgetId)
            val result = sql.update(tableProvider.tableName, values,
                    " ${WidgetTable.WIDGET} = ? ", arrayOf("$oldWidgetId"))
            return result > 0
        }

        private fun getWidgetBySql(sqlStr: String, list: ArrayList<WidgetInfo>): Boolean {
            list.clear()
            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(sqlStr, null)
            while (c.moveToNext()) {
                list.add(WidgetTableProvider.createWidgetInfo(c))
            }
            c.close()
            return true
        }

        fun getShownWidget(list: ArrayList<WidgetInfo>): Boolean {
            return getWidgetBySql(WidgetTable.SELECT_SHOWN_WIDGET, list)
        }

        fun getHideWidget(list: ArrayList<WidgetInfo>): Boolean {
            return getWidgetBySql(WidgetTable.SELECT_HIDE_WIDGET, list)
        }

        fun getAshcanWidget(list: ArrayList<WidgetInfo>): Boolean {
            return getWidgetBySql(WidgetTable.SELECT_ASHCAN, list)
        }

        fun removeById(id: Int): Boolean {
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            values.put(WidgetTable.REMOVE, 1)
            val result = sql.update(tableProvider.tableName, values,
                    " ${WidgetTable.ID} = ? ", arrayOf("$id"))
            return result > 0
        }

    }

}