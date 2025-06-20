package org.example.lifechart.domain.user.port;

import org.example.lifechart.domain.user.dto.AccountCreatedEvent;

public interface AccountEventPublisherPort {
    void publishAccountCreatedEvent(AccountCreatedEvent event);
}
