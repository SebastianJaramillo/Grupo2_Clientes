package com.banquito.core.banking.clientes.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.banquito.core.banking.clientes.domain.ClientePersonaRelacion;
import com.banquito.core.banking.clientes.domain.Estado;

public interface ClientePersonaRelacionRepository extends CrudRepository<ClientePersonaRelacion, Long> {

    Optional<ClientePersonaRelacion> findByCodigoPersonaAndCodigoEmpresaAndEstado(Long codigoPersona, Long codigoEmpresa, Estado estado);

    List<ClientePersonaRelacion> findByCodigoPersona(Long codigoPersona);

    List<ClientePersonaRelacion> findByCodigoEmpresa(Long codigoEmpresa);
}