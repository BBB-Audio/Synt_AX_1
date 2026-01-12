package bbb.audio.syntAX1.data.repository

import bbb.audio.syntAX1.data.local.dao.PatchDao
import bbb.audio.syntAX1.data.model.Patch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PatchRepository(private val patchDao: PatchDao) {

    suspend fun savePatch(name: String, synthStateJson: String) {
        withContext(Dispatchers.IO) {
            val blob = synthStateJson.toByteArray()
            val patch = Patch(name = name, synthStateBlob = blob)
            patchDao.insertPatch(patch)
        }
    }

    suspend fun getPatch(id: Long): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            val patch = patchDao.getPatch(id)
            patch?.let {
                val synthStateJson = String(it.synthStateBlob)
                Pair(it.name, synthStateJson)
            }
        }
    }

    fun getAllPatches(): Flow<List<Pair<Long, String>>> {
        return patchDao.getAllPatches().map { patches ->
            patches.map { it.id to it.name }
        }
    }

    suspend fun deletePatch(id: Long) {
        withContext(Dispatchers.IO) {
            patchDao.deletePatchById(id)
        }
    }
}