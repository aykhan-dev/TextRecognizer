package ev.aykhan.textrecognizer.repository

import ev.aykhan.textrecognizer.local.AppDatabase
import ev.aykhan.textrecognizer.model.entity.ExtractedText

class ExtractedTextsRepository(database: AppDatabase) {

    private val dao = database.extractedTextsDao

    val listOfExtractedTexts = dao.getAllExtractedTexts()

    suspend fun insert(extractedText: ExtractedText): Unit = dao.insert(extractedText)

    suspend fun delete(extractedText: ExtractedText): Unit = dao.delete(extractedText)

}