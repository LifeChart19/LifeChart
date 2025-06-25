package org.example.lifechart.domain.goal.dto.response;

import java.util.List;

public record CursorPageResponse<T>(
	List<T> contents,
	Long nextCursor,
	boolean hasNext
) {}
