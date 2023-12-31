package com.banquito.core.banking.clientes.controller;

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
import com.banquito.core.banking.clientes.domain.TipoIdentificacion;
import com.banquito.core.banking.clientes.service.ClientePersonaService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private ClientePersonaService clienteService;

    public ClienteController(ClientePersonaService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/listar")
    public ResponseEntity<Iterable<Cliente>> listAll() {
        return ResponseEntity.ok().body(clienteService.listAll());
    }
    
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Optional<Cliente>> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(clienteService.findById(id));
    }

    @GetMapping("/buscar/{tipoIdentificacion}/{numeroIdentificacion}")
    public ResponseEntity<Optional<Cliente>> findById(@PathVariable TipoIdentificacion tipoIdentificacion, @PathVariable String numeroIdentificacion) {
        return ResponseEntity.ok().body(clienteService.findByIdentificacion(tipoIdentificacion, numeroIdentificacion));
    }

    @PostMapping("/persona/crear")
    public ResponseEntity<Cliente> save(@RequestBody Cliente persona) {
        return ResponseEntity.ok().body(clienteService.crearPersona(persona));
    }

    @PutMapping("/persona/actualizar")
    public ResponseEntity<Cliente> update(@RequestBody Cliente persona) {
        return ResponseEntity.ok().body(clienteService.actualizarPersona(persona));
    }

    @PutMapping("/persona/eliminar/{id}")
    public ResponseEntity<Cliente> delete(@PathVariable Long id) {
        return ResponseEntity.ok().body(clienteService.eliminarPersona(id));
    }
}
