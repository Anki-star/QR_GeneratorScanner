package com.example.qrgeneratorandscanner

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrgeneratorandscanner.ui.theme.QRGeneratorAndScannerTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            QRGeneratorAndScannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QRCodeApp();
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeApp(){
    var inputText by remember { mutableStateOf("") }
    var qrBitmap by remember{ mutableStateOf<Bitmap?>(null)}
    var scanResult by remember { mutableStateOf("")}

    var scanLauncher = rememberLauncherForActivityResult(contract = ScanContract()){ result ->
        if(result.contents != null){
            scanResult = result.contents
        }else{
            scanResult = "Scan Cancelled"
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("QR Code App") })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input field
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
//                        .border(
////                            border = BorderStroke(1.dp, Color.GRAY),
//
//                        )
                        .padding(8.dp),
                    singleLine = true
                )

                // Generate QR Code Button
                Button(onClick = {
                    qrBitmap = generateQRCode(inputText)
                }) {
                    Text("Generate QR Code")
                }

                // Display Generated QR Code
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Generated QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                }

                // Scan QR Code Button
                Button(onClick = {
                    val options = ScanOptions().apply {
                        setPrompt("Scan a QR code")
                        setBeepEnabled(true)
                    }
                    scanLauncher.launch(options)
                }) {
                    Text("Scan QR Code")
                }

                // Display Scan Result
                if (scanResult.isNotEmpty()) {
                    Text("Scanned Result: $scanResult", color = androidx.compose.ui.graphics.Color.Black, fontSize = 16.sp)
                }
            }
        }
    )

}
fun generateQRCode(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (bitMatrix[x, y]) {
                    android.graphics.Color.BLACK
                } else {
                    android.graphics.Color.WHITE
                }
            }
        }
        Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}
