package org.example.lifechart.domain.shareGoal.controller;

import org.example.lifechart.domain.shareGoal.service.ShareGoalService;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ShareGoalController {
	private final ShareGoalService shareGoalService;
}
