package org.example.lifechart.domain.simulation.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.simulation.logging.enums.ChangeType;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SimulationCreatedEvent {

    private final Long userId;

    private final Long simulationId;

    private final List<Long> goalIds;

    private final String params;

    private final String results;

    private final ChangeType changeType;

}