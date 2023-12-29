package com.banquito.core.banking.clientes.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.banquito.core.banking.clientes.domain.Cliente;
import com.banquito.core.banking.clientes.domain.ClientePersonaRelacion;

public interface ClientePersonaRelacionRepository extends CrudRepository<ClientePersonaRelacion, Long> {

    List<ClientePersonaRelacion> findByClienteEmpresaAndEstado(Cliente clienteEmpresa, String estado);

    List<ClientePersonaRelacion> findByClientePersonaAndEstado(Cliente clientePersona, String estado);

    List<ClientePersonaRelacion> findByTipoRelacionAndEstado(Cliente tiporelacion, String estado);
}