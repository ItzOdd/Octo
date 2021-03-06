package cegepst.example.octo.viewModels

import androidx.lifecycle.ViewModel
import cegepst.example.octo.R
import cegepst.example.octo.models.stored.User
import cegepst.example.octo.stores.AppStore
import cegepst.example.octo.views.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnexionViewModel: ViewModel() {

    private lateinit var database: AppStore
    private lateinit var activity: BaseActivity

    lateinit var signUpError: String
    lateinit var usernameTakenError: String
    lateinit var logInError: String

    fun initialize(activity: BaseActivity) {
        this.activity = activity
        this.database = AppStore(activity)
        this.signUpError = activity.getString(R.string.sign_up_error)
        this.usernameTakenError = activity.getString(R.string.username_taken_error)
        this.logInError = activity.getString(R.string.log_in_error)
    }

    fun update(user: User, lambda: () -> Unit) {
        GlobalScope.launch {
            database.userDAO().update(user)
            withContext(Dispatchers.Main) {
                lambda()
            }
        }
    }

    fun register(user: User, save: (User) -> Unit, proceed: () -> Unit) {
        database.userDAO().getByUsernameObserve(user.username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    activity.alert(usernameTakenError)
                },
                onError = {
                    val conclude = {
                        getSave(user, save, proceed)
                    }
                    insert(user, conclude)
                }
            )
    }

    private fun insert(user: User, conclude: () -> Unit) {
        GlobalScope.launch {
            database.userDAO().insert(user)
            withContext(Dispatchers.Main) {
                conclude()
            }
        }
    }

    private fun getSave(user: User, save: (User) -> Unit, proceed: () -> Unit) {
        GlobalScope.launch {
            val freshUser = database.userDAO().getByUsername(user.username)
            withContext(Dispatchers.Main) {
                save(freshUser)
                proceed()
            }
        }
    }

    private fun getUser(username: String, saveUser: (User) -> Unit, logUser: () -> Unit) {
        GlobalScope.launch {
            val user = database.userDAO().getByUsername(username)
            withContext(Dispatchers.Main) {
                saveUser(user)
                logUser()
            }
        }
    }

    fun log(user: User, saveUser: (User) -> Unit, logUser: () -> Unit) {
        database.userDAO().getByUsernamePassword(user.username, user.password)
                .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    getUser(user.username, saveUser, logUser)
                },
                onError = {
                    activity.alert(logInError)
                }
            )
    }

    fun getUserById(id: Long, callback: (User) -> Unit, activity: BaseActivity) {
        GlobalScope.launch {
            initialize(activity)
            val user = database.userDAO().getById(id)
            withContext(Dispatchers.Main) {
                callback(user)
            }
        }
    }
}
