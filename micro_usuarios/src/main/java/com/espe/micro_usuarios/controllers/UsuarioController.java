package com.espe.micro_usuarios.controllers;

import com.espe.micro_usuarios.models.entities.Usuario;
import com.espe.micro_usuarios.services.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

        @PostMapping("/register")
        public ResponseEntity<Usuario> register(@Valid @RequestBody Usuario usuario){
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        }

        @PostMapping("/login")
        public ResponseEntity<Usuario> login(@RequestBody Usuario usuario) {
            return ResponseEntity.ok(usuarioService.login(usuario));
        }

        @GetMapping("/login/google")
        public ResponseEntity<String> loginWithGoogle(HttpServletResponse response) throws IOException {
            response.sendRedirect("/oauth2/authorization/google");
            return ResponseEntity.ok("Redirigiendo ...");
        }

        @GetMapping("/loginSuccess")
        public ResponseEntity<?> handleGoogleSuccess(OAuth2AuthenticationToken auth2AuthenticationToken) {
            Usuario usuario = usuarioService.loginRegisterByGoogleOAuth2(auth2AuthenticationToken);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:3000/home")).build();
        }



    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> findAll() {
        List<Usuario> usuarios = usuarioService.findAll();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    // Buscar un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable @Positive(message = "El ID debe ser un número positivo.") Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<Usuario> save(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.save(usuario);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // Actualizar un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable @Positive(message = "El ID debe ser un número positivo.") Long id,
            @Valid @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id); // Asegurarse de que el ID no cambie
            Usuario usuarioActualizado = usuarioService.save(usuario);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } else {
            // Mensaje claro si el usuario no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró un usuario con el ID proporcionado: " + id);
        }
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable @Positive(message = "El ID debe ser un número positivo.") Long id) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuarioService.deleteById(id);
            return new ResponseEntity<>("Usuario eliminado correctamente.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
    }
}
