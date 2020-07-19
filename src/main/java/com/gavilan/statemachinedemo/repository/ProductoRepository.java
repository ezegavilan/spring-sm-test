package com.gavilan.statemachinedemo.repository;

import com.gavilan.statemachinedemo.dominio.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
