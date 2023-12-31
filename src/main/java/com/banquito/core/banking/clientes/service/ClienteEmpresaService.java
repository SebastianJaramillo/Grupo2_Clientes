package com.banquito.core.banking.clientes.service;

import java.util.Calendar;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.core.banking.clientes.dao.ClienteRepository;
import com.banquito.core.banking.clientes.dao.TipoRelacionRepository;
import com.banquito.core.banking.clientes.domain.Cliente;
import com.banquito.core.banking.clientes.domain.Estado;
import com.banquito.core.banking.clientes.domain.TipoCliente;
import com.banquito.core.banking.clientes.domain.TipoIdentificacion;
import com.banquito.core.banking.clientes.domain.TipoRelacion;
import com.banquito.core.banking.clientes.utils.TransaccionException;
import com.banquito.core.banking.clientes.utils.EncontrarClienteException;
import com.banquito.core.banking.clientes.utils.ValidacionException;

@Service
public class ClienteEmpresaService {
    private final ClienteRepository clienteRepository;
    private final TipoRelacionRepository tipoRelacionRepository;

    public ClienteEmpresaService(ClienteRepository clienteRepository, TipoRelacionRepository tipoRelacionRepository) {
        this.clienteRepository = clienteRepository;
        this.tipoRelacionRepository = tipoRelacionRepository;
    }

    @Transactional
    public Cliente crearEmpresa(Cliente cliente) {
        try {
            if (TipoCliente.JUR.name().equals(cliente.getTipoCliente().name())) {
                if (TipoIdentificacion.RUC.name().equals(cliente.getTipoIdentificacion().name())) {
                    if (this.validarRuc(cliente.getNumeroIdentificacion())) {
                        Calendar fechaActual = Calendar.getInstance();
                        Calendar fechaConstitucion = Calendar.getInstance();
                        fechaConstitucion.setTime(cliente.getFechaConstitucion());

                        if (fechaConstitucion.before(fechaActual)) {
                            Optional<Cliente> optionalCliente = this.clienteRepository
                                    .findByTipoIdentificacionAndNumeroIdentificacion(cliente.getTipoIdentificacion(),
                                            cliente.getNumeroIdentificacion());
                            if (!optionalCliente.isPresent()) {
                                cliente.setFechaCreacion(fechaActual.getTime());
                                cliente.setEstado(Estado.ACT);
                                return this.clienteRepository.save(cliente);
                            } else {
                                throw new EncontrarClienteException(
                                        "Empresa con " + cliente.getTipoIdentificacion().name() + ": "
                                                + cliente.getNumeroIdentificacion() + " ya existe");
                            }
                        } else {
                            throw new ValidacionException("la fecha de constitucion");
                        }
                    } else {
                        throw new ValidacionException("el numero de identificacion");
                    }
                } else {
                    throw new RuntimeException("El tipo de identificacion es incorrecto para empresa");
                }
            } else {
                throw new RuntimeException("El tipo de cliente es invalido para empresa");
            }
        } catch (Exception e) {
            throw new TransaccionException(
                    "Error en creacion de Cliente tipo empresa: " + cliente + ", el error es: " + e);
        }
    }

    @Transactional
    public Cliente actualizarEmpresa(Cliente cliente) {
        try {
            Optional<Cliente> optionalCliente = clienteRepository.findById(cliente.getCodigo());
            if (optionalCliente.isPresent()) {
                if (TipoCliente.JUR.name().equals(cliente.getTipoCliente().name())) {
                    if (TipoIdentificacion.RUC.name().equals(cliente.getTipoIdentificacion().name())) {
                        if (this.validarRuc(cliente.getNumeroIdentificacion())) {
                            Calendar fechaActual = Calendar.getInstance();
                            Calendar fechaConstitucion = Calendar.getInstance();
                            fechaConstitucion.setTime(cliente.getFechaConstitucion());

                            if (fechaConstitucion.before(fechaActual)) {
                                cliente.setFechaUltimoCambio(fechaActual.getTime());
                                return this.clienteRepository.save(cliente);
                            } else {
                                throw new ValidacionException("la fecha de constitucion");
                            }
                        } else {
                            throw new ValidacionException("el numero de identificacion");
                        }
                    } else {
                        throw new RuntimeException("El tipo de identificacion es incorrecto para persona");
                    }
                } else {
                    throw new RuntimeException("El tipo de cliente es invalido para empresa");
                }
            } else {
                throw new EncontrarClienteException("No se pudo encontrar cliente");
            }
        } catch (Exception e) {
            throw new TransaccionException(
                    "Error en actualizacion de Cliente tipo empresa: " + cliente + ", el error es: " + e);
        }
    }

    public TipoRelacion crearTipoRelacion(TipoRelacion tipoRelacion) {
        try {
            return this.tipoRelacionRepository.save(tipoRelacion);
        } catch (Exception e) {
            throw new TransaccionException(
                    "Ocurrio un error al crear el TipoRelacion: " + tipoRelacion + " error: " + e.getMessage(), e);
        }
    }

    public Iterable<TipoRelacion> listAll() {
        return this.tipoRelacionRepository.findAll();
    }

    public Boolean validarRuc(String ruc) {
        Integer total = 0;
        Integer[] coeficientes6 = { 4, 3, 2, 7, 6, 5, 4, 3, 2, 1, 1, 1, 1 };
        Integer[] coeficientes9 = { 3, 2, 7, 6, 5, 4, 3, 2, 1, 1, 1, 1, 1 };

        if (ruc.matches("[0-9]*") && ruc.length() == 13) {
            Integer provincia = Integer.parseInt(ruc.charAt(0) + "" + ruc.charAt(1));
            Integer digitoTres = Integer.parseInt(ruc.charAt(2) + "");
            Integer digitoUltimo = 0;

            if ((provincia > 0 && provincia <= 24) && (digitoTres == 6 || digitoTres == 9)) {
                if (digitoTres == 6) {
                    digitoUltimo = Integer.parseInt(ruc.charAt(8) + "");
                    for (Integer i = 0; i < coeficientes6.length; i++) {
                        total += Integer.parseInt(coeficientes6[i] + "") * Integer.parseInt(ruc.charAt(i) + "");
                    }
                } else {
                    digitoUltimo = Integer.parseInt(ruc.charAt(9) + "");
                    for (Integer i = 0; i < coeficientes9.length; i++) {
                        total += Integer.parseInt(coeficientes9[i] + "") * Integer.parseInt(ruc.charAt(i) + "");
                    }
                }
                Integer digitoVerificador = (total % 11) == 0 ? 0 : 11 - (total % 11);
                if (digitoVerificador == digitoUltimo) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
