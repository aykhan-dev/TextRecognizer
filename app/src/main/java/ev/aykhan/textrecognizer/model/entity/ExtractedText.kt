package ev.aykhan.textrecognizer.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "extractedTexts")
data class ExtractedText(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val content: String
)