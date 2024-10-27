package com.macaosoftware.plugin.account

import com.macaosoftware.util.MacaoResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.SignOutScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

class SupabaseAccountPlugin : AccountPlugin {

    private val TAG = "SupabaseAuthPlugin"

    private val supabase: SupabaseClient by lazy {

        createSupabaseClient(
            supabaseUrl = "YOUR_URL",
            supabaseKey = "YOUR_KEY"
        ) {
            install(Auth) {
                //your config
            }
            /*install(ComposeAuth) {
                googleNativeLogin(serverClientId = "google-client-id")
                appleNativeLogin()
            }*/
        }
    }

    override suspend fun initialize(): Boolean {
        return true
    }

    override suspend fun createUserWithEmailAndPassword(signUpRequest: SignUpRequest): MacaoResult<MacaoUser, SignupError> = try {

        supabase.auth.signUpWith(Email) {
            email = "example@email.com"
            password = "example-password"
        }?.let {
            MacaoResult.Success(it.toMacaoUser())
        } ?: run {
            MacaoResult.Error(SignupError(errorDescription = "createUserWithEmailAndPassword:success but unexpected use null"))
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        MacaoResult.Error(SignupError(errorDescription = ex.message.orEmpty()))
    }

    override suspend fun signInWithEmailAndPassword(signInRequest: SignInRequest): MacaoResult<MacaoUser, LoginError> = try {

        withContext(Dispatchers.Main) {
            withTimeout(15.seconds) {
                supabase.auth
                    .sessionStatus
                    .onStart {
                        supabase.auth.signInWith(Email) {
                            email = "example@email.com"
                            password = "example-password"
                        }
                    }
                    .transformWhile<SessionStatus, UserInfo?> {
                        when (it) {
                            is SessionStatus.Authenticated -> {
                                val user = it.session.user
                                println(user)
                                emit(user)
                                false
                            }

                            SessionStatus.LoadingFromStorage -> {
                                println("Loading from storage")
                                true
                            }

                            SessionStatus.NetworkError -> {
                                println("Network error")
                                emit(null)
                                false
                            }

                            SessionStatus.NotAuthenticated -> {
                                println("Not authenticated")
                                true
                            }
                        }
                    }.firstOrNull()
            }.let { userInfo ->
                MacaoResult.Success(userInfo.toMacaoUser())
            }
        }

    } catch (ex: Exception) {
        ex.printStackTrace()
        MacaoResult.Error(LoginError(errorDescription = ex.message.orEmpty()))
    }

    override suspend fun signInWithEmailLink(signInRequest: SignInRequestForEmailLink): MacaoResult<MacaoUser, LoginError> = try {

        withTimeout(15.seconds) {
            supabase.auth
                .sessionStatus
                .onStart {
                    supabase.auth.signInWith(OTP) {
                        email = "example@email.com"
                    }
                }
                .transformWhile<SessionStatus, UserInfo?> {
                    when (it) {
                        is SessionStatus.Authenticated -> {
                            val user = it.session.user
                            println(user)
                            emit(user)
                            false
                        }

                        SessionStatus.LoadingFromStorage -> {
                            println("Loading from storage")
                            true
                        }

                        SessionStatus.NetworkError -> {
                            println("Network error")
                            emit(null)
                            false
                        }

                        SessionStatus.NotAuthenticated -> {
                            println("Not authenticated")
                            true
                        }
                    }
                }.firstOrNull()
        }.let { userInfo ->
            MacaoResult.Success(userInfo.toMacaoUser())
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        MacaoResult.Error(LoginError(errorDescription = ex.message.orEmpty()))
    }

    override suspend fun sendSignInLinkToEmail(emailLinkData: EmailLinkData): MacaoResult<Unit, AccountPluginError> = try {

        supabase.auth.verifyEmailOtp(
            type = OtpType.Email.MAGIC_LINK,
            email = "example@email.com",
            token = "magic_token"
        )

        MacaoResult.Success(Unit)
    } catch (ex: Exception) {
        ex.printStackTrace()
        MacaoResult.Error(SignupError(errorDescription = ex.message.orEmpty()))
    }

    override suspend fun getCurrentUser(): MacaoResult<MacaoUser, SignupError> = try {

        val user = supabase.auth.retrieveUserForCurrentSession(updateSession = true)
        MacaoResult.Success(user.toMacaoUser())
    } catch (ex: Exception) {
        ex.printStackTrace()
        MacaoResult.Error(SignupError(errorDescription = ex.message.orEmpty()))
    }

    override suspend fun getProviderData(): MacaoResult<ProviderData, SignupError> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProfile(displayName: String, photoUrl: String): MacaoResult<MacaoUser, SignupError> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFullProfile(
        displayName: String?,
        country: String?,
        photoUrl: String?,
        phoneNo: String?,
        facebookLink: String?,
        linkedIn: String?,
        github: String?
    ): MacaoResult<UserData, SignupError> {
        TODO("Not yet implemented")
    }

    override suspend fun updateEmail(newEmail: String): MacaoResult<MacaoUser, SignupError> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(newPassword: String): MacaoResult<MacaoUser, SignupError> {
        TODO("Not yet implemented")
    }

    override suspend fun sendEmailVerification(): MacaoResult<MacaoUser, SignupError> {
        TODO("Not yet implemented")
    }

    override suspend fun sendPasswordReset(): MacaoResult<MacaoUser, SignupError> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(): MacaoResult<Unit, SignupError> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchUserData(): MacaoResult<UserData, SignupError> {
        TODO("Not yet implemented")
    }

    override suspend fun checkAndFetchUserData(): MacaoResult<UserData, SignupError> {
        println("Supabase_checkAndFetchUserData")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun signOut(): MacaoResult<Unit, SignupError> = try {
        supabase.auth.signOut(SignOutScope.LOCAL)
        MacaoResult.Success(Unit)
    } catch (ex: Exception) {
        ex.printStackTrace()
        MacaoResult.Error(SignupError(errorDescription = ex.message.orEmpty()))
    }

    private fun Email.Result.toMacaoUser(): MacaoUser {
        return MacaoUser(email)
    }

    private fun UserInfo?.toMacaoUser(): MacaoUser {
        return MacaoUser(this?.email.orEmpty())
    }
}
