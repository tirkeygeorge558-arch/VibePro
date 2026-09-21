package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.PartyRoomDao
import com.example.data.dao.RecordingDao
import com.example.data.dao.SongDao
import com.example.data.dao.UserDao
import com.example.data.model.PartyRoomEntity
import com.example.data.model.RecordingEntity
import com.example.data.model.SongEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        SongEntity::class,
        PartyRoomEntity::class,
        RecordingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun songDao(): SongDao
    abstract fun partyRoomDao(): PartyRoomDao
    abstract fun recordingDao(): RecordingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "starmaker_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                val userDao = database.userDao()
                val songDao = database.songDao()
                val partyRoomDao = database.partyRoomDao()

                // 1. FIRST USER: Receives ADMIN privileges automatically!
                // The prompt explicitly states: "first user can get admin panel"
                val firstUser = UserEntity(
                    username = "StarKing_Alex",
                    standardId = "SM-10001",
                    exclusiveId = "★STAR-EMPEROR★",
                    level = 58, // Above 50 so exclusive ID is active!
                    currentExp = 28400,
                    coins = 50000,
                    diamonds = 2500,
                    vipTier = 5, // Star Diamond VIP
                    nobleTitle = "Emperor",
                    isAdmin = true, // FIRST USER HAS ADMIN PANEL!
                    bio = "StarMaker Pioneer & Admin 👑 Level 50+ Exclusive ID Holder",
                    songsSung = 42,
                    followers = 1890,
                    following = 80
                )
                val firstUserId = userDao.insertUser(firstUser)

                // Add a second user (standard user below level 50)
                val secondUser = UserEntity(
                    username = "NeonVocalist",
                    standardId = "SM-88231",
                    exclusiveId = null, // Level < 50 so exclusive id is locked
                    level = 24,
                    currentExp = 12000,
                    coins = 4500,
                    diamonds = 200,
                    vipTier = 2,
                    nobleTitle = "Knight",
                    isAdmin = false,
                    bio = "Pop & Ballad karaoke lover! Aiming for Level 50 Exclusive ID 🎤",
                    songsSung = 15,
                    followers = 340,
                    following = 110
                )
                userDao.insertUser(secondUser)

                // 2. Initial songs with real LRC timestamped lyrics
                val initialSongs = listOf(
                    SongEntity(
                        title = "Neon Star Anthem",
                        artist = "StarMaker All-Stars",
                        genre = "Pop Hits",
                        durationSec = 120,
                        lrcLyrics = """
                            [00:02.00] Step into the neon spotlight tonight
                            [00:05.50] All the party rooms are glowing bright
                            [00:09.20] Grab the golden mic, let your passion soar
                            [00:13.40] Hear the crowd singing back and screaming for more!
                            [00:18.00] We are the StarMaker legends in the sky
                            [00:22.50] Watch our golden stars shining high
                            [00:27.00] Level up the room, let the music flow
                            [00:31.50] Welcome to the greatest singing show!
                            [00:36.00] Sing it loud, sing it proud!
                            [00:40.00] StarMaker stars will never back down!
                        """.trimIndent(),
                        bpm = 128,
                        baseKeyNote = "C4",
                        chordProgression = "C,G,Am,F",
                        difficulty = "Easy",
                        timesSung = 890,
                        coverEmoji = "🌟",
                        uploadedByAdmin = true
                    ),
                    SongEntity(
                        title = "Diamond Crown Serenade",
                        artist = "Luna Silver",
                        genre = "Ballad",
                        durationSec = 110,
                        lrcLyrics = """
                            [00:01.80] When the twilight whispers in the dark
                            [00:05.20] Every sweet melody leaves a spark
                            [00:09.60] A royal crown of diamonds on the stage
                            [00:14.00] Turning music into our golden page
                            [00:19.00] Close your eyes and sing the gentle line
                            [00:24.00] In this room forever you will shine
                            [00:29.50] Noble hearts and dreams that never fade
                            [00:34.50] The sweetest karaoke melody we made
                        """.trimIndent(),
                        bpm = 95,
                        baseKeyNote = "G4",
                        chordProgression = "G,D,Em,C",
                        difficulty = "Medium",
                        timesSung = 560,
                        coverEmoji = "👑",
                        uploadedByAdmin = true
                    ),
                    SongEntity(
                        title = "Midnight Galaxy Beat",
                        artist = "DJ CyberPulse",
                        genre = "EDM",
                        durationSec = 130,
                        lrcLyrics = """
                            [00:02.50] Three two one drop the bassline down!
                            [00:06.00] We're turning up the volume in this town
                            [00:10.00] Super rockets flying in the live room air
                            [00:14.50] Feel the sound waves rushing everywhere!
                            [00:19.00] Level 50 exclusive aura in our veins
                            [00:23.50] Nothing holding back our party train
                            [00:28.00] Drop the beat, feel the groove
                            [00:32.50] Every singer got that rhythm in the move!
                            [00:37.00] StarMaker galaxy tonight!
                        """.trimIndent(),
                        bpm = 135,
                        baseKeyNote = "A4",
                        chordProgression = "Am,F,C,G",
                        difficulty = "Hard",
                        timesSung = 1240,
                        coverEmoji = "🪐",
                        uploadedByAdmin = true
                    ),
                    SongEntity(
                        title = "Acoustic Sunset Breeze",
                        artist = "River & Strings",
                        genre = "Acoustic",
                        durationSec = 105,
                        lrcLyrics = """
                            [00:02.00] Sitting by the open campfire glow
                            [00:06.50] Soft chords playing easy and slow
                            [00:11.00] Sing a song for the friends you hold dear
                            [00:15.50] Every voice sounds crystal and clear
                            [00:20.50] Just a guitar and a simple rhyme
                            [00:25.50] Freezing our memories in time
                        """.trimIndent(),
                        bpm = 85,
                        baseKeyNote = "D4",
                        chordProgression = "D,A,Bm,G",
                        difficulty = "Easy",
                        timesSung = 320,
                        coverEmoji = "🎸",
                        uploadedByAdmin = true
                    )
                )
                songDao.insertSongs(initialSongs)

                // 3. Initial Party Rooms
                val initialRooms = listOf(
                    PartyRoomEntity(
                        title = "🔥 24/7 Global Star Karaoke Battle",
                        hostUserId = firstUserId,
                        hostUsername = firstUser.username,
                        category = "Karaoke Club",
                        roomExp = 12400,
                        roomLevel = 21,
                        onlineCount = 89,
                        announcement = "🎤 Mic open! Sing your best songs for gifts & EXP!",
                        roomTheme = "GoldGala",
                        totalGiftsValue = 18500
                    ),
                    PartyRoomEntity(
                        title = "👑 VIP & Noble Royal Singing Hall",
                        hostUserId = firstUserId,
                        hostUsername = firstUser.username,
                        category = "Pop Stage",
                        roomExp = 26500,
                        roomLevel = 45,
                        onlineCount = 142,
                        announcement = "🌟 Emperor and King hosts welcome you. Send gifts to level up room!",
                        roomTheme = "Cyberpunk",
                        totalGiftsValue = 48000
                    ),
                    PartyRoomEntity(
                        title = "🌙 Late Night Chill Acoustic & Duets",
                        hostUserId = 2,
                        hostUsername = "NeonVocalist",
                        category = "Late Night Chill",
                        roomExp = 4200,
                        roomLevel = 8,
                        onlineCount = 34,
                        announcement = "☕ Grab a coffee and sing soft ballads with us.",
                        roomTheme = "NeonClub",
                        totalGiftsValue = 6200
                    )
                )
                partyRoomDao.insertRooms(initialRooms)
            }
        }
    }
}
