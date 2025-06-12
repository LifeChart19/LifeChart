package org.example.lifechart.domain.comment.entity;

import org.example.lifechart.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Comment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// @ManyToOne
	// @JoinColumn(nullable = false, name = "user_id")
	// private User userId;
	// @ManyToOne
	// @JoinColumn(nullable = false, name = "goal_id")
	// private Goal goalId;
	private Long userId;
	private Long goalId;
	private String contents;

	public static Comment createComment(Long userId, Long goalId, String contents) {
		return Comment.builder()
			.userId(userId)
			.goalId(goalId)
			.contents(contents)
			.build();
	}

	public void updateContents(String updateContents) {
		if (updateContents != null) {
			this.contents = updateContents;
		}
	}

}
