package nl.joshlong.modulith_demo.orders;

import org.springframework.modulith.events.Externalized;

@Externalized(target = AmqIntegrationConfiguration.ORDERS_Q)
public record OrderPlacedEvent(int order) {
}
