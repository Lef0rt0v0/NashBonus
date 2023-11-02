package ru.acted.nashbonus.utils

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.UUID

@Entity(tableName = "cards",
    indices = [
        Index("id")
    ]
)
data class Card (
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val brand: String,
    val number: Int
)

@Dao
interface CardDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteCard(id: String)

    @Query("SELECT * FROM cards")
    suspend fun getCards(): List<Card>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCard(id: String): Card
}

@Database(entities = [Card::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun testDao(): CardDAO

    companion object {
        fun getDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "local-db"
            ).build()

        }
    }
}

class CardManager(private val context: Context) {
    enum class TablePrefix(val prefix: String) {
        CARDS("CARD")
    }
    fun generateId(table: TablePrefix): String {
        val uuid = UUID.randomUUID().toString().replace("-", "").take(16 - table.prefix.length)
        return table.prefix + uuid
    }
    private val cardDAO = AppDatabase.getDatabase(context).testDao()

    suspend fun insertCard(card: Card) = cardDAO.insertCard(card)
    suspend fun deleteCard(id: String) = cardDAO.deleteCard(id)
    suspend fun getCards() = cardDAO.getCards()
    suspend fun getCard(id: String) = cardDAO.getCard(id)
}