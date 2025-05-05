package com.cornellappdev.resell.android.model.classes

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

data class ResellFilter(
    val priceRange: IntRange = 0..1000,
    val itemsOnSale: Boolean = false,
    val categoriesSelected: List<FilterCategory> = emptyList(),
    val conditionSelected: FilterCondition? = null,
    val sortBy: SortBy = SortBy.ANY
)