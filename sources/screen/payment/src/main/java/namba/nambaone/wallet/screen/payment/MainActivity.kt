package namba.nambaone.wallet.screen.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import namba.wallet.nambaone.common.network.okhttp.OkHttpClientFactory
import namba.wallet.nambaone.screen.payment.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.textView).text =
            OkHttpClientFactory.Type.SSE.name
    }
}