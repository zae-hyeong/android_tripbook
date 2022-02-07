package com.olympos.tripbook.src.trip

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.olympos.tripbook.R
import com.olympos.tripbook.config.BaseActivity
import com.olympos.tripbook.databinding.ActivityTripBinding
import com.olympos.tripbook.src.trip.model.Trip
import com.olympos.tripbook.src.trip.model.TripPostProcess
import com.olympos.tripbook.src.trip.model.TripService
import com.olympos.tripbook.src.tripcourse.TripcourseActivity
import com.olympos.tripbook.utils.saveTripIdx

class TripActivity : BaseActivity(), TripPostProcess {
    private lateinit var binding: ActivityTripBinding
    private lateinit var departureDate: String
    private lateinit var arrivalDate: String

    private var trip = Trip()
//    private var decorator = RangeDayDecorator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        //click 리스너
        binding.tripTopbarLayout.topbarBackIb.setOnClickListener(this)
        binding.tripTopbarLayout.topbarSubbuttonIb.setOnClickListener(this)
        binding.tripThemeTheme1Ll.setOnClickListener(this)
        binding.tripThemeTheme2Ll.setOnClickListener(this)
        binding.tripThemeTheme3Ll.setOnClickListener(this)
        binding.tripThemeTheme4Ll.setOnClickListener(this)
        binding.tripNextStepBtnTv.setOnClickListener(this)
        binding.tripCalendarMcv.setOnClickListener(this)
    }

    private fun initView() {
        //상단바
        binding.tripTopbarLayout.topbarTitleTv.setText(R.string.trip_title)
        binding.tripTopbarLayout.topbarSubtitleTv.visibility = View.GONE
        binding.tripTopbarLayout.topbarSubbuttonIb.visibility = View.GONE

        //캘린더
        val calendar = binding.tripCalendarMcv
//        val year = binding.tripDatePickerYear
//        val month = binding.tripDatePickerMonth

        //년, 월 최대 최소 값 및 현재값 세팅
//        year.maxValue = getCurrentDate().split(".")[0].toInt()
//        year.minValue = 1980
//        month.maxValue = 12
//        month.minValue = 1
//        year.value = getCurrentDate().split(".")[0].toInt()
//        month.value = getCurrentDate().split(".")[1].toInt()
//
//        //Number Picker 순환안되도록
//        year.wrapSelectorWheel = false
//        month.wrapSelectorWheel = false


//        val jwt = getJwt(this)
//        trip.userIdx = jwt.toString()
        trip.userIdx = "1"

        //날짜 선택
        calendar.topbarVisible = true
        calendar.setOnRangeSelectedListener { widget, dates ->
//            val regex = "\\^\\d{4}.\\d{1,2}.\\d{1,2}\\$/".toRegex()
//            departureDate = regex.find(dates.first().toString()).toString()
//            arrivalDate = regex.find(dates.last().toString()).toString()
//            Log.d("정규식 확인", departureDate)
//            departureDate = dates.first().toString().substring(12).split("-")
            arrivalDate = dates.last().toString()

            //출발일
            binding.tripDateDepartureMonthTv.text = departureDate.split("-")[1]
            binding.tripDateDepartureDayTv.text = departureDate.split("-")[2].dropLast(1)
            //도착일
            binding.tripDateArrivalMonthTv.text = arrivalDate.split("-")[1]
            binding.tripDateArrivalDayTv.text = arrivalDate.split("-")[2].dropLast(1)

            trip.departureDate = departureDate
            trip.arrivalDate = arrivalDate

        }
        //        calendar.setOnDateChangedListener(this)
//        calendar.addDecorator(decorator)
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v!!.id) {
            R.id.topbar_back_ib ->
                finish()
            R.id.trip_theme_theme1_ll, R.id.trip_theme_theme2_ll, R.id.trip_theme_theme3_ll ->
                themeSelected(v)
            R.id.trip_theme_theme4_ll ->
                Toast.makeText(this, "추후 추가 예정", Toast.LENGTH_SHORT).show()
            R.id.trip_next_step_btn_tv -> {
                //제목 입력
                trip.tripTitle = binding.tripTitleEt.text.toString()

//                //validation
//                if(trip.tripTitle.length > 15)
//                    Toast.makeText(this, "제목은 14자로 이내로 입력해주세요", Toast.LENGTH_SHORT).show()
//                if( trip.userIdx==""||
//                    trip.tripTitle==""||
//                    trip.departureDate==""||
//                    trip.arrivalDate==""||
//                    trip.themeIdx==0) {
//                    Toast.makeText(this, "모든 값을 입력해주세요", Toast.LENGTH_SHORT).show()
//                }

                postTrip(trip)

                Log.d("api test 확인용", "userIdx: " + trip.userIdx + "tripTitle: " + trip.tripTitle +
                        "departureDate: " + trip.departureDate + "arrivalDate: " + trip.arrivalDate + "themeIdx: " + trip.themeIdx)
            }
        }
    }

    //테마 선택 시 글씨색 변경 및 테두리색 변경
    private fun themeSelected(v: View?) {
        Log.d("click listener", "themeSelect")
        when (v!!.id) {
            R.id.trip_theme_theme1_ll -> {
                binding.tripThemeTheme1Cv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tripbook_main_1)))
                binding.tripThemeTheme1Tv.setTextColor(ContextCompat.getColor(this, R.color.tripbook_main_1))
                binding.tripThemeTheme2Cv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_gray)))
                binding.tripThemeTheme2Tv.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                binding.tripThemeTheme3Cv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_gray)))
                binding.tripThemeTheme3Tv.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))

                trip.themeIdx = 1
            }
            R.id.trip_theme_theme2_ll -> {
                binding.tripThemeTheme1Cv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_gray)))
                binding.tripThemeTheme1Tv.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                binding.tripThemeTheme2Cv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tripbook_main_1)))
                binding.tripThemeTheme2Tv.setTextColor(ContextCompat.getColor(this, R.color.tripbook_main_1))
                binding.tripThemeTheme3Cv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_gray)))
                binding.tripThemeTheme3Tv.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))

                trip.themeIdx = 2
            }
            R.id.trip_theme_theme3_ll -> {
                binding.tripThemeTheme1Cv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_gray)))
                binding.tripThemeTheme1Tv.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                binding.tripThemeTheme2Cv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_gray)))
                binding.tripThemeTheme2Tv.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
                binding.tripThemeTheme3Cv.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tripbook_main_1)))
                binding.tripThemeTheme3Tv.setTextColor(ContextCompat.getColor(this, R.color.tripbook_main_1))

                trip.themeIdx = 3
            }
        }
    }

    private fun postTrip(trip: Trip) {
        val tripService = TripService()
        tripService.setProcess(this)

        tripService.postTrip(trip)
    }

    override fun onPostTripLoading() {
        binding.tripLoadingPb.visibility = View.VISIBLE
    }

    override fun onPostTripSuccess(result : Int) {
        binding.tripLoadingPb.visibility = View.GONE

        // response로 받은 tripIdx를 저장한 후 TripcourseActivity 실행
        saveTripIdx(this, result)
        startTripcourseActivity()
    }

    override fun onPostTripFailure(code: Int, message: String) {
        binding.tripLoadingPb.visibility = View.GONE

        when(code) {
            400 -> Toast.makeText(this, "네트워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
            2101 -> Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            2102 -> Toast.makeText(this, "출발일을 지정해주세요.", Toast.LENGTH_SHORT).show()
            2103 -> Toast.makeText(this, "도착일을 지정해주세요.", Toast.LENGTH_SHORT).show()
            2104 -> Toast.makeText(this, "테마를 선택해주세요.", Toast.LENGTH_SHORT).show()
            2105 -> Toast.makeText(this, "유저 인덱스 오류", Toast.LENGTH_SHORT).show()
            2107 -> Toast.makeText(this, "제목을 14자 이내로 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTripcourseActivity() {
        val intent = Intent(this, TripcourseActivity::class.java)
        startActivity(intent)
    }
}