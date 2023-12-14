package com.example.mytestpeoject

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.gcacace.signaturepad.views.SignaturePad

import java.io.*


class SignaturePadViewActivity : AppCompatActivity() {

    private var isSignatured = false
    lateinit var signaturePad:SignaturePad
    lateinit var saveButton:Button
    lateinit var clearButton:Button
    lateinit var btnDone:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature_pad_view)
        signaturePad = findViewById<SignaturePad>(R.id.signature_pad)
        saveButton = findViewById<Button>(R.id.save_button)
        clearButton = findViewById<Button>(R.id.clear_button)
        btnDone = findViewById<Button>(R.id.btn_done)

        signaturePad!!.setOnSignedListener(object :
            SignaturePad.OnSignedListener {
            override fun onStartSigning() {

            }

            override fun onSigned() {
                isSignatured = true
                saveButton!!.isEnabled = true
                clearButton!!.isEnabled = true
            }

            override fun onClear() {
                isSignatured = false
                saveButton!!.isEnabled = false
                clearButton!!.isEnabled = false

            }
        })

        clearButton!!.setOnClickListener { signaturePad!!.clear() }
        saveButton!!.setOnClickListener {
            val signatureBitmap = signaturePad!!.signatureBitmap
            if (addJpgSignatureToGallery(signatureBitmap)) Toast.makeText(
                this@SignaturePadViewActivity,
                "",
                Toast.LENGTH_SHORT
            ).show() else Toast.makeText(
                this@SignaturePadViewActivity,
                "",
                Toast.LENGTH_SHORT
            ).show()
            if (addSvgSignatureToGallery(signaturePad!!.signatureSvg)) {
                Toast.makeText(
                    this@SignaturePadViewActivity,
                    "",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@SignaturePadViewActivity,
                    "",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnDone.setOnClickListener {
            val byteArrayOutputStream = ByteArrayOutputStream()
            signaturePad!!.signatureBitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                byteArrayOutputStream
            )
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            //if(varsignaturePadBinding.signaturePad.signatureBitmap == null)
            if (isSignatured == false) {
                Toast.makeText(
                    this@SignaturePadViewActivity,
                    "",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = getIntent()
                intent.putExtra("SignImage", byteArray)
                setResult(RESULT_OK, intent)
                finish()

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
    // If request is cancelled, the result arrays are empty.
                if (grantResults.size <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) Toast.makeText(
                    this@SignaturePadViewActivity,
                    "Cannot write images to external storage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getAlbumStorageDir(albumName: String?): File {

        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
        }
        return file
    }

    @Throws(IOException::class)
    private fun saveBitmapToJPG(bitmap: Bitmap, photo: File?) {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val stream: OutputStream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

    private fun addJpgSignatureToGallery(signature: Bitmap): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.jpg", System.currentTimeMillis())
            )
            saveBitmapToJPG(signature, photo)
            scanMediaFile(photo)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(photo)
        mediaScanIntent.data = contentUri
        this@SignaturePadViewActivity.sendBroadcast(mediaScanIntent)
    }

    private fun addSvgSignatureToGallery(signatureSvg: String?): Boolean {
        var result = false
        try {
            val svgFile = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.svg", System.currentTimeMillis())
            )
            val stream: OutputStream = FileOutputStream(svgFile)
            val writer = OutputStreamWriter(stream)
            writer.write(signatureSvg)
            writer.close()
            stream.flush()
            stream.close()
            scanMediaFile(svgFile)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        fun verifyStoragePermissions(activity: Activity?) {
            // Check if we have write permission
            val permission = ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }
}