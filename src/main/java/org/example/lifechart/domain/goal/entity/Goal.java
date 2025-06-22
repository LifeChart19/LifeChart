package org.example.lifechart.domain.goal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.goal.dto.request.GoalUpdateRequest;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.user.entity.User;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true) // 객체2 = 객체1.toBuilder()...를 사용하면 이미 생성된 객체로부터 Builder를 생성해 값 일부만 수정한 새 객체를 만들 수 있음
@Table(name="goal")
public class Goal extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category;

	@Column(nullable = false)
	private Long targetAmount;

	@Column(nullable = false)
	private LocalDateTime startAt;

	@Column(nullable = false)
	private LocalDateTime endAt;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Share share;

	@Column(nullable = false)
	private int commentCount = 0;

	@Column(nullable = false)
	private int likeCount = 0;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
	@Column(name = "tag")
	private List<String> tags = new ArrayList<>();

	public void delete() {
		this.status = Status.DELETED;
	}

	public void update(GoalUpdateRequest request) {
		title = request.getTitle();
		targetAmount = request.getTargetAmount();
		startAt = request.getStartAt();
		endAt = request.getEndAt();
		share = request.getShare();
		tags =  request.getTags();
	}
}
