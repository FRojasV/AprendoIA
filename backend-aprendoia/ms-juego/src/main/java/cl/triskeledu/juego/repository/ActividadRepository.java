package cl.triskeledu.juego.repository;

import cl.triskeledu.juego.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    List<Actividad> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
