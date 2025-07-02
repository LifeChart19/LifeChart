package org.example.lifechart.adapter.in;

import org.example.lifechart.domain.goal.service.DefaultRetirementGoalService;
import org.example.lifechart.domain.user.dto.AccountCreatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCreatedEventListener {

	private final DefaultRetirementGoalService defaultRetirementGoalService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(AccountCreatedEvent event) {
		try {
			defaultRetirementGoalService.createDefaultRetirementGoal(event.getUserId());
		} catch (Exception e) {
			log.warn("기본 은퇴 목표 생성 실패 - userId={}", event.getUserId(), e);
		}
	}
}