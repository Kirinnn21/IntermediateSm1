    package com.dutaram.intermediatesm1

    import android.content.Context
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import com.dutaram.intermediatesm1.data.pref.UserPreference
    import com.dutaram.intermediatesm1.di.Injection.provideRepository
    import com.dutaram.intermediatesm1.view.liststory.ListStoryViewModel
    import com.dutaram.intermediatesm1.view.login.LoginViewModel
    import com.dutaram.intermediatesm1.view.main.MainViewModel
    import com.dutaram.intermediatesm1.view.map.MapViewModel
    import com.dutaram.intermediatesm1.view.signup.SignUpViewModel
    import com.dutaram.intermediatesm1.view.story.AddStoryViewModel


    class ViewModelFactory(private val pref: UserPreference, private val context: Context) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                    MainViewModel(pref) as T
                }
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                    LoginViewModel(pref) as T
                }
                modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                    SignUpViewModel() as T
                }
                modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
                    ListStoryViewModel(pref, provideRepository(context)) as T
                }
                modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                    AddStoryViewModel(pref, context) as T
                }
                modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                    MapViewModel(pref) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }
    }