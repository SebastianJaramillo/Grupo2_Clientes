package com.banquito.core.banking.clientes.service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.core.banking.clientes.dao.ClienteRepository;
import com.banquito.core.banking.clientes.dao.TipoRelacionRepository;
import com.banquito.core.banking.clientes.domain.Cliente;
import com.banquito.core.banking.clientes.domain.Cliente.TipoCliente;
import com.banquito.core.banking.clientes.domain.Cliente.TipoIdentificacion;
import com.banquito.core.banking.clientes.utils.CreacionException;
import com.banquito.core.banking.clientes.utils.EncontrarClienteException;
import com.banquito.core.banking.clientes.utils.ValidacionException;
import com.banquito.core.banking.clientes.domain.TipoRelacion;

@Service
public class ClienteService {

    public static final String TIPO_CLIENTE_PERSONA = "NAT";
    private final ClienteRepository clienteRepository;
    private final TipoRelacionRepository tipoRelacionRepository;

    public ClienteService(ClienteRepository clienteRepository, TipoRelacionRepository tipoRelacionRepository) {
        this.clienteRepository = clienteRepository;
        this.tipoRelacionRepository = tipoRelacionRepository;
    }

    public Iterable<Cliente> listAll() {
        return this.clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return this.clienteRepository.findById(id);
    }

    @Transactional
    public Cliente crearPersona(Cliente cliente) {
        try {
            if (TipoCliente.NAT.name().equals(cliente.getTipoCliente().name())) {
                if (!TipoIdentificacion.RUC.name().equals(cliente.getTipoIdentificacion().name())) {
                    if (this.validarCedula(cliente.getNumeroIdentificacion())) {
                        Calendar fechaActual = Calendar.getInstance();
                        Calendar fechaNacimiento = Calendar.getInstance();
                        fechaNacimiento.setTime(cliente.getFechaNacimiento());

                        if (fechaNacimiento.before(fechaActual)) {
                            Optional<Cliente> optionalCliente = this.clienteRepository.findByTipoIdentificacionAndNumeroIdentificacion(cliente.getTipoIdentificacion(), cliente.getNumeroIdentificacion());
                            if (!optionalCliente.isPresent()) {
                                return this.clienteRepository.save(cliente);
                            } else {
                                throw new EncontrarClienteException("Persona con " + cliente.getTipoIdentificacion().name() + ": " + cliente.getNumeroIdentificacion() + " ya existe");
                            }
                        } else {
                            throw new ValidacionException("la fecha de nacimiento");
                        }
                    } else {
                        throw new ValidacionException("el numero de identificacion");                        
                    }
                } else {
                    throw new RuntimeException("El tipo de identificacion es incorrecto para persona");
                }
            } else {
                throw new RuntimeException("El tipo de cliente es invalido");
            }
        } catch (Exception e) {
            throw new CreacionException("Error en creacion de Cliente tipo persona: " + cliente + " el error es: " + e);
        }
    }

    public List<Cliente> obtenerPersonaPorApellidos(String apellidos) {
        return this.clienteRepository.findByTipoClienteAndApellidosLikeOrderByApellidos(TipoCliente.NAT.name(),
                apellidos);
    }

    public TipoRelacion crearTipoRelacion(TipoRelacion tipoRelacion) {
        try {
            return this.tipoRelacionRepository.save(tipoRelacion);
        } catch (Exception e) {
            throw new CreacionException(
                    "Ocurrio un error al crear el TipoRelacion: " + tipoRelacion + " error: " + e.getMessage(), e);
        }
    }

    public Iterable<TipoRelacion> listAllTipoRelacion() {
        return this.tipoRelacionRepository.findAll();
    }

    private Boolean validarCedula(String cedula) {
        Integer total = 0;
        Integer[] coeficientes = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };

        if (cedula.matches("[0-9]*") && cedula.length() == 10) {
            Integer provincia = Integer.parseInt(cedula.charAt(0) + "" + cedula.charAt(1));
            Integer digitoTres = Integer.parseInt(cedula.charAt(2) + "");

            if ((provincia > 0 && provincia <= 24) && digitoTres < 6) {
                Integer digitoUltimo = Integer.parseInt(cedula.charAt(9) + "");

                for (Integer i = 0; i < coeficientes.length; i++) {
                    Integer valor = Integer.parseInt(coeficientes[i] + "") * Integer.parseInt(cedula.charAt(i) + "");
                    total = valor >= 10 ? total + (valor - 9) : total + valor;
                }

                Integer digitoVerificador = total >= 10 ? (total % 10) != 0 ? 10 - (total % 10) : (total % 10) : total;
                if (digitoVerificador == digitoUltimo) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
