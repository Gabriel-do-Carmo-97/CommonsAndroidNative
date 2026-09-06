package br.com.gds.commonsandroidnative

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import br.com.gds.authentication.navigation.AuthNavigation
import br.com.gds.commonsandroidnative.ui.theme.CommonsAndroidNativeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommonsAndroidNativeTheme {
                AuthNavigation(
                    modifier = Modifier.fillMaxSize(),
                    authSuccess = {
                        Toast.makeText(this, "Autenticação realizada com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}