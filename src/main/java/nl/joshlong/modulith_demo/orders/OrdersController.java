package nl.joshlong.modulith_demo.orders;

import org.springframework.amqp.core.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@ResponseBody
@RequestMapping("/orders")
public class OrdersController {

    private final Orders orders;

    public OrdersController(Orders orders) {
        this.orders = orders;
    }

    @PostMapping
    void place(@RequestBody Order order) {
        this.orders.place(order);
    }
}

@Service
@Transactional
class Orders {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;

    Orders(OrderRepository orderRepository, ApplicationEventPublisher publisher) {
        this.orderRepository = orderRepository;
        this.publisher = publisher;
    }

    void place(Order order){
        Order saved = this.orderRepository.save(order);
        System.out.println("saved :: ["+saved+"]");

//        products.updateInventory();
        this.publisher.publishEvent(new OrderPlacedEvent(saved.id()));
    }
}

@Repository
interface OrderRepository extends ListCrudRepository<Order, Integer> {

}

@Table("orders")
record Order(@Id Integer id, Set<LineItem> lineItems) { }

@Table("orders_line_items")
record LineItem(@Id Integer id, int product, int quantity) {}

@Configuration
class AmqIntegrationConfiguration {
    static final String ORDERS_Q = "orders";

    @Bean
    Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDERS_Q).noargs();
    }

    @Bean
    Exchange exchange() {
        return ExchangeBuilder.directExchange(ORDERS_Q).build();
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable(ORDERS_Q).build();
    }
}
