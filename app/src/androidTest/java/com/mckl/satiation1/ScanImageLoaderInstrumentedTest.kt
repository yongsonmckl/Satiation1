package com.mckl.satiation1

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mckl.satiation1.ai.ScanImageLoader
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScanImageLoaderInstrumentedTest {

    @Test
    fun loadBitmap_rejectsUnreadableImageContent() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val corruptImage = File(context.cacheDir, "corrupt_scan_image.jpg").apply {
            writeText("this is not an image")
        }

        val result = runCatching {
            ScanImageLoader.loadBitmap(context, Uri.fromFile(corruptImage))
        }

        assertTrue(result.exceptionOrNull()?.message.orEmpty().isNotBlank() || result.isFailure)
    }
}
