package com.espe.micro_cursos.clients;

import com.espe.micro_cursos.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "micro-usuarios", url = "micro-usuarios:8004/api/usuarios")
public interface UsuarioClientRest {
    @GetMapping("/{id}")
    Usuario findById(@PathVariable Long id);
    @GetMapping
    List<Usuario> findAll();
    @PostMapping
    Usuario save(@RequestBody Usuario usuario);
}

