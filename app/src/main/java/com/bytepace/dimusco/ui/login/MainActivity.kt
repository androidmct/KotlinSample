package com.bytepace.dimusco.ui.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bytepace.dimusco.Activity.LibraryActivity
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.LoginResult
import com.bytepace.dimusco.data.repository.LoginCallback
import com.bytepace.dimusco.data.repository.UserRepository
import com.bytepace.dimusco.databinding.ActivityMainBinding
import com.bytepace.dimusco.helper.GlobalFunctions.Companion.openBrowser
import com.bytepace.dimusco.service.ConnectivityService
import com.bytepace.dimusco.service.LocalizationService
import com.bytepace.dimusco.ui.components.LanguageMenu
import com.bytepace.dimusco.utils.*
import com.gmail.samehadar.iosdialog.IOSDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.alert_create_reset_acc.view.*
import kotlinx.android.synthetic.main.alert_restore.view.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), LoginCallback, CoroutineScope{

    private val userRepository = UserRepository.get()
    private var iosDialog: IOSDialog? = null
    private var isLogged: Boolean = false

    private var job: Job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: LoginViewModel

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job + exceptionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setViewModelObservers()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = this@MainActivity.viewModel
        }

        iosDialog = IOSDialog.Builder(this)
            .setCancelable(false)
            .setSpinnerClockwise(false)
            .setMessageContentGravity(Gravity.END)
            .build()

        binding.btnLogin.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                onClickLogin()
                if(isLogged){
                    mStartActivity(LibraryActivity())
                    isLogged = false
                }
            }
        }

