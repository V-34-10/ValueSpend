package com.finance.valuespend.utils

import androidx.annotation.StringRes
import com.finance.valuespend.R

data class CategoryOption(
    val id: String,
    @StringRes val labelRes: Int
)

val DefaultCategories: List<CategoryOption> = listOf(
    CategoryOption(id = "food", labelRes = R.string.category_food),
    CategoryOption(id = "transport", labelRes = R.string.category_transport),
    CategoryOption(id = "health", labelRes = R.string.category_health),
    CategoryOption(id = "home", labelRes = R.string.category_home),
    CategoryOption(id = "entertainment", labelRes = R.string.category_entertainment),
    CategoryOption(id = "education", labelRes = R.string.category_education),
    CategoryOption(id = "shopping", labelRes = R.string.category_shopping),
    CategoryOption(id = "other", labelRes = R.string.category_other)
)

