package fi.danielz.publisher;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.AppEdit;
import com.google.api.services.androidpublisher.model.Bundle;
import com.google.api.services.androidpublisher.model.LocalizedText;
import com.google.api.services.androidpublisher.model.Track;
import com.google.api.services.androidpublisher.model.TrackRelease;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class JavaPublisher extends DefaultTask {

    @TaskAction
    public void publishProdRelease() {
        System.out.println("Starting Java publisher");
        try {
            String rootPath = ".";
            String appId = "fi.danielz.bussini";
            String credentialsPath = String.format("%s/publishKeyBussini.json", rootPath);
            String bundlePath = String.format("%s/app/build/outputs/bundle/release/app-release.aab", rootPath);
            System.out.printf("Running with params\nappId: %1$s\ncredPath: %2$s\nbundlePath: %3$s", appId, credentialsPath, bundlePath);

            GoogleCredentials credentials = authenticate(
                    credentialsPath
            );
            System.out.println("Authentication successful");
            AndroidPublisher androidPublisher = makePublisher(
                    credentials
            );
            System.out.println("Making publisher successful");

            long bundleVersionNumber = uploadBundle(
                    appId,
                    bundlePath,
                    androidPublisher
            );
            System.out.println("Bundle upload successful");

            updateTrack(androidPublisher, appId, bundleVersionNumber, null);
            System.out.println("Track update successful, exiting");

        } catch (Exception e) {
            System.out.print("Error while running publisher");
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
    }

    private GoogleCredentials authenticate(String credentialsPath) throws IOException {
        return ServiceAccountCredentials
                .fromStream(Files.newInputStream(Paths.get(credentialsPath)))
                .createScoped(AndroidPublisherScopes.all());
    }

    private AndroidPublisher makePublisher(GoogleCredentials credentials) throws GeneralSecurityException, IOException {
        return new AndroidPublisher.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                setHttpTimeout(new HttpCredentialsAdapter(credentials))
        ).setApplicationName("Bussini").build();
    }

    private long uploadBundle(String appId, String bundlePath, AndroidPublisher publisher) throws IOException {
        final AppEdit uploadEdit = publisher.edits().insert(
                appId,
                null
        ).execute();
        System.out.println("Created edit for uploading Bundle");

        final Bundle bundle = publisher.edits().bundles().upload(
                appId,
                uploadEdit.getId(),
                new FileContent("application/octet-stream", new File(bundlePath))
        ).execute();

        System.out.printf("Uploaded bundle, versionCode %d%n", bundle.getVersionCode());

        publisher.edits().commit(appId, uploadEdit.getId()).execute();

        System.out.println("Bundle succesfully uploaded");

        return bundle.getVersionCode().longValue();
    }

    private void updateTrack(
            AndroidPublisher publisher,
            String appId,
            long newVersionCode,
            List<LocalizedText> newReleaseNotes
    ) throws Exception {
        final AppEdit trackEdit = publisher.edits().insert(appId, null).execute();
        System.out.println("Starting edit for track");

        final List<Track> tracks = publisher.edits().tracks().list(appId, trackEdit.getId()).execute().getTracks();

        Track wearOsProdTrack = null;
        String WEAR_OS_PROD_TRACK = "wear:production";
        for (Track track : tracks) {
            if (Objects.equals(track.getTrack(), WEAR_OS_PROD_TRACK)) {
                wearOsProdTrack = track;
                break;
            }
        }
        if (wearOsProdTrack == null) {
            throw new Exception("Could not find wear os release track!");
        }
        TrackRelease prevRelease = wearOsProdTrack.getReleases().get(wearOsProdTrack.getReleases().size() - 1);

        TrackRelease newRelease = new TrackRelease();
        newRelease.setVersionCodes(new ArrayList<Long>(1) {{
            add(newVersionCode);
        }});
        newRelease.setReleaseNotes(
                newReleaseNotes == null ? prevRelease.getReleaseNotes() : newReleaseNotes
        );
        newRelease.setName(String.format("Release version %s", newVersionCode));
        newRelease.setStatus("draft");

        wearOsProdTrack.setReleases(new ArrayList<TrackRelease>(1) {{
            add(newRelease);
        }});

        System.out.println("Attempting a new track release:");
        System.out.println(newRelease);

        publisher.edits().tracks().update(
                appId,
                trackEdit.getId(),
                WEAR_OS_PROD_TRACK,
                wearOsProdTrack
        ).execute();
        publisher.edits().commit(appId, trackEdit.getId()).execute();
    }

    private HttpRequestInitializer setHttpTimeout(HttpRequestInitializer initializer) {
        return request -> {
            initializer.initialize(request);
            request.setConnectTimeout(3 * 60000);
            request.setReadTimeout(3 * 60000);
        };
    }

}