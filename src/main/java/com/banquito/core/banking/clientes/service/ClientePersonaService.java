package com.banquito.core.banking.clientes.service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.core.banking.clientes.dao.ClienteRepository;
import com.banquito.core.banking.clientes.domain.Cliente;
import com.banquito.core.banking.clientes.domain.Estado;
import com.banquito.core.banking.clientes.domain.TipoCliente;
import com.banquito.core.banking.clientes.domain.TipoIdentificacion;
import com.banquito.core.banking.clientes.utils.TransaccionException;
import com.banquito.core.banking.clientes.utils.EncontrarException;
import com.banquito.core.banking.clientes.utils.ValidacionException;

@Service
public class ClientePersonaService {

    private final ClienteRepository clienteRepository;

    public ClientePersonaService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listAll() {
        return this.clienteRepository.findByEstadoOrderByCodigo(Estado.ACT);
    }

    public Optional<Cliente> findById(Long id) {
        return this.clienteRepository.findById(id);
    }

    public Optional<Cliente> findByIdentificacion(TipoIdentificacion tipoIdentificacion, String numeroIdentificacion) {
        return this.clienteRepository.findByTipoIdentificacionAndNumeroIdentificacion(tipoIdentificacion,
                numeroIdentificacion);
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
                            Optional<Cliente> optionalCliente = this.clienteRepository
                                    .findByTipoIdentificacionAndNumeroIdentificacion(cliente.getTipoIdentificacion(),
                                            cliente.getNumeroIdentificacion());
                            if (!optionalCliente.isPresent()) {
                                cliente.setFechaCreacion(fechaActual.getTime());
                                cliente.setEstado(Estado.ACT);
                                return this.clienteRepository.save(cliente);
                            } else {
                                throw new EncontrarException(
                                        "Persona con " + cliente.getTipoIdentificacion().name() + ": "
                                                + cliente.getNumeroIdentificacion() + " ya existe");
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
                throw new RuntimeException("El tipo de cliente es invalido para persona");
            }
        } catch (Exception e) {
            throw new TransaccionException(
                    "Error en creacion de Cliente tipo persona: " + cliente + ", el error es: " + e);
        }
    }

    @Transactional
    public Cliente actualizarPersona(Cliente cliente) {
        try {
            Optional<Cliente> optionalCliente = clienteRepository.findById(cliente.getCodigo());
            if (optionalCliente.isPresent()) {
                if (TipoCliente.NAT.name().equals(cliente.getTipoCliente().name())) {
                    if (!TipoIdentificacion.RUC.name().equals(cliente.getTipoIdentificacion().name())) {
                        if (this.validarCedula(cliente.getNumeroIdentificacion())) {
                            Calendar fechaActual = Calendar.getInstance();
                            Calendar fechaNacimiento = Calendar.getInstance();
                            fechaNacimiento.setTime(cliente.getFechaNacimiento());

                            if (fechaNacimiento.before(fechaActual)) {
                                cliente.setFechaUltimoCambio(fechaActual.getTime());
                                return this.clienteRepository.save(cliente);
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
                    throw new RuntimeException("El tipo de cliente es invalido para persona");
                }
            } else {
                throw new EncontrarException("No se pudo encontrar cliente");
            }
        } catch (Exception e) {
            throw new TransaccionException(
                    "Error en actualizacion de Cliente tipo persona: " + cliente + ", el error es: " + e);
        }
    }

    @Transactional
    public Cliente eliminarPersona(Long id) {
        try {
            Optional<Cliente> optionalCliente = clienteRepository.findById(id);
            if (optionalCliente.isPresent()) {
                Calendar fechaActual = Calendar.getInstance();
                Cliente cliente = optionalCliente.get();
                cliente.setFechaUltimoCambio(fechaActual.getTime());
                cliente.setEstado(Estado.INA);
                return this.clienteRepository.save(cliente);
            } else {
                throw new EncontrarException("No se pudo encontrar cliente");
            }
        } catch (Exception e) {
            throw new TransaccionException("Error en eliminacion de Cliente tipo persona, el error es: " + e);
        }
    }

    public List<Cliente> findByTipoCliente() {
        return this.clienteRepository.findByTipoClienteOrderByApellidos(TipoCliente.NAT);
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
                    Integer valor = coeficientes[i] * Integer.parseInt(cedula.charAt(i) + "");
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
