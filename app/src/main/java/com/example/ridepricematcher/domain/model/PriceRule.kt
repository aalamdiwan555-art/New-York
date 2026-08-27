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
    /** Returns false for malformed persisted settings instead of matching an invalid rule. */
    fun isValid(): Boolean {
        val fares = listOf(minimumFare, maximumFare, exactFare, preferredMin, preferredMax, minFarePerKm)
        if (fares.any { value -> value != null && (!value.isFinite() || value < 0.0) }) return false
        if (minimumFare != null && maximumFare != null && minimumFare > maximumFare) return false
        if (preferredMin != null && preferredMax != null && preferredMin > preferredMax) return false
        if (distanceRangeKm != null &&
            (!distanceRangeKm.start.isFinite() || !distanceRangeKm.endInclusive.isFinite() ||
                distanceRangeKm.start < 0.0 || distanceRangeKm.endInclusive < distanceRangeKm.start)
        ) return false
        if (durationMinutes != null && durationMinutes < 0) return false
        if (currency.isBlank()) return false
        return minimumFare != null || maximumFare != null || exactFare != null ||
            (preferredMin != null && preferredMax != null)
    }

    fun matches(price: PriceResult): Boolean {
        if (!isValid() || price.currency != currency) return false
        exactFare?.let { if (price.amount != it) return false }
        minimumFare?.let { if (price.amount < it) return false }
        maximumFare?.let { if (price.amount > it) return false }
        preferredMin?.let { if (price.amount < it) return false }
        preferredMax?.let { if (price.amount > it) return false }
        return true
    }
}
