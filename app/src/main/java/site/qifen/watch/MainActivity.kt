package site.qifen.watch

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import site.qifen.watchview.WatchView
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val watchView: WatchView = findViewById(R.id.watch)


        //案例一 设置时分秒停在某一时间
//        val instance = Calendar.getInstance()
//        instance.set(Calendar.HOUR, 3)
//        instance.set(Calendar.MINUTE, 30)
//        instance.set(Calendar.SECOND, 45)
//        watchView.stopTime = instance

        //案例二 时分秒停在当前时间
//        watchView.run = false

        //案例三 更改颜色、字体大小、刻度粗细
//        watchView.dialColor = Color.YELLOW
//        watchView.hourNumberSize = 30f
//        watchView.hoursScaleDegree = 25f

    }
}