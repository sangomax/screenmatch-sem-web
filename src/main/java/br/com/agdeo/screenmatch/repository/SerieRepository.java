package br.com.agdeo.screenmatch.repository;

import br.com.agdeo.screenmatch.model.Categoria;
import br.com.agdeo.screenmatch.model.Episodio;
import br.com.agdeo.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer totalTemporadas, Double avaliacao);

    @Query("Select s from Serie s Where s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAValiacao(Integer totalTemporadas, Double avaliacao);

    @Query("SELECT ep FROM Serie s JOIN s.episodios ep WHERE ep.titulo Ilike %:episodioTitulo%")
    List<Episodio> episodioPorTitulo(String episodioTitulo);

    @Query("SELECT ep FROM Serie s JOIN s.episodios ep WHERE s = :serie ORDER BY ep.avaliacao DESC LIMIT 5")
    List<Episodio> top5EpisodioPorSerie(Serie serie);

    @Query("SELECT ep FROM Serie s JOIN s.episodios ep WHERE s = :serie AND YEAR(ep.dataLancamento) >= :anoBusca")
    List<Episodio> episodioPorSerieEAnoLancamento(Serie serie, int anoBusca);
}
