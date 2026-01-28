package jyhs.trail.infrastructure.persistence.adapters;

import jakarta.transaction.Transactional;
import jyhs.trail.domain.model.RefreshToken;
import jyhs.trail.domain.model.User;
import jyhs.trail.domain.repository.RefreshTokenRepository;
import jyhs.trail.infrastructure.persistence.entities.RefreshTokenEntity;
import jyhs.trail.infrastructure.persistence.entities.UserEntity;
import jyhs.trail.infrastructure.persistence.repositories.SpringDataRefreshTokenRepository;
import jyhs.trail.infrastructure.persistence.repositories.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RefreshTokenJpaAdapter implements RefreshTokenRepository {
    private final SpringDataRefreshTokenRepository jpaTokenRepository;
    private final SpringDataUserRepository jpaUserRepository; // Para buscar el UserEntity

    public RefreshTokenJpaAdapter(SpringDataRefreshTokenRepository jpaTokenRepository, SpringDataUserRepository jpaUserRepository) {
        this.jpaTokenRepository = jpaTokenRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    @Transactional // 🛡️ Asegura que el borrado y guardado sean una sola operación atómica
    public RefreshToken save(RefreshToken refreshToken) {
        // 1. Limpieza: Borramos tokens previos para que el usuario solo tenga uno activo
        jpaTokenRepository.deleteByUser_Id(refreshToken.user().id());

        // 🚀 TRUCO DE SENIOR: Forzamos a Hibernate a ejecutar el DELETE inmediatamente
        jpaTokenRepository.flush();

        // 2. Buscamos el UserEntity
        UserEntity userEntity = jpaUserRepository.findById(refreshToken.user().id())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Mapeamos de Dominio a Entidad
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setToken(refreshToken.token());
        entity.setExpiryDate(refreshToken.expiryDate());
        entity.setUser(userEntity);

        // 4. Guardamos
        RefreshTokenEntity saved = jpaTokenRepository.save(entity);

        return new RefreshToken(
                saved.getId(),
                saved.getToken(),
                saved.getExpiryDate(),
                refreshToken.user()
        );
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaTokenRepository.findByToken(token)
                .map(entity -> {
                    // Mapeamos UserEntity de vuelta a User de Dominio (sin rol por ahora)
                    User userDomain = new User(
                            entity.getUser().getId(),
                            entity.getUser().getUsername(),
                            entity.getUser().getEmail(),
                            entity.getUser().getPassword()
                    );

                    return new RefreshToken(
                            entity.getId(),
                            entity.getToken(),
                            entity.getExpiryDate(),
                            userDomain
                    );
                });
    }

    @Override
    @Transactional // 🛡️ Necesario para operaciones de borrado en Spring Data
    public void deleteByUser(Long userId) {
        jpaTokenRepository.deleteByUser_Id(userId);
    }
}