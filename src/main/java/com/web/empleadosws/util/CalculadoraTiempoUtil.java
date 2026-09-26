package com.web.empleadosws.util;

import com.web.empleadosws.dto.TiempoDTO;

import java.time.LocalDate;
import java.time.Period;

public class CalculadoraTiempoUtil {

    private CalculadoraTiempoUtil() {

    }

    public static TiempoDTO calcular(LocalDate desde, LocalDate hasta) {
        Period periodo = Period.between(desde, hasta);
        TiempoDTO tiempo = new TiempoDTO();
        tiempo.setAnios(periodo.getYears());
        tiempo.setMeses(periodo.getMonths());
        tiempo.setDias(periodo.getDays());
        return tiempo;
    }

}
