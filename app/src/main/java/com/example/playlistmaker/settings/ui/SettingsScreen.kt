package com.example.playlistmaker.settings.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.example.playlistmaker.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsScreen(
    observeErrorState: LiveData<String>?,
    share: () -> Unit,
    switchDarkTheme: () -> Unit,
    support: () -> Unit,
    agreement: () -> Unit,
) {

    val errorState by observeErrorState!!.observeAsState()

    val settingsColors = ButtonDefaults.buttonColors(
        containerColor = colorResource(R.color.settings_back),
    )

    Surface(color = colorResource(R.color.settings_back)) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings_header)) },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.settings_back),
                    titleContentColor = colorResource(R.color.settings_text),
                )
            )

            Button(
                modifier = Modifier.height(dimensionResource(R.dimen.small_btn_size)),
                shape = RectangleShape,
                colors = settingsColors,
                contentPadding = PaddingValues(0.dp),
                onClick = { switchDarkTheme() },
                enabled = true,
                content = {
                    ContentButton(
                        title = R.string.btn_night_theme,
                        icon = R.drawable.btn_switch
                    )
                }
            )

            Button(
                modifier = Modifier.height(dimensionResource(R.dimen.small_btn_size)),
                shape = RectangleShape,
                colors = settingsColors,
                contentPadding = PaddingValues(0.dp),
                onClick = { share() },
                enabled = true,
                content = { ContentButton(title = R.string.btn_share, icon = R.drawable.btn_share) }
            )

            Button(
                modifier = Modifier.height(dimensionResource(R.dimen.small_btn_size)),
                shape = RectangleShape,
                colors = settingsColors,
                contentPadding = PaddingValues(0.dp),
                onClick = { support() },
                enabled = true,
                content = {
                    ContentButton(
                        title = R.string.btn_write_support,
                        icon = R.drawable.btn_support
                    )
                }
            )

            Button(
                modifier = Modifier.height(dimensionResource(R.dimen.small_btn_size)),
                shape = RectangleShape,
                colors = settingsColors,
                contentPadding = PaddingValues(0.dp),
                onClick = { agreement() },
                enabled = true,
                content = {
                    ContentButton(
                        title = R.string.btn_user_agreement,
                        icon = R.drawable.btn_agreement
                    )
                }
            )
        }
    }
}

@Composable
fun ContentButton(title: Int, icon: Int) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(title),
            modifier = Modifier.align(Alignment.CenterStart),
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontSize = dimensionResource(R.dimen.middle_text).value.sp,
            color = colorResource(R.color.settings_text)
        )
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(title),
            modifier = Modifier.align(alignment = Alignment.CenterEnd),
            tint = Color.Unspecified
        )
    }
}

@Composable
@Preview
fun SettingsScreenPreview() {
    SettingsScreen(null, {}, {}, {}, {})
}