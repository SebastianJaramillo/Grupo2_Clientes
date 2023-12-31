package com.banquito.core.banking.clientes.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.core.banking.clientes.domain.Cliente;
import com.banquito.core.banking.clientes.domain.ClientePersonaRelacion;
import com.banquito.core.banking.clientes.domain.TipoIdentificacion;
import com.banquito.core.banking.clientes.domain.TipoRelacion;
import com.banquito.core.banking.clientes.service.ClienteEmpresaService;
import com.banquito.core.banking.clientes.service.ClientePersonaService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private ClientePersonaService clientePersonaService;
    private ClienteEmpresaService clienteEmpresaService;

    public ClienteController(ClientePersonaService clientePersonaService, ClienteEmpresaService clienteEmpresaService) {
        this.clientePersonaService = clientePersonaService;
        this.clienteEmpresaService = clienteEmpresaService;
    }

    @GetMapping("/listar")
    public ResponseEntity<Iterable<Cliente>> listAll() {
        return ResponseEntity.ok().body(clientePersonaService.listAll());
    }
    
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Optional<Cliente>> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(clientePersonaService.findById(id));
    }

    @GetMapping("/buscar/{tipoIdentificacion}/{numeroIdentificacion}")
    public ResponseEntity<Optional<Cliente>> findById(@PathVariable TipoIdentificacion tipoIdentificacion, @PathVariable String numeroIdentificacion) {
        return ResponseEntity.ok().body(clientePersonaService.findByIdentificacion(tipoIdentificacion, numeroIdentificacion));
    }

    //PERSONA
    @GetMapping("/persona/listar")
    public ResponseEntity<List<Cliente>> findPersona() {
        return ResponseEntity.ok().body(clientePersonaService.findByTipoCliente());
    }

    @PostMapping("/persona/crear")
    public ResponseEntity<Cliente> savePersona(@RequestBody Cliente persona) {
        return ResponseEntity.ok().body(clientePersonaService.crearPersona(persona));
    }

    @PutMapping("/persona/actualizar")
    public ResponseEntity<Cliente> updatePersona(@RequestBody Cliente persona) {
        return ResponseEntity.ok().body(clientePersonaService.actualizarPersona(persona));
    }

    @PutMapping("/persona/eliminar/{id}")
    public ResponseEntity<Cliente> deletePersona(@PathVariable Long id) {
        return ResponseEntity.ok().body(clientePersonaService.eliminarPersona(id));
    }
    
    //EMPRESA
    @PostMapping("/empresa/crear")
    public ResponseEntity<Cliente> saveEmpresa(@RequestBody Cliente empresa) {
        return ResponseEntity.ok().body(clienteEmpresaService.crearEmpresa(empresa));
    }

    @PutMapping("/empresa/actualizar")
    public ResponseEntity<Cliente> updateEmpresa(@RequestBody Cliente empresa) {
        return ResponseEntity.ok().body(clienteEmpresaService.actualizarEmpresa(empresa));
    }

    @PostMapping("/empresa/persona/agregar")
    public ResponseEntity<List<ClientePersonaRelacion>> addPersonasEmpresa(@RequestBody List<ClientePersonaRelacion> personas) {
        return ResponseEntity.ok().body(clienteEmpresaService.agregarPersonasRelacion(personas));
    }

    @PutMapping("/empresa/persona/eliminar")
    public ResponseEntity<ClientePersonaRelacion> deletePersonaEmpresa(@RequestBody ClientePersonaRelacion persona) {
        return ResponseEntity.ok().body(clienteEmpresaService.eliminarPersonaEmpresa(persona));
    }

    @PutMapping("/empresa/eliminar/{id}")
    public ResponseEntity<Cliente> deleteEmpresa(@PathVariable Long id) {
        return ResponseEntity.ok().body(clienteEmpresaService.eliminarEmpresa(id));
    }

    //TIPO RELACION
    @GetMapping("/relacion/listar")
    public ResponseEntity<Iterable<TipoRelacion>> listAllTipoRelacion() {
        return ResponseEntity.ok().body(clienteEmpresaService.listAllTipoRelacion());
    }
}
