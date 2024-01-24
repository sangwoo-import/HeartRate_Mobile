package com.example.saloris


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.saloris.Database.SmartwatchHeart.SmartHeartDao
import com.example.saloris.R.color
import com.example.saloris.R.string
import com.example.saloris.databinding.SmartHeartBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId





class HeartActivity: AppCompatActivity() {

    lateinit var binding: SmartHeartBinding

//    데이터를 받을 떄와 안받을 때를 구분하기 위한 boolean 구문
    private var startheart: Boolean = true
    private var hr = 0
    private var hrs: String = ""
    private var time = 0

    //    date:LocalDate
    private fun recHr() {

        val zoneId = ZoneId.systemDefault()

//     val start = Instant.from(ZonedDateTime.of(date, LocalTime.MIDNIGHT, zoneId))
//     val stop = Instant.from(ZonedDateTime.of(date, LocalTime.of(23, 59, 59), zoneId))
        val start = Instant.now()
        val stop = start.minusSeconds(5)
        val dao = SmartHeartDao()
         lifecycleScope.launch(Dispatchers.IO) {
            println("$stop")

            val heartRateList = dao.getByUserAndPeriod(stop, start)
            if (heartRateList.isNotEmpty()) {
                startheart = true
                println("Heeart rate list data ??: $heartRateList")
                for (heartRate in heartRateList) {
                    hrs = heartRate.value.toString()
                    hr = hrs.toInt()
                    binding.bpmText.text = hrs
                    chart.addEntry(hr)
                    println("hearRATE Please : ${heartRate}")
                    if (heartRateList.isEmpty()) {

                        binding.bpmText.text = getText(string.waiting)
                    }
                }
            }
        }
        binding.heartStop.setOnClickListener {
//            stop 버튼을 눌렀을 때 데이터가 그만 온다는 표시
            startheart = false
            binding.bpmText.text = "심박수 종료"
        }


    }


    /* Graph  mpAndroid chart library 사용 함수 */
    private lateinit var chart: LineChart
    private val lineColors = ArrayList<Int>()

    private fun initChart() {
        chart = binding.graphHrRealtime
        with(chart) {
            setDrawGridBackground(false)
            setTouchEnabled(false)
            setNoDataText(null)
            setViewPortOffsets(0f, 0f, 0f, 0f)
            description.isEnabled = false
            legend.isEnabled = false

            with(xAxis) {
                setDrawGridLines(false)
                setDrawLabels(false)
                setDrawAxisLine(false)
            }
            with(axisLeft) {
                setDrawGridLines(false)
                setDrawLabels(false)
                setDrawAxisLine(false)
            }
            axisRight.isEnabled = false

            val dataSet = LineDataSet(null, "HeartRate")
            with(dataSet) {
                setDrawValues(false)
                setDrawCircles(false)
                lineWidth = 1.5f
                colors = lineColors
                mode = LineDataSet.Mode.LINEAR
            }
            data = LineData(dataSet)
        }
    }

    /* 심박수 데이터가 들어와서 데이터가 mpAndroidChart에 UI에 표시됨 */
    private fun LineChart.addEntry(hr: Int) {
        data.addEntry(Entry(time.toFloat(), hr.toFloat()), 0)
        data.notifyDataChanged()
        lineColors.add(
            ContextCompat.getColor(
                /* 67이상이면 하얀색 그 밑이면 주황색을 띈다 */
                context, if (hr >= 67) color.white else color.drowsy
            )
        )
        time++

        setVisibleXRange(120f, 120f)

        with(axisLeft) {
            /* 50 ~ 130 사이의 차트를 범위를 지정함 */
            axisMinimum = hr.coerceAtMost(50).toFloat()
            axisMaximum = hr.coerceAtLeast(130).toFloat()
        }

        notifyDataSetChanged()
        moveViewToX(xChartMax)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SmartHeartBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.heartStart.setOnClickListener {
            binding.bpmText.text = getText(string.waiting)
            lifecycleScope.launch(Dispatchers.IO) {

                while (true) {
                    recHr()
                    startheart = true
                    delay(1000)

                    if(startheart == false){
                        cancel()
                    }
                }
            }
            initChart()
        }
    }
}
