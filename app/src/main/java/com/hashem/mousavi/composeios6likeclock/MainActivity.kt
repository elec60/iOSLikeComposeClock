package com.hashem.mousavi.composeios6likeclock

import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Clock()
            }
        }
    }
}


@Composable
fun Clock(
    size: Dp = 250.dp,
    smallDegreesWidth: Dp = 2.5.dp,
    smallDegreesHeight: Dp = 8.dp,
    bigDegreesWidth: Dp = 8.dp,
    bigDegreesHeight: Dp = 20.dp,
    secondHandCircleRadius: Dp = 8.dp
) {

    val initialSecondAngle by remember {
        val cal = Calendar.getInstance()
        val second = cal.get(Calendar.SECOND)
        mutableStateOf(6f * (second - 15f))
    }
    val initialMinuteAngle by remember {
        val cal = Calendar.getInstance()
        val minute = cal.get(Calendar.MINUTE).toFloat() + cal.get(Calendar.SECOND).toFloat() / 60f
        mutableStateOf(6f * (minute - 15f))
    }
    val initialHourAngle by remember {
        val cal = Calendar.getInstance()
        val hour =
            cal.get(Calendar.HOUR).toFloat() + cal.get(Calendar.MINUTE).toFloat() / 60f + cal.get(
                Calendar.SECOND
            ).toFloat() / 3600f
        mutableStateOf(30 * (hour - 3f))
    }

    val infiniteTransition = rememberInfiniteTransition()

    val hourHandRotation by infiniteTransition.animateFloat(
        initialValue = initialHourAngle,
        targetValue = (initialHourAngle + 360f),
        animationSpec = infiniteRepeatable(
            animation = tween(12 * 60 * 60 * 1000, easing = LinearEasing)
        )
    )

    val minuteHandRotation by infiniteTransition.animateFloat(
        initialValue = initialMinuteAngle,
        targetValue = (initialMinuteAngle + 360f),
        animationSpec = infiniteRepeatable(
            animation = tween(60 * 60 * 1000, easing = LinearEasing)
        )
    )

    val secondHandRotation by infiniteTransition.animateFloat(
        initialValue = initialSecondAngle,
        targetValue = (initialSecondAngle + 360f),
        animationSpec = infiniteRepeatable(
            animation = tween(60 * 1000, easing = LinearEasing)
        )
    )

    val bigCircleAnim = animateDpAsState(
        targetValue = if (secondHandRotation % 360 in 180.0..360.0) (-25).dp else size/2 - bigDegreesHeight - 15.dp,
        animationSpec = tween(durationMillis = 2000, easing = Easing {
            AccelerateInterpolator().getInterpolation(it)
        })
    )

    Box(modifier = Modifier.size(size = size)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color(0xFF242424)
            )
            val clipPath = Path().apply {
                addArc(
                    oval = Rect(0f, 0f, size.toPx(), size.toPx()),
                    0f,
                    360f
                )
            }
            clipPath(clipPath) {
                drawArc(
                    color = Color(0xFF3B3B3B),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(0f, -size.toPx() / 2),
                    size = Size(size.toPx(), size.toPx())
                )
            }

            for (degree in 0..354 step 6) {
                rotate(degree.toFloat()) {
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(size.toPx() / 2f - smallDegreesWidth.toPx() / 2f, 0f),
                        size = Size(smallDegreesWidth.toPx(), smallDegreesHeight.toPx())
                    )

                    if (degree % 30 == 0) {
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(size.toPx() / 2f - bigDegreesWidth.toPx() / 2f, 0f),
                            size = Size(bigDegreesWidth.toPx(), bigDegreesHeight.toPx())
                        )
                    }
                }
            }

            translate(left = this.size.width / 2, top = this.size.height / 2) {

                rotate(hourHandRotation, pivot = Offset(0f, 0f)) {
                    drawLine(
                        color = Color.White,
                        start = Offset(-25.dp.toPx(), 0f),
                        end = Offset(
                            this.size.width / 2f - bigDegreesHeight.toPx() - 30.dp.toPx(),
                            0f
                        ),
                        strokeWidth = bigDegreesWidth.toPx()
                    )
                }


                rotate(minuteHandRotation, pivot = Offset(0f, 0f)) {

                    drawLine(
                        color = Color.White,
                        start = Offset(-25.dp.toPx(), 0f),
                        end = Offset(this.size.width / 2f - smallDegreesHeight.toPx(), 0f),
                        strokeWidth = bigDegreesWidth.toPx()
                    )
                }

                rotate(secondHandRotation, pivot = Offset(0f, 0f)) {

                    //draw second hand small section
                    drawLine(
                        color = Color(0xFFB90017),
                        start = Offset(-25.dp.toPx(), 0f),
                        end = Offset(
                            this.size.width / 2 - bigDegreesHeight.toPx() - 15.dp.toPx(),
                            0f
                        ), strokeWidth = 2.5.dp.toPx()
                    )
                    drawCircle(
                        color = Color(0xFFB90017),
                        radius = secondHandCircleRadius.toPx(),
                        center = Offset(
                            bigCircleAnim.value.toPx(),
                            0f
                        )
                    )
                }

                drawCircle(
                    color = Color(0xFFB90017),
                    radius = 4.dp.toPx(),
                    center = Offset(0f, 0f)
                )

                drawCircle(
                    color = Color(0xFFE15C62),
                    radius = 2.dp.toPx(),
                    center = Offset(0f, 0f)
                )
            }


        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Box(contentAlignment = Alignment.Center) {
        Clock()
    }
}