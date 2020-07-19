package com.gavilan.statemachinedemo.services;

import com.gavilan.statemachinedemo.dominio.Producto;
import com.gavilan.statemachinedemo.dominio.ProductoState;
import com.gavilan.statemachinedemo.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ProductoServiceTest {

    @Autowired
    ProductoService productoService;

    @Autowired
    ProductoRepository productoRepository;

    Producto producto;

    @BeforeEach
    void setUp() {
        producto = Producto.builder().nombre("Alfajor").precio(20.0).cantidad(200).build();
    }

    @Transactional
    @RepeatedTest(10)
    void actualizarStock() {
        int cantidad;

        cantidad = (int) (Math.random() * 250) + 1;

        Producto productoGuardado = productoService.crearProducto(producto);

        log.info(productoGuardado.toString());

        log.info("Cantidad = ".concat(Integer.toString(cantidad)));

        assertFalse(cantidad > 200);

        productoService.actualizarStock(productoGuardado.getId(), cantidad);

        Producto prodNuevoStock = productoRepository.getOne(productoGuardado.getId());

        log.info(prodNuevoStock.toString());

        if (prodNuevoStock.getCantidad() == 0) {
            assertSame(prodNuevoStock.getEstado(), ProductoState.SIN_STOCK);

            assertSame(prodNuevoStock.getEstado(), ProductoState.SIN_STOCK);
        }

        if (prodNuevoStock.getCantidad() < 30) {
            assertSame(prodNuevoStock.getEstado(), ProductoState.STOCK_MINIMO);

            assertSame(prodNuevoStock.getEstado(), ProductoState.STOCK_MINIMO);
        }

        if (prodNuevoStock.getCantidad() >= 30) {
            assertSame(prodNuevoStock.getEstado(), ProductoState.EN_STOCK);

            assertSame(prodNuevoStock.getEstado(), ProductoState.EN_STOCK);
        }


    }
}