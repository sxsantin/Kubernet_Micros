    package com.espe.micro_cursos.controllers;

    import com.espe.micro_cursos.models.Usuario;
    import com.espe.micro_cursos.models.entities.Curso;
    import com.espe.micro_cursos.services.CursoService;
    import feign.FeignException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/cursos")
    public class CursoController {

        @Autowired
        private CursoService cursoService;

        // Obtener todos los cursos
        @GetMapping
        public ResponseEntity<List<Curso>> findAll() {
            List<Curso> cursos = cursoService.findAll();
            return new ResponseEntity<>(cursos, HttpStatus.OK);
        }

        // Buscar un curso por ID
        @GetMapping("/{id}")
        public ResponseEntity<Curso> findById(@PathVariable Long id) {
            Optional<Curso> curso = cursoService.findById(id);
            return curso.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        // Crear un nuevo curso
        @PostMapping
        public ResponseEntity<Curso> save(@RequestBody Curso curso) {
            Curso nuevoCurso = cursoService.save(curso);
            return new ResponseEntity<>(nuevoCurso, HttpStatus.CREATED);
        }

        // Actualizar un curso existente
        @PutMapping("/{id}")
        public ResponseEntity<Curso> update(@PathVariable Long id, @RequestBody Curso curso) {
            Optional<Curso> cursoExistente = cursoService.findById(id);
            if (cursoExistente.isPresent()) {
                curso.setId(id); // Asegurarse de que el ID no cambie
                Curso cursoActualizado = cursoService.save(curso);
                return new ResponseEntity<>(cursoActualizado, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        // Eliminar un curso por ID
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteById(@PathVariable Long id) {
            Optional<Curso> cursoExistente = cursoService.findById(id);
            if (cursoExistente.isPresent()) {
                cursoService.deleteById(id);
                return new ResponseEntity<>("Curso eliminado con éxito", HttpStatus.OK); // Cambié el código de estado a OK
            } else {
                return new ResponseEntity<>("Curso no encontrado", HttpStatus.NOT_FOUND);
            }
        }

        @PostMapping("/{id}")
        public ResponseEntity<?> addUser(@PathVariable Long id, @RequestBody Usuario usuario) {
            Optional<Usuario> optional;
            try{
                optional = cursoService.addUser(usuario, id);
            }catch (FeignException ex){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Usuario no encontrado" + ex.getMessage()));
            }
            if (optional.isPresent()){
                return ResponseEntity.status(HttpStatus.CREATED).body(optional.get());
        }
            return ResponseEntity.notFound().build();
        }

        @GetMapping("/{id}/usuarios")
        public ResponseEntity<List<Usuario>> findUsersByCursoId(@PathVariable Long id) {
            List<Usuario> usuarios = cursoService.findUsersByCursoId(id);
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        }

        @PostMapping("/usuarios")
        public ResponseEntity<Usuario> addUsuario(@RequestBody Usuario usuario) {
            Usuario nuevoUsuario = cursoService.addUsuario(usuario);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        }

        @DeleteMapping("/{cursoId}/usuarios/{usuarioId}")
        public ResponseEntity<String> removeUser(@PathVariable Long cursoId, @PathVariable Long usuarioId) {
            boolean removed = cursoService.removeUserFromCurso(cursoId, usuarioId);
            if (removed) {
                return new ResponseEntity<>("Usuario desmatriculado con éxito", HttpStatus.OK); // Mensaje de éxito
            } else {
                return new ResponseEntity<>("Usuario no encontrado en el curso", HttpStatus.NOT_FOUND); // Mensaje de error
            }
        }
    }
