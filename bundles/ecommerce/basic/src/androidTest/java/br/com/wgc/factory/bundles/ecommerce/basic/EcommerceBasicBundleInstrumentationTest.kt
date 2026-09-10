package br.com.wgc.factory.bundles.ecommerce.basic

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EcommerceBasicBundleInstrumentationTest {

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(appContext)
        EcommerceBasicBundle.initialize(appContext)
        assertEquals("ecommerce", EcommerceBasicBundle.DOMAIN)
    }
}
