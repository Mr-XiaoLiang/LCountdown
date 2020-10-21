package liang.lollipop.lcountdown.util

import liang.lollipop.lcountdown.R

/**
 * @author lollipop
 * @date 2020/6/26 21:34
 * 文字格式化
 */
object TextFormat {

    const val KEY_DAY_WITH_MONTH = "#DWM#"
    const val KEY_DAY_WITH_YEAR = "#DWY#"
    const val KEY_DAY_WITH_WEEK = "#DWW#"

    const val KEY_MONTH = "#MM#"
    const val KEY_MONTH_FULL = "#MF#"
    const val KEY_MONTH_JAPAN = "#MJ#"
    const val KEY_MONTH_ENGLISH = "#ME#"
    const val KEY_MONTH_CHINA = "#MC#"
    const val KEY_MONTH_TRADITIONAL = "#MT#"

    const val KEY_YEAR = "#YY#"
    const val KEY_YEAR_FULL = "#YF#"

    const val KEY_WEEK = "#WW#"
    const val KEY_WEEK_JAPAN = "#WJ#"
    const val KEY_WEEK_US = "#WU#"
    const val KEY_WEEK_NUM = "#WN#"
    const val KEY_WEEK_CHINA = "#WC#"

    const val KEY_HOUR = "#HH#"
    const val KEY_HOUR_FULL = "#HF#"

    const val KEY_MINUTE = "#MS#"
    const val KEY_MINUTE_FULL = "#MSF#"

    const val KEY_TIME_CHINA = "#TC#"
    const val KEY_TIME_ENGLISH = "#TE#"

    const val KEY_COUNTDOWN_DAYS = "#CD#"
    const val KEY_COUNTDOWN_DAY_OF_MONTH = "#CDM#"
    const val KEY_COUNTDOWN_DAY_OF_YEAR = "#CDY#"
    const val KEY_COUNTDOWN_DAY_OF_WEEK = "#CDW#"
    const val KEY_COUNTDOWN_HOUR = "#CH#"
    const val KEY_COUNTDOWN_HOUR_FULL = "#CHF#"
    const val KEY_COUNTDOWN_MINUTE = "#CM#"
    const val KEY_COUNTDOWN_MINUTE_FULL = "#CMF#"

    val KEYS = ReadOnlyArray.create(
            Part(R.string.key_name_day_with_month, KEY_DAY_WITH_MONTH),
            Part(R.string.key_name_day_with_year, KEY_DAY_WITH_YEAR),
            Part(R.string.key_name_day_with_week, KEY_DAY_WITH_WEEK),
            Part(R.string.key_name_month, KEY_MONTH),
            Part(R.string.key_name_month_full, KEY_MONTH_FULL),
            Part(R.string.key_name_month_japan, KEY_MONTH_JAPAN),
            Part(R.string.key_name_month_english, KEY_MONTH_ENGLISH),
            Part(R.string.key_name_month_china, KEY_MONTH_CHINA),
            Part(R.string.key_name_month_traditional, KEY_MONTH_TRADITIONAL),
            Part(R.string.key_name_year, KEY_YEAR),
            Part(R.string.key_name_year_full, KEY_YEAR_FULL),
            Part(R.string.key_name_week, KEY_WEEK),
            Part(R.string.key_name_week_japan, KEY_WEEK_JAPAN),
            Part(R.string.key_name_week_china, KEY_WEEK_US),
            Part(R.string.key_name_week_num, KEY_WEEK_NUM),
            Part(R.string.key_name_week_china, KEY_WEEK_CHINA),
            Part(R.string.key_name_hour, KEY_HOUR),
            Part(R.string.key_name_hour_full, KEY_HOUR_FULL),
            Part(R.string.key_name_minute, KEY_MINUTE),
            Part(R.string.key_name_minute_full, KEY_MINUTE_FULL),
            Part(R.string.key_name_time_china, KEY_TIME_CHINA),
            Part(R.string.key_name_time_english, KEY_TIME_ENGLISH),
            Part(R.string.key_name_countdown_days, KEY_COUNTDOWN_DAYS),
            Part(R.string.key_name_countdown_day_of_month, KEY_COUNTDOWN_DAY_OF_MONTH),
            Part(R.string.key_name_countdown_day_of_year, KEY_COUNTDOWN_DAY_OF_YEAR),
            Part(R.string.key_name_countdown_day_of_week, KEY_COUNTDOWN_DAY_OF_WEEK),
            Part(R.string.key_name_countdown_hour, KEY_COUNTDOWN_HOUR),
            Part(R.string.key_name_countdown_hour_full, KEY_COUNTDOWN_HOUR_FULL),
            Part(R.string.key_name_countdown_minute, KEY_COUNTDOWN_MINUTE),
            Part(R.string.key_name_countdown_minute_full, KEY_COUNTDOWN_MINUTE_FULL),
    )

    data class Part(val name: Int, val value: String)

}