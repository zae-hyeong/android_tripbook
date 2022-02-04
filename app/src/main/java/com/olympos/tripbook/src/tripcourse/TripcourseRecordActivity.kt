package com.olympos.tripbook.src.tripcourse

import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.olympos.tripbook.R
import com.olympos.tripbook.config.BaseActivity
import com.olympos.tripbook.databinding.ActivityTripcourseRecordBinding
import com.olympos.tripbook.src.tripcourse.model.Card


class TripcourseRecordActivity : BaseActivity() {

    lateinit var binding : ActivityTripcourseRecordBinding

    private var card : Card = Card()
//    private var hashtag : Hashtag = Hashtag()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripcourseRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        addHashtagDumyInfo()

        //click 리스너
        binding.tripcourseRecordTopbarLayout.topbarBackIb.setOnClickListener(this)
        binding.tripcourseRecordTopbarLayout.topbarSubbuttonIb.setOnClickListener(this)
        binding.tripcourseRecordImgCl.setOnClickListener(this)
        binding.tripcourseRecordSelectCountryBtn.setOnClickListener(this)
        binding.tripcourseRecordHashtagAddBtn.setOnClickListener(this)

        //내용 최대 200자 이벤트 처리
        binding.tripcourseRecordBodyEt.addTextChangedListener(object : TextWatcher {
            val wordCountTv = binding.tripcourseRecordContentWordcountTv
            var userInput = binding.tripcourseRecordBodyEt

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                wordCountTv.text = "0 / 200"
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                wordCountTv.text = userInput.length().toString() + " / 200"
            }

            override fun afterTextChanged(s: Editable?) {
                if (userInput.isFocused && userInput.length() > 200) {
                    userInput.setText(s.toString().substring(0, 200))
                    userInput.setSelection(s!!.length - 1)
                    Toast.makeText(this@TripcourseRecordActivity, "200자까지 입력 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    //종료된 액티비티에서 정보 받아오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode) {
            COUNTRY_ACTIVITY_CODE -> { //SelectCountryActivity에서 장소 정보 가져오기
                card.country = data?.getStringExtra("country_result")!!
                binding.tripcourseRecordSelectCountryBtn.setText(card.country)
            }
            HASHTAG_ACTIVITY_CODE -> { //SelectHashtagActivity에서 해시태그 정보 가져오기
                //해시태그 저장
            }
        }
    }

    private fun initView() {
        //상단바
        binding.tripcourseRecordTopbarLayout.topbarTitleTv.setText(R.string.tripcourse_record_title)
        binding.tripcourseRecordTopbarLayout.topbarSubbuttonIb.setImageResource(R.drawable.btn_base_check_black)
        binding.tripcourseRecordTopbarLayout.topbarSubtitleTv.visibility = View.GONE

        //여행 날짜 선택 - Dialog 생성
        binding.tripcourseRecordSelectDateBtn.setOnClickListener {

        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v!!.id) {
            R.id.topbar_back_ib ->
                showDialog("발자국 작성 취소","발자국 작성을 취소하시겠습니까?\n" + "작성하셨던 내용은 임시저장됩니다.", "확인")
            R.id.topbar_subbutton_ib -> {
                getInputInfo()
            }
            R.id.tripcourse_record_img_cl ->
                photoSelect()

            //여행 도시 선택 - TripcourseSelectContryActivity로 이동
            R.id.tripcourse_record_select_country_btn ->
                startTripcourseSelectCountryActivity()

            //해시태그 선택 - TripcourseSelectHashtagActivity로 이동
            R.id.tripcourse_record_hashtag_add_btn ->
                startTripcourseSelectHashtagActivity()
        }
    }

    private fun photoSelect() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivity(intent)
    }

    override fun onOKClicked() {
        super.onOKClicked()
        finish()
    }

    private fun startTripcourseSelectCountryActivity() {
        val intent = Intent(this, TripcourseSelectCountryActivity::class.java)
        startActivity(intent)
    }

    private fun startTripcourseSelectHashtagActivity() {
        val intent = Intent(this, TripcourseSelectHashtagActivity::class.java)
        startActivity(intent)
    }

    private fun addHashtagDumyInfo() {
//        var i = 0
//        for(i in 0..10) {
//            hashtagInfo.location
//            hashtagInfo.weather
//            hashtagInfo.feeling
//            hashtagInfo.companion
//            hashtagInfo.event
//        }
    }

    private fun getInputInfo() {
        //필수요소 : 제목
        if(binding.tripcourseRecordTitleEt.text.toString().isEmpty()) {
            Toast.makeText(this.applicationContext, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
        }
        else {
            card.hasData = TRUE

            card.title = binding.tripcourseRecordTitleEt.text.toString()
            if(!binding.tripcourseRecordBodyEt.text.toString().isEmpty())
                card.body = binding.tripcourseRecordBodyEt.text.toString()

        }
    }
}