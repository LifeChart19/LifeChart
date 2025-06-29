package org.example.lifechart.domain.follow.repository;

import java.util.Optional;

import org.example.lifechart.domain.follow.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	Page<Follow> findByReceiverId(Long receiverId, Pageable pageable);

	Page<Follow> findByRequesterId(Long userId, Pageable pageable);

	Optional<Follow> findByRequesterIdAndReceiverId(Long myId, Long userId);

	boolean existsByRequesterIdAndReceiverId(Long myId, Long userId);

}
