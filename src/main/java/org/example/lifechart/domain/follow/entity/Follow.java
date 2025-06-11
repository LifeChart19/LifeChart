package org.example.lifechart.domain.follow.entity;

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
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {"request_id", "receiver_id"})
})
public class Follow extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// @ManyToOne
	// @JoinColumn(nullable = false, name = "request_id")
	// private User requestId;
	// @ManyToOne
	// @JoinColumn(nullable = false, name = "receiver_id")
	// private User receiverId;
	private Long requestId;
	private Long receiverId;

	public static Follow createFollow(Long myId, Long userId) {
		return Follow.builder()
			.requestId(myId)
			.receiverId(userId)
			.build();
	}

}
