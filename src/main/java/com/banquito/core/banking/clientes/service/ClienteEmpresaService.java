package com.banquito.core.banking.clientes.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.core.banking.clientes.dao.ClientePersonaRelacionRepository;
import com.banquito.core.banking.clientes.dao.ClienteRepository;
import com.banquito.core.banking.clientes.dao.TipoRelacionRepository;
import com.banquito.core.banking.clientes.domain.Cliente;
import com.banquito.core.banking.clientes.domain.ClientePersonaRelacion;
import com.banquito.core.banking.clientes.domain.Estado;
import com.banquito.core.banking.clientes.domain.TipoCliente;
import com.banquito.core.banking.clientes.domain.TipoIdentificacion;
import com.banquito.core.banking.clientes.domain.TipoRelacion;
import com.banquito.core.banking.clientes.utils.TransaccionException;
import com.banquito.core.banking.clientes.utils.EncontrarException;
import com.banquito.core.banking.clientes.utils.ValidacionException;

@Service
public class ClienteEmpresaService {
    private final ClienteRepository clienteRepository;
    private final TipoRelacionRepository tipoRelacionRepository;
    private final ClientePersonaRelacionRepository clientePersonaRelacionRepository;

    public ClienteEmpresaService(ClienteRepository clienteRepository, TipoRelacionRepository tipoRelacionRepository,
            ClientePersonaRelacionRepository clientePersonaRelacionRepository) {
        this.clienteRepository = clienteRepository;
        this.tipoRelacionRepository = tipoRelacionRepository;
        this.clientePersonaRelacionRepository = clientePersonaRelacionRepository;
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
                            List<Cliente> optionalListaRucs = this.clienteRepository
                                    .findByTipoIdentificacionAndNumeroIdentificacionContaining(
                                            cliente.getTipoIdentificacion(),
                                            cliente.getNumeroIdentificacion().substring(0, 9));
                            if (optionalListaRucs.isEmpty()) {
                                cliente.setFechaCreacion(fechaActual.getTime());
                                cliente.setEstado(Estado.ACT);
                                return this.clienteRepository.save(cliente);
                            } else {
                                throw new EncontrarException(
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
                        throw new RuntimeException("El tipo de identificacion es incorrecto para empresa");
                    }
                } else {
                    throw new RuntimeException("El tipo de cliente es invalido para empresa");
                }
            } else {
                throw new EncontrarException("No se pudo encontrar cliente");
            }
        } catch (Exception e) {
            throw new TransaccionException(
                    "Error en actualizacion de Cliente tipo empresa: " + cliente + ", el error es: " + e);
        }
    }

    @Transactional
    public TipoRelacion crearTipoRelacion(TipoRelacion tipoRelacion) {
        try {
            return this.tipoRelacionRepository.save(tipoRelacion);
        } catch (Exception e) {
            throw new TransaccionException(
                    "Ocurrio un error al crear el TipoRelacion: " + tipoRelacion + " error: " + e.getMessage(), e);
        }
    }

    public Iterable<TipoRelacion> listAllTipoRelacion() {
        return this.tipoRelacionRepository.findAll();
    }

    @Transactional
    public List<ClientePersonaRelacion> agregarPersonasRelacion(List<ClientePersonaRelacion> listaPersonas) {
        try {
            List<ClientePersonaRelacion> resultado = new ArrayList<>();

            if (!listaPersonas.isEmpty()) {
                for (ClientePersonaRelacion clientePersonaRelacion : listaPersonas) {
                    Optional<TipoRelacion> optionalTipoRelacion = this.tipoRelacionRepository
                            .findById(clientePersonaRelacion.getCodigoRelacion());
                    Optional<Cliente> optionalPersona = this.clienteRepository
                            .findById(clientePersonaRelacion.getCodigoPersona());
                    Optional<Cliente> optionalEmpresa = this.clienteRepository
                            .findById(clientePersonaRelacion.getCodigoEmpresa());

                    if (optionalTipoRelacion.isPresent()) {
                        if (optionalPersona.isPresent()) {
                            if (optionalEmpresa.isPresent()) {
                                Optional<ClientePersonaRelacion> optionalClientePersonaRelacion = this.clientePersonaRelacionRepository
                                        .findByCodigoPersonaAndCodigoEmpresaAndEstado(
                                                clientePersonaRelacion.getCodigoPersona(),
                                                clientePersonaRelacion.getCodigoEmpresa(), Estado.ACT);
                                if (!optionalClientePersonaRelacion.isPresent()) {
                                    Calendar fechaActual = Calendar.getInstance();
                                    Calendar fechaInicio = Calendar.getInstance();
                                    fechaInicio.setTime(clientePersonaRelacion.getFechaInicio());

                                    if (fechaInicio.before(fechaActual)) {
                                        clientePersonaRelacion.setEstado(Estado.ACT);
                                        this.clientePersonaRelacionRepository.save(clientePersonaRelacion);
                                        resultado.add(clientePersonaRelacion);
                                    } else {
                                        throw new ValidacionException("la fecha de inicio");
                                    }
                                } else {
                                    throw new RuntimeException("Cliente persona ya existe en esa empresa");
                                }
                            } else {
                                throw new EncontrarException("No se pudo encontrar cliente empresa");
                            }
                        } else {
                            throw new EncontrarException("No se pudo encontrar cliente persona");
                        }
                    } else {
                        throw new EncontrarException("No se pudo encontrar tipo de relacion");
                    }
                }
                return resultado;
            } else {
                throw new RuntimeException("Se debe agregar una persona que represente a la empresa");
            }
        } catch (Exception e) {
            throw new TransaccionException(
                    "Error al agregar clientes persona a una empresa: " + listaPersonas + ", el error es: " + e);
        }
    }

    @Transactional
    public ClientePersonaRelacion eliminarPersonaEmpresa(ClientePersonaRelacion clientePersonaRelacion) {
        try {
            Optional<ClientePersonaRelacion> optionalPersona = clientePersonaRelacionRepository
                    .findByCodigoPersonaAndCodigoEmpresaAndEstado(clientePersonaRelacion.getCodigoPersona(),
                            clientePersonaRelacion.getCodigoEmpresa(), Estado.ACT);
            if (optionalPersona.isPresent()) {
                Calendar fechaActual = Calendar.getInstance();

                Calendar fechaInicio = Calendar.getInstance();
                fechaInicio.setTime(clientePersonaRelacion.getFechaInicio());

                Calendar fechaFin = Calendar.getInstance();
                fechaFin.setTime(clientePersonaRelacion.getFechaFin());

                if (fechaFin.after(fechaInicio) && fechaFin.before(fechaActual)) {
                    clientePersonaRelacion.setEstado(Estado.INA);
                    return this.clientePersonaRelacionRepository.save(clientePersonaRelacion);
                } else {
                    throw new ValidacionException("la fecha de fin");
                }
            } else {
                throw new EncontrarException("No se pudo encontrar persona asociada a la empresa");
            }
        } catch (Exception e) {
            throw new TransaccionException(
                    "Error en eliminacion de Cliente persona asociado a la empresa, el error es: " + e);
        }
    }

    @Transactional
    public Cliente eliminarEmpresa(Long id) {
        try {
            Integer contador = 0;
            List<ClientePersonaRelacion> empresa = clientePersonaRelacionRepository.findByCodigoEmpresa(id);
            for (ClientePersonaRelacion clientePersonaRelacion : empresa) {
                if (Estado.ACT.equals(clientePersonaRelacion.getEstado())) {
                    contador++;
                }
            }

            if (contador == 0) {
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
            } else {
                throw new RuntimeException("La empresa tiene clientes persona ACTIVOS");
            }

        } catch (Exception e) {
            throw new TransaccionException("Error en eliminacion de Cliente tipo empresa, el error es: " + e);
        }
    }

    public Boolean validarRuc(String ruc) {
        Integer total = 0;
        Integer[] coeficientes6 = { 3, 2, 7, 6, 5, 4, 3, 2 };
        Integer[] coeficientes9 = { 4, 3, 2, 7, 6, 5, 4, 3, 2 };

        if (ruc.matches("[0-9]*") && ruc.length() == 13) {
            Integer provincia = Integer.parseInt(ruc.charAt(0) + "" + ruc.charAt(1));
            Integer digitoTres = Integer.parseInt(ruc.charAt(2) + "");
            Integer digitoUltimo = 0;

            if ((provincia > 0 && provincia <= 24) && (digitoTres == 6 || digitoTres == 9)) {
                if (digitoTres == 6) {
                    digitoUltimo = Integer.parseInt(ruc.charAt(8) + "");
                    for (Integer i = 0; i < coeficientes6.length; i++) {
                        total += coeficientes6[i] * Integer.parseInt(ruc.charAt(i) + "");
                    }
                } else {
                    digitoUltimo = Integer.parseInt(ruc.charAt(9) + "");
                    for (Integer i = 0; i < coeficientes9.length; i++) {
                        total += coeficientes9[i] * Integer.parseInt(ruc.charAt(i) + "");
                    }
                }

                Integer digitoVerificador = (total % 11) == 0 ? 0 : 11 - (total % 11);
                if (digitoVerificador == digitoUltimo) {
                    if (digitoTres == 6) {
                        if (!"0000".equals(ruc.substring(9))) {
                            return true;
                        }
                    } else {
                        if (!"000".equals(ruc.substring(10))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }
}
