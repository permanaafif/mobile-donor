package com.afifpermana.donor

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.afifpermana.donor.UploadRequestBody.ProgressUpdater
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class UploadRequestBodyFragment(
    private val gambar: File,
    private val contentType: String,
    private val callback: FragmentActivity
): RequestBody() {
    override fun contentType()= MediaType.parse("$contentType/*")

    override fun contentLength() = gambar.length()

    override fun writeTo(sink: BufferedSink) {
        val length = gambar.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(gambar)
        var uploaded = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also { read = it } != -1) {
                handler.post(ProgressUpdater(uploaded, length))
                uploaded += read
                sink.write(buffer, 0, read)
            }
        }
    }

    interface UploadCallback {
        fun onProgressUpdate(percentage: Int)
    }

    inner class ProgressUpdater(
        private val uploaded: Long,
        private val total: Long
    ) : Runnable {
        override fun run() {
//            callback.onProgressUpdate((100 * uploaded / total).toInt())
        }
    }

}