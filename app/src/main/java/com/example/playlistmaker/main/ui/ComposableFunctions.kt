package com.example.playlistmaker.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
    Row(modifier = Modifier.clickable(onClick = {onItemClick(track)})) {

        GlideImage(
            model = track.artworkUrl100,
            contentDescription = "image",
            modifier = Modifier.size(46.dp),
            contentScale = ContentScale.Fit,
            loading = placeholder(painterResource(R.drawable.track_empty_img)),
            failure = placeholder(painterResource(R.drawable.track_empty_img)),
        )

        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            Text(
                text = track.trackName,
                fontSize = dimensionResource(R.dimen.middle_text).value.sp,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Row() {
                Text(
                    text = track.artistName,
                    fontSize = dimensionResource(R.dimen.small_text).value.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Icon(
                    painter = painterResource(R.drawable.btn_agreement),
                    contentDescription = "",
                    modifier = Modifier
                        .size(4.dp)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = timeFormat.format(Date(track.trackTimeMillis)),
                    fontSize = dimensionResource(R.dimen.small_text).value.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(Modifier.weight(1F))

        Icon(
            painter = painterResource(R.drawable.btn_agreement),
            contentDescription = "АВ",
            tint = Color(0xCC, 0xCC, 0xCC),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically)
        )
    }
}