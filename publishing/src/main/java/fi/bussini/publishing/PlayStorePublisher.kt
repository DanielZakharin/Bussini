package fi.bussini.publishing

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import com.google.api.services.androidpublisher.model.AppEdit
import com.google.api.services.androidpublisher.model.Bundle
import com.google.api.services.androidpublisher.model.LocalizedText
import com.google.api.services.androidpublisher.model.TrackRelease
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import java.io.File
import java.io.FileInputStream

private const val WEAR_OS_PROD_TRACK = "wear:production"

private val allowedPublishStatuses = listOf("complete", "draft")

// from https://github.com/stasheq/google-play-apk-upload/blob/main/apk_upload/src/main/kotlin/me/szymanski/apkupload/Main.kt
object PlayStorePublisher {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            println("Started upload script at ${System.currentTimeMillis()}!")
            val appId = "fi.danielz.bussini"
            val apkPath = args[0]
            val credentialsPath = args[1]
            val publishStatus = args.getOrElse(2) { "complete" }
            if (!allowedPublishStatuses.contains(publishStatus)) {
                throw Exception("Illegal publish status!")
            }
            println("Release will have status: $publishStatus")
            val credentials = authenticate(credentialsPath)
            println("Succesfully authenticated with credentials")
            val androidPublisher = makePublisher(credentials)
            println("Succesfully instantiated AndroidPublisher")

            val bundleVersionNumber = uploadBundle(appId, apkPath, androidPublisher)
            updateTrack(androidPublisher, appId, bundleVersionNumber, publishStatus)
            println("Success! New release created")
        } catch (e: Exception) {
            println("Exception when trying to upload to Play Store!")
            println(e.printStackTrace())
        }
    }
}

private fun authenticate(credentialsPath: String): GoogleCredentials {
    return ServiceAccountCredentials
        .fromStream(FileInputStream(credentialsPath))
        .createScoped(AndroidPublisherScopes.all())
}

private fun makePublisher(credentials: GoogleCredentials): AndroidPublisher {
    return AndroidPublisher.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JacksonFactory.getDefaultInstance(),
        setHttpTimeout(HttpCredentialsAdapter(credentials))
    ).setApplicationName("Bussini").build()
}

private fun uploadBundle(appId: String, apkPath: String, publisher: AndroidPublisher): Long {

    val uploadEdit: AppEdit = publisher.edits().insert(appId, null).execute()

    println("Created edit for uploading bundle: ${uploadEdit.id}")

    val bundle: Bundle = publisher.edits().bundles().upload(
        appId,
        uploadEdit.id,
        FileContent("application/octet-stream", File(apkPath))
    ).execute()

    println("Uploaded apk, versionCode: ${bundle.versionCode}")

    publisher.edits().commit(appId, uploadEdit.id).execute()
    println("Committed edit with a new bundle")

    return bundle.versionCode.toLong()
}

private fun updateTrack(
    publisher: AndroidPublisher,
    appId: String,
    newVersionCode: Long,
    publishStatus: String,
    newReleaseNotes: List<LocalizedText>? = null,
) {
    val trackEdit: AppEdit = publisher.edits().insert(appId, null).execute()
    println("Created edit for editing track: ${trackEdit.id}")

    val tracks = publisher.edits().tracks().list(appId, trackEdit.id).execute().tracks

    val wearOSProdTrack = tracks.firstOrNull {
        it.track == WEAR_OS_PROD_TRACK
    } ?: throw Exception("Could not find wear os release track!")

    val prevRelease = wearOSProdTrack.releases.lastOrNull()

    val newRelease = TrackRelease().apply {
        this.versionCodes = listOf(newVersionCode)
        this.releaseNotes = newReleaseNotes ?: prevRelease?.releaseNotes
        this.name = "Release version $newVersionCode"
        this.status = publishStatus
    }

    wearOSProdTrack.releases = listOf(newRelease)

    println("Attempting to make new track release:")
    println("$newRelease")

    publisher.edits().tracks().update(
        appId,
        trackEdit.id,
        WEAR_OS_PROD_TRACK,
        wearOSProdTrack
    ).execute()

    publisher.edits().commit(appId, trackEdit.id).execute()
}

private fun setHttpTimeout(requestInitializer: HttpRequestInitializer) =
    HttpRequestInitializer { request ->
        requestInitializer.initialize(request)
        request.connectTimeout = 3 * 60000
        request.readTimeout = 3 * 60000
    }