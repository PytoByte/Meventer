package pachmp.meventer.components.auth.login.screens

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(start = true)
@NavGraph
annotation class LoginNavGraph(
    val start: Boolean = false
)