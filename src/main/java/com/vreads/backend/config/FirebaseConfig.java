//package com.vreads.backend.config;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//import javax.annotation.PostConstruct;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.vreads.backend.controller.AuthApiController;
//
//@Configuration
//public class FirebaseConfig {
//
//	private static final Logger logger = LoggerFactory.getLogger(AuthApiController.class);
//
//	@Bean
//	public FirebaseApp firebaseApp() throws IOException {
//		logger.info("Initializing Firebase.");
//
//		FileInputStream serviceAccount = new FileInputStream("src/main/resources/sdkServiceKey.json");
//
//		FirebaseOptions options = new FirebaseOptions.Builder()
//				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
//				.setDatabaseUrl("https://vreads-app-default-rtdb.asia-southeast1.firebasedatabase.app")// 사용할 firebase 프로젝트명
//				.build();
//
//		FirebaseApp app = FirebaseApp.initializeApp(options);
//		logger.info("FirebaseApp initialized" + app.getName());
//		return app;
//	}
//
//	@Bean
//	@DependsOn(value = "createFireBaseApp")
//	public FirebaseAuth getFirebaseAuth(){
//		return FirebaseAuth.getInstance();
//	}
//}
