package com.cornellappdev.resell.android.model.classes

import com.cornellappdev.resell.android.model.api.FilterRequest
import com.cornellappdev.resell.android.model.api.PriceRange
import kotlin.collections.map


data class ResellFilter(
    val priceRange: IntRange = 0..1000,
    val itemsOnSale: Boolean = false,
    val categoriesSelected: List<FilterCategory> = emptyList(),
    val conditionSelected: FilterCondition? = null,
    val sortBy: SortBy = SortBy.ANY
){
    enum class SortBy(val label: String) {
        ANY("Any"),
        NEWLY_LISTED("Newly Listed"),
        PRICE_HIGH_TO_LOW("Price: High to Low"),
        PRICE_LOW_TO_HIGH("Price: Low to High")
    }

    enum class FilterCondition(val label: String) {
        GENTLY_USED("Gently Used"),
        WORN("Worn"),
        NEVER_USED("Never Used")
    }

    enum class FilterCategory(val label: String) {
        CLOTHING("Clothing"),
        BOOKS("Books"),
        SCHOOL("School"),
        ELECTRONICS("Electronics"),
        HANDMADE("Handmade"),
        SPORTS("Sports & Outdoors"),
        OTHER("Other")
    }
}

fun ResellFilter.toFilterRequest(): FilterRequest {
    return FilterRequest(
        price = PriceRange(
            lowerBound = priceRange.first.toDouble(),
            upperBound = priceRange.last.toDouble()
        ),
        condition = when (conditionSelected) {
            ResellFilter.FilterCondition.GENTLY_USED -> "gentlyUsed"
            ResellFilter.FilterCondition.NEVER_USED -> "new"
            ResellFilter.FilterCondition.WORN -> "worn"
            null -> null
        },
        categories = if (categoriesSelected.isEmpty()) null else categoriesSelected.map { it.label },
        sortField = when (sortBy) {
            ResellFilter.SortBy.ANY -> "any"
            ResellFilter.SortBy.PRICE_LOW_TO_HIGH -> "priceLowToHigh"
            ResellFilter.SortBy.PRICE_HIGH_TO_LOW -> "priceHighToLow"
            ResellFilter.SortBy.NEWLY_LISTED -> "newlyListed"
        }
    )
}