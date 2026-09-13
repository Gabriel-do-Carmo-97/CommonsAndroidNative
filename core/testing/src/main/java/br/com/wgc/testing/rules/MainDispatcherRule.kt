package br.com.wgc.testing.rules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Regra JUnit 4 reutilizável para gerenciar o despachante principal (`Dispatchers.Main`) em testes de unidade.
 *
 * Configura o [Dispatchers.setMain] antes de cada método de teste com o [testDispatcher] fornecido
 * e restaura o despachante original com [Dispatchers.resetMain] após a conclusão do teste,
 * garantindo isolamento total entre suítes de testes assíncronos.
 *
 * @property testDispatcher O [TestDispatcher] usado para simular a thread principal. Por padrão, utiliza [UnconfinedTestDispatcher].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}
