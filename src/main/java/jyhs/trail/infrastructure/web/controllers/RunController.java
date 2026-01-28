package jyhs.trail.infrastructure.web.controllers;

import jyhs.trail.application.usecases.run.DeleteRunUseCase;
import jyhs.trail.application.usecases.run.EditRunUserCase;
import jyhs.trail.application.usecases.run.GetAllRunsByUserUseCase;
import jyhs.trail.application.usecases.run.SaveRunUseCase;
import jyhs.trail.domain.model.Run;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/runs")
@CrossOrigin(origins = "*") // Permite que Angular se conecte
public class RunController {

    // Inyectamos solo lo que necesitamos
    private final SaveRunUseCase saveRunUseCase;
    private final EditRunUserCase editRunUserCase;
    private final GetAllRunsByUserUseCase getAllRunsByUserUseCase;
    private final DeleteRunUseCase deleteRunUseCase;

    public RunController(SaveRunUseCase saveRunUseCase, GetAllRunsByUserUseCase getAllRunsByUserUseCase, DeleteRunUseCase deleteRunUseCase, EditRunUserCase editRunUserCase) {
        this.saveRunUseCase = saveRunUseCase;
        this.getAllRunsByUserUseCase = getAllRunsByUserUseCase;
        this.deleteRunUseCase = deleteRunUseCase;
        this.editRunUserCase = editRunUserCase;
    }

    @GetMapping
    public ResponseEntity<List<Run>> list() {
        // El SecurityContextHolder ya tiene el username gracias al filtro JWT
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username == null || username.equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Run> runs = getAllRunsByUserUseCase.execute(username);
        return ResponseEntity.ok(runs);
    }

    @PostMapping
    public Run create(@RequestBody Run run) {
        // Obtenemos el username de la sesión actual
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return saveRunUseCase.execute(run, username);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Run> updateRun(
            @PathVariable Long id,
            @RequestBody Run runRequest) {

        // Tu lógica de servicio para buscar, validar que pertenezca al usuario y actualizar
        Run updatedRun = editRunUserCase.execute(id, runRequest);
        return ResponseEntity.ok(updatedRun);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteRunUseCase.execute(id);
        return ResponseEntity.noContent().build(); // Devuelve un 204 No Content
    }
}