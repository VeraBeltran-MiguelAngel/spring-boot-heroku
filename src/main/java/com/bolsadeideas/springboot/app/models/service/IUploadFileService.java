package com.bolsadeideas.springboot.app.models.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {

	//para cargar la imagen(puede devolver una url mal formada)
	public Resource load(String filename) throws MalformedURLException;
	// retorna el nombre unico de la imagen, la copia y renombra en el nuevo directorio
	public String copy (MultipartFile file) throws IOException;
	//
	public boolean delete (String filename);
	// borrar todo el directorio de forma recursiva
	public void deleteAll();
	// Crear nuevamente el directorio uploads
	public void init() throws IOException;
}
