package com.bytepace.dimusco.ui.login

import android.util.Patterns.EMAIL_ADDRESS
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.bytepace.dimusco.data.model.Language
import com.bytepace.dimusco.data.model.LoginResult
import com.bytepace.dimusco.data.repository.DBRepository
import com.bytepace.dimusco.data.repository.LoginCallback
import com.bytepace.dimusco.data.repository.UserRepository
import com.bytepace.dimusco.service.ConnectivityService
import com.bytepace.dimusco.service.LocalizationService
import com.bytepace.dimusco.ui.base.BaseViewModel
import com.bytepace.dimusco.utils.STR_ERR_LOGIN_EMAIL
import com.bytepace.dimusco.utils.STR_ERR_LOGIN_PASSWORD
import com.bytepace.dimusco.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel(), LoginCallback {

    val email = ObservableField<String>()
    val password = ObservableField<String>()
    val rememberPassword = ObservableBoolean()

    val showPassword = ObservableBoolean()
    val isLoading = ObservableBoolean()
    val language: String? get() = LocalizationService.instance.language
    val languages: Map<String, Language>? get() = LocalizationService.instance.languages
    val onClickLocaleEvent = SingleLiveEvent<Any>()
    val onClickRestoreEvent = SingleLiveEvent<Any>()
    val onErrorEvent = SingleLiveEvent<String>()
    val onLanguageChangedEvent = SingleLiveEvent<Any>()
    val onLoginEvent = SingleLiveEvent<Any>()
    val onClickCreateAccountEvent = SingleLiveEvent<Any>()
    val onRestoredDataEvent = SingleLiveEvent<Any>()

    private val userRepository = UserRepository.get()

    init {
        userRepository.clearSession()
        email.set(userRepository.getEmail())
        password.set(userRepository.getPassword())
        rememberPassword.set(!userRepository.getPassword().isNullOrEmpty())
    }

    fun onClickLocale() {
        onClickLocaleEvent.call()
    }

    fun setLanguage(language: String) {
        LocalizationService.instance.language = language
        onLanguageChangedEvent.call()
    }

    fun onClickLogin() {
        val email = email.get()?.trim()
        val password = password.get()

        if (isInputValid(email, password)) {
            login(email!!, password!!)
        }
    }

    fun onClickRestore() {
        onClickRestoreEvent.call()
    }

    fun toggleRememberPassword() {
        rememberPassword.set(!rememberPassword.get())
    }

    fun login(email: String, password: String) {
        executeOnUI {
            isLoading.set(true)
            executePending {
                val savePassword = rememberPassword.get()
                when (ConnectivityService.isConnected) {
                    true -> userRepository.login(email, password, savePassword, this)
                    else -> userRepository.loginOffline(email, password, savePassword, this)
                }
            }
        }
    }

    private fun isInputValid(email: String?, password: String?): Boolean {
        return when {
            email.isNullOrEmpty() || !EMAIL_ADDRESS.matcher(email).matches() -> {
                onErrorEvent.value = STR_ERR_LOGIN_EMAIL
                false
            }
            password.isNullOrEmpty() -> {
                onErrorEvent.value = STR_ERR_LOGIN_PASSWORD
                false
            }
            else -> true
        }
    }

    override fun onLogin(result: LoginResult) {
        isLoading.set(false)
        when (result) {
            is LoginResult.Success -> onLoginEvent.postValue(Any())
            is LoginResult.Error -> result.error?.let { onErrorEvent.postValue(it) }
        }
    }

    fun onClickCreateAccount() {
        onClickCreateAccountEvent.call()
    }

    fun restoreData() {
        viewModelScope.launch {
            DBRepository.clearDB()
            userRepository.logout()
            onRestoredDataEvent.call()
        }
    }
}