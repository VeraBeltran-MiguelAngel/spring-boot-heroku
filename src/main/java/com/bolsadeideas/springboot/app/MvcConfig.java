package com.bolsadeideas.springboot.app;

import java.util.Locale;

import org.springframework.context.annotation.Bean;

//import java.nio.file.Paths;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
//import org.springframework.web.servlet.i18n.SessionLocaleResolver;

//para ver  las imagenes en la vista
@Configuration
public class MvcConfig implements WebMvcConfigurer {

	// debug del resource path
	// private final Logger log= LoggerFactory.getLogger(getClass());
	/*
	 * @Override
	 * public void addResourceHandlers(ResourceHandlerRegistry registry) {
	 * // TODO Auto-generated method stub
	 * WebMvcConfigurer.super.addResourceHandlers(registry);
	 * 
	 * //toUri toma el path y agrega el esquema "file:/" y lo convierte a string
	 * String resourcePath =
	 * Paths.get("uploads").toAbsolutePath().toUri().toString();
	 * log.info(resourcePath); // pra visulaizar la ruta en consola
	 * //registrar la nueva ruta con recursos estaticos
	 * //agregamos doble * para mapear al nombre de la img y que esta se pueda
	 * cargar en ver.html,
	 * //corresponden a un nombre variable de la img
	 * // solo es la url que apunta al dir fisico
	 * registry.addResourceHandler("/uploads/**")
	 * .addResourceLocations(resourcePath);//contiene el uploads con la ruta
	 * absoluta con esquema file:/
	 * }
	 */
	/**
	 * este metodo debe llamarse asi addViewControllers y solo es para mostrar una
	 * vista
	 */
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/error_403").setViewName("error_403");
		;
	}

	/**
	 * metodo que se inyecta en springsecurityconfig
	 * 
	 * @return
	 */
	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Metodo para poner nuestro proyecto en español españa por default
	 * aqui se guarda el parametro de nuestro lenguaje en la session
	 * 
	 * @return
	 */
	@Bean
	public LocaleResolver localeResolver() {

		// se guarda en la session http cada que modifiquemos un nuevo locale
		// SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		// si deseas conservar el lenguaje actual antes de cerrar sesion usas:
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("es", "ES"));
		return localeResolver;

	}

	/**
	 * crear el interceptor para cambiar el locale cada que se envie el parametro
	 * del lenguaje con el nuevo idioma
	 * interceptor que se encarga de modificar el lenguaje cada vez que pasemos el
	 * parametro lang por url
	 * 
	 * @return
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
		localeInterceptor.setParamName("lang");
		return localeInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		registry.addInterceptor(localeChangeInterceptor());
	}

	/** convertir un objeto a XML el proceso se llama "Marshalling" */
	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// es la clase root que contiene los elementos para nuestro XML 
		marshaller.setClassesToBeBound(new Class[] { com.bolsadeideas.springboot.app.view.xml.ClienteList.class
		});
		return marshaller;

	}

}
