package nano.server.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

import org.springframework.web.multipart.MultipartFile;

import nano.server.enums.EntityType;
import nano.server.enums.FileType;
import nano.server.enums.FolderType;

public class FileUtils {
	private final static String ROOT_FOLDER = "apps";

	// Was resolved via JNDI (java:comp/env, bound from ROOT.xml's <Environment> entries), but that
	// lookup races Tomcat's own context startup — losing it leaves ServerProperties (which calls
	// this for FolderType.CONFIG) permanently unable to load app.properties for the process's whole
	// lifetime, silently nulling every DB/WSSE/email/API credential. Hardcoded to match ROOT.xml's
	// fixed values (this deployment only ever runs with these) rather than fixing the race.
	private final static String BASE_PATH = "/etc";
	private final static String APP_NAME = "nano_server";

	public static String getContent(String path, Charset encoding) throws Exception {
		byte[] encoded = Files.readAllBytes(Paths.get(path));

		return new String(encoded, encoding);
	}

	public static String getBasePath(FolderType folderType) throws Exception {
		String basePath = BASE_PATH;
		String appName = APP_NAME;

		String path = String.format(Locale.getDefault(), "%s%s%s%s%s%s%s",
				basePath, File.separator, ROOT_FOLDER, File.separator,
				folderType.getValue(), File.separator, appName);
		
		File file = new File(path);
		
		if (file.exists() && file.isDirectory()) {
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}
	
	public static String getPath(FolderType folderType, FileType fileType,
			EntityType entityType) throws Exception {
		String basePath = getBasePath(folderType);
		
		if (basePath != null) {
			String path = String.format(Locale.getDefault(), "%s/%s/%s",
					basePath, fileType.getValue(), entityType.getValue());
			
			File file = new File(path);
			
			if (!file.exists() || !file.isDirectory()) {
				if (!file.mkdirs()) {
					return null;
				}
			}
			
			return file.getAbsolutePath();
		}
		
		return null;
	}
	
	public static String getFilePath(FileType fileType, EntityType entityType, String filename) throws Exception {
		return getFilePath(fileType, entityType, filename, true);
	}
	
	public static String getFilePath(FileType fileType, EntityType entityType,
			String filename, boolean checkIfExists) throws Exception {
		String path = getPath(FolderType.FILES, fileType, entityType);
		
		if (path != null) {
			String filePath = String.format(Locale.getDefault(), "%s/%s", path, filename);
			
			File file = new File(filePath);
			
			if (checkIfExists) {
				if (file.exists()) {
					return file.getAbsolutePath();
				}
			} else {
				return file.getAbsolutePath();
			}
		}
		
		return null;
	}
	
	public static String saveImage(EntityType entityType, String name, String base64Content) throws Exception {
		if (name != null && !name.isEmpty()) {
			String filename = String.format(Locale.getDefault(), "%s.png", name.replace(" ", ""));

			return saveFile(FileType.IMAGES, entityType, filename, base64Content);
		}
		
		return null;
	}
	
	public static String saveImage(EntityType entityType, String name, MultipartFile file) throws Exception {
		if (name != null && !name.isEmpty() && file != null) {
			String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
			String filename = String.format(Locale.getDefault(), "%s.%s", name.replace(" ", ""), extension);

			return saveFile(FileType.IMAGES, entityType, filename, file.getBytes());
		}
		
		return null;
	}
	
	public static String saveFile(FileType fileType, EntityType entityType,
			String filename, String base64Content) throws Exception {
		if (base64Content != null && !base64Content.isEmpty()) {
			if (base64Content.startsWith("data") && base64Content.contains(",")) {
				base64Content = base64Content.split(",")[1];
			}
			
			byte[] content = Base64.getDecoder().decode(base64Content.getBytes(StandardCharsets.UTF_8));
			
			return saveFile(fileType, entityType, filename, content);
		}
		
		return null;
	}
	
	public static String saveFile(FileType fileType, EntityType entityType,
			String filename, byte[] content) throws Exception {
		String uniqueFilename = String.format(Locale.getDefault(),
				"%d_%s", new Date().getTime(), filename);
		
		String filePath = getFilePath(fileType, entityType, uniqueFilename, false);
		
		if (filePath != null) {
			File file = new File(filePath);
			
			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(content);
			outputStream.close();
			
			return uniqueFilename;
		}
		
		return null;
	}
	
	public static byte[] loadFile(FileType fileType, EntityType entityType, String filename) throws Exception {
		String filePath = getFilePath(fileType, entityType, filename, true);
		
		if (filePath != null) {
			File file = new File(filePath);
			
			return Files.readAllBytes(file.toPath());
		}
		
		return null;
	}
	
	public static void deleteFile(FileType fileType, EntityType entityType, String filename) throws Exception {
		String filePath = getFilePath(fileType, entityType, filename, true);
		
		if (filePath != null) {
			File file = new File(filePath);
			
			Files.delete(file.toPath());
		}
	}
 }
