package br.com.wgc.commonsandroidnative.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Testes unitários para modelos de dados de transição compartilhada [SharedProductItem].
 */
class WgcSharedElementTransitionTest {

    @Test
    fun `sharedProductItem creates with valid fields and generates consistent shared keys`() {
        val product = SharedProductItem(
            id = "item-42",
            title = "Combo Especial",
            price = "R$ 49,90",
            description = "Delicioso combo com lanche e bebida",
            category = "Combos"
        )

        assertNotNull(product)
        assertEquals("item-42", product.id)
        assertEquals("Combo Especial", product.title)
        assertEquals("R$ 49,90", product.price)

        val containerKey = "product-container-${product.id}"
        val imageKey = "product-image-${product.id}"
        val titleKey = "product-title-${product.id}"

        assertEquals("product-container-item-42", containerKey)
        assertEquals("product-image-item-42", imageKey)
        assertEquals("product-title-item-42", titleKey)
    }
}
