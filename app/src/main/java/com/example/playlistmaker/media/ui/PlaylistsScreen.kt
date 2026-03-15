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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
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

    if (playlistsState?.isEmpty?:true) {
        NoPlaylists(addNewPlaylist)
    } else {
        PlaylistsList(playlistsState!!.playlists, openPlaylist)
    }
}

@Composable
fun PlaylistsList(playlists: List<Playlist>, openPlaylist: (Long) -> Unit ){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
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
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = playlist.description,
            fontSize = 12.sp,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun NoPlaylists(newPlaylist: () -> Unit){
    Column(Modifier
        .padding(vertical = 12.dp)
        .fillMaxWidth()) {
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = newPlaylist,
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
        Image(
            modifier = Modifier
                .size(120.dp, 120.dp)
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(R.drawable.nothing_found),
            contentDescription = "nothing foung",
            contentScale = ContentScale.FillWidth
        )
        Text(modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.nothing_found),
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
    NoPlaylists({})
}

@Composable
@Preview
fun PlaylistsPreview(){
    PlaylistsList(dummyPlaylists(), {})
}

@Composable
private fun dummyPlaylists():List<Playlist>{
    return listOf<Playlist>(
        Playlist(1001L, "Серебро", "Серебро", "",
            emptyList<Long>().toMutableList(), 0, 10),
        Playlist(1002L, "Варвара", "Ага", "",
            emptyList<Long>().toMutableList(), 0, 20),
        Playlist(1003L, "Варвара", "Ага", "",
            emptyList<Long>().toMutableList(), 0, 30),
        Playlist(1004L, "Варвара", "Ага", "",
            emptyList<Long>().toMutableList(), 0, 40),
        Playlist(1005L, "Варвара", "Ага", "",
            emptyList<Long>().toMutableList(), 0, 50),
    )
}