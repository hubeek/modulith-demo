package nl.joshlong.modulith_demo.products;

import nl.joshlong.modulith_demo.orders.OrderPlacedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
class Products {

    @ApplicationModuleListener
    void on(OrderPlacedEvent event) throws  Exception{
        System.out.println("starting ["+event+"]");
        Thread.sleep(5000);
        System.out.println("stop ["+event+"]");

    }
}
