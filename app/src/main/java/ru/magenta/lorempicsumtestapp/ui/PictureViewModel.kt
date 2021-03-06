package ru.magenta.lorempicsumtestapp.ui

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.magenta.lorempicsumtestapp.domain.PicturesInteractor

class PictureViewModel(
    private val picturesInteractor: PicturesInteractor,
    private val mapper: PicturesDomainToUiMapper,
    private val communication: PictureCommunication,
    private val pictureCache: PictureCache
) : ViewModel() {
    fun fetchPictures() {
        if (communication.isEmpty()) {
            communication.map(listOf(PictureUi.Progress))
            viewModelScope.launch(Dispatchers.IO) {
                val result = picturesInteractor.fetchPictures()
                val resultUi = result.map(mapper)
                withContext(Dispatchers.Main) {
                    resultUi.map(communication)
                }
            }
        }
    }

    fun observe(owner: LifecycleOwner, observer: Observer<List<PictureUi>>) {
        communication.observe(owner, observer)
    }

    fun likeOrUnlike(id: Int) = pictureCache.changeState(id)

    fun saveState() = pictureCache.finishId()
    fun startCache() = pictureCache.readId()

    fun addNewPicture() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = picturesInteractor.fetchPictures()
            val resultUi = result.map(mapper)
            withContext(Dispatchers.Main) {
                resultUi.map(communication)
            }
        }
    }
}