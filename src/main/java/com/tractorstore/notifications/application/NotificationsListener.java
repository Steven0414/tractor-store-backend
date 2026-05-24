package com.tractorstore.notifications.application;

import com.tractorstore.shared.events.OrderPlacedEvent;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationsListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationsListener.class);

  private final List<String> sentEmails = new ArrayList<>();

  @TransactionalEventListener
  public void onOrderPlaced(OrderPlacedEvent event) {
    String simulatedEmail = "order=" + event.orderId() + " lines=" + event.lines().size();
    sentEmails.add(simulatedEmail);
    LOGGER.info("Simulated notification sent: {}", simulatedEmail);
  }

  public List<String> sentEmails() {
    return List.copyOf(sentEmails);
  }
}
