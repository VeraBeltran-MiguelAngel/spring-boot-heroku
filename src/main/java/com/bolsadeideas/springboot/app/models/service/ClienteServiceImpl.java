package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.dao.IClienteDao;
import com.bolsadeideas.springboot.app.models.dao.IFacturaDao;
import com.bolsadeideas.springboot.app.models.dao.IProductoDao;
import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.Producto;

@Service // se puede interactuar con diferentes DAO
public class ClienteServiceImpl implements IClienteService {

	@Autowired
	private IClienteDao clienteDao;

	@Autowired
	private IProductoDao productoDao;

	@Autowired
	private IFacturaDao facturaDao;

	/**
	 * *Metodo para listar todos los clientes
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Cliente> findAll() {
		return (List<Cliente>) clienteDao.findAll();// agregamos un cast a lista
		// por que el metodo original devuelve un iterable
	}

	/**
	 * Metodo para guardar un cliente
	 */
	@Override
	@Transactional // por que es de escritura al insertar un nuevo registro
	public void save(Cliente cliente) {
		clienteDao.save(cliente);
	}

	/**
	 * *Metodo para buscar un cliente por ID, al trabajar con optional
	 * *necesitamos el orElse
	 */
	@Override
	@Transactional(readOnly = true)
	public Cliente findOne(Long id) {
		return clienteDao.findById(id).orElse(null);
	}

	/**
	 * Metodo optimizado con JOIN 
	 */
	@Override
	@Transactional(readOnly = true)
	public Cliente fecthByIdWithFacturas(Long id) {
		return clienteDao.fecthByIdWithFacturas(id);
	}

	/**
	 * Metodo para eliminar un cliente
	 */
	@Override
	@Transactional
	public void delete(Long id) {
		clienteDao.deleteById(id);
	}

	/**
	 * Metodo para devolver todos los clientes em un paginable en vez de una lista
	 * el paginable es una sublista de un List
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Cliente> findAll(Pageable pageable) {
		return clienteDao.findAll(pageable);
	}

	/**
	 * Metodo para devolver la lista de productos
	 */
	@Transactional(readOnly = true)
	public List<Producto> findByNombre(String term) {
		return productoDao.findByNombre(term);

	}

	/**
	 * Meotodo para guardar una factura
	 */
	@Override
	@Transactional
	public void saveFactura(Factura factura) {
		facturaDao.save(factura);
	}

	/**
	 * Metodo para encontrar un producto por ID
	 */
	@Override
	@Transactional(readOnly = true)
	public Producto findProductoById(Long id) {
		return productoDao.findById(id).orElse(null);
	}

	/**
	 * Metodo para encontrar un factura por su id
	 */
	@Override
	@Transactional(readOnly = true)
	public Factura findFacturaById(Long id) {
		return facturaDao.findById(id).orElse(null);
	}

	/**
	 * Metodo para eliminar factura
	 */
	@Override
	@Transactional
	public void deleteFactura(Long id) {
		facturaDao.deleteById(id);
	}

	/**
	 * Meotodo que llama a la cosulta personalizada
	 */
	@Override
	@Transactional
	public Factura fetchFacturaByIdWithClienteWithItemFacturaWithProducto(Long id) {
		// consulta personalizada en IFacturaDao
		return facturaDao.fetchByIdWithClienteWithItemFacturaWithProducto(id);
	}


}
