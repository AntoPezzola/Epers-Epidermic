package ar.edu.unq.eperdemic.persistencia.dao.interfaces

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

interface EstadisticaDAO {
    fun especieLider(): Especie
//    especieLider(): Especie retorna la especie que haya infectado a más humanos
    fun lideres(): List<Especie>
    //retorna las especies que hayan infectado la mayor cantidad total de vectores humanos y animales combinados en orden descendente.


}
