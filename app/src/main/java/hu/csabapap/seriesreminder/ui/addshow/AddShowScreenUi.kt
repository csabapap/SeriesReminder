package hu.csabapap.seriesreminder.ui.addshow

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AddShowScreenUi(
    viewModel: AddShowViewModel,
    imageColorState: ImageColorState,
    onBackPress: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()
    when (val result = state) {
        is DisplayShow -> ShowDetails(result.show, imageColorState, onBackPress)
        else -> Text(text = "something something")
    }
}

@Composable
fun ShowDetails(
    show: SRShow,
    imageColorState: ImageColorState,
    onBackPress: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.Green)
    ) {
        Box(contentAlignment = Alignment.TopStart) {
            AddShowHeader(show, imageColorState)
            Box(modifier = Modifier
                .padding(PaddingValues(start = 8.dp, top = 8.dp))
                .width(48.dp)
                .height(48.dp)
                .clip(CircleShape)
                .background(Color.White),
            ) {
                IconButton(onClick = onBackPress) {
                    Icon(painter = painterResource(id = R.drawable.ic_arrow_back_24dp), contentDescription = null)
                }
            }
        }
        Text(text = "something something")
    }
}

@Composable
fun AddShowHeader(
    show: SRShow,
    imageColorState: ImageColorState
) {
    DynamicallyColoredHeader(colorState = imageColorState) {
        
        LaunchedEffect(show.poster) {
            imageColorState.updatePrimaryColors(getThumbnailUrl(show.poster))
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(260.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)) {
            Card(modifier = Modifier
                .width(120.dp)
                .height(180.dp)) {
                AsyncImage(
                    model = getThumbnailUrl(show.poster),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                )
            }
        }
    }
}

@Composable
fun DynamicallyColoredHeader(
    colorState: ImageColorState,
    content: @Composable () -> Unit
) {
    MaterialTheme(colors = MaterialTheme.colors.copy(
        primary = colorState.color,
        onPrimary = colorState.onColor
    ), content = content)
}

class ImageColorState(
    val context: Context,
    private val defaultColor: Color,
    private val defaultOnColor: Color
) {
    var color by mutableStateOf(defaultColor)
        private  set
    var onColor by mutableStateOf(defaultOnColor)
        private set

    suspend fun updatePrimaryColors(url: String) {
        val result = getPrimaryColors(url)
        color = result?.primary ?: defaultColor
        onColor = result?.primary ?: defaultOnColor
    }

    private suspend fun getPrimaryColors(url: String): PrimaryColors? {
        return updateColorsFromImageUrl(url).maxByOrNull { swatch -> swatch.population }
            ?.let {
                PrimaryColors(
                    Color(it.rgb),
                    Color(it.titleTextColor)
                )
            }
    }

    private suspend fun updateColorsFromImageUrl(url: String): List<Palette.Swatch> {
        val request = ImageRequest.Builder(context)
            .data(url)
            .size(120, 180).scale(Scale.FILL)
            .allowHardware(false)
            .memoryCacheKey("$url.palette")
            .build()
        val bitmap = when (val result = context.imageLoader.execute(request)) {
            is SuccessResult -> result.drawable.toBitmap()
            else -> null
        }

        return bitmap?.let {
            withContext(Dispatchers.Default) {
                val palette = Palette.from(it)
                    .resizeBitmapArea(0)
                    .maximumColorCount(8)
                    .generate()
                palette.swatches
            }
        } ?: emptyList()
    }
}

data class PrimaryColors(val primary: Color, val onPrimary: Color)