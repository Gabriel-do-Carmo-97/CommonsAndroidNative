package br.com.wgc.authentication.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [EmulatorAndDebugDetector] validando detecção de emuladores e debuggers.
 */
class EmulatorAndDebugDetectorTest {

    private lateinit var detector: EmulatorAndDebugDetector

    @Before
    fun setup() {
        detector = EmulatorAndDebugDetector()
    }

    @Test
    fun `inspectEnvironment marks genuine physical device as safe in release`() {
        val physicalDevice = DeviceHardwareProfile(
            fingerprint = "google/pixel7/pixel7:14/UQ1A.240205.004/11269751:user/release-keys",
            model = "Pixel 7",
            manufacturer = "Google",
            hardware = "panther",
            product = "panther"
        )

        val result = detector.inspectEnvironment(
            isReleaseBuild = true,
            profile = physicalDevice,
            isDebuggerAttached = false
        )

        assertFalse(result.isEmulator)
        assertFalse(result.isDebuggerConnected)
        assertTrue(result.isSafeForRelease)
        assertTrue(result.detectedIndicators.isEmpty())
    }

    @Test
    fun `inspectEnvironment detects emulator keywords and marks unsafe in release`() {
        val emulatorDevice = DeviceHardwareProfile(
            fingerprint = "google/sdk_gphone64_x86_64/emulator:14/release-keys",
            model = "Android SDK built for x86_64",
            manufacturer = "Google",
            hardware = "ranchu",
            product = "sdk_gphone64_x86_64"
        )

        val result = detector.inspectEnvironment(
            isReleaseBuild = true,
            profile = emulatorDevice,
            isDebuggerAttached = false
        )

        assertTrue(result.isEmulator)
        assertFalse(result.isSafeForRelease)
        assertTrue(result.detectedIndicators.isNotEmpty())
    }

    @Test
    fun `inspectEnvironment marks unsafe in release when debugger is connected`() {
        val physicalDevice = DeviceHardwareProfile(
            fingerprint = "samsung/sm-g991b/galaxy:13/release-keys",
            model = "SM-G991B",
            manufacturer = "Samsung",
            hardware = "exynos2100",
            product = "o1s"
        )

        val result = detector.inspectEnvironment(
            isReleaseBuild = true,
            profile = physicalDevice,
            isDebuggerAttached = true
        )

        assertFalse(result.isEmulator)
        assertTrue(result.isDebuggerConnected)
        assertFalse(result.isSafeForRelease)
    }

    @Test
    fun `inspectEnvironment allows debugging and emulation in debug build`() {
        val emulatorDevice = DeviceHardwareProfile(
            fingerprint = "generic/emulator",
            model = "Emulator",
            manufacturer = "Unknown",
            hardware = "goldfish",
            product = "generic"
        )

        val result = detector.inspectEnvironment(
            isReleaseBuild = false,
            profile = emulatorDevice,
            isDebuggerAttached = true
        )

        assertTrue(result.isEmulator)
        assertTrue(result.isDebuggerConnected)
        assertTrue(result.isSafeForRelease) // In debug builds, development is permitted
    }
}
