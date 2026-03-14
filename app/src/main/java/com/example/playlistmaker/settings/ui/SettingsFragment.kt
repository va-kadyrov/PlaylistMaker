package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.viewModelFactory
//import com.example.playlistmaker.main.ui.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.main.ui.App
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
//    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding = FragmentSettingsBinding.inflate(inflater, container, false)
//        return binding.root
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen(viewModel::share,
                    viewModel::switchDarkTheme,
                    viewModel::support,
                    viewModel::agreement)
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
    }

    @Composable
    @Preview
    fun SettingsScreenPreview() {
        SettingsScreen({}, {}, {},{} )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
//fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel())
fun SettingsScreen(share: () -> Unit,
                   switchDarkTheme: () -> Unit,
                   support: () -> Unit,
                   agreement: () -> Unit,)
{

//    val darkThemeState by viewModel.observeDarkTheme().observeAsState()
//    val errorState by viewModel.observeErrorState().observeAsState()

    val settingsColors = ButtonDefaults.buttonColors(
        containerColor = colorResource(R.color.settings_back),
        contentColor = colorResource(R.color.settings_text)
    )

    Column {
        TopAppBar(
            title = {Text (text = stringResource(R.string.settings_header))},
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(color = colorResource(R.color.settings_back))
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.small_btn_size)),
            shape = RectangleShape,
            colors = settingsColors,
            onClick = { switchDarkTheme() },
            enabled = true,
            content = {ContentButton(title = R.string.btn_night_theme, icon = R.drawable.btn_switch)}
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.small_btn_size)),
            shape = RectangleShape,
            colors = settingsColors,
            onClick = { share() },
            enabled = true,
            content = {ContentButton(title = R.string.btn_share, icon = R.drawable.btn_share)}
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.small_btn_size)),
            shape = RectangleShape,
            colors = settingsColors,
            onClick = { support() },
            enabled = true,
            content = {ContentButton(title = R.string.btn_write_support, icon = R.drawable.btn_support)}
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.small_btn_size)),
            shape = RectangleShape,
            colors = settingsColors,
            onClick = { agreement() },
            enabled = true,
            content = {ContentButton(title = R.string.btn_user_agreement, icon = R.drawable.btn_agreement)}
        )
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
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(title),
            modifier = Modifier.align(alignment = Alignment.CenterEnd)
        )
    }
}
