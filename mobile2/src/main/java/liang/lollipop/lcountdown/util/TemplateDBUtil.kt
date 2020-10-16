package liang.lollipop.lcountdown.util

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import liang.lollipop.lcountdown.info.TemplateInfo

/**
 * @author lollipop
 * @date 10/15/20 11:35
 * 小部件管理的数据库对象
 */
class TemplateDBUtil private constructor(context: Context): BaseDBUtil<TemplateInfo>(
        context, DB_NAME, BASE_VERSION + VERSION) {

    companion object {

        private const val BASE_VERSION = 0
        private const val DB_NAME = "TemplateDatabase"
        private const val VERSION = 1

        fun read(context: Context): SqlDB<TemplateInfo> {
            return SqlDB(TemplateDBUtil(context), TemplateTableProvider(), false)
        }

        fun write(context: Context): SqlDB<TemplateInfo> {
            return SqlDB(TemplateDBUtil(context), TemplateTableProvider(), true)
        }

    }

    private object TemplateTable {
        const val NAME = "Widget"
        const val KEY = "KEY"
        const val INFO = "INFO"

        const val CREATE_TABLE = "CREATE TABLE $NAME ( " +
                " $KEY VARCHAR , " +
                " $INFO VARCHAR " +
                " );"

        const val FIND_BY_KEY = " select $KEY, $INFO from $NAME WHERE $KEY = ? ;"

        const val SELECT_ALL = " select $KEY, $INFO from $NAME ;"
    }

    override val createSql: String = TemplateTable.CREATE_TABLE

    private class TemplateTableProvider: TableProvider<TemplateInfo> {
        override val tableName: String = TemplateTable.NAME

        override val idName: String = TemplateTable.KEY

        override fun getSelectSql(): String {
            return TemplateTable.SELECT_ALL
        }

        override fun getSelectByIdSql(): String {
            return TemplateTable.FIND_BY_KEY
        }

        override fun putData(info: TemplateInfo, contentValues: ContentValues) {
            contentValues.apply {
                put(TemplateTable.KEY, info.name)
                put(TemplateTable.INFO, info.toString())
            }
        }

        override fun getId(info: TemplateInfo): String {
            return info.name
        }

        override fun createInfo(cursor: Cursor): TemplateInfo {
            return TemplateInfo.createBy(cursor.getString(cursor.getColumnIndex(TemplateTable.INFO)))
        }

    }

}