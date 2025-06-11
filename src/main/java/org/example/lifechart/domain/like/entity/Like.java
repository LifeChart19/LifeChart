package org.example.lifechart.domain.like.entity;

import org.example.lifechart.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likes", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"goal_id", "user_id"})
})
public class Like extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// @ManyToOne
	// @JoinColumn(name = "user_id", nullable = false)
	// private User userId;
	// @ManyToOne
	// @JoinColumn(name = "goal_id", nullable = false)
	// private Goal goalId;
	private Long userId;
	private Long goalId;

	public static Like createLike(Long userId, Long goalId) {
		return Like.builder()
			.userId(userId)
			.goalId(goalId)
			.build();
	}
}
