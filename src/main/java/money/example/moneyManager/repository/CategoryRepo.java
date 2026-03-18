package money.example.moneyManager.repository;

import money.example.moneyManager.entity.CatogoreyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<CatogoreyEntity,Long> {

    List<CatogoreyEntity> findByProfileId(Long profileId);

    Optional<CatogoreyEntity> findByIdAndProfileId(Long id, Long profileId);

    List<CatogoreyEntity> findByTypeAndProfileId(String type, Long profileId);

    // Change 'Profile' to 'ProfileId'
    Boolean existsByNameAndProfileId(String name, Long profileId);
}
