package liang.lollipop.lcountdown.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import liang.lollipop.lcountdown.bean.WidgetBean

/**
 * 小部件的数据库操作类
 */
class WidgetDBUtil private constructor(context: Context): SQLiteOpenHelper(context,DB_NAME,null, VERSION)  {

    companion object {

        private const val DB_NAME = "WidgetDatabase"
        private const val VERSION = 6

        fun read(context: Context): SqlDB {
            return SqlDB(WidgetDBUtil(context), false)
        }

        fun write(context: Context): SqlDB {
            return SqlDB(WidgetDBUtil(context), true)
        }

        fun Boolean.b2i(): Int {
            return if(this){1}else{0}
        }

        fun Int.i2b(): Boolean {
            return this > 0
        }

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(WidgetTable.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion >= newVersion) {
            return
        }
        when(oldVersion){

            1 -> {
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.NO_TIME} INTEGER DEFAULT 0")
            }

            2 -> {
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.PREFIX_NAME} VARCHAR DEFAULT ''")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.SUFFIX_NAME} VARCHAR DEFAULT ''")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.DAY_UNIT} VARCHAR DEFAULT ''")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.HOUR_UNIT} VARCHAR DEFAULT ''")
            }

            3 -> {
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.PREFIX_FONT_SIZE} VARCHAR DEFAULT 16")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.NAME_FONT_SIZE} VARCHAR DEFAULT 24")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.SUFFIX_FONT_SIZE} VARCHAR DEFAULT 16")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.DAY_FONT_SIZE} VARCHAR DEFAULT 34")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.DAY_UNIT_FONT_SIZE} VARCHAR DEFAULT 16")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.HOUR_FONT_SIZE} VARCHAR DEFAULT 32")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.HOUR_UNIT_FONT_SIZE} VARCHAR DEFAULT 12")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.TIME_FONT_SIZE} VARCHAR DEFAULT 18")
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.SIGN_FONT_SIZE} VARCHAR DEFAULT 12")
            }

            4 -> {
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.NOT_COUNTDOWN} INTEGER DEFAULT 0")
            }

            5 -> {
                db?.execSQL("ALTER TABLE ${WidgetTable.TABLE} ADD ${WidgetTable.IN_ONE_DAY} INTEGER DEFAULT 0")
            }

        }

        onUpgrade(db,oldVersion+1,newVersion)

    }



    private object WidgetTable{

        // V1
        const val TABLE = "COUNTDOWN_TABLE"
        const val ID = "WIDGET_ID"
        const val END_TIME = "END_TIME"
        const val NAME = "NAME"
        const val STYLE = "STYLE"
        const val SIGN_VALUE = "SIGN_VALUE"
        const val WIDGET_INDEX = "WIDGET_INDEX"

        // V2
        const val NO_TIME = "NO_TIME"

        // V3
        const val PREFIX_NAME = "PREFIX_NAME"
        const val SUFFIX_NAME = "SUFFIX_NAME"
        const val DAY_UNIT = "DAY_UNIT"
        const val HOUR_UNIT = "HOUR_UNIT"

        // V4
        const val PREFIX_FONT_SIZE = "PREFIX_FONT_SIZE"
        const val NAME_FONT_SIZE = "NAME_FONT_SIZE"
        const val SUFFIX_FONT_SIZE = "SUFFIX_FONT_SIZE"
        const val DAY_FONT_SIZE = "DAY_FONT_SIZE"
        const val DAY_UNIT_FONT_SIZE = "DAY_UNIT_FONT_SIZE"
        const val HOUR_FONT_SIZE = "HOUR_FONT_SIZE"
        const val HOUR_UNIT_FONT_SIZE = "HOUR_UNIT_FONT_SIZE"
        const val TIME_FONT_SIZE = "TIME_FONT_SIZE"
        const val SIGN_FONT_SIZE = "SIGN_FONT_SIZE"

        // V5
        const val NOT_COUNTDOWN = "NOT_COUNTDOWN"

        // V6
        const val IN_ONE_DAY = "IN_ONE_DAY"

        const val ALL = (
                " $ID , " +
                " $END_TIME , " +
                " $NAME , " +
                " $STYLE , " +
                " $SIGN_VALUE , " +
                " $WIDGET_INDEX , " +

                " $NO_TIME , " +

                " $PREFIX_NAME , " +
                " $SUFFIX_NAME , " +
                " $DAY_UNIT , " +
                " $HOUR_UNIT , " +

                " $PREFIX_FONT_SIZE , " +
                " $NAME_FONT_SIZE , " +
                " $SUFFIX_FONT_SIZE , " +
                " $DAY_FONT_SIZE , " +
                " $DAY_UNIT_FONT_SIZE , " +
                " $HOUR_FONT_SIZE , " +
                " $HOUR_UNIT_FONT_SIZE , " +
                " $TIME_FONT_SIZE , " +
                " $SIGN_FONT_SIZE , " +

                " $NOT_COUNTDOWN , " +

                " $IN_ONE_DAY ")

        const val SELECT_ALL_SQL = " select $ALL from $TABLE order by $WIDGET_INDEX ;"

        const val SELECT_ONE_SQL = " select $ALL from $TABLE WHERE $ID = ? ;"

        const val CREATE_TABLE = "create table $TABLE ( " +
                " $NAME VARCHAR , " +
                " $ID INTEGER , " +
                " $END_TIME INTEGER , " +
                " $STYLE INTEGER , " +
                " $SIGN_VALUE VARCHAR , " +
                " $WIDGET_INDEX INTEGER , " +

                " $NO_TIME INTEGER , " +

                " $PREFIX_NAME VARCHAR , " +
                " $SUFFIX_NAME VARCHAR , " +
                " $DAY_UNIT VARCHAR , " +
                " $HOUR_UNIT VARCHAR , " +

                " $PREFIX_FONT_SIZE INTEGER , " +
                " $NAME_FONT_SIZE INTEGER , " +
                " $SUFFIX_FONT_SIZE INTEGER , " +
                " $DAY_FONT_SIZE INTEGER , " +
                " $DAY_UNIT_FONT_SIZE INTEGER , " +
                " $HOUR_FONT_SIZE INTEGER , " +
                " $HOUR_UNIT_FONT_SIZE INTEGER , " +
                " $TIME_FONT_SIZE INTEGER , " +
                " $SIGN_FONT_SIZE INTEGER , " +
                " $NOT_COUNTDOWN INTEGER , " +
                " $IN_ONE_DAY INTEGER " +
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
                bean.putData(c)
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
                bean.putData(c)
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
            values.putData(bean)
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
            values.putData(widgetBean)
            sql.update(WidgetTable.TABLE, values, " ${WidgetTable.ID} = ? ", arrayOf("${widgetBean.widgetId}"))
            return this
        }

        fun updateAll(list: List<WidgetBean>): SqlDB{
            val sql = getSqLiteDatabase()
            val values = ContentValues()
            sql.beginTransaction()
            try {
                for(widgetBean in list){
                    values.putData(widgetBean)
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

        private fun ContentValues.putData(widgetBean: WidgetBean){
            clear()
            put(WidgetTable.ID, widgetBean.widgetId)
            put(WidgetTable.NAME, widgetBean.countdownName)
            put(WidgetTable.END_TIME, widgetBean.endTime)
            put(WidgetTable.STYLE, widgetBean.widgetStyle.value)
            put(WidgetTable.SIGN_VALUE, widgetBean.signValue)
            put(WidgetTable.WIDGET_INDEX, widgetBean.index)

            put(WidgetTable.NO_TIME, widgetBean.noTime.b2i())

            put(WidgetTable.PREFIX_NAME,widgetBean.prefixName)
            put(WidgetTable.SUFFIX_NAME,widgetBean.suffixName)
            put(WidgetTable.DAY_UNIT,widgetBean.dayUnit)
            put(WidgetTable.HOUR_UNIT,widgetBean.hourUnit)

            put(WidgetTable.PREFIX_FONT_SIZE,widgetBean.prefixFontSize)
            put(WidgetTable.NAME_FONT_SIZE,widgetBean.nameFontSize)
            put(WidgetTable.SUFFIX_FONT_SIZE,widgetBean.suffixFontSize)
            put(WidgetTable.DAY_FONT_SIZE,widgetBean.dayFontSize)
            put(WidgetTable.DAY_UNIT_FONT_SIZE,widgetBean.dayUnitFontSize)
            put(WidgetTable.HOUR_FONT_SIZE,widgetBean.hourFontSize)
            put(WidgetTable.HOUR_UNIT_FONT_SIZE,widgetBean.hourUnitFontSize)
            put(WidgetTable.TIME_FONT_SIZE,widgetBean.timeFontSize)
            put(WidgetTable.SIGN_FONT_SIZE,widgetBean.signFontSize)

            put(WidgetTable.NOT_COUNTDOWN, widgetBean.noCountdown.b2i())

            put(WidgetTable.IN_ONE_DAY, widgetBean.inOneDay.b2i())
        }

        private fun WidgetBean.putData(c: Cursor){
            widgetId = c.getInt(c.getColumnIndex(WidgetTable.ID))
            countdownName = c.getString(c.getColumnIndex(WidgetTable.NAME))?:""
            endTime = c.getLong(c.getColumnIndex(WidgetTable.END_TIME))
            signValue = c.getString(c.getColumnIndex(WidgetTable.SIGN_VALUE))?:""
            index = c.getInt(c.getColumnIndex(WidgetTable.WIDGET_INDEX))

            noTime = c.getInt(c.getColumnIndex(WidgetTable.NO_TIME)).i2b()

            parseStyle(c.getInt(c.getColumnIndex(WidgetTable.STYLE)))
            prefixName = c.getString(c.getColumnIndex(WidgetTable.PREFIX_NAME))?:""
            suffixName = c.getString(c.getColumnIndex(WidgetTable.SUFFIX_NAME))?:""
            dayUnit = c.getString(c.getColumnIndex(WidgetTable.DAY_UNIT))?:""
            hourUnit = c.getString(c.getColumnIndex(WidgetTable.HOUR_UNIT))?:""

            prefixFontSize = c.getInt(c.getColumnIndex(WidgetTable.PREFIX_FONT_SIZE))
            nameFontSize = c.getInt(c.getColumnIndex(WidgetTable.NAME_FONT_SIZE))
            suffixFontSize = c.getInt(c.getColumnIndex(WidgetTable.SUFFIX_FONT_SIZE))
            dayFontSize = c.getInt(c.getColumnIndex(WidgetTable.DAY_FONT_SIZE))
            dayUnitFontSize = c.getInt(c.getColumnIndex(WidgetTable.DAY_UNIT_FONT_SIZE))
            hourFontSize = c.getInt(c.getColumnIndex(WidgetTable.HOUR_FONT_SIZE))
            hourUnitFontSize = c.getInt(c.getColumnIndex(WidgetTable.HOUR_UNIT_FONT_SIZE))
            timeFontSize = c.getInt(c.getColumnIndex(WidgetTable.TIME_FONT_SIZE))
            signFontSize = c.getInt(c.getColumnIndex(WidgetTable.SIGN_FONT_SIZE))

            noCountdown = c.getInt(c.getColumnIndex(WidgetTable.NOT_COUNTDOWN)).i2b()

            inOneDay = c.getInt(c.getColumnIndex(WidgetTable.IN_ONE_DAY)).i2b()
        }

    }



}