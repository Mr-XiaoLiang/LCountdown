package liang.lollipop.lcountdown.util

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import liang.lollipop.lcountdown.info.JsonInfo

/**
 * @author lollipop
 * @date 10/15/20 15:50
 */
abstract class BaseDBUtil<T: JsonInfo> (context: Context, dbName: String, dbVersion: Int): SQLiteOpenHelper(
        context, dbName, null, dbVersion) {

    interface TableProvider<T: JsonInfo> {
        val tableName: String
        val idName: String
        fun getSelectSql(): String
        fun getSelectByIdSql(): String
        fun putData(info: T, contentValues: ContentValues)
        fun getId(info: T): String
        fun createInfo(cursor: Cursor): T
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createSql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    abstract val createSql: String

    open class SqlDB<T: JsonInfo> constructor(
            private var databaseHelper: BaseDBUtil<T>?,
            val tableProvider: TableProvider<T>,
            isWritable: Boolean) {

        private var sqLiteDatabase: SQLiteDatabase? = null

        init {
            if (isWritable) {
                this.sqLiteDatabase = databaseHelper?.writableDatabase
            } else {
                this.sqLiteDatabase = databaseHelper?.readableDatabase
            }
        }

        protected fun getSqLiteDatabase(): SQLiteDatabase {
            if (sqLiteDatabase == null) {
                throw RuntimeException("SQLiteDatabase was close")
            }
            return sqLiteDatabase!!
        }

        fun add(info: T): Boolean {
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            values.putData(info)
            val result = sql.insert(tableProvider.tableName, "", values)
            return result >= 0
        }

        fun update(info: T): Boolean {
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            values.putData(info)
            val result = sql.update(tableProvider.tableName, values,
                    " ${tableProvider.idName} = ? ", arrayOf(tableProvider.getId(info)))
            return result >= 0
        }

        fun delete(id: Int): Boolean {
            val result = getSqLiteDatabase().delete(tableProvider.tableName,
                    " ${tableProvider.idName} = ? ", arrayOf("$id"))
            return result >= 0
        }

        fun find(info: T): Boolean {
            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(tableProvider.getSelectByIdSql(), arrayOf(tableProvider.getId(info)))
            var isRight = false
            if (c.moveToNext()) {
                info.copy(tableProvider.createInfo(c))
                isRight = true
            }
            c.close()
            return isRight
        }

        fun getAll(list: ArrayList<T>): Boolean {
            list.clear()
            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(tableProvider.getSelectSql(), null)
            while (c.moveToNext()) {
                list.add(tableProvider.createInfo(c))
            }
            c.close()
            return true
        }

        fun close() {
            sqLiteDatabase!!.close()
            sqLiteDatabase = null
            databaseHelper!!.close()
            databaseHelper = null
        }

        private fun ContentValues.putData(info: T) {
            clear()
            tableProvider.putData(info, this)
        }

    }

}