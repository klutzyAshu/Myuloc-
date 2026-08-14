package com.example.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// --- Room Entities ---

@Entity(tableName = "cached_tracks")
data class CachedTrack(
    @PrimaryKey val id: String, // Google Drive File ID
    val title: String,
    val artist: String,
    val durationMs: Long,
    val source: String, // "Locker"
    val streamUrl: String,
    val thumbnailUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_tracks")
data class FavoriteTrack(
    @PrimaryKey val id: String, // File ID or YouTube ID
    val title: String,
    val artist: String,
    val durationMs: Long,
    val source: String, // "Locker" or "Search"
    val streamUrl: String,
    val thumbnailUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "offline_tracks")
data class OfflineTrack(
    @PrimaryKey val id: String, // File ID (e.g. Drive id or local file path hash)
    val title: String,
    val artist: String,
    val durationMs: Long,
    val source: String, // "Locker" or "Local"
    val localUri: String, // Local filepath uri (e.g. file:///...)
    val thumbnailUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

// --- DAOs (Data Access Objects) ---

@Dao
interface CachedTrackDao {
    @Query("SELECT * FROM cached_tracks ORDER BY title ASC")
    fun getAllCachedTracks(): Flow<List<CachedTrack>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<CachedTrack>)

    @Query("DELETE FROM cached_tracks")
    suspend fun clearAll()
}

@Dao
interface FavoriteTrackDao {
    @Query("SELECT * FROM favorite_tracks ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteTrack>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(track: FavoriteTrack)

    @Query("DELETE FROM favorite_tracks WHERE id = :trackId")
    suspend fun removeFavorite(trackId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE id = :trackId)")
    suspend fun isFavorite(trackId: String): Boolean
}

@Dao
interface PreferenceDao {
    @Query("SELECT value FROM preferences WHERE `key` = :prefKey")
    suspend fun getPreference(prefKey: String): String?

    @Query("SELECT * FROM preferences WHERE `key` LIKE :prefix || '%'")
    suspend fun getPreferencesWithPrefix(prefix: String): List<PreferenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPreference(pref: PreferenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPreferences(prefs: List<PreferenceEntity>)

    @Query("DELETE FROM preferences WHERE `key` = :prefKey")
    suspend fun removePreference(prefKey: String)
}

@Dao
interface OfflineTrackDao {
    @Query("SELECT * FROM offline_tracks ORDER BY timestamp DESC")
    fun getAllOfflineTracks(): Flow<List<OfflineTrack>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineTrack(track: OfflineTrack)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineTracks(tracks: List<OfflineTrack>)

    @Query("DELETE FROM offline_tracks WHERE id = :trackId")
    suspend fun removeOfflineTrack(trackId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM offline_tracks WHERE id = :trackId)")
    suspend fun isOffline(trackId: String): Boolean

    @Query("SELECT * FROM offline_tracks WHERE id = :trackId")
    suspend fun getOfflineTrack(trackId: String): OfflineTrack?
}

// --- App Database ---

@Database(
    entities = [CachedTrack::class, FavoriteTrack::class, PreferenceEntity::class, OfflineTrack::class],
    version = 2,
    exportSchema = true
)
abstract class MyuLocDatabase : RoomDatabase() {
    abstract fun cachedTrackDao(): CachedTrackDao
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun preferenceDao(): PreferenceDao
    abstract fun offlineTrackDao(): OfflineTrackDao

    companion object {
        @Volatile
        private var INSTANCE: MyuLocDatabase? = null

        fun getDatabase(context: Context): MyuLocDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    MyuLocDatabase::class.java,
                    "myuloc_database"
                )
                if (com.example.BuildConfig.DEBUG) {
                    builder.fallbackToDestructiveMigration(dropAllTables = true)
                }
                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }
    }
}
