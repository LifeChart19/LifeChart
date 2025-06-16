package org.example.lifechart.domain.follow.entity;

import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@UniqueConstraint(columnNames = {"requester_id", "receiver_id"})
})
public class Follow extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(nullable = false, name = "requester_id")
	private User requester;
	@ManyToOne
	@JoinColumn(nullable = false, name = "receiver_id")
	private User receiver;


	public static Follow createFollow(User authUser, User user) {
		return Follow.builder()
			.requester(authUser)
			.receiver(user)
			.build();
	}

}
