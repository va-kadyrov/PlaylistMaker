package com.example.playlistmaker.search.ui

import android.service.autofill.OnClickAction
import android.widget.ProgressBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.fontResource
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
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import com.example.playlistmaker.main.ui.TrackRow
import com.example.playlistmaker.settings.ui.ContentButton
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date


@Composable
@Preview
fun SettingsScreenPreview() {
//    SearchScreen()
}

@Composable
fun SearchScreen(
    observeTrackState: LiveData<TracksState>,
    observeTrackHistoryState: LiveData<TracksHistoryState>,
//    observeInputTextState: LiveData<String>,
    searchTextEntered: () -> Unit,
    searchTextChanged: (String) -> Unit,
    repeatSearch: () -> Unit,
    addTrackToHistory: (Track) -> Unit,
    clearTracksHistory: () -> Unit,
    searchFielsOnFocus: () -> Unit,
    openPlayer: (Track) -> Unit,
    )

{
    val trackState by observeTrackState.observeAsState()
    val trackHistoryState by observeTrackHistoryState.observeAsState()
//    val restoredInputText by observeInputTextState.observeAsState()

//    val focusManager = LocalFocusManager.current
//    val keyboardController = LocalSoftwareKeyboardController.current

    Column {

        TopBar()

        SearchTextField(searchTextEntered, searchTextChanged)

        when {
            trackHistoryState == null -> {}
            trackHistoryState!!.isVisible -> HistoryTracksList(
                trackHistoryState!!.content, openPlayer,
                clearTracksHistory
            )

            trackState == null -> {}
            trackState!!.isLoading -> MyProgressBar()
            trackState!!.isEmpty -> NothingFound()
            trackState!!.isError -> NetworkError(repeatSearch)
            else -> TracksList(trackState!!.content, { track -> addTrackToHistory(track); openPlayer(track)})
        }
    }
}

@Composable
private fun MyProgressBar(){
    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}


@Composable
private fun NothingFound(){
    NoTracks(R.drawable.nothing_found, R.string.nothing_found)
}


@Composable
private fun NetworkError(repeatSearch: () -> Unit){
    NoTracks(R.drawable.network_error, R.string.network_error, true, repeatSearch)
}


@Composable
private fun NoTracks(imageResource: Int, textResource: Int,
                     haveButton: Boolean = false, repeatSearch: () -> Unit = {}){
    Column(Modifier
        .padding(vertical = 12.dp)
        .fillMaxWidth()) {
        Image(
            modifier = Modifier
                .size(120.dp, 120.dp)
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(imageResource),
            contentDescription = "nothing foung",
            contentScale = ContentScale.FillWidth
        )
        Text(modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(textResource),
            textAlign = TextAlign.Center,
            fontSize = dimensionResource(R.dimen.big_text).value.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = colorResource(R.color.search_pl_text),

        )
        if (haveButton) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = repeatSearch,
                colors = ButtonDefaults.buttonColors(
                contentColor = colorResource(R.color.settings_back),
                containerColor = colorResource(R.color.search_pl_text)
                ),
            ) {
                Text(
                    text = stringResource(R.string.btn_reload),
                    fontSize = dimensionResource(R.dimen.little_text).value.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                )
            }
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(){
    TopAppBar(
        title = {Text (text = stringResource(R.string.search_header))},
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(color = colorResource(R.color.search_btn_back))
    )

}


@Composable
private fun SearchTextField(
//    restoredInputText: String,
    searchTextEntered: () -> Unit,
    searchTextChanged: (String) -> Unit,
) {
    var inputText = rememberSaveable{ mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = dimensionResource(R.dimen.padding_horizontal)),

        ) {
        OutlinedTextField(
            shape = RoundedCornerShape(8.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = Color.Transparent,
//                unfocusedBorderColor = Color.Transparent
//            ),
            modifier = Modifier
                .background(color = colorResource(R.color.search_ev_back))
                .fillMaxWidth(),
            value = inputText.value,
            onValueChange = { newText -> inputText.value = newText; searchTextChanged(newText) },
            keyboardActions = KeyboardActions(
                onDone = {searchTextEntered()},
                onSearch = {searchTextEntered()},
            ),
            leadingIcon = {Icon(
                painter = painterResource(R.drawable.btn_search_small),
                contentDescription = null,
                tint = Color(0xCC, 0xCC, 0xCC),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(alignment = Alignment.CenterStart)
            )},
            placeholder = { Text(stringResource(R.string.search_hint)) },
            singleLine = true,
        )

        IconButton(onClick = { inputText.value = ""; searchTextChanged(""); searchTextEntered(); },
            modifier = Modifier.align(alignment = Alignment.CenterEnd)
        )  {
            Icon(
                painter = painterResource(R.drawable.search_clear),
                contentDescription = "Очистить"
            )
        }
    }
}

@Composable
fun TracksList(tracks: List<Track>, onItemClick: (Track) -> Unit) {
    LazyColumn() {
        items(tracks.size) { index -> TrackRow(tracks[index], onItemClick) }
    }
}

@Composable
fun HistoryTracksList(tracks: List<Track>, onItemClick: (Track) -> Unit, clearTracksHistory: () -> Unit) {
    Column(){
    LazyColumn() {
        items(tracks.size) { index -> TrackRow(tracks[index], onItemClick) }
    }

    Button(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        onClick = clearTracksHistory,
        colors = ButtonDefaults.buttonColors(
            contentColor = colorResource(R.color.settings_back),
            containerColor = colorResource(R.color.search_pl_text)
        ),
    ) {
        Text(
            text = stringResource(R.string.btn_clear_history),
            fontSize = dimensionResource(R.dimen.little_text).value.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        )
    }}
}


@Composable
@Preview
fun TracksListPreview(){
    TracksList(dummyTracks(), {})
}

@Composable
@Preview
fun NothingFoundPreview(){
    NothingFound()
}

@Composable
@Preview
fun NetworkErrorPreview(){
    NetworkError({})
}

@Composable
private fun dummyTracks():List<Track>{
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
    )
}
