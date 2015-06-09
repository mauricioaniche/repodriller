package br.com.metricminer2.persistence.google;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.java6.auth.oauth2.GooglePromptReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;

import br.com.metricminer2.persistence.PersistenceMechanism;

/**
 * Main class for the Cloud Storage API command line sample. Demonstrates how to
 * make an authenticated API call using OAuth 2 helper classes.
 */
public class GoogleStorage implements PersistenceMechanism {

	/**
	 * Be sure to specify the name of your application. If the application name
	 * is {@code null} or blank, the application will log a warning. Suggested
	 * format is "MyCompany-ProductName/1.0". If you are running the sample on a
	 * machine where you have access to a browser, set AUTH_LOCAL_WEBSERVER to
	 * true.
	 */
	private static final String APPLICATION_NAME = "MM2-STORAGE";
	private static final String BUCKET_NAME = "mm2-bucket";
	private static final String CLIENT_SECRET_FILENAME = "client_secrets.json";
	private static final boolean AUTH_LOCAL_WEBSERVER = true;

	/** Directory to store user credentials. */
//	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/storage_teste");
//	private static final java.io.File DATA_STORE_DIR = new java.io.File("/home/MM2", "store/google_storage");
	
	
	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to
	 * make it a single globally shared instance across your application.
	 */
	private static FileDataStoreFactory dataStoreFactory;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	private static Storage client;
	
	private static StringBuilder stringBuilderContent = new StringBuilder();
	
	private String fileName;
	private File dataStore;

	public GoogleStorage(File dataStore, String fileName) {
		this.dataStore = dataStore;
		this.fileName = fileName;
	}
	
	/** Authorizes the installed application to access user's protected data. */
	private static Credential authorize() throws Exception {
   // Load client secrets.
   GoogleClientSecrets clientSecrets = null;
   try {
     clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
         new InputStreamReader(GoogleStorage.class.getResourceAsStream(String.format("/%s",CLIENT_SECRET_FILENAME))));
     if (clientSecrets.getDetails().getClientId() == null ||
         clientSecrets.getDetails().getClientSecret() == null) {
         throw new Exception("client_secrets not well formed.");
     }
   } catch (Exception e) {
     System.out.println("Problem loading client_secrets.json file. Make sure it exists, you are " + 
                       "loading it with the right path, and a client ID and client secret are " +
                       "defined in it.\n" + e.getMessage());
     System.exit(1);
   }

   // Set up authorization code flow.
   // Ask for only the permissions you need. Asking for more permissions will
   // reduce the number of users who finish the process for giving you access
   // to their accounts. It will also increase the amount of effort you will
   // have to spend explaining to users what you are doing with their data.
   // Here we are listing all of the available scopes. You should remove scopes
   // that you are not actually using.
   Set<String> scopes = new HashSet<String>();
   scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
   scopes.add(StorageScopes.DEVSTORAGE_READ_ONLY);
   scopes.add(StorageScopes.DEVSTORAGE_READ_WRITE);

   GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
       httpTransport, JSON_FACTORY, clientSecrets, scopes)
       .setDataStoreFactory(dataStoreFactory)
       .build();
   // Authorize.
   VerificationCodeReceiver receiver = 
       AUTH_LOCAL_WEBSERVER ? new LocalServerReceiver() : new GooglePromptReceiver();
   return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");    
 }

	public void insertFile(String fileName, String content) {

		try {
			// Initialize the transport.
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			// Initialize the data store factory.
			dataStoreFactory = new FileDataStoreFactory(dataStore);

			// Authorization.
			Credential credential = authorize();

			// Set up global Storage instance.
			client = new Storage.Builder(httpTransport, JSON_FACTORY,
					credential).setApplicationName(APPLICATION_NAME).build();

			// Get metadata about the specified bucket.
			Storage.Buckets.Get getBucket = client.buckets().get(BUCKET_NAME);
			getBucket.setProjection("full");
			Bucket bucket = getBucket.execute();
			System.out.println("name: " + BUCKET_NAME);
			System.out.println("location: " + bucket.getLocation());
			System.out.println("timeCreated: " + bucket.getTimeCreated());
			System.out.println("owner: " + bucket.getOwner());

			InputStream inputStream = new ByteArrayInputStream(
					content.getBytes(StandardCharsets.UTF_8));
			long byteCount = content.length(); // size of input stream

			InputStreamContent mediaContent = new InputStreamContent(
					"application/octet-stream", inputStream);
			// Knowing the stream length allows server-side optimization, and
			// client-side progress
			// reporting with a MediaHttpUploaderProgressListener.
			mediaContent.setLength(byteCount);

			StorageObject objectMetadata = null;

			Storage.Objects.Insert insertObject;
			insertObject = client.objects().insert(BUCKET_NAME, objectMetadata,
					mediaContent);
			// If you don't provide metadata, you will have specify the object
			// name by parameter. You will probably also want to ensure that
			// your
			// default object ACLs (a bucket property) are set appropriately:
			// https://developers.google.com/storage/docs/json_api/v1/buckets#defaultObjectAcl
			insertObject.setName(fileName);

			// For small files, you may wish to call
			// setDirectUploadEnabled(true), to
			// reduce the number of HTTP requests made to the server.
			if (mediaContent.getLength() > 0
					&& mediaContent.getLength() <= 2 * 1000 * 1000 /* 2MB */) {
				insertObject.getMediaHttpUploader()
						.setDirectUploadEnabled(true);
			}

			insertObject.execute();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(Object... line) {
		boolean first = true;
		for(Object o : line) {
			if(!first) {
				stringBuilderContent.append(",");
			}
			stringBuilderContent.append(o);
			first=false;
		}
		stringBuilderContent.append("\n");
	}

	public void close() {
		Format formatter = new SimpleDateFormat("ssmmHH-ddMMyyyy");
		String stringNow = formatter.format(Calendar.getInstance().getTime());
		insertFile(stringNow + fileName, stringBuilderContent.toString());
	}
}