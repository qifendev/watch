package site.qifen.watchview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

import java.util.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin


/**
 * @author qifen
 * @since 2020/9/27
 */


class WatchView : View {


    //是否根据时间转
    @Volatile
    var run = true

    //设置时间
    var stopTime: Calendar? = null


    //表盘
    val dialPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val hoursCaliPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val hoursTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val minutesCaliPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val centerCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val dialImagePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    //指针
    val hoursPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val minutesPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val secondsPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    //指针默认指向12点钟方向为0度
    var hoursAngle = 0.0
    var minutesAngle = 0.0
    var secondsAngle = 0.0

    //指针长度,值越大对应指针越短,以下为默认值(半径减去对应值)
    var hoursLength = 200
    var minutesLength = 150
    var secondsLength = 100

    //表盘的刻度粗细,以下为默认
    var hoursScaleDegree = 20f
    var minutesScaleDegree = 10f

    //时表盘刻度长度,以下为默认
    var hoursScaleLength = 60f
    var minutesScaleLength = 40f


    //指针粗细
    var hoursDegree = 20f
    var minutesDegree = 15f
    var secondsDegree = 10f


    //表盘小时数字的高度(相对顶部)
    var hoursNumberHeight = 100f


    //小时字符显示大小
    var hourNumberSize = 40f

    //中心点大小
    var centerPoint = 20f

    //表盘颜色
    var dialColor = Color.BLACK

    //小时标记的颜色
    var hoursCaliColor = Color.RED

    //分钟标记的颜色
    var minutesCaliColor = Color.GREEN

    //小时字体颜色
    var hourFontColor = Color.WHITE

    //表盘中间圆点颜色
    var centerCircleColor = Color.WHITE

    //指针颜色
    var hoursPointerColor = Color.RED
    var minutesPointerColor = Color.GREEN
    var secondsPointerColor = Color.BLUE

    //表盘的图片颜色
    var dialImageBitmap: Bitmap? = null


