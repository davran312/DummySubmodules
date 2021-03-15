package namba.wallet.nambaone.uikit.extensions

import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import timber.log.Timber

fun <T> RequestBuilder<T>.applyDefaultCrop() = circleCrop()

fun <T> RequestBuilder<T>.setProgressListener(progressBar: ProgressBar): RequestBuilder<T> {
    return listener(object : RequestListener<T> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>?,
            isFirstResource: Boolean
        ): Boolean {
            Timber.tag("____").e(e)
            progressBar.isVisible = false
            return false
        }

        override fun onResourceReady(
            resource: T?,
            model: Any?,
            target: Target<T>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            progressBar.isVisible = false
            return false
        }
    })
}
