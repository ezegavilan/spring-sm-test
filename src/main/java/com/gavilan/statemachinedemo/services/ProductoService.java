package com.gavilan.statemachinedemo.services;

import com.gavilan.statemachinedemo.ProductoException;
import com.gavilan.statemachinedemo.dominio.Producto;
import com.gavilan.statemachinedemo.dominio.ProductoEvent;
import com.gavilan.statemachinedemo.dominio.ProductoState;
import com.gavilan.statemachinedemo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: Eze Gavilán
 **/

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductoService {
    public static final String PRODUCTO_ID_HEADER = "producto_id";

    private final ProductoRepository productoRepository;
    private final StateMachineService stateMachineService;

    @Transactional
    public Producto crearProducto(Producto producto) {
        producto.setEstado(ProductoState.EN_STOCK);
        return productoRepository.save(producto);
    }

    @Transactional
    public void actualizarStock(Long productoId, Integer cantidad) {

        Producto producto = this.productoRepository.getOne(productoId);

        StateMachine<ProductoState, ProductoEvent> sm = stateMachineService.build(productoId);

        sm.getExtendedState().getVariables().put("producto", producto);
        sm.getExtendedState().getVariables().put("cantidad", cantidad);

        if (producto.getCantidad() - cantidad < 0) {
            throw new ProductoException("La cantidad de productos no puede ser menor a 0");
        }

        log.info("Estado antes de actualizar: " + sm.getState().getId());

        stateMachineService.sendEvent(productoId, sm, ProductoEvent.ACTUALIZAR_STOCK);

        log.info("Estado Actual: " + sm.getState().getId());
    }
    

}
