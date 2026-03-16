package com.example.playlistmaker.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Date

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun TrackRow(track: Track, onItemClick: (Track) -> Unit) {
    val timeFormat = remember { SimpleDateFormat("mm:ss") }
    Row(modifier = Modifier.clickableDebounce(onClick = {onItemClick(track)}).padding(start = 5.dp, end = 12.dp)) {

        GlideImage(
            model = track.artworkUrl100,
            contentDescription = "image",
            modifier = Modifier
                .padding(all = 8.dp)
                .size(46.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Fit,
            loading = placeholder(painterResource(R.drawable.track_empty_img)),
            failure = placeholder(painterResource(R.drawable.track_empty_img)),
        )

        Column(modifier = Modifier.align(Alignment.CenterVertically).weight(100F)) {
            Text(
                text = track.trackName,
                fontSize = dimensionResource(R.dimen.middle_text).value.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                color = colorResource(R.color.search_tracklist_name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier.padding(end = 4.dp)
                        .weight(1F, fill = false)
                        ,
                    text = track.artistName,
                    fontSize = dimensionResource(R.dimen.small_text).value.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    color = colorResource(R.color.search_tracklist_text),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(
                    modifier = Modifier
                        .size(4.dp)
                        .background(color = colorResource(R.color.search_tracklist_text), shape = CircleShape)
                        .align(alignment = Alignment.CenterVertically)
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = timeFormat.format(Date(track.trackTimeMillis)),
                    fontSize = dimensionResource(R.dimen.small_text).value.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    color = colorResource(R.color.search_tracklist_text),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.weight(1F))

        Icon(
            painter = painterResource(R.drawable.btn_agreement),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically),
            tint = colorResource(R.color.search_tracklist_text)
        )
    }
}


@Composable
fun dummyTracks():List<Track>{
    return listOf<Track>(
        Track("Выборы", "День выборов",
            "Шнур", 100000L, Date(), "",
            "Россия", "", 10200L, ""),
        Track("Цезарь", "Империя",
            "Черный обелиск", 200000L, Date(), "",
            "Россия", "", 10201L, ""),
        Track("Бухгалтер", "Сказки",
            "Алена Апина", 150000L, Date(), "",
            "Россия", "", 10202L, ""),
        Track("Муттер", "Муттер",
            "Рамштайн", 170000L, Date(), "",
            "Германия", "", 10203L, ""),
        Track("супер пупер длинное название трека которое не поместиться в поле вывода", "Муттер",
            "супер пупер длинное название исполнителя которое не поместиться в поле вывода", 170000L, Date(), "",
            "Германия", "", 10203L, ""),
    )
}

@Composable
fun Modifier.clickableDebounce(
    enabled: Boolean = true,
    delay: Long = 1000L, // Задержка в мс
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    this.clickable(enabled = enabled) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > delay) {
            lastClickTime = currentTime
            onClick()
        }
    }
}