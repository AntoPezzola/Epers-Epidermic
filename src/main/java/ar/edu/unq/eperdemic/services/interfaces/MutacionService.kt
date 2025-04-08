package ar.edu.unq.eperdemic.services.interfaces

import ar.edu.unq.eperdemic.modelo.MutacionV


interface MutacionService {
    fun agregarMutacion(especieId:Long, mutacion: MutacionV) : MutacionV// Agrega la mutacion a la lista de posibles mutaciones de la especie.

    fun crearMutacion(mutacion: MutacionV):MutacionV
    fun clearAll()

}
