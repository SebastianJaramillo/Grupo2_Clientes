package com.banquito.core.banking.clientes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.core.banking.clientes.domain.Cliente;
import com.banquito.core.banking.clientes.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/listar")
    public ResponseEntity<Iterable<Cliente>> listAll() {
        return ResponseEntity.ok().body(clienteService.listAll());
    }

    @PostMapping("/persona/crear")
    public ResponseEntity<Cliente> save(@RequestBody Cliente persona) {
        return ResponseEntity.ok().body(clienteService.crearPersona(persona));
    }
}
