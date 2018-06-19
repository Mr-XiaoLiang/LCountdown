package liang.lollipop.lcountdown.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import liang.lollipop.lcountdown.bean.TimingBean

/**
 * 计时的数据库操作类
 */
class TimingDBUtil private constructor(context: Context): SQLiteOpenHelper(context,DB_NAME,null, VERSION)  {

    companion object {

        private const val DB_NAME = "TimingDatabase"
        private const val VERSION = 1

        fun read(context: Context): SqlDB {
            return SqlDB(TimingDBUtil(context), false)
        }

        fun write(context: Context): SqlDB {
            return SqlDB(TimingDBUtil(context), true)
        }

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TimingTable.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    private object TimingTable{

        const val TABLE = "TIMING_TABLE"
        const val ID = "TIMING_ID"
        const val START_TIME = "START_TIME"
        const val END_TIME = "END_TIME"
        const val NAME = "NAME"

        const val SELECT_ALL_SQL = " select " +
                " $ID , " +
                " $END_TIME , " +
                " $START_TIME , " +
                " $NAME " +
                " from $TABLE order by $END_TIME ASC , $ID DESC ;"

        const val SELECT_ONE_SQL = " select " +
                " $ID , " +
                " $END_TIME , " +
                " $START_TIME , " +
                " $NAME " +
                " from $TABLE WHERE $ID = ? ;"

        const val CREATE_TABLE = "create table $TABLE ( " +
                " $NAME VARCHAR , " +
                " $ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " $END_TIME INTEGER , " +
                " $START_TIME INTEGER " +
                " );"

        const val DROP_TABLE = " DROP TABLE $TABLE "

        const val SELECT_LAST_ID = "select last_insert_rowid() from $TABLE"
    }

    class SqlDB constructor(private var wordDatabaseHelper: TimingDBUtil?, isWritable: Boolean) {
        private var sqLiteDatabase: SQLiteDatabase? = null

        init {
            if (isWritable) {
                this.sqLiteDatabase = wordDatabaseHelper!!.writableDatabase
            } else {
                this.sqLiteDatabase = wordDatabaseHelper!!.readableDatabase
            }
        }

        fun getAll(list: ArrayList<TimingBean>): SqlDB {
            list.clear()
            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(TimingTable.SELECT_ALL_SQL, null)
            while (c.moveToNext()) {
                val bean = TimingBean()
                bean.putData(c)
                list.add(bean)
            }
            c.close()
            return this
        }

        fun getAllId(idList: ArrayList<Int>): SqlDB{

            idList.clear()
            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(TimingTable.SELECT_ALL_SQL, null)
            while (c.moveToNext()) {
                idList.add(c.getInt(c.getColumnIndex(TimingTable.ID)))
            }
            c.close()

            return this
        }

        fun get(bean: TimingBean): SqlDB{

            val sql = getSqLiteDatabase()
            val c = sql.rawQuery(TimingTable.SELECT_ONE_SQL, arrayOf("${bean.id}"))
            if (c.moveToNext()) {
                bean.putData(c)
            }
            c.close()

            return this
        }

        fun deleteAll(): SqlDB {
            getSqLiteDatabase().delete(TimingTable.TABLE, null, null)
            return this
        }

        fun delete(id: Int): SqlDB {
            getSqLiteDatabase().delete(TimingTable.TABLE, " ${TimingTable.ID} = ? ", arrayOf("$id"))
            return this
        }

        fun add(bean: TimingBean): SqlDB{
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            values.putData(bean)
            values.remove(TimingTable.ID)
            sql.insert(TimingTable.TABLE, "", values)

            bean.id = 0
            val cursor = sql.rawQuery(TimingTable.SELECT_LAST_ID,null)?:return this
            if(cursor.moveToFirst()){
                bean.id = cursor.getInt(0)
            }
            cursor.close()
            return this
        }

        fun update(timingBean: TimingBean): SqlDB{
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            values.putData(timingBean)
            sql.update(TimingTable.TABLE, values, " ${TimingTable.ID} = ? ", arrayOf("${timingBean.id}"))
            return this
        }

        fun updateEndTime(id: Int): SqlDB{
            return updateEndTime(System.currentTimeMillis(),id)
        }

        fun updateEndTime(endTime: Long,id: Int): SqlDB{
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            values.put(TimingTable.END_TIME,endTime)
            sql.update(TimingTable.TABLE, values, " ${TimingTable.ID} = ? ", arrayOf("$id"))
            return this
        }

        fun updateAll(list: List<TimingBean>): SqlDB{
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            sql.beginTransaction()
            try {
                for(timingBean in list){
                    values.putData(timingBean)
                    sql.update(TimingTable.TABLE, values, " ${TimingTable.ID} = ? ", arrayOf("${timingBean.id}"))
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
            sqLiteDatabase?.close()
            sqLiteDatabase = null
            wordDatabaseHelper?.close()
            wordDatabaseHelper = null
        }

        private fun getSqLiteDatabase(): SQLiteDatabase {
            if (sqLiteDatabase == null) {
                throw RuntimeException("SQLiteDatabase was close")
            }
            return sqLiteDatabase!!
        }

        private fun ContentValues.putData(timingBean: TimingBean){
            clear()
            put(TimingTable.ID, timingBean.id)
            put(TimingTable.NAME, timingBean.name)
            put(TimingTable.END_TIME, timingBean.endTime)
            put(TimingTable.START_TIME, timingBean.startTime)
        }

        private fun TimingBean.putData(c: Cursor){
            id = c.getInt(c.getColumnIndex(TimingTable.ID))
            name = c.getString(c.getColumnIndex(TimingTable.NAME))?:""
            endTime = c.getLong(c.getColumnIndex(TimingTable.END_TIME))
            startTime = c.getLong(c.getColumnIndex(TimingTable.START_TIME))
        }

    }



}