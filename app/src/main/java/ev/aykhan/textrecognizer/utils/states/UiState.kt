package ev.aykhan.textrecognizer.utils.states

sealed class UiState {
    object Loading : UiState()
    object Finished : UiState()
}