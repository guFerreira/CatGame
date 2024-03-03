package com.example.catgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catgame.ui.theme.CatGameTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen()
                }
            }
        }
    }
}

@Composable
fun GameScreen() {
    // quantidade de objetos derrubados
    var scoreValue by remember { mutableStateOf(0) }

    // range min que o target vai aparecer
    var startPercentage by remember { mutableStateOf(0.60f) }

    // range max que o target vai aparecer
    var endPercentage by remember { mutableStateOf(0.90f) }

    // range max que o target vai aparecer
    var intervalPercentage by remember { mutableStateOf(0.35f) }

    // velocidade da barra
    var barSpeed by remember { mutableStateOf(2000) }

    // o valor atual da barra
    val infiniteTransition = rememberInfiniteTransition()
    val progressBarValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(barSpeed),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Ajude o gato a derrubar os objetos no momento certo!",
            style = TextStyle(textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Score(scoreValue)
        CatImage()
        CatBar(progressBarValue, startPercentage, endPercentage)
        Spacer(modifier = Modifier.height(24.dp))
        ActionButton("Derrubar"){
            if(progressBarValue >= startPercentage && progressBarValue <= endPercentage){
                if(intervalPercentage > 0.10f){
                    intervalPercentage -= 0.01f
                }
                scoreValue++
                barSpeed -= 30
                startPercentage = Random.nextDouble(0.0, 1.0 - intervalPercentage).toFloat()
                endPercentage = startPercentage + intervalPercentage
            }
        }
    }
}

@Composable
fun Score(number: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Objetos derrubados:")
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Red),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number.toString(), style = TextStyle(color = Color.White))
        }
    }
}


@Composable
fun CatBar(progress:Float, startPercentage: Float, endPercentage: Float) {

    Box(
        modifier = Modifier.width(300.dp),
        contentAlignment = Alignment.Center
    ) {
        LinearProgressIndicator(
            progress = progress,
            Modifier
                .height(20.dp)
                .fillMaxWidth()
        )
        Target(startPercentage, endPercentage)
    }
}

@Composable
fun Target(startPercentage: Float, endPercentage: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val startX = canvasWidth * startPercentage
        val endX = canvasWidth * endPercentage

        val transparentBlue = Color.Red.copy(alpha = 0.2f)

        drawRoundRect(
            color = transparentBlue,
            topLeft = Offset(startX, 0f),
            size = Size((endX - startX), canvasHeight),
            cornerRadius = CornerRadius(x = 24f, y = 24f)
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun GameScreenPreview() {
    GameScreen()
}

@Composable
fun CatImage() {
    val imagePainter: Painter = painterResource(id = R.drawable.gato)
    Image(
        painter = imagePainter,
        contentDescription = null,
        modifier = Modifier
            .size(300.dp)
    )
}

@Composable
fun ActionButton(description: String, dropObject: () -> Unit) {
    Button(onClick = { dropObject() }, modifier = Modifier.height(40.dp)) {
        Text(text = description)
    }
}
