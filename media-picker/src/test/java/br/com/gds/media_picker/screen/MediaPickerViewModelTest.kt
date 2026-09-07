package br.com.gds.media_picker.screen

import android.net.Uri
import app.cash.turbine.test
import br.wgc.omnibackend.core.repository.StorageRepository
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.InputStream

private class FakeStorageRepository(private val resultUri: Uri) : StorageRepository {
    override fun uploadFile(path: String, fileData: ByteArray): Flow<DataResult<Uri>> = emptyFlow()
    override fun uploadFile(path: String, fileUri: Uri): Flow<DataResult<Uri>> = flowOf(DataResult.Success(resultUri))
    override fun uploadFile(path: String, inputStream: InputStream): Flow<DataResult<Uri>> = emptyFlow()
    override suspend fun uploadFileDirect(path: String, fileData: ByteArray): DataResult<Uri> = DataResult.Success(resultUri)
    override suspend fun uploadFileDirect(path: String, fileUri: Uri): DataResult<Uri> = DataResult.Success(resultUri)
    override suspend fun getDownloadUrl(path: String): DataResult<Uri> = DataResult.Success(resultUri)
    override suspend fun delete(path: String): DataResult<Unit> = DataResult.Success(Unit)
}

@OptIn(ExperimentalCoroutinesApi::class)
class MediaPickerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val mockUri = mockk<Uri>(relaxed = true)
    private val uploadedUri = mockk<Uri>(relaxed = true)
    private lateinit var storageRepository: StorageRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        storageRepository = FakeStorageRepository(uploadedUri)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onMediaSelected updates state with selected uri`() = runTest {
        val viewModel = MediaPickerViewModel(storageRepository)
        viewModel.onMediaSelected(mockUri)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(mockUri, state.selectedMediaUri)
        }
    }

    @Test
    fun `uploadMedia emits uploaded url on success`() = runTest {
        val viewModel = MediaPickerViewModel(storageRepository)
        viewModel.onMediaSelected(mockUri)
        viewModel.uploadMedia("test_path")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(uploadedUri.toString(), state.uploadedUrl)
            assertFalse(state.isUploading)
        }
    }
}
