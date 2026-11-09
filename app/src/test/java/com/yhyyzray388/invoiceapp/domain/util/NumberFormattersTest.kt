package com.yhyyzray388.invoiceapp.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberFormattersTest {

    @Test
    fun wholeRialAmountsUseThousandsSeparatorsWithoutDecimalPlaces() {
        assertEquals("60,000", formatAmount(60000.0))
        assertEquals("1,000", formatAmount(1000.0))
    }

    @Test
    fun wholeQuantitiesDoNotShowDecimalSuffix() {
        assertEquals("1", formatQuantity(1.0))
        assertEquals("23", formatQuantity(23.0))
        assertEquals("100", formatQuantity(100.0))
    }

    @Test
    fun meaningfulFractionalQuantitiesArePreserved() {
        assertEquals("1.25", formatQuantity(1.25))
    }
}
