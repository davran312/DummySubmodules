package namba.nambaone.wallet.domain.shared.model

import android.content.Context
import android.graphics.Bitmap
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileGateway(context: Context) {

    private val receiptFolder: File
    private val qrFolder: File

    init {
        val rootFolder = File(context.cacheDir.toString() + File.separator + "nambaone")
        receiptFolder = File(rootFolder, "receipts")
        qrFolder = File(rootFolder, "qr")
    }

    suspend fun saveReceipt(
        operationId: String,
        bytes: ByteArray
    ): File = withContext(Dispatchers.IO) {
        ensureReceiptFolderExists()

        val receiptFile = getReceiptFile(operationId)
        BufferedOutputStream(FileOutputStream(receiptFile)).use {
            it.write(bytes)
            it.flush()
        }

        return@withContext receiptFile
    }

    suspend fun getReceipt(operationId: String): File? = withContext(Dispatchers.IO) {
        getReceiptFile(operationId).takeIf { it.exists() }
    }

    suspend fun saveQr(name: String, bitmap: Bitmap): File = withContext(Dispatchers.IO) {
        ensureQrFolderExists()

        val qrFile = getQrFile(name)
        FileOutputStream(qrFile).use { bitmap.compress(Bitmap.CompressFormat.PNG, 0, it) }
        return@withContext qrFile
    }

    private fun getReceiptFile(operationId: String): File = File(receiptFolder, "$operationId.pdf")

    private fun getQrFile(name: String): File = File(qrFolder, "$name.png")

    private fun ensureReceiptFolderExists() {
        receiptFolder.mkdirs()
    }

    private fun ensureQrFolderExists() {
        qrFolder.mkdirs()
    }
}
