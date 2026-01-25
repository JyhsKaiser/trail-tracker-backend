package jyhs.trail.application.usecases.run;

import jyhs.trail.domain.repository.RunRepository;
import org.springframework.stereotype.Component;

@Component
public class DeleteRunUseCase {
    private final RunRepository repository;

    public DeleteRunUseCase(RunRepository repository) {
        this.repository = repository;
    }

    public void execute(Long id) {
        repository.deleteById(id);
    }
}