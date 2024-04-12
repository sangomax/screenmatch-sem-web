package br.com.agdeo.screenmatch.repository;

import br.com.agdeo.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieRepository extends JpaRepository<Serie, Long> {
}
