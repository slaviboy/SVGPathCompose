package com.slaviboy.svgpathcomposeexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.slaviboy.svgpathcompose.SvgPath

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val size = 100f
            val sizeDp = LocalDensity.current.run { size.toDp() }
            val path by remember {
                mutableStateOf(
                    SvgPath(
                        data = "M 36.4583,7.62939e-006C 31.6833,0.0742874 27.3126,2.69669 25,6.87501C 22.6874,2.69669 18.3167,0.0742874 13.5417,7.62939e-006C 5.75652,0.338264 -0.293823,6.90055 0,14.6875C 0,24.1604 9.97083,34.5062 18.3333,41.5208C 22.1877,44.7598 27.8123,44.7598 31.6667,41.5208C 40.0292,34.5062 50,24.1604 50,14.6875C 50.2938,6.90055 44.2435,0.338264 36.4583,7.62939e-006 Z",
                        width = size,
                        height = size
                    ).generatePath()
                )
            }
            Canvas(
                modifier = Modifier
                    .size(sizeDp)
                    .background(Color.Red)
            ) {
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        0f to Color.Green,
                        1f to Color.Blue
                    )
                )
            }
        }
    }
}