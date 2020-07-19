package com.gavilan.statemachinedemo.services;

import com.gavilan.statemachinedemo.dominio.Producto;
import com.gavilan.statemachinedemo.dominio.ProductoEvent;
import com.gavilan.statemachinedemo.dominio.ProductoState;
import com.gavilan.statemachinedemo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author: Eze Gavilán
 **/

@RequiredArgsConstructor
@Component
public class ProductoStateChangeInterceptor extends StateMachineInterceptorAdapter<ProductoState, ProductoEvent> {

    private final ProductoRepository productoRepository;

    @Override
    public void preStateChange(State<ProductoState, ProductoEvent> state, Message<ProductoEvent> message, Transition<ProductoState, ProductoEvent> transition, StateMachine<ProductoState, ProductoEvent> stateMachine) {

        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(ProductoService.PRODUCTO_ID_HEADER, -1L)))
                    .ifPresent(productoId -> {
                        Producto producto = this.productoRepository.getOne(productoId);
                        producto.setEstado(state.getId());
                        this.productoRepository.save(producto);
                    });
        });
    }
}
