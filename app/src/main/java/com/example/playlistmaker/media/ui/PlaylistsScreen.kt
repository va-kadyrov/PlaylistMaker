package com.example.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.playlistmaker.R
import com.example.playlistmaker.media.data.Playlist

@Composable
fun PlaylistsScreen(
    observePlaylistsState: LiveData<PlaylistsState>,
    addNewPlaylist: () -> Unit,
    openPlaylist: (Long) -> Unit
) {

    val playlistsState by observePlaylistsState.observeAsState()

    PlaylistsScreen1(playlistsState!!, addNewPlaylist, openPlaylist)
}

@Composable
fun PlaylistsScreen1(
    playlistsState: PlaylistsState,
    addNewPlaylist: () -> Unit,
    openPlaylist: (Long) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        ButtonNewPlaylist(addNewPlaylist)

        if (playlistsState?.isEmpty ?: true) {
            NoPlaylists()
        } else {
            PlaylistsList(playlistsState!!.playlists, openPlaylist)
        }
    }
}

@Composable
private fun ButtonNewPlaylist(onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(top = 24.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = colorResource(R.color.settings_back),
            containerColor = colorResource(R.color.search_pl_text)
        ),
    ) {
        Text(
            text = stringResource(R.string.new_playlist),
            fontSize = dimensionResource(R.dimen.little_text).value.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        )
    }
}

@Composable
fun PlaylistsList(playlists: List<Playlist>, openPlaylist: (Long) -> Unit ){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement =  Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(playlists.size) { index -> PlaylistRow(playlists[index], openPlaylist) }
    }
}


@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun PlaylistRow(playlist: Playlist, openPlaylist: (Long) -> Unit){
    Column(modifier = Modifier.clickable{openPlaylist(playlist.id)}) {
        GlideImage(
            model = playlist.filePath,
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1F)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            loading = placeholder(painterResource(R.drawable.album_cover_empty)),
            failure = placeholder(painterResource(R.drawable.album_cover_empty)),
        )
        Text(
            text = playlist.name,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(R.color.almostblack_white),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = trackCountsToString(playlist.trackCounts),
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(R.color.almostblack_white),
            maxLines = 1,
        )
    }
}


private fun trackCountsToString(trackCounts: Int): String {
    when {
        trackCounts in 11..20 -> return "$trackCounts треков"
        trackCounts % 10 == 1 -> return "$trackCounts трек"
        trackCounts % 10 in 2..4 -> return "$trackCounts трека"
        else -> return "$trackCounts треков"
    }
}


@Composable
private fun NoPlaylists(){
    Column(Modifier
        .padding(vertical = 46.dp)
        .fillMaxWidth()) {
        Image(
            modifier = Modifier
                .size(120.dp, 120.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(R.drawable.nothing_found),
            contentDescription = "nothing foung",
            contentScale = ContentScale.FillWidth
        )
        Text(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = 24.dp, vertical = 16.dp),
            text = stringResource(R.string.no_playlists),
            textAlign = TextAlign.Center,
            fontSize = dimensionResource(R.dimen.big_text).value.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(R.color.search_pl_text),
        )
    }
}




@Composable
@Preview
fun NoPlaylistsPreview(){
    PlaylistsScreen1(PlaylistsState(true,dummyPlaylists()), {}, {})
}


@Composable
@Preview
fun PlaylistsPreview(){
    PlaylistsScreen1(PlaylistsState(false,dummyPlaylists()), {}, {})
}


@Composable
private fun dummyPlaylists():List<Playlist>{
    return listOf<Playlist>(
        Playlist(1001L, "Серебро Серебро Серебро Серебро ", "Серебро", "",
            emptyList<Long>().toMutableList(), 0, 10),
        Playlist(1002L, "Варвара", "Ага", "",
            emptyList<Long>().toMutableList(), 11, 20),
        Playlist(1003L, "Варвара", "Ага", "",
            emptyList<Long>().toMutableList(), 2, 30),
        Playlist(1004L, "Варвара", "Ага", "",
            emptyList<Long>().toMutableList(), 21, 40),
        Playlist(1005L, "Варвара", "Ага", "",
            emptyList<Long>().toMutableList(), 3, 50),
    )
}