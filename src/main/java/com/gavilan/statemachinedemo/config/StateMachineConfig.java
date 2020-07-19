package com.gavilan.statemachinedemo.config;

import com.gavilan.statemachinedemo.dominio.Producto;
import com.gavilan.statemachinedemo.dominio.ProductoEvent;
import com.gavilan.statemachinedemo.dominio.ProductoState;
import com.gavilan.statemachinedemo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * @author: Eze Gavilán
 **/

@Slf4j
@EnableStateMachineFactory
@RequiredArgsConstructor
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<ProductoState, ProductoEvent> {

    private final ProductoRepository productoRepository;

    @Override
    public void configure(StateMachineStateConfigurer<ProductoState, ProductoEvent> states) throws Exception {

        states.withStates()
                .initial(ProductoState.EN_STOCK)
                .states(EnumSet.allOf(ProductoState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ProductoState, ProductoEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(ProductoState.EN_STOCK).target(ProductoState.EN_STOCK).event(ProductoEvent.ACTUALIZAR_STOCK).guard(enStock()).action(actualizarStock())
                .and()
                .withExternal()
                .source(ProductoState.EN_STOCK).target(ProductoState.STOCK_MINIMO).event(ProductoEvent.ACTUALIZAR_STOCK).guard(stockMinimo()).action(actualizarStock())
                .and()
                .withExternal().source(ProductoState.EN_STOCK).target(ProductoState.SIN_STOCK).event(ProductoEvent.ACTUALIZAR_STOCK).guard(sinStock())
                .and()
                .withExternal().source(ProductoState.STOCK_MINIMO).target(ProductoState.STOCK_MINIMO).event(ProductoEvent.ACTUALIZAR_STOCK).guard(stockMinimo()).action(actualizarStock())
                .and()
                .withExternal().source(ProductoState.STOCK_MINIMO).target(ProductoState.SIN_STOCK).event(ProductoEvent.ACTUALIZAR_STOCK).guard(sinStock()).action(actualizarStock())
                .and()
                .withExternal().source(ProductoState.STOCK_MINIMO).target(ProductoState.EN_STOCK).event(ProductoEvent.ACTUALIZAR_STOCK).guard(enStock()).action(actualizarStock())
                .and()
                .withExternal().source(ProductoState.SIN_STOCK).target(ProductoState.EN_STOCK).event(ProductoEvent.ACTUALIZAR_STOCK).action(actualizarStock());
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ProductoState, ProductoEvent> config) throws Exception {

        StateMachineListenerAdapter<ProductoState, ProductoEvent> adapter = new StateMachineListenerAdapter<>() {

            @Override
            public void stateChanged(State<ProductoState, ProductoEvent> from, State<ProductoState, ProductoEvent> to) {
                log.info(String.format("stateChanged(from: %s, to: %s)", from.getId(), to.getId()));
            }
        };
        config.withConfiguration().listener(adapter);
    }

    private Action<ProductoState, ProductoEvent> actualizarStock() {
        return stateContext -> {
            StateMachine<ProductoState, ProductoEvent> sm = stateContext.getStateMachine();
            Producto producto = sm.getExtendedState().get("producto", Producto.class);
            Integer cantidad = stateContext.getExtendedState().get("cantidad", Integer.class);

            producto.setCantidad(producto.getCantidad() - cantidad);

            this.productoRepository.save(producto);
        };
    }

    private Guard<ProductoState, ProductoEvent> enStock() {
        return stateContext -> {
            StateMachine<ProductoState, ProductoEvent> sm = stateContext.getStateMachine();

            Producto producto = this.getProducto(sm);
            Integer cantidad = this.getCantidad(sm);

            int nuevaCantidad = producto.getCantidad() - cantidad;

            return nuevaCantidad >= 30;
        };
    }

    private Guard<ProductoState, ProductoEvent> stockMinimo() {
        return stateContext -> {
            StateMachine<ProductoState, ProductoEvent> sm = stateContext.getStateMachine();

            Producto producto = this.getProducto(sm);
            Integer cantidad = this.getCantidad(sm);

            int nuevaCantidad = producto.getCantidad() - cantidad;

            return nuevaCantidad < 30;
        };
    }

    private Guard<ProductoState, ProductoEvent> sinStock() {
        return stateContext -> {
            StateMachine<ProductoState, ProductoEvent> sm = stateContext.getStateMachine();

            Producto producto = this.getProducto(sm);
            Integer cantidad = this.getCantidad(sm);

            int nuevaCantidad = producto.getCantidad() - cantidad;

            return nuevaCantidad == 0;
        };
    }

    private Producto getProducto(StateMachine<ProductoState, ProductoEvent> sm) {
        return sm.getExtendedState().get("producto", Producto.class);
    }

    private Integer getCantidad(StateMachine<ProductoState, ProductoEvent> sm) {
        return sm.getExtendedState().get("cantidad", Integer.class);
    }
}
