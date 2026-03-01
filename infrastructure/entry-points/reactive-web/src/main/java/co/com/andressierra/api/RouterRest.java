package co.com.andressierra.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    private static final String CARD = "/api/v1/cards";
    private static final String TRANSACTION = "/api/v1/transactions";

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route()
                .POST(CARD, handler::createCard)
                .GET(CARD + "/{identifier}", handler::getCard)
                .PUT(CARD + "/{identifier}/enroll", handler::enrollCard)
                .DELETE(CARD + "/{identifier}", handler::deleteCard)

                .POST(TRANSACTION, handler::createTransaction)
                .GET(TRANSACTION + "/{reference}", handler::getTransaction)
                .PUT(TRANSACTION + "/{reference}/cancel", handler::cancelTransaction)

                .build();
    }
}
