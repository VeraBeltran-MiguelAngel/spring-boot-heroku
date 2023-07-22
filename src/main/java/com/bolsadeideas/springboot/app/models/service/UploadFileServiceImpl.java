package com.bolsadeideas.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService {

	// mostar en la consola y hacer un debug de los nombres del dir
	private final Logger log = LoggerFactory.getLogger(getClass());
	// folder "uploads" (raiz del proyecto)
	private final static String UPLOADS_FOLDER = "uploads";

	/**
	 * Metodo para cargar el archivo img
	 * 
	 * @param filename nombre del archivo
	 * @return mostramos el recurso
	 */
	@Override
	public Resource load(String filename) throws MalformedURLException {
		// dir raiz "uploads" con ruta absoluta C:
		Path pathFoto = getPath(filename);
		// ver la dir en consola
		log.info("pathFoto: " + pathFoto);
		Resource recurso = null;
		recurso = new UrlResource(pathFoto.toUri());// (agregar el file:) aqui se carga la imagen
		// si no existe y no es leible
		if (!recurso.exists() || !recurso.isReadable()) {
			throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
		}

		return recurso;
	}

	/**
	 * Mettodo para copiar un archivo
	 * 
	 * @param file archivo a copiar
	 * @return nombre unico del archivo
	 */
	@Override
	public String copy(MultipartFile file) throws IOException {
		// para evitar que las img con mismo nombre se reemplazen
		// Universaly unic identifier
		String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		// imagenes guardadas fuera del proyecto
		Path rootPath = getPath(uniqueFilename);
		log.info("rootPath: " + rootPath); // path relativo al proyecto (se muestra en consola)
		// copiar la img a subir a uploads
		Files.copy(file.getInputStream(), rootPath);

		return uniqueFilename;
	}

	/**
	 * Metodo para eliminar el archivo 
	 * 
	 * @param filename nombre del archivo
	 */
	@Override
	public boolean delete(String filename) {
		//obtener la ruta
		Path rootPath = getPath(filename);
		//convertirlo en archivo
		File archivo = rootPath.toFile();
		
		if (archivo.exists() && archivo.canRead()) {
			//si se puede eliminar
			if(archivo.delete()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo para obtener la ruta absoluta del archivo
	 * 
	 * @param filename nombre del archivo
	 * @return ruta absoluta del archivo
	 */
	public Path getPath(String filename) {
		return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
	}

	/**
	 * Metodo para que se elimine la carpeta "uploads" 
	 * con todo su contenido
	 */
	@Override
	public void deleteAll() {
		
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
	}

	/**
	 * Metodo para crear el dir."uploads" al iniciar el proyecto
	 */
	@Override
	public void init() throws IOException {
		Files.createDirectory(Paths.get(UPLOADS_FOLDER));
		
	}

}
