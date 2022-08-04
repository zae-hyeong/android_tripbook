package com.olympos.tripbook.src.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.olympos.tripbook.R
import com.olympos.tripbook.config.BaseActivity
import com.olympos.tripbook.databinding.ActivityUserSigninBinding
import com.olympos.tripbook.src.home.MainActivity
import com.olympos.tripbook.utils.*
import com.olympos.tripbook.utils.ApplicationClass.Companion.TAG


class SigninActivity : BaseActivity(), UserAuthApiView {

    private val userAuthApiController = UserAuthApiController()

    private lateinit var binding: ActivityUserSigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityUserSigninBinding.inflate(layoutInflater)
        binding.signinSigninBtnIv.setOnClickListener(this)

        setContentView(binding.root)

        userAuthApiController.setUserView(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.signin_signin_btn_iv -> signinByKakaotalk()
        }
    }

    private fun signinByKakaotalk() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("SigninActivity", error.toString())
            } else if (token != null) {
                Log.d("SigninActivity", "signinByKakaoTalk()")
                UserApiClient.instance.me { user, _ ->
                    if (user!!.kakaoAccount?.emailNeedsAgreement == true) {
                        requireEmailNeedsAgreement()
                    } else {
                        saveNickname(user.kakaoAccount!!.profile!!.nickname!!)
                        saveKakaoAccessToken(token.accessToken)
                        saveKakaoRefreshToken(token.refreshToken)

                        val tokens = HashMap<String, String>()
                        tokens["kakaoAccessToken"] = token.accessToken
                        tokens["kakaoRefreshToken"] = token.refreshToken
                        userAuthApiController.kakaoSignin(tokens)
                    }
                }
            }
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SigninActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(this@SigninActivity, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this@SigninActivity, callback = callback)
        }
    }

    private fun requireEmailNeedsAgreement() {

        val scopes = mutableListOf<String>()

        scopes.add("account_email")
        Log.d(TAG, "사용자에게 추가 동의를 받아야 합니다.")

        UserApiClient.instance.loginWithNewScopes(this@SigninActivity, scopes) { token, error ->
            if (error != null) {
                Log.e(TAG, "사용자 추가 동의 실패", error)
                Toast.makeText(this, "이메일 사용에 동의해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "allowed scopes: ${token!!.scopes}")
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    } else if (user != null) {
                        saveNickname(user.kakaoAccount!!.profile!!.nickname!!)
                        saveKakaoAccessToken(token.accessToken)
                        saveKakaoRefreshToken(token.refreshToken)

                        val tokens = HashMap<String, String>()
                        tokens["kakaoAccessToken"] = token.accessToken
                        tokens["kakaoRefreshToken"] = token.refreshToken
                        userAuthApiController.kakaoSignin(tokens)
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun autoSigninSuccess() {
        Log.d("SigninActivity.kt", " \nautoSigninSuccess()" +
                "\nuserIdx: " + getUserIdx() +
                "\nAT: " + getAccessToken() +
                "\nRT: " + getRefreshToken() +
                "\nKAT: " + getKakaoAccessToken() +
                "\nKRT: " + getKakaoRefreshToken()
        )
        startMainActivity()
    }

    override fun autoSigninFailure(code: Int) {
        Log.e("SigninActivity.kt", "autoSigninFailure() status code: $code")
        when (code) {
            1504, 1507, 1509 -> {
                val refreshToken = getRefreshToken()
                val userIdx = getUserIdx()
                if (refreshToken != null && userIdx != 0) {
                    val tokens = HashMap<String, String>()
                    tokens["refreshToken"] = refreshToken
                    userAuthApiController.updateAccessToken(tokens, userIdx)
                }
            }
        }
    }

    override fun signUpUserSuccess() {
        Log.d("SigninActivity.kt", "signUpUserSuccess()")
        val kakaoAccessToken = getKakaoAccessToken()
        if (kakaoAccessToken != null) {
            val token = HashMap<String, String>()
            token["kakaoAccessToken"] = kakaoAccessToken
            userAuthApiController.signUpProfile(token)
        }
    }

    override fun signUpUserFailure(code: Int) {
        Log.e("SigninActivity.kt", "signUpUserFailure() status code $code")
    }

    override fun signUpProfileSuccess() {
        Log.d("SigninActivity.kt", "signUpProfileSuccess()")
        val kakaoAccessToken = getKakaoAccessToken()
        val kakaoRefreshToken = getKakaoRefreshToken()
        Log.e("SigninActivity.kt", " \nKAT: $kakaoAccessToken \nKRT: $kakaoRefreshToken")
        if (kakaoAccessToken != null && kakaoRefreshToken != null) {
            val tokens = HashMap<String, String>()
            tokens["accessToken"] = kakaoAccessToken
            tokens["refreshToken"] = kakaoRefreshToken
            userAuthApiController.kakaoSignin(tokens)
        }
    }

    override fun signUpProfileFailure(code: Int) {
        Log.e("SigninActivity.kt", "signUpProfileFailure() status code $code")
    }

    override fun kakaoSigninSuccess() {
        val kat = getKakaoAccessToken()
        val krt = getKakaoRefreshToken()
        Log.d("SigninActivity.kt", "kakaoSigninSuccess()")
        Log.d("SigninActivity.kt", " \nKAT: $kat \nKRT: $krt")
        val accessToken = getAccessToken()
        if (accessToken != null) {
            userAuthApiController.autoSignin()
        }
    }

    override fun kakaoSigninFailure(code: Int) {
        Log.e("SigninActivity.kt", "kakaoSigninFailure() status code $code")
        when (code) {
            2050 -> {
                val kakaoAccessToken = getKakaoAccessToken()
                val kakaoRefreshToken = getKakaoRefreshToken()
                if (kakaoAccessToken != null && kakaoRefreshToken != null) {
                    val token = HashMap<String, String>()
                    token["kakaoAccessToken"] = kakaoAccessToken
                    token["kakaoRefreshToken"] = kakaoRefreshToken
                    userAuthApiController.kakaoSignin(token)
                }
            }
            2052 -> {
                val kakaoAccessToken = getKakaoAccessToken()
                if (kakaoAccessToken != null) {
                    val token = HashMap<String, String>()
                    token["kakaoAccessToken"] = kakaoAccessToken
                    userAuthApiController.signUpUser(token)
                }
            }
            2057 -> {
                val userIdx = getUserIdx()
                val kakaoRefreshToken = getKakaoRefreshToken()
                if (kakaoRefreshToken != null) {
                    val token = HashMap<String, String>()
                    token["kakaoRefreshToken"] = kakaoRefreshToken
                    userAuthApiController.updateKakaoAccessToken(token, userIdx)
                }
            }
        }
    }

    override fun updateKakaoAccessTokenSuccess() {
        Log.d("SigninActivity.kt", "updateKakaoAccessTokenSuccess()")
        val kakaoAccessToken = getKakaoAccessToken()
        val kakaoRefreshToken = getKakaoRefreshToken()
        if (kakaoAccessToken != null && kakaoRefreshToken != null) {
            val tokens = HashMap<String, String>()
            tokens["kakaoAccessToken"] = kakaoAccessToken
            tokens["kakaoRefreshToken"] = kakaoRefreshToken
            userAuthApiController.kakaoSignin(tokens)
        }
    }

    override fun updateKakaoAccessTokenFailure(code: Int) {
        Log.e("SigninActivity.kt", "updateKakaoAccessTokenFailure() status code $code")
    }

    override fun updateProfileSuccess() {
        Log.d("SigninActivity.kt", "updateProfileSuccess()")
        userAuthApiController.getProfile(getUserIdx())
    }

    override fun updateProfileFailure(code: Int) {
        Log.e("SigninActivity.kt", "updateProfileFailure() status code $code")
    }

    override fun getProfileSuccess() {
        val nickname = getNickname()
        val userImg = getUserImage()
        Log.d("SigninActivity.kt", "getProfileSuccess()")
        Log.d("SigninActivity.kt", "nickname: $nickname, userImg: $userImg")
    }

    override fun getProfileFailure(code: Int) {
        Log.e("SigninActivity.kt", "getProfileFailure() status code $code")
        Toast.makeText(this, "프로필 업데이트 실패", Toast.LENGTH_SHORT).show()
    }

    override fun updateAccessTokenSuccess() {
        Log.d("SigninActivity.kt", "updateAccessTokenSuccess()")
        val accessToken = getAccessToken()
        if (accessToken != null) {
            userAuthApiController.autoSignin()
        }
    }

    override fun updateAccessTokenFailure(code: Int) {
        Log.e("SigninActivity.kt", "updateAccessTokenFailure() status code $code")
        when (code) {
            1505, 1509 -> {
                val kakaoAccessToken = getKakaoAccessToken()
                val kakaoRefreshToken = getKakaoRefreshToken()
                if (kakaoAccessToken != null && kakaoRefreshToken != null) {
                    val tokens = HashMap<String, String>()
                    tokens["kakaoAccessToken"] = kakaoAccessToken
                    tokens["kakaoRefreshToken"] = kakaoRefreshToken
                    userAuthApiController.kakaoSignin(tokens)
                }
            }
        }
    }
}