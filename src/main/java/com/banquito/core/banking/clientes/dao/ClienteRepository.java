package com.banquito.core.banking.clientes.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.banquito.core.banking.clientes.domain.Cliente;
import com.banquito.core.banking.clientes.domain.Estado;
import com.banquito.core.banking.clientes.domain.TipoCliente;
import com.banquito.core.banking.clientes.domain.TipoIdentificacion;

public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    List<Cliente> findByEstadoOrderByCodigo(Estado estado);;
    
    Optional<Cliente> findByTipoIdentificacionAndNumeroIdentificacion(TipoIdentificacion tipoIdentificacion, String numeroIdentificacion);

    Optional<Cliente> findByCorreoElectronico(String correoElectronico);

    List<Cliente> findByTelefonoOrderByCodigo(String telefono);

    List<Cliente> findByTipoClienteOrderByApellidos(TipoCliente tipoCliente);

    List<Cliente> findByTipoClienteAndRazonSocialLikeOrderByRazonSocial(String tipoCliente, String razonSocial);

    List<Cliente> findByTipoIdentificacionAndNumeroIdentificacionContaining(TipoIdentificacion tipoIdentificacion, String numeroIdentificacion);

}