    package com.example.campreview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.Surface
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.camera.core.Preview
import androidx.camera.core.ViewPort
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.campreview.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

    @Composable
    fun CameraPreview()
    {
        val previewH = (LocalConfiguration.current.screenWidthDp/3).dp
        fun startCamera(context: Context,
                        previewView: PreviewView) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // CameraSelector for back camera
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                // Set up Preview use case
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                try {
                    // Unbind all use cases before rebinding
                    cameraProvider.unbindAll()

                    val viewPort = ViewPort.Builder(Rational(3,1), Surface.ROTATION_0).
                    setScaleType(ViewPort.FILL_CENTER).build()
                    val useGrp = UseCaseGroup.Builder().
                    addUseCase(preview).
                    addUseCase(ImageAnalysis.Builder().build()).
                    setViewPort(viewPort).build()
                    // Bind the camera to the lifecycle and the Preview use case
                    cameraProvider.bindToLifecycle(context as LifecycleOwner,
                        cameraSelector, useGrp)
                } catch (exc: Exception) {
                    Log.e("CameraXApp", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
        }

        Box(modifier = Modifier.fillMaxWidth().height(previewH))
        {
            AndroidView(
                modifier = Modifier.fillMaxSize()
                    .onGloballyPositioned { c ->
                        Log.i("PREVIEW", "Androview size: ${c.size}")
                    }
                ,
                factory = { context ->
                    val previewView = PreviewView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    //focusFactory = previewView.meteringPointFactory
                    startCamera(context, previewView)
                    previewView
                }
            )
        }
    }

    @Composable
    fun Content(modifier: Modifier = Modifier)
    {
        Column(modifier = modifier)
        {
            CameraPreview()
            Text("ABYR")
            Text("VALG")
            Text("qwert")
            Text("zxcv")
        }
    }
