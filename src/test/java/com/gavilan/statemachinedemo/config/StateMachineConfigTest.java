package com.gavilan.statemachinedemo.config;

import com.gavilan.statemachinedemo.dominio.ProductoEvent;
import com.gavilan.statemachinedemo.dominio.ProductoState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<ProductoState, ProductoEvent> factory;

    @Test
    void testNewStateMachine() {
        StateMachine<ProductoState, ProductoEvent> sm = factory.getStateMachine();

        sm.start();

        System.out.println(sm.getState().toString());

        sm.sendEvent(ProductoEvent.ACTUALIZAR_STOCK);

        System.out.println(sm.getState().toString());

    }

}