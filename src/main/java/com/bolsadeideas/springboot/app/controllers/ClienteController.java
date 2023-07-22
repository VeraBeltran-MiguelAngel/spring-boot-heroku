package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;
import com.bolsadeideas.springboot.app.view.xml.ClienteList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
// indicamos que se va a guardar el objeto cliente como atributo de la sesion y
// evitar perdida de datos entre la navegacion
@SessionAttributes("cliente")
public class ClienteController {
	// para tener un log de inicios de sesion y verlos en consola
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired // siempre se inyecta la interfaz para hacerlo generico
	private IClienteService clienteService;
	@Autowired
	private IUploadFileService uploadFileService;
	@Autowired // a traves de este objeto obtenemos el idioma
	private MessageSource messageSource;

	/**
	 * Mostrar la imagen por Http para que spring no trunque o borre la extension
	 * del archivo usa la expresion regular ":.+"
	 * 
	 * @param filename nombre del archivo
	 * @return en la respuesta regresamos la imagen
	 */

	@SuppressWarnings("null")
	@Secured("ROLE_USER")
	@GetMapping(value = "/uploads/{filename:.+}")
	// de lo contario solo pasaria el nombre "imagen" y no "imagen.jpg"
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return // juntar la imagen y la respuesta
		ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso); // se anexa el recurso al body

	}

	/**
	 * Metodo para ver el detalle a traves del id
	 * 
	 * @param id    Id del cliente
	 * @param model se usa para enviar datos a la vista ("ID","Data")
	 * @param flash para enviar mensajes de confirmacion
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_USER')") // 2° forma usando el metodo hasRole(propio de spring security)
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash,
			Locale locale) {
		// obtener el cliente por id
		// Cliente cliente = clienteService.findOne(id);
		// consulta optimizada en vez de dos select tenemos un inner join
		// cliente-factura
		Cliente cliente = clienteService.fecthByIdWithFacturas(id);

		if (cliente == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", messageSource.getMessage("text.cliente.detalle.titulo", null, locale).concat(": ")
				.concat(cliente.getNombre()));
		return "ver";
	}

	/**
	 * Metodo handler para listar una vista REST
	 * 
	 * @return en formato JSON
	 */
	@GetMapping(value = "/listar-rest")
	/**
	 * el listado de clientes se va a almacenar en el cuerpo de la respuesta y al
	 * guardarse de forma automatica spring  va a deducir que es un REST (JSON o XML)
	 
	public @ResponseBody List<Cliente> listarRest() { // de esta manera solo devolvemos JSON
		return clienteService.findAll();
	} */

	/**si deseas que regrese XML y JSON  http://localhost:8080/listar-rest?format=xml/json*/
	public @ResponseBody ClienteList listarRest() { // de esta manera devolvemos XML y JSON
		return new ClienteList(clienteService.findAll());
	}

	/**
	 * Muestra los registros de clientes de manera paginada
	 * 
	 * @param page  pagina por default empieza en 0 y se envia a pageRender
	 * @param model se usa para enviar datos a la vista ("ID","Data")
	 * @return devuelve el listar.html
	 */
	@RequestMapping(value = { "/listar", "/" }, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication, HttpServletRequest request, Locale locale) {

		if (authentication != null) {
			logger.info("Hola usuario autenticado, tu username es:".concat(authentication.getName()));
		}
		// para validar autorizacion en el logger (consola)
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			logger.info(
					"Utilizando forma estatica SecurityContextHolder.getContext().getAuthentication(), Usuario:"
							.concat(auth.getName()));
		}
		// 1 ° forma de checar rol
		if (hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}
		// segundo froma de checar autorizacion de rol
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request,
				"");

		if (securityContext.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName())
					.concat(" tienes acceso!"));
		} else {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName())
					.concat(" NO tienes acceso!"));
		}
		// 3° forma tambien se puede hacer usadon solo el request del HTTP

		if (request.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName())
					.concat(" tienes acceso!"));
		} else {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName())
					.concat(" NO tienes acceso!"));
		}

		// mostrar 4 registros por página
		Pageable pageRequest = PageRequest.of(page, 4);// se envia a pageRender
		// lista paginada
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		// url, page
		PageRender<Cliente> pageRender = new PageRender<Cliente>("/listar", clientes);

		model.addAttribute("titulo",
				messageSource.getMessage("text.cliente.listar.titulo", null, locale));
		// aqui ya viene el lisstado con paginacion
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";
	}

	/**
	 * Muestra el formulario para crear el cliente
	 * 
	 * @param locale hace referencia a una ruta anterior
	 * @param model  se usa para enviar datos a la vista ("ID","Data")
	 * @return devuelve el form.html
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model, Locale locale) {
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", messageSource.getMessage("text.cliente.form.titulo.crear", null, locale));
		return "form";
	}

	/**
	 * Muestra el formulario con el mensaje "Editar cliente" y diferenciarlo de
	 * "Crear"
	 * 
	 * @param id    Id del cliente a editar
	 * @param model se usa para enviar datos a la vista ("ID","Data")
	 * @param flash para enviar mensajes de confirmacion
	 * @return te muetra el form.html
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash,
			Locale locale) {
		Cliente cliente = null;
		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.id.error", null, locale));
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", messageSource.getMessage("text.cliente.form.titulo.editar", null, locale));
		return "form";
	}

	/**
	 * Guardar la informacion del cliente a crear o editar
	 * 
	 * @param cliente entidad cliente con sus atributos y validar algun error
	 * @param result  debe ir junto al parametro a validar
	 * @param model   se usa para enviar datos a la vista ("ID","Data")
	 * @param foto    parametro que hace referencia al input "fyle" del form.html
	 * @param flash   para enviar mensajes de confirmacion
	 * @param status  para validar los atributos creados en una sesion
	 * @param locale  para ver la url anterior
	 * @return nos redirige a listar.html
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status, Locale locale) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", messageSource.getMessage("text.cliente.form.titulo", null, locale));
			return "form";
		}

		if (!foto.isEmpty()) {
			// eliminar la imagen cuando un usuario se esta editando y se reemplaza la img
			// con la nueva
			// validar que sea un usuario valido y que si tenga foto
			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {

				uploadFileService.delete(cliente.getFoto());

			}
			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flash.addFlashAttribute("info",
					messageSource.getMessage("text.cliente.flash.foto.subir.success", null, locale) + "'"
							+ uniqueFilename + "'");
			cliente.setFoto(uniqueFilename);

		}

		// forma sin traducir String mensajeFlash = (cliente.getId() != null) ? "Cliente
		// editado con exito" : "Cliente creado con exito";
		// forma para traducir los mensajes
		String mensajeFlash = (cliente.getId() != null)
				? messageSource.getMessage("text.cliente.flash.editar.success", null, locale)
				: messageSource.getMessage("text.cliente.flash.crear.success", null, locale);
		clienteService.save(cliente);
		status.setComplete(); // aqui elimina el objeto cliente de la sesion
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";
	}

	/**
	 * Elimina el registro del cliente junto con su imagen
	 * 
	 * @param id    Id del cliente a eliminar
	 * @param flash enviar mensajes de confirmacion
	 * @return redirect to listar.html
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {

		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);

			clienteService.delete(id);
			flash.addFlashAttribute("success",
					messageSource.getMessage("text.cliente.flash.eliminar.success", null, locale));

			// si se puede eliminar
			if (uploadFileService.delete(cliente.getFoto())) {
				String mensajeFotoEliminar = String.format(
						messageSource.getMessage("text.cliente.flash.foto.eliminar.success", null, locale),
						cliente.getFoto());
				flash.addFlashAttribute("info", mensajeFotoEliminar);
			}
		}

		return "redirect:/listar";
	}

	/**
	 * Metodo para validar si x usuario tiene x rol
	 * 
	 * @param role
	 * @return
	 */
	public boolean hasRole(String role) {
		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return false;
		}

		Authentication auth = context.getAuthentication();

		if (auth == null) {
			return false;
		}
		// cualquier objeto que implemente la interfaz grantedAuthority
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		// el metodo contains (grantedAuthority) retorna un booleano, si contiene o no
		// el elemento en la coleccion
		return authorities.contains(new SimpleGrantedAuthority(role));
		/*
		 * for (GrantedAuthority authority : authorities) {
		 * if (role.equals(authority.getAuthority())) {
		 * logger.info(
		 * "Hola usuario ".concat(auth.getName())
		 * .concat(" tu role es: ".concat(authority.getAuthority())));
		 * return true;
		 * }
		 * 
		 * }
		 * return false;
		 */

	}

}
