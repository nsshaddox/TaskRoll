package com.nshaddox.randomtask.ui.screens.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.nshaddox.randomtask.domain.model.SortOrder
import com.nshaddox.randomtask.domain.model.ThemeVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dataStoreScope: TestScope
    private lateinit var dataStore: DataStore<Preferences>

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dataStoreScope = TestScope(testDispatcher + Job())
        dataStore =
            PreferenceDataStoreFactory.create(
                scope = dataStoreScope,
                produceFile = { tempFolder.newFile("test_settings.preferences_pb") },
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(store: DataStore<Preferences> = dataStore) =
        SettingsViewModel(
            dataStore = store,
            ioDispatcher = testDispatcher,
        )

    @Test
    fun `initial state emits SettingsUiState defaults`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                val initial = awaitItem()
                assertEquals(ThemeVariant.OBSIDIAN, initial.appTheme)
                assertEquals(SortOrder.CREATED_DATE_DESC, initial.sortOrder)
            }
        }

    @Test
    fun `setTheme VAPOR updates uiState appTheme to VAPOR`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial defaults
                awaitItem()

                viewModel.setTheme(ThemeVariant.VAPOR)

                val updated = awaitItem()
                assertEquals(ThemeVariant.VAPOR, updated.appTheme)
            }
        }

    @Test
    fun `setSortOrder TITLE_ASC updates uiState sortOrder to TITLE_ASC`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Initial defaults
                awaitItem()

                viewModel.setSortOrder(SortOrder.TITLE_ASC)

                val updated = awaitItem()
                assertEquals(SortOrder.TITLE_ASC, updated.sortOrder)
            }
        }

    @Test
    fun `state persists across new VM instance reading same DataStore`() =
        runTest(testDispatcher) {
            val viewModel1 = createViewModel()

            viewModel1.uiState.test {
                // Initial defaults
                awaitItem()

                viewModel1.setTheme(ThemeVariant.NEO_BRUTALIST)
                viewModel1.setSortOrder(SortOrder.PRIORITY_DESC)

                // Consume updates until we see both changes applied
                var state = awaitItem()
                if (state.sortOrder != SortOrder.PRIORITY_DESC) {
                    state = awaitItem()
                }
                assertEquals(ThemeVariant.NEO_BRUTALIST, state.appTheme)
                assertEquals(SortOrder.PRIORITY_DESC, state.sortOrder)
            }

            // Create a second VM from the same DataStore
            val viewModel2 = createViewModel()

            viewModel2.uiState.test {
                val initial = awaitItem()
                // May be defaults briefly; await the DataStore-loaded state
                if (initial.appTheme == ThemeVariant.OBSIDIAN) {
                    val loaded = awaitItem()
                    assertEquals(ThemeVariant.NEO_BRUTALIST, loaded.appTheme)
                    assertEquals(SortOrder.PRIORITY_DESC, loaded.sortOrder)
                } else {
                    assertEquals(ThemeVariant.NEO_BRUTALIST, initial.appTheme)
                    assertEquals(SortOrder.PRIORITY_DESC, initial.sortOrder)
                }
            }
        }
}
