package cl.triskeledu.contenidos.repository;

import cl.triskeledu.contenidos.model.Capsula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapsulaRepository extends JpaRepository<Capsula, Long> {

    List<Capsula> findAllByOrderByPublicadoAscTituloAsc();

    List<Capsula> findByPublicadoTrueOrderByTituloAsc();

    List<Capsula> findByPuntoIdOrderByTituloAsc(Long puntoId);

    Optional<Capsula> findByCodigoQrIgnoreCase(String codigoQr);

    boolean existsByCodigoQrIgnoreCase(String codigoQr);

    boolean existsByCodigoQrIgnoreCaseAndIdNot(String codigoQr, Long id);
}