    init {
        if (stopTime == null) {
            Thread {
                while (run) {
                    postInvalidate()
                    Thread.sleep(1000)
                }
            }.start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.WatchesView);
        hoursLength = typedArray.getDimension(R.styleable.WatchesView_hoursLength, 200f).toInt()
        minutesLength = typedArray.getDimension(R.styleable.WatchesView_minutesLength, 150f).toInt()
        secondsLength = typedArray.getDimension(R.styleable.WatchesView_secondsLength, 100f).toInt()
        hoursScaleDegree =
            typedArray.getDimension(R.styleable.WatchesView_hoursScaleDegree, 20f)
        minutesScaleDegree =
            typedArray.getDimension(R.styleable.WatchesView_minutesScaleDegree, 10f)
        hoursScaleLength = typedArray.getDimension(R.styleable.WatchesView_hoursScaleLength, 60f)
        minutesScaleLength =
            typedArray.getDimension(R.styleable.WatchesView_minutesScaleLength, 40f)
        hoursDegree = typedArray.getDimension(R.styleable.WatchesView_hoursDegree, 20f)
        minutesDegree = typedArray.getDimension(R.styleable.WatchesView_minutesDegree, 15f)
        secondsDegree = typedArray.getDimension(R.styleable.WatchesView_secondsDegree, 10f)
        hoursNumberHeight = typedArray.getDimension(R.styleable.WatchesView_hoursNumberHeight, 100f)
        hourNumberSize = typedArray.getDimension(R.styleable.WatchesView_hourNumberSize, 40f)
        centerPoint = typedArray.getDimension(R.styleable.WatchesView_centerPoint, 20f)
        //color
        dialColor = typedArray.getColor(R.styleable.WatchesView_dialColor, Color.BLACK)
        hoursCaliColor = typedArray.getColor(R.styleable.WatchesView_hoursCaliColor, Color.RED)
        minutesCaliColor =
            typedArray.getColor(R.styleable.WatchesView_minutesCaliColor, Color.GREEN)
        hourFontColor = typedArray.getColor(R.styleable.WatchesView_hourFontColor, Color.WHITE)
        centerCircleColor =
            typedArray.getColor(R.styleable.WatchesView_centerCircleColor, Color.WHITE)
        hoursPointerColor =
            typedArray.getColor(R.styleable.WatchesView_hoursPointerColor, Color.RED)
        minutesPointerColor =
            typedArray.getColor(R.styleable.WatchesView_minutesPointerColor, Color.GREEN)
        secondsPointerColor =
            typedArray.getColor(R.styleable.WatchesView_secondsPointerColor, Color.BLUE)

        run = typedArray.getBoolean(R.styleable.WatchesView_run, run)


//        dialImageBitmap = BitmapFactory.decodeResource(
//            resources,
//            typedArray.getResourceId(R.styleable.WatchesView_dialImageBitmap, null)
//        )

        typedArray.recycle()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        //dialPaint
        dialPaint.color = dialColor
        dialPaint.style = Paint.Style.FILL
        dialPaint.isAntiAlias = true
        //hoursCaliPaint
        hoursCaliPaint.color = hoursCaliColor
        hoursCaliPaint.style = Paint.Style.FILL
        hoursCaliPaint.strokeWidth = hoursScaleDegree
        hoursCaliPaint.isAntiAlias = true
        //minutesCaliPaint
        minutesCaliPaint.color = minutesCaliColor
        minutesCaliPaint.style = Paint.Style.FILL
        minutesCaliPaint.strokeWidth = minutesScaleDegree
        minutesCaliPaint.isAntiAlias = true
        //hoursTextPaint
        hoursTextPaint.textSize = hourNumberSize
        hoursTextPaint.color = hourFontColor
        hoursTextPaint.isAntiAlias = true
        //centerCirclePaint
        centerCirclePaint.color = centerCircleColor
        centerCirclePaint.style = Paint.Style.FILL
        centerCirclePaint.isAntiAlias = true
        //hoursPaint
        hoursPaint.color = hoursPointerColor
        hoursPaint.style = Paint.Style.FILL
        hoursPaint.strokeWidth = hoursDegree
        hoursPaint.isAntiAlias = true
        hoursPaint.isDither = true

        //minutesPaint
        minutesPaint.color = minutesPointerColor
        minutesPaint.style = Paint.Style.FILL
        minutesPaint.strokeWidth = minutesDegree
        minutesPaint.isAntiAlias = true
        minutesPaint.isDither = true

        //secondsPaint
        secondsPaint.color = secondsPointerColor
        secondsPaint.style = Paint.Style.FILL
        secondsPaint.strokeWidth = secondsDegree
        secondsPaint.isAntiAlias = true
        secondsPaint.isDither = true

        //dialImagePaint
        dialImagePaint.isAntiAlias = true
        dialImagePaint.alpha = 256


        val date: Calendar = if (stopTime != null) stopTime!! else Calendar.getInstance()
        val hours = date.get(Calendar.HOUR)
        val minutes = date.get(Calendar.MINUTE)
        val seconds = date.get(Calendar.SECOND)
        secondsAngle = seconds * 6.0
        minutesAngle = (minutes + (seconds / 60.0)) * 6.0
        hoursAngle = (hours + (minutes / 60.0) + (seconds / 3600)) * 30.0


        //dialPaint
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            getMinRadius(),
            dialPaint
        )


//        canvas.drawBitmap(
//            dialImageBitmap!!,
//            (width / 2) - getMinRadius(),
//            (height / 2) - getMinRadius(),
//            dialImagePaint
//        )


        //hoursCaliPaint
        canvas.save()
        for (i in 0..360 step 30) {
            canvas.drawLine(
                width / 2f,
                (height / 2f) - getMinRadius(),
                width / 2f,
                ((height / 2f) - getMinRadius()) + hoursScaleLength,
                hoursCaliPaint
            )
            canvas.rotate(30f, width / 2f, height / 2f)
        }




        for (i in 0..360 step 6) {
            if (i % 30 != 0) {
                canvas.drawLine(
                    (width / 2f),
                    (height / 2f) - getMinRadius(),
                    width / 2f,
                    ((height / 2f) - getMinRadius()) + minutesScaleLength,
                    minutesCaliPaint
                )
            }
            canvas.rotate(6f, width / 2f, height / 2f)
        }
        canvas.restore()

