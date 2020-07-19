package com.gavilan.statemachinedemo.services;

import com.gavilan.statemachinedemo.dominio.Producto;
import com.gavilan.statemachinedemo.dominio.ProductoEvent;
import com.gavilan.statemachinedemo.dominio.ProductoState;
import com.gavilan.statemachinedemo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import static com.gavilan.statemachinedemo.services.ProductoService.PRODUCTO_ID_HEADER;

/**
 * @author: Eze Gavilán
 **/

@Service
@RequiredArgsConstructor
public class StateMachineService {

    private final ProductoRepository productoRepository;
    private final StateMachineFactory<ProductoState, ProductoEvent> stateMachineFactory;
    private final ProductoStateChangeInterceptor productoStateChangeInterceptor;

    public void sendEvent(Long productoId, StateMachine<ProductoState, ProductoEvent> sm, ProductoEvent event) {
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(PRODUCTO_ID_HEADER, productoId)
                .build();

        sm.sendEvent(msg);
    }

    public StateMachine<ProductoState, ProductoEvent> build(Long productoId) {
        Producto producto = this.productoRepository.getOne(productoId);

        StateMachine<ProductoState, ProductoEvent> sm = stateMachineFactory.getStateMachine(Long.toString(producto.getId()));

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(productoStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(producto.getEstado(), null, null, null));
                });

        sm.start();

        return sm;
    }
}
