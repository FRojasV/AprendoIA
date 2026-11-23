package cl.triskeledu.juego.repository;

import cl.triskeledu.juego.model.Descubrimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DescubrimientoRepository extends JpaRepository<Descubrimiento, Long> {

    List<Descubrimiento> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    Optional<Descubrimiento> findByUsuarioIdAndCapsulaId(Long usuarioId, Long capsulaId);

    boolean existsByUsuarioIdAndPuntoId(Long usuarioId, Long puntoId);

    long countByUsuarioId(Long usuarioId);

    long countByUsuarioIdAndVistoTrue(Long usuarioId);

    @Query("select coalesce(sum(d.puntos), 0) from Descubrimiento d where d.usuarioId = :usuarioId")
    long sumarPuntos(Long usuarioId);

    @Query("select count(distinct d.puntoId) from Descubrimiento d where d.usuarioId = :usuarioId")
    long contarLugares(Long usuarioId);

    @Query("select d.usuarioId as usuarioId, sum(d.puntos) as puntos from Descubrimiento d group by d.usuarioId")
    List<PuntajeUsuario> puntosPorUsuario();

    void deleteByUsuarioId(Long usuarioId);
}
