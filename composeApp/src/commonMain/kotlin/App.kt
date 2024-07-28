import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import networking.InsultCensorClient
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.NetworkError
import util.onError
import util.onSuccess


@Composable
@Preview
fun App(client: InsultCensorClient) {
    MaterialTheme {

        var censoredText by remember {
            mutableStateOf<String?>(null)
        }

        var unCensoredText by remember {
            mutableStateOf<String>("")
        }

        var isLoading by remember {
            mutableStateOf<Boolean>(false)
        }

        var errorMessage by remember {
            mutableStateOf<NetworkError?>(null)
        }

        val scope = rememberCoroutineScope()

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            TextField(
                value = unCensoredText,
                onValueChange = { unCensoredText = it },
                modifier = Modifier.padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text("Enter text to censor")
                }
            )
            Button(onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null
                    censoredText = null

                    client.censorWords(unCensoredText)
                        .onSuccess {
                            censoredText = it
                        }
                        .onError {
                            errorMessage = it
                        }

                    isLoading = false


                }
            }) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 1.dp,
                        color = Color.White
                    )
                } else {
                    Text("Censor")
                }
            }

            censoredText?.let {
                Text(it)
            }

            errorMessage?.let {
                Text(it.name, color = Color.Red)
            }
        }
    }
}