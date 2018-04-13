package liang.lollipop.lcountdown.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import liang.lollipop.lcountdown.bean.WidgetBean

class WidgetDBUtil private constructor(context: Context): SQLiteOpenHelper(context,DB_NAME,null, VERSION)  {

    companion object {

        private const val DB_NAME = "WidgetDatabase"
        private const val VERSION = 1

        fun read(context: Context): SqlDB {
            return SqlDB(WidgetDBUtil(context), false)
        }

        fun write(context: Context): SqlDB {
            return SqlDB(WidgetDBUtil(context), true)
        }

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(WidgetTable.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    private object WidgetTable{

        const val TABLE = "COUNTDOWN_TABLE"
        const val ID = "WIDGET_ID"
        const val END_TIME = "END_TIME"
        const val NAME = "NAME"
        const val STYLE = "STYLE"
        const val SIGN_VALUE = "SIGN_VALUE"
        const val WIDGET_INDEX = "WIDGET_INDEX"

        const val SELECT_ALL_SQL = " select " +
                " $ID , " +
                " $END_TIME , " +
                " $NAME , " +
                " $STYLE , " +
                " $SIGN_VALUE , " +
                " $WIDGET_INDEX " +
                " from $TABLE order by $WIDGET_INDEX ;"

        const val SELECT_ONE_SQL = " select " +
                " $ID , " +
                " $END_TIME , " +
                " $NAME , " +
                " $STYLE , " +
                " $SIGN_VALUE , " +
                " $WIDGET_INDEX " +
                " from $TABLE WHERE $ID = ? ;"

        const val CREATE_TABLE = "create table $TABLE ( " +
                " $NAME VARCHAR , " +
                " $ID INTEGER , " +
                " $END_TIME INTEGER , " +
                " $STYLE INTEGER , " +
                " $SIGN_VALUE VARCHAR , " +
                " $WIDGET_INDEX INTEGER " +
                " );"

    }

    class SqlDB constructor(private var wordDatabaseHelper: WidgetDBUtil?, isWritable: Boolean) {
        private var sqLiteDatabase: SQLiteDatabase? = null

        init {
            if (isWritable) {
                this.sqLiteDatabase = wordDatabaseHelper!!.writableDatabase
            } else {
                this.sqLiteDatabase = wordDatabaseHelper!!.readableDatabase
            }
        }

        fun getAll(list: ArrayList<WidgetBean>): SqlDB {
            list.clear()
            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(WidgetTable.SELECT_ALL_SQL, null)
            while (c.moveToNext()) {
                val bean = WidgetBean()
                bean.widgetId = c.getInt(c.getColumnIndex(WidgetTable.ID))
                bean.countdownName = c.getString(c.getColumnIndex(WidgetTable.NAME))
                bean.signValue = c.getString(c.getColumnIndex(WidgetTable.SIGN_VALUE))
                bean.endTime = c.getLong(c.getColumnIndex(WidgetTable.END_TIME))
                bean.index = c.getInt(c.getColumnIndex(WidgetTable.WIDGET_INDEX))
                bean.parseStyle(c.getInt(c.getColumnIndex(WidgetTable.STYLE)))
                list.add(bean)
            }
            c.close()
            return this
        }

        fun getAllId(idList: ArrayList<Int>): SqlDB{

            idList.clear()
            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(WidgetTable.SELECT_ALL_SQL, null)
            while (c.moveToNext()) {
                idList.add(c.getInt(c.getColumnIndex(WidgetTable.ID)))
            }
            c.close()

            return this
        }

        fun get(bean: WidgetBean): SqlDB{

            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(WidgetTable.SELECT_ONE_SQL, arrayOf("${bean.widgetId}"))
            if (c.moveToNext()) {
                bean.widgetId = c.getInt(c.getColumnIndex(WidgetTable.ID))
                bean.countdownName = c.getString(c.getColumnIndex(WidgetTable.NAME))
                bean.endTime = c.getLong(c.getColumnIndex(WidgetTable.END_TIME))
                bean.signValue = c.getString(c.getColumnIndex(WidgetTable.SIGN_VALUE))
                bean.index = c.getInt(c.getColumnIndex(WidgetTable.WIDGET_INDEX))
                bean.parseStyle(c.getInt(c.getColumnIndex(WidgetTable.STYLE)))
            }
            c.close()

            return this
        }

        fun deleteAll(): SqlDB {
            getSqLiteDatabase().delete(WidgetTable.TABLE, null, null)
            return this
        }

        fun delete(id: Int): SqlDB {
            getSqLiteDatabase().delete(WidgetTable.TABLE, " ${WidgetTable.ID} = ? ", arrayOf("$id"))
            return this
        }

        fun add(bean: WidgetBean): SqlDB{
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            values.clear()
            values.put(WidgetTable.ID, bean.widgetId)
            values.put(WidgetTable.NAME, bean.countdownName)
            values.put(WidgetTable.END_TIME, bean.endTime)
            values.put(WidgetTable.STYLE, bean.widgetStyle.value)
            values.put(WidgetTable.SIGN_VALUE, bean.signValue)
            values.put(WidgetTable.WIDGET_INDEX, bean.index)
            sql.insert(WidgetTable.TABLE, "", values)
            return this
        }

//        fun addAll(list: ArrayList<WidgetBean>): SqlDB {
//            deleteAll()
//            val sql = getSqLiteDatabase()
//            sql.beginTransaction()
//            try {
//                val values = ContentValues()
//                for (value in list) {
//                    values.clear()
//                    values.put(WidgetTable.ID, value.widgetId)
//                    values.put(WidgetTable.NAME, value.countdownName)
//                    values.put(WidgetTable.END_TIME, value.endTime)
//                    values.put(WidgetTable.STYLE, value.widgetStyle.value)
//                    values.put(WidgetTable.SIGN_VALUE, value.signValue)
//                    values.put(WidgetTable.WIDGET_INDEX, value.index)
//                    sql.insert(WidgetTable.TABLE, "", values)
//                }
//                sql.setTransactionSuccessful()
//            } catch (e: Exception) {
//                Log.e("addAll", e.message)
//            } finally {
//                sql.endTransaction()
//            }
//            return this
//        }

        fun update(widgetBean: WidgetBean): SqlDB{
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            values.clear()
            values.put(WidgetTable.ID, widgetBean.widgetId)
            values.put(WidgetTable.NAME, widgetBean.countdownName)
            values.put(WidgetTable.END_TIME, widgetBean.endTime)
            values.put(WidgetTable.STYLE, widgetBean.widgetStyle.value)
            values.put(WidgetTable.SIGN_VALUE, widgetBean.signValue)
            values.put(WidgetTable.WIDGET_INDEX, widgetBean.index)
            sql.update(WidgetTable.TABLE, values, " ${WidgetTable.ID} = ? ", arrayOf("${widgetBean.widgetId}"))
            return this
        }

        fun updateAll(list: List<WidgetBean>): SqlDB{
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            sql.beginTransaction()
            try {
                for(widgetBean in list){
                    values.clear()
                    values.put(WidgetTable.ID, widgetBean.widgetId)
                    values.put(WidgetTable.NAME, widgetBean.countdownName)
                    values.put(WidgetTable.END_TIME, widgetBean.endTime)
                    values.put(WidgetTable.STYLE, widgetBean.widgetStyle.value)
                    values.put(WidgetTable.SIGN_VALUE, widgetBean.signValue)
                    values.put(WidgetTable.WIDGET_INDEX, widgetBean.index)
                    sql.update(WidgetTable.TABLE, values, " ${WidgetTable.ID} = ? ", arrayOf("${widgetBean.widgetId}"))
                }
                sql.setTransactionSuccessful()
            }catch (e: Exception) {
                Log.e("updateAll", e.message)
            } finally {
                sql.endTransaction()
            }

            return this
        }

        fun close() {
            sqLiteDatabase!!.close()
            sqLiteDatabase = null
            wordDatabaseHelper!!.close()
            wordDatabaseHelper = null
        }

        private fun getSqLiteDatabase(): SQLiteDatabase {
            if (sqLiteDatabase == null) {
                throw RuntimeException("SQLiteDatabase was close")
            }
            return sqLiteDatabase!!
        }

    }

}