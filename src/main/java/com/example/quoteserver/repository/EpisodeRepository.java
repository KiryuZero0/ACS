package com.example.quoteserver.repository;

import com.example.quoteserver.model.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
}
