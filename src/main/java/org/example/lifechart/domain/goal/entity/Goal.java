package org.example.lifechart.domain.goal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.user.entity.User;

import java.time.LocalDateTime;

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

	public void delete() {
		this.status = Status.DELETED;
	}
}
