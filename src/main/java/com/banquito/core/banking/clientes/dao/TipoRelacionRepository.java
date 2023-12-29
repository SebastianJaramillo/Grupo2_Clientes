package com.banquito.core.banking.clientes.dao;

import org.springframework.data.repository.CrudRepository;

import com.banquito.core.banking.clientes.domain.TipoRelacion;

public interface TipoRelacionRepository extends CrudRepository<TipoRelacion, String> {

}