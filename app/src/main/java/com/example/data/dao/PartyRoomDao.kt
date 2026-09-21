package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PartyRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartyRoomDao {
    @Query("SELECT * FROM party_rooms ORDER BY roomLevel DESC, onlineCount DESC")
    fun getAllRooms(): Flow<List<PartyRoomEntity>>

    @Query("SELECT * FROM party_rooms WHERE id = :id LIMIT 1")
    fun getRoomById(id: Long): Flow<PartyRoomEntity?>

    @Query("SELECT * FROM party_rooms WHERE id = :id LIMIT 1")
    suspend fun getRoomByIdOnce(id: Long): PartyRoomEntity?

    @Query("SELECT COUNT(*) FROM party_rooms")
    suspend fun getRoomCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: PartyRoomEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<PartyRoomEntity>)

    @Update
    suspend fun updateRoom(room: PartyRoomEntity)

    @Query("UPDATE party_rooms SET roomExp = roomExp + :addedExp, roomLevel = (roomExp + :addedExp) / 600 + 1 WHERE id = :roomId")
    suspend fun addRoomExp(roomId: Long, addedExp: Long)

    @Query("DELETE FROM party_rooms WHERE id = :id")
    suspend fun deleteRoom(id: Long)
}
