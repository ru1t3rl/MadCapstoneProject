package tech.ru1t3rl.madcapstoneproject.dao

import tech.ru1t3rl.madcapstoneproject.model.Run

interface RunDao {
    fun getAllRuns(): List<Run>
    fun addRun(run: Run): String
    fun getRun(id: String) : Run?
}