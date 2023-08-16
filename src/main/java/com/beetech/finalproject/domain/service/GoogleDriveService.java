package com.beetech.finalproject.domain.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleDriveService {
    @Value("${drive.application.name}")
    private String APPLICATION_NAME;
    @Value("${drive.credentials.file}")
    private String CREDENTIALS_FILE_PATH;
    @Value("${drive.token.directory}")
    private java.io.File TOKENS_DIRECTORY_PATH;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Get drive service
     *
     * @return - drive after built
     * @throws IOException - error
     * @throws GeneralSecurityException - error
     */
    private Drive getDriveService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, Collections.singletonList(DriveScopes.DRIVE))
                .setDataStoreFactory(new FileDataStoreFactory(TOKENS_DIRECTORY_PATH))
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost("localhost").setPort(2223).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("atifarlunar.official@gmail.com");

        return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Load client secret key from credential json file
     *
     * @return - secret key
     * @throws IOException - error
     */
    private GoogleClientSecrets loadClientSecrets() throws IOException {
        InputStream in = GoogleDriveService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    /**
     * Upload file to drive
     *
     * @param imageFile - input file(image, file)
     * @return -  file after upload
     * @throws IOException - error
     * @throws GeneralSecurityException -error
     */
    public File uploadToDrive(MultipartFile imageFile, String folderId) throws IOException, GeneralSecurityException {
        Drive driveService = getDriveService();

        // name of file
        String fileName = imageFile.getOriginalFilename();
        // extension of file
        String mimeType = imageFile.getContentType();

        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setMimeType(mimeType);
        fileMetadata.setParents(Collections.singletonList(folderId));

        InputStreamContent mediaContent = new InputStreamContent(mimeType, imageFile.getInputStream());

        Drive.Files.Create createRequest = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, webContentLink");
        MediaHttpUploader uploader = createRequest.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(false); // Try setting it to false if this doesn't work

        return createRequest.execute();
    }

    /**
     * Get drive's id from drive
     *
     * @param imageFile - input file(image, file)
     * @return - drive's id
     * @throws IOException - error
     * @throws GeneralSecurityException - error
     */
    public String uploadImageAndGetId(MultipartFile imageFile, String folderId) throws IOException, GeneralSecurityException {
        return uploadToDrive(imageFile, folderId).getId();
    }

    /**
     * Get link download image from drive
     *
     * @param fileId - input file(image, file)
     * @return - link download
     * @throws IOException - error
     * @throws GeneralSecurityException - error
     */
    public File downloadFromDrive(String fileId) throws IOException, GeneralSecurityException {
        Drive driveService = getDriveService();
        return driveService.files().get(fileId).execute();
    }

    /**
     * download file content from drive
     *
     * @param fileId - input file(image, file)
     * @return - link download
     * @throws IOException - error
     * @throws GeneralSecurityException - error
     */
    public byte[] downloadFileContent(String fileId) throws IOException, GeneralSecurityException {
        Drive driveService = getDriveService();

        // Get the file content using the file ID
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Delete file in drive
     *
     * @param fileId - input drive's id
     * @throws IOException - error
     * @throws GeneralSecurityException - error
     */
    public void deleteImageFromDrive(String fileId) throws IOException, GeneralSecurityException {
        Drive driveService = getDriveService();

        try {
            driveService.files().delete(fileId).execute();
            System.out.println("File deleted successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while deleting the file: " + e.getMessage());
        }
    }
}