//        btn_locale.setOnClickListener { showLocaleMenu() }
//        checkBox.setOnClickListener {
//            if(checkBox.isChecked){
//                input_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            } else {
//                input_password.transformationMethod = PasswordTransformationMethod.getInstance()
//            }
//        }
    }

    private fun setViewModelObservers() {
        viewModel.apply {
            onClickLocaleEvent.observe(this@MainActivity) { showLocaleMenu() }
//            onClickRestoreEvent.observe(this@MainActivity) { showRestoreDialog() }
            onErrorEvent.observe(this@MainActivity) { onError(it) }
            onLanguageChangedEvent.observe(this@MainActivity) { refreshActivity() }
//            onLoginEvent.observe(this@MainActivity) { handleLogin() }
//            onClickCreateAccountEvent.observe(this@MainActivity) { redirectToBrowser() }
            onRestoredDataEvent.observe(this@MainActivity) { clearFields() }
        }
    }

    private fun refreshActivity(){
//        finish()
//        startActivity(intent)
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private fun clearFields() {
        binding.inputEmail.text = null
        binding.inputPassword.text = null
        this.cacheDir.deleteRecursively()
    }

    suspend fun onClickLogin() {

        val email = viewModel.email.get()?.trim()
        val password = viewModel.password.get()

        if (isInputValid(email, password)) {
            login(email!!, password!!)
        }

    }

    suspend fun <T> executePending(block: suspend () -> T): T =
        withContext(Dispatchers.IO) { block() }

    suspend fun login(email: String, password: String){
        iosDialog?.show()
        executePending {
            val savePassword = viewModel.rememberPassword.get()
            when (ConnectivityService.isConnected) {
                true -> userRepository.login(email, password, savePassword, this)
                else -> userRepository.loginOffline(email, password, savePassword, this)
            }
        }
    }

//    private fun showRestoreDialog() {
//        RestoreAppDialog(onRestoreClickListener)
//                .show(childFragmentManager, RestoreAppDialog::class.java.simpleName)
//    }

    private fun onError(error: String?) {
        val context = this@MainActivity
        when (error) {
            null -> showError(getTranslatedString(context, STR_GLOBAL_ERROR))
            STR_ERR_LOGIN_EMAIL -> showError(getTranslatedString(context, error))
            STR_ERR_LOGIN_PASSWORD -> showError(getTranslatedString(context, error))
            STR_ERR_LOGIN_OFFLINE -> showOfflineError()
            else -> showError(error)
        }
    }

    private fun showError(error: String) {
        this.window.decorView.let { Snackbar.make(it, error, Snackbar.LENGTH_SHORT).show() }
    }

    private fun showOfflineError() {
        val translatedString = getTranslatedString(this@MainActivity, STR_ERR_LOGIN_OFFLINE)
        Toast.makeText(this, translatedString, Toast.LENGTH_LONG).show()
    }

    fun onClickCreateAccount(v: View){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_create_reset_acc, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        val ssText = resources.getString(R.string.create_account_link_text)
        val ssLinkText = SpannableString(ssText)
        val indexStart = ssText.indexOf("dimusco.com")
        val indexEnd = indexStart + 11

        ssLinkText.setSpan(object : ClickableSpan(){
            override fun onClick(widget: View) {
                openBrowser("https://dimusco.com",this@MainActivity)
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
//                ds.isUnderlineText = false
                ds.color = resources.getColor(R.color.cl4)
            }
        }, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mDialogView.txt_alert_link.text = ssLinkText
        mDialogView.txt_alert_link.isClickable = true
        mDialogView.txt_alert_link.movementMethod = LinkMovementMethod.getInstance()

        val btnCRCancel = mDialogView.findViewById<Button>(R.id.btnCRCancel)
        btnCRCancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    fun onClickResetPassword(v: View){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_create_reset_acc, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        val ssText = resources.getString(R.string.forgot_password_link_txt)
        val ssLinkText = SpannableString(ssText)
        val indexStart = ssText.indexOf("dimusco.com")
        val indexEnd = indexStart + 11

        ssLinkText.setSpan(object : ClickableSpan(){
            override fun onClick(widget: View) {
                openBrowser("https://dimusco.com",this@MainActivity)
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
//                ds.isUnderlineText = false
                ds.color = resources.getColor(R.color.cl4)
            }
        }, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mDialogView.txt_alert_link.text = ssLinkText
        mDialogView.txt_alert_link.isClickable = true
        mDialogView.txt_alert_link.movementMethod = LinkMovementMethod.getInstance()

        val btnCRCancel = mDialogView.findViewById<Button>(R.id.btnCRCancel)
        btnCRCancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    fun onClickRestoreApp(v: View){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_restore, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()


        mDialogView.btn_reset.setOnClickListener {
            mAlertDialog?.dismiss()
        }

        mDialogView.btn_cancel.setOnClickListener {
            mAlertDialog?.dismiss()
        }
    }

    private fun mStartActivity(mActivity: Activity){
        val myIntent = Intent(this@MainActivity, mActivity::class.java)
        startActivity(myIntent)
        finish()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
                view is EditText && !view.javaClass.name.startsWith("android.webkit.")) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                    Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                    this.window.decorView.applicationWindowToken,
                    0
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onLogin(result: LoginResult) {
        iosDialog?.dismiss()
        when (result) {
            is LoginResult.Success -> {
                isLogged = true
                viewModel.onLoginEvent.postValue(Any())
            }
            is LoginResult.Error -> result.error?.let { viewModel.onErrorEvent.postValue(it) }
        }
    }

    private fun isInputValid(email: String?, password: String?): Boolean {
        return when {
            email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                viewModel.onErrorEvent.value = STR_ERR_LOGIN_EMAIL
                false
            }
            password.isNullOrEmpty() -> {
                viewModel.onErrorEvent.value = STR_ERR_LOGIN_PASSWORD
                false
            }
            else -> true
        }
    }

    private fun showLocaleMenu(){
        val languages = viewModel.languages ?: return
        val anchor = binding.btnLocale
        LanguageMenu(this, anchor, languages.toSortedMap(), viewModel.language) {
            viewModel.setLanguage(it)
        }.show()
    }
}