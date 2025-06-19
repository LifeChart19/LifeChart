package org.example.lifechart.domain.comment.entity;

import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "goal_id")
	private Goal goal;
	@Column(nullable = false)
	private String contents;

	public static Comment createComment(User user, Goal goal, String contents) {
		return Comment.builder()
			.user(user)
			.goal(goal)
			.contents(contents)
			.build();
	}

	public void updateContents(String updateContents) {
		if (updateContents != null) {
			this.contents = updateContents;
		}
	}

}
