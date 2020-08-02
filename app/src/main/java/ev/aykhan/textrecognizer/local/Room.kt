package ev.aykhan.textrecognizer.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import ev.aykhan.textrecognizer.model.entity.ExtractedText

@Dao
abstract class ExtractedTextDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(item: ExtractedText)

    @Query("select * from extractedTexts")
    abstract fun getAllExtractedTexts(): LiveData<List<ExtractedText>>

    @Delete
    abstract suspend fun delete(item: ExtractedText)

}

@Database(
    entities = [
        ExtractedText::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract val extractedTextsDao: ExtractedTextDao
}

private const val DATABASE_NAME = "TextRecognizer.db"
private lateinit var INSTANCE: AppDatabase

fun getDatabase(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}