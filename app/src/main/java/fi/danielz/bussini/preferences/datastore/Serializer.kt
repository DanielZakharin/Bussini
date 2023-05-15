package fi.danielz.bussini.preferences.datastore

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object StopDisplayDataSerializer : Serializer<StopDisplayData>() {
    override val defaultValue: StopDisplayData = StopDisplayData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): StopDisplayData {
        TODO("Not yet implemented")
    }

    override suspend fun writeTo(t: StopDisplayData, output: OutputStream) {
        TODO("Not yet implemented")
    }

}