package uz.jahongir.cameragallery

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import uz.jahongir.cameragallery.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gallery.setOnClickListener{
            val imageFile = createImageFile()
            photoUri = FileProvider.getUriForFile(
                this, BuildConfig.APPLICATION_ID, imageFile)
            getTakeImage.launch(photoUri)
        }
        binding.image.setOnClickListener {
            binding.image.start()
        }
        binding.image.setOnLongClickListener {
            binding.image.pause()
            true
        }

        binding.clear.setOnClickListener {
            clearImage()
        }
    }

    private fun clearImage() {
        val externalFilesDir = filesDir
        if (externalFilesDir?.isDirectory  == true){
            val listFiles = externalFilesDir.listFiles()
            if (listFiles?.isEmpty()==true){
                Toast.makeText(this, "No image!", Toast.LENGTH_SHORT).show()
                return
            }
            listFiles?.forEach {
                println(it)
                it.delete()
            }
        }
    }

    private val getTakeImage =
        registerForActivityResult(ActivityResultContracts.TakeVideo()) {
            //if (it){
                val inputStream = contentResolver.openInputStream(photoUri)
                val format = SimpleDateFormat("yyyyMM_HHmmss", Locale.getDefault()).format(Date())
                val file = File(filesDir,"$format.mp4")
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)
                inputStream?.close()
                binding.image.setVideoURI(photoUri)
            //}
        }

    var filePath = ""
    private fun createImageFile(): File {
        val format = SimpleDateFormat("yyyyMM_HHmmss", Locale.getDefault()).format(Date())
        val externalFileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("Jpeg_$format", ".mp4", externalFileDir).apply {
            filePath = absolutePath
        }
    }
}