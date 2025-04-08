package ar.edu.unq.eperdemic.modelo
import javax.persistence.*
import javax.validation.constraints.*

@Entity

class MutacionV() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ManyToOne
    lateinit var especie : Especie
    @NotNull(message = "tipoMutacion not null")
    var tipoMutacion : TipoMutacion? = null

    var tipoDeVector : TipoVector? = null

    @field:Min(1, message = "La potencia de mutación debe ser mayor o igual a 1")
    @field:Max(100, message = "La potencia de mutación debe ser menor o igual a 100")
    var potenciaDeMutacion: Long? = null

    constructor (tipoVector: TipoVector) : this(){
        this.tipoDeVector = tipoVector
        this.tipoMutacion = TipoMutacion.BIOALTGENETICA

    }
    constructor(potMutacion : Long):this(){
        this.tipoMutacion = TipoMutacion.SUPBIOMECANICA
        this.potenciaDeMutacion = potMutacion
    }
    constructor (tipoVector: TipoVector, tipoDeMutacion: TipoMutacion) : this(){
        this.tipoDeVector = tipoVector
        this.tipoMutacion = tipoDeMutacion

    }

    fun mutarAVector(vector:Vector){
        vector.agregarMutacion(this)
        if(tipoMutacion == TipoMutacion.SUPBIOMECANICA ) {
            vector.infecciones = vector.infecciones.filter { e -> e.tieneDefensaMayorV(potenciaDeMutacion!!) || e.id == especie.id}.toMutableList()        }
    }


    fun addEspecie(especie: Especie) {
        this.especie = especie
    }

}