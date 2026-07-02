package com.example.studymateandroidapp

import com.example.studymateandroidapp.data.repository.AuthRepository
import com.example.studymateandroidapp.utils.sync.SyncManager
import com.example.studymateandroidapp.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

import com.example.studymateandroidapp.data.local.PreferenceManager

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val authRepository: AuthRepository = mock()
    private val syncManager: SyncManager = mock()
    private val preferenceManager: PreferenceManager = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Set the Main dispatcher to our test dispatcher for ViewModelScope
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(authRepository, syncManager, preferenceManager)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original
        Dispatchers.resetMain()
    }

    @Test
    fun `signInWithEmail success updates state to Success and triggers sync`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        whenever(authRepository.signInWithEmail(email, password)).thenReturn(Result.success(Unit))

        viewModel.signInWithEmail(email, password)
        
        // Assert initial state is Loading
        assertEquals(AuthViewModel.AuthUiState.Loading, viewModel.uiState.value)
        
        // Execute the coroutine
        advanceUntilIdle()

        // Assert final state is Success
        assertEquals(AuthViewModel.AuthUiState.Success, viewModel.uiState.value)
        verify(syncManager).triggerImmediateSync()
    }

    @Test
    fun `signInWithEmail failure updates state to Error`() = runTest {
        val email = "test@example.com"
        val password = "wrong_password"
        val errorMessage = "Invalid credentials"
        whenever(authRepository.signInWithEmail(email, password))
            .thenReturn(Result.failure(Exception(errorMessage)))

        viewModel.signInWithEmail(email, password)
        advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assertTrue(currentState is AuthViewModel.AuthUiState.Error)
        assertEquals(errorMessage, (currentState as AuthViewModel.AuthUiState.Error).message)
        verify(syncManager, never()).triggerImmediateSync()
    }

    @Test
    fun `signUpWithEmail success updates state to Success and triggers sync`() = runTest {
        val email = "new@example.com"
        val password = "new_password"
        whenever(authRepository.signUpWithEmail(email, password)).thenReturn(Result.success(Unit))

        viewModel.signUpWithEmail(email, password)
        advanceUntilIdle()

        assertEquals(AuthViewModel.AuthUiState.Success, viewModel.uiState.value)
        verify(syncManager).triggerImmediateSync()
    }

    @Test
    fun `resetPassword success updates state to Success`() = runTest {
        val email = "reset@example.com"
        whenever(authRepository.sendPasswordResetEmail(email)).thenReturn(Result.success(Unit))

        viewModel.resetPassword(email)
        advanceUntilIdle()

        assertEquals(AuthViewModel.AuthUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `clearState resets state to Idle`() = runTest {
        // First set a state other than Idle
        whenever(authRepository.signInWithEmail(any(), any())).thenReturn(Result.success(Unit))
        viewModel.signInWithEmail("test@test.com", "pass")
        advanceUntilIdle()
        
        // Verify it changed
        assertEquals(AuthViewModel.AuthUiState.Success, viewModel.uiState.value)

        // Reset
        viewModel.clearState()

        // Verify it's back to Idle
        assertEquals(AuthViewModel.AuthUiState.Idle, viewModel.uiState.value)
    }
}
