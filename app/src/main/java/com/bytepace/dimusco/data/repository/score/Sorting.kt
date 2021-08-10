package com.bytepace.dimusco.data.repository.score

import com.bytepace.dimusco.utils.STR_GLOBAL_COMPOSER_ASC
import com.bytepace.dimusco.utils.STR_GLOBAL_COMPOSER_DESC
import com.bytepace.dimusco.utils.STR_GLOBAL_NAME_ASC
import com.bytepace.dimusco.utils.STR_GLOBAL_NAME_DESC

const val SORT_BY_COMPOSER_ASC = 0
const val SORT_BY_COMPOSER_DESC = 1
const val SORT_BY_NAME_ASC = 2
const val SORT_BY_NAME_DESC = 3

enum class Sorting(val label: String, val key: Int) {
    COMPOSER_ASC(STR_GLOBAL_COMPOSER_ASC, SORT_BY_COMPOSER_ASC),
    COMPOSER_DESC(STR_GLOBAL_COMPOSER_DESC, SORT_BY_COMPOSER_DESC),
    NAME_ASC(STR_GLOBAL_NAME_ASC, SORT_BY_NAME_ASC),
    NAME_DESC(STR_GLOBAL_NAME_DESC, SORT_BY_NAME_DESC)
}