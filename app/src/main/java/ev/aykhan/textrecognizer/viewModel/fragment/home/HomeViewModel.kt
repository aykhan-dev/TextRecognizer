package ev.aykhan.textrecognizer.viewModel.fragment.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import ev.aykhan.textrecognizer.R
import ev.aykhan.textrecognizer.local.getDatabase
import ev.aykhan.textrecognizer.model.entity.ExtractedText
import ev.aykhan.textrecognizer.repository.ExtractedTextsRepository
import ev.aykhan.textrecognizer.utils.LANGUAGES
import ev.aykhan.textrecognizer.utils.isInternetConnectionAvailable
import ev.aykhan.textrecognizer.utils.states.UiState
import ev.aykhan.textrecognizer.utils.toastShort
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository = ExtractedTextsRepository(getDatabase(app))

    val listOfExtractedTexts = repository.listOfExtractedTexts

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    fun deleteExtractedText(text: ExtractedText) {
        viewModelScope.launch { repository.delete(text) }
    }

    fun processImage(uri: Uri) {
        if (app.isInternetConnectionAvailable()) processImageOnline(uri)
        else app.toastShort(app.getString(R.string.no_internet_connection))
    }

    private fun processImageOnline(uri: Uri) {
        try {

            _uiState.value = UiState.Loading

            val image = FirebaseVisionImage.fromFilePath(app, uri)

            val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(LANGUAGES)
                .build()

            val recognizer = FirebaseVision.getInstance().getCloudTextRecognizer(options)

            recognizer.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    _uiState.value = UiState.Finished
                    processTextRecognitionResultOnline(firebaseVisionText)
                }
                .addOnFailureListener { exception ->
                    _uiState.value = UiState.Finished
                    exception.printStackTrace()
                    app.toastShort(app.getString(R.string.reading_exception))
                }

        } catch (e: IOException) {
            _uiState.value = UiState.Finished
            e.printStackTrace()
            app.toastShort(app.getString(R.string.error_message))
        }
    }

    private fun processTextRecognitionResultOnline(result: FirebaseVisionText) {
        viewModelScope.launch {
            repository.insert(ExtractedText(content = result.text))
        }
    }

}