package cl.triskeledu.contenidos.repository;

import cl.triskeledu.contenidos.model.Punto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuntoRepository extends JpaRepository<Punto, Long> {
    List<Punto> findAllByOrderByNombreAsc();
}
