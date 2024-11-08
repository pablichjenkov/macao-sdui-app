package com.macaosoftware.plugin.account

import com.macaosoftware.util.MacaoResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * The real implementation of the interface FirebaseAuthKmpSwiftWrapper can be found in
 * the Swift Package hosted at:
 * https://github.com/pablichjenkov/firebase-kmp/blob/main/FirebaseAuthKmp/Sources/FirebaseAuthKmpWrapperImpl.swift
 * */
class FirebaseAccountPlugin(
    private val firebaseAuthKmpSwiftWrapper: FirebaseAuthKmpSwiftWrapper
) : AccountPlugin {

    private val TAG = "FirebaseAccountPlugin"

    override suspend fun initialize(): Boolean {
        return true
    }

    override suspend fun createUserWithEmailAndPassword(signUpRequest: SignUpRequest): MacaoResult<MacaoUser, SignupError> {

        return suspendCoroutine { continuation ->
            firebaseAuthKmpSwiftWrapper.createUserWithEmailAndPassword(
                email = signUpRequest.email,
                password = signUpRequest.password,
                onResult = {
                    continuation.resume(MacaoResult.Success(it))
                },
                onError = {
                    continuation.resume(MacaoResult.Error(SignupError(errorDescription = it)))
                }
            )
        }
    }

    override suspend fun signInWithEmailAndPassword(signInRequest: SignInRequest): MacaoResult<MacaoUser, LoginError> {

        return suspendCoroutine { continuation ->
            firebaseAuthKmpSwiftWrapper.signInWithEmailAndPassword(
                email = signInRequest.email,
                password = signInRequest.password,
                onResult = {
                    continuation.resume(MacaoResult.Success(it))
                },
                onError = {
                    continuation.resume(MacaoResult.Error(LoginError(errorDescription = it)))
                }
            )
        }
    }

    override suspend fun signInWithEmailLink(signInRequest: SignInRequestForEmailLink): MacaoResult<MacaoUser, LoginError> {

        return suspendCoroutine { continuation ->
            firebaseAuthKmpSwiftWrapper.signInWithEmailLink(
                email = signInRequest.email,
                magicLink = signInRequest.magicLink,
                onResult = {
                    continuation.resume(MacaoResult.Success(it))
                },
                onError = {
                    continuation.resume(MacaoResult.Error(LoginError(errorDescription = it)))
                }
            )
        }
    }

    override suspend fun sendSignInLinkToEmail(emailLinkData: EmailLinkData): MacaoResult<Unit, AccountPluginError> {
        println("IosFirebase_sendSignInLinkToEmail")
        return MacaoResult.Success(Unit)
    }

    override suspend fun getCurrentUser(): MacaoResult<MacaoUser, SignupError> {
        println("IosFirebase_getCurrentUser")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun getProviderData(): MacaoResult<ProviderData, SignupError> {
        println("IosFirebase_getProviderData")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun updateProfile(
        displayName: String,
        photoUrl: String
    ): MacaoResult<MacaoUser, SignupError> {
        println("IosFirebase_updateProfile")
        return MacaoResult.Error(SignupError(errorDescription = ""))
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
        println("IosFirebase_updateFullProfile")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun updateEmail(newEmail: String): MacaoResult<MacaoUser, SignupError> {
        println("IosFirebase_updateEmail")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun updatePassword(newPassword: String): MacaoResult<MacaoUser, SignupError> {
        println("IosFirebase_updatePassword")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun sendEmailVerification(): MacaoResult<MacaoUser, SignupError> {
        println("IosFirebase_sendEmailVerification")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun sendPasswordReset(): MacaoResult<MacaoUser, SignupError> {
        println("IosFirebase_sendPasswordReset")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun deleteUser(): MacaoResult<Unit, SignupError> {
        println("IosFirebase_deleteUser")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun fetchUserData(): MacaoResult<UserData, SignupError> {
        println("IosFirebase_fetchUserData")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun checkAndFetchUserData(): MacaoResult<UserData, SignupError> {
        println("IosFirebase_checkAndFetchUserData")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

    override suspend fun signOut(): MacaoResult<Unit, SignupError> {
        println("IosFirebase_logoutUser")
        return MacaoResult.Error(SignupError(errorDescription = ""))
    }

}
