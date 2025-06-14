package org.example.lifechart.domain.goal.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.example.lifechart.domain.goal.dto.request.GoalRetirementCalculateRequest;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalRetirementCalculateServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private GoalRetirementCalculateService goalRetirementCalculateService;

	@Test
	@DisplayName("예상 은퇴 비용을 정상적으로 계산한다.")
	void 예상_은퇴_비용을_정상적으로_계산한다() {
		// given
		User user = User.builder()
			.id(1L)
			.birthDate(LocalDate.of(1990, 1, 1))
			.gender("male")
			.build();

		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
		GoalRetirementCalculateRequest request = GoalRetirementCalculateRequest.builder()
			.endAt(LocalDateTime.of(2055,1, 31,0,0,0))
			.expectedLifespan(85L)
			.monthlyExpense(2_000_000L)
			.retirementType(RetirementType.COUPLE)
			.build();

		// when
		Long targetAmount = goalRetirementCalculateService.calculateTargetAmount(request, user.getId());

		// then
		verify(userRepository).findById(user.getId());
		assertThat(targetAmount).isEqualTo(502_000_000L); // 예상 사망일: 1990 + 85 = 2075 -> 2055.01.31~2075.12.31 = 20년 * 251개월 * 2백만원 = 5.02억

	}
}