        for (i in 0..360 step 30) {
            if (i == 0) continue
            canvas.drawText(
                getHour(i),
                (width / 2f) + (sin(Math.toRadians(i.toDouble())) * (getMinRadius() - hoursNumberHeight)).toFloat() - getHoursAverageWidth(
                    i
                ),
                (height / 2f) - (cos(Math.toRadians(i.toDouble())) * (getMinRadius() - hoursNumberHeight)).toFloat() + getHoursAverageHeight(
                    i
                ),
                hoursTextPaint
            )

            //hoursPaint
            canvas.drawLine(
                width / 2f,
                height / 2f,
                (width / 2f) + (sin(Math.toRadians(hoursAngle)) * (getMinRadius() - hoursLength)).toFloat(),
                (height / 2f) - (cos(Math.toRadians(hoursAngle)) * (getMinRadius() - hoursLength)).toFloat(),
                hoursPaint
            )

            //minutesPaint
            canvas.drawLine(
                width / 2f,
                height / 2f,
                (width / 2f) + (sin(Math.toRadians(minutesAngle)) * (getMinRadius() - minutesLength)).toFloat(),
                (height / 2f) - (cos(Math.toRadians(minutesAngle)) * (getMinRadius() - minutesLength)).toFloat(),
                minutesPaint
            )

            //secondsPaint
            canvas.drawLine(
                width / 2f,
                height / 2f,
                (width / 2f) + (sin(Math.toRadians(secondsAngle)) * (getMinRadius() - secondsLength)).toFloat(),
                (height / 2f) - (cos(Math.toRadians(secondsAngle)) * (getMinRadius() - secondsLength)).toFloat(),
                secondsPaint
            )

        }


        //centerCirclePaint
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            centerPoint, //指针中心点大小
            centerCirclePaint
        )
    }

    fun getHoursAverageWidth(i: Int): Float {
        val hour = getHour(i)
        return hoursTextPaint.measureText(hour) / 2
    }

    fun getHoursAverageHeight(i: Int): Float {
        val hour = getHour(i)
        return hoursTextPaint.measureText(hour) / 2
    }

    fun getHour(i: Int): String = if (i == 1) {
        "12"
    } else {
        (i / 30).toString()
    }


    //    watches min width
    private fun getMinRadius(): Float {
        val maxPadding = max(max(paddingBottom, paddingTop), max(paddingLeft, paddingRight))
        val maxMargin = max(max(marginBottom, marginTop), max(marginLeft, marginRight))

        return if (mode != 1)
//            (((if (width < height) width else height) / 2f) - (maxPadding + maxMargin))
            ((if (getScreenWidth() < getScreenHeight()) getScreenWidth() else getScreenHeight()) / 2f) - (maxPadding + maxMargin)
        else
            (((if (width < height) width else height) / 2f) - (maxPadding + maxMargin))
    }


//    private fun getMinRadius(): Float {
//        val maxPadding = max(max(paddingBottom, paddingTop), max(paddingLeft, paddingRight))
//        val maxMargin = max(max(marginBottom, marginTop), max(marginLeft, marginRight))
//        return ((if (getScreenWidth() < getScreenHeight()) getScreenWidth() else getScreenHeight()) / 2f) - (maxPadding + maxMargin)
//    }


    private fun getScreenWidth() = resources.displayMetrics.widthPixels
    private fun getScreenHeight() = resources.displayMetrics.heightPixels


    var mode = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mode = 0;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val viewWidth = when (widthMode) {
            MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED -> {
                mode = 1; widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(heightSize, widthSize)
            }
            else -> getMinRadius().toInt()
        }

        val viewHeight = when (heightMode) {
            MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED -> {
                mode = 1; heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(heightSize, widthSize)
            }
            else -> getMinRadius().toInt()
        }
        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }


    //dp to px
    public fun dp2px(dpValue: Float): Int =
        ((context.resources.displayMetrics.density) * dpValue + 0.5f).toInt()

    //px to dp
    public fun px2dp(pxValue: Float): Int =
        (pxValue / (context.resources.displayMetrics.density) + 0.5f).toInt()


}