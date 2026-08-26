package com.example.ridepricematcher.domain.model

data class PriceRule(
    val minimumFare: Double? = null,
    val maximumFare: Double? = null,
    val exactFare: Double? = null,
    val preferredMin: Double? = null,
    val preferredMax: Double? = null,
    val minFarePerKm: Double? = null,
    val distanceRangeKm: ClosedFloatingPointRange<Double>? = null,
    val durationMinutes: Int? = null,
    val currency: String = "INR"
) {
    fun isValid(): Boolean {
        return minimumFare != null || maximumFare != null || exactFare != null ||
                (preferredMin != null && preferredMax != null)
    }

    fun matches(price: PriceResult): Boolean {
        if (price.currency != currency) return false

        exactFare?.let { if (price.amount != it) return false }
        minimumFare?.let { if (price.amount < it) return false }
        maximumFare?.let { if (price.amount > it) return false }
        preferredMin?.let { if (price.amount < it) return false }
        preferredMax?.let { if (price.amount > it) return false }

        return true
    }
}
