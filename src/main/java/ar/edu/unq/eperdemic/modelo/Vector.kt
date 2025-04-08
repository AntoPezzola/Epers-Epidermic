package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.modelo.exceptions.UbicacacionNoAlcanzable
import javax.persistence.*

@Entity
class Vector() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, length = 500)
    lateinit var tipoVector: TipoVector

    @OneToOne
    lateinit var ubicacion: Ubicacion

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    lateinit var infecciones: MutableList<Especie>

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var mutaciones: MutableList<MutacionV> = mutableListOf()


    constructor(tipoVector: TipoVector, ubicacion: Ubicacion) : this() {
        this.tipoVector = tipoVector
        this.ubicacion = ubicacion
        infecciones = mutableListOf()
    }

    fun estaSano(): Boolean {
        return infecciones.isEmpty()
    }

    fun puedeContagiar(vector1: Vector): Boolean {
        return when (vector1.tipoVector) {
            TipoVector.HUMANO -> {
                when (this.tipoVector) {
                    TipoVector.HUMANO, TipoVector.ANIMAL, TipoVector.INSECTO -> true
                    else -> false
                }
            }

            TipoVector.ANIMAL -> {
                when (this.tipoVector) {
                    TipoVector.INSECTO -> true
                    else -> false
                }
            }

            TipoVector.INSECTO -> {
                when (this.tipoVector) {
                    TipoVector.HUMANO, TipoVector.ANIMAL -> true
                    else -> false
                }
            }

            else -> false
        }
    }

    fun poseeLaInfeccion(unaEspecie: Especie): Boolean {
        return infecciones.any {it.nombre == unaEspecie.nombre}
    }

    fun intentarContagiar(unVector: Vector) {
        infecciones.forEach { especie: Especie -> contagiar(unVector, especie)}
    }

    fun moverEInfectar(ubicacionDestino: Ubicacion, vectoresDeUbicacionAInfectar: List<Vector>) {
        ubicacion = ubicacionDestino
        intentarContagiarAVectores(vectoresDeUbicacionAInfectar)
    }
    fun intentarContagiarAVectores(vectoresDeUbicacionAInfectar: List<Vector>) {
        vectoresDeUbicacionAInfectar.forEach {vector: Vector ->  intentarContagiar(vector)}
    }


//IMPLEMENTACION NEO4J

    fun posiblesRecorridosSegunTipoVector():MutableList<String> {
        return when (this.tipoVector) {
            TipoVector.HUMANO -> mutableListOf(TipoCamino.CAMINOMARITIMO.toString(), TipoCamino.CAMINOTERRESTRE.toString())
            TipoVector.INSECTO -> mutableListOf(TipoCamino.CAMINOAEREO.toString(), TipoCamino.CAMINOTERRESTRE.toString())
            TipoVector.ANIMAL -> mutableListOf(TipoCamino.CAMINOMARITIMO.toString(), TipoCamino.CAMINOAEREO.toString(), TipoCamino.CAMINOTERRESTRE.toString())
            else -> mutableListOf()
        }
    }


    fun puedeContagiarA(vector:Vector, especie: Especie):Boolean{

        return tengoAlteracionGeneticaValida(especie, vector.tipoVector)
                || puedeContagiar(vector)
    }

    fun tengoAlteracionGeneticaValida(especie: Especie, tipoVector: TipoVector):Boolean{
        val altGenetica =  mutaciones.find{ it.tipoMutacion == TipoMutacion.BIOALTGENETICA  && it.especie.id == especie.id}

        return altGenetica != null && altGenetica.tipoDeVector == tipoVector
    }

    fun contagiar(vector: Vector, especie: Especie):Vector{
        if(puedeContagiarA(vector, especie)){
            especie.infectarVector(vector, this)
        }
        return this
    }



    fun infectar(especieRecuperada: Especie) {
        this.infecciones.add(especieRecuperada)
    }

    fun tieneMutacion(mutacion: MutacionV): Boolean {
        return mutaciones.any{it.id == mutacion.id}
    }

    fun agregarMutacion(mutacion: MutacionV) {
        mutaciones.add(mutacion)
    }

    fun superaLasPotenciasDeMutacion(especie: Especie): Boolean {
       val mutacionesSupresionBioMecanica:List<MutacionV> = mutaciones.filter { m -> m.tipoMutacion == TipoMutacion.SUPBIOMECANICA }
        return mutacionesSupresionBioMecanica.all { mutacion ->
            mutacion.potenciaDeMutacion!! <= especie.defensaContraMicroOrganismos()}
    }

    fun caminosPermitidos(): List<TipoCamino> {
        val caminosPermitidosDeVector   = this.tipoVector.caminosPermitidos()
        var caminosPermitidosDeMutacion: MutableList<TipoCamino> = mutableListOf()
        this.mutaciones.forEach { caminosPermitidosDeMutacion.addAll(it.tipoMutacion!!.caminosPermitidos())}
        caminosPermitidosDeMutacion.addAll(caminosPermitidosDeVector)
        return caminosPermitidosDeMutacion
    }
}

