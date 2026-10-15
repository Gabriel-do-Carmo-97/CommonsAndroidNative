package br.com.wgc.commonsandroidnative.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import br.com.wgc.design_system.core.WgcBrandTheme

/**
 * Tema unificado e motor de White-Labeling do monorepo CommonsAndroidNative.
 *
 * Suporta o motor de marcas [WgcBrandTheme] do `DesignSystemAndroid 0.25.0`, garantindo compatibilidade total
 * com Dark Mode, contraste elevado (WCAG AAA) e suporte nativo a cores dinâmicas (Material You) no Android 12+.
 *
 * ### 📌 Quando Usar:
 * Envolva a árvore raiz da aplicação (`MainActivity` ou fluxos isolados) com este tema para garantir
 * que todos os componentes, templates e telas do monorepo reflitam a paleta de cores corporativa da marca ativa.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Uso padrão com marca do gerenciador:
 * CommonsAndroidNativeTheme {
 *     MainAppShowcaseScreen(...)
 * }
 *
 * // 2. Uso com marca personalizada explícita:
 * CommonsAndroidNativeTheme(brand = WgcBrandTheme.finance()) {
 *     PaymentScreen(...)
 * }
 * ```
 *
 * @param brand Marca corporativa [WgcBrandTheme] a ser aplicada. O padrão é a marca do [WgcBrandThemeManager].
 * @param darkTheme Define se o esquema de cores escuro deve ser adotado. O padrão respeita o sistema operacional.
 * @param highContrast Define se regras de contraste máximo (WCAG AAA) devem ser forçadas em superfícies e bordas.
 * @param dynamicColor Habilita cores dinâmicas (Material You) a partir do Android 12 (SDK 31+). Padrão falso para preservar a identidade da marca.
 * @param content Conteúdo Composable a ser renderizado sob as regras do tema.
 */
@Composable
fun CommonsAndroidNativeTheme(
    brand: WgcBrandTheme = WgcBrandThemeManager.getBrand(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val brandPrimary = Color(brand.primaryColor)

    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> {
            darkColorScheme(
                primary = brandPrimary,
                onPrimary = if (highContrast) Color.White else Color.Black,
                primaryContainer = brandPrimary.copy(alpha = 0.25f),
                onPrimaryContainer = Color.White,
                secondary = Color(brand.secondaryColor),
                onSecondary = Color.White,
                background = if (highContrast) Color.Black else Color(0xFF121212),
                onBackground = Color(0xFFE0E0E0),
                surface = if (highContrast) Color(0xFF000000) else Color(0xFF1E1E1E),
                onSurface = Color(0xFFEEEEEE),
                outline = if (highContrast) Color.White else Color(0xFF888888)
            )
        }
        else -> {
            lightColorScheme(
                primary = brandPrimary,
                onPrimary = Color.White,
                primaryContainer = brandPrimary.copy(alpha = 0.12f),
                onPrimaryContainer = brandPrimary,
                secondary = Color(brand.secondaryColor),
                onSecondary = Color.White,
                background = if (highContrast) Color.White else Color(0xFFFAFAFA),
                onBackground = if (highContrast) Color.Black else Color(0xFF1C1B1F),
                surface = Color.White,
                onSurface = if (highContrast) Color.Black else Color(0xFF1C1B1F),
                outline = if (highContrast) Color.Black else Color(0xFF79747E)
            )
        }
    }

    CompositionLocalProvider(LocalWgcBrandTheme provides brand) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}