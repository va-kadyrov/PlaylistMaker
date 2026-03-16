package com.example.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.TrackRow
import com.example.playlistmaker.main.ui.dummyTracks
import com.example.playlistmaker.search.domain.Track

@Composable
fun FavoriteTracksScreen(
    observeFavoriteTrackState: LiveData<FavoriteTracksState>,
    openPlayer: (Track) -> Unit
) {
    val favoriteTrackState by observeFavoriteTrackState.observeAsState()

    if (favoriteTrackState!!.isEmpty) NoTracks()
    else TrackList(favoriteTrackState!!.tracks, openPlayer)

}

@Composable
private fun NoTracks(){
    Column(Modifier
        .padding(top = 106.dp)
        .fillMaxWidth()) {
        Image(
            modifier = Modifier
                .size(120.dp, 120.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(R.drawable.nothing_found),
            contentDescription = "nothing foung",
            contentScale = ContentScale.FillWidth
        )
        Text(modifier = Modifier.align(Alignment.CenterHorizontally)
            .padding(top = 16.dp),
            text = stringResource(R.string.no_favorite_tracks),
            textAlign = TextAlign.Center,
            fontSize = dimensionResource(R.dimen.big_text).value.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(R.color.search_pl_text),
        )
    }
}

@Composable
private fun TrackList(tracks: List<Track>, openPlayer: (Track) -> Unit ) {
    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
        items(tracks.size) { index -> TrackRow(tracks[index], openPlayer) }
    }
}

@Composable
@Preview
private fun NoTracksPreview(){
    NoTracks()
}

@Composable
@Preview
private fun TracksPreview(){
    TrackList(dummyTracks(), {})
}

