package net.majorkernelpanic.spydroid.ui;

import java.io.File;

public class UploadManager {

	private static UploadManager instance;

	public static UploadManager getInstance() {
		if (instance == null) {
			instance = new UploadManager();
		}
		return instance;
	}

	public void upload(File file) {
		if(file != null && file.exists()){
			
		}
	}

}
