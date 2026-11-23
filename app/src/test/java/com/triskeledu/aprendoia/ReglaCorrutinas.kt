package com.triskeledu.aprendoia

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Los ViewModel usan viewModelScope, que corre en Dispatchers.Main.
 * En pruebas JVM no existe el hilo principal de Android: esta regla lo
 * reemplaza por un dispatcher de prueba que ejecuta todo al instante.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReglaCorrutinas(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
