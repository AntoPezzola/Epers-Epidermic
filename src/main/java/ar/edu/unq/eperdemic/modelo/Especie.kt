package ar.edu.unq.eperdemic.modelo
import ar.edu.unq.eperdemic.modelo.exceptions.ErrorNoHayVectoresEnLaUbicacion
import org.hibernate.annotations.Proxy
import org.springframework.transaction.annotation.Transactional
import javax.persistence.*
import kotlin.random.Random

@Entity
class Especie() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var nombre: String? = null

    @ManyToOne
    // @JoinColumn(name = "paisDeOrigen") // Asegúrate de que el nombre de la columna sea el correcto
    var paisDeOrigen: Ubicacion? = null

    @ManyToOne
    var patogeno: Patogeno? = null

    @ManyToMany(mappedBy = "infecciones", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var vectores: MutableList<Vector> = mutableListOf()

    @OneToMany(mappedBy = "especie", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var mutaciones: MutableList<MutacionV> = mutableListOf()

    constructor(patogeno: Patogeno?, nombreEspecie: String, ubicacion: Ubicacion) : this() {
        this.patogeno = patogeno
        this.nombre = nombreEspecie
        this.paisDeOrigen = ubicacion
    }


    fun infectarUnVectorAlAzar(vectores: List<Vector>) {
        val unVectorAlAzar = elegirVectorAlAzar(vectores)
        unVectorAlAzar.infectar(this)
    }

    fun elegirVectorAlAzar(vectores: List<Vector>): Vector {
        val vectoresDeUnaUbicacion = vectores
        if (vectoresDeUnaUbicacion.isEmpty()) {
            throw ErrorNoHayVectoresEnLaUbicacion()
        }
        return vectoresDeUnaUbicacion.random()
    }

    private fun capacidadDeContagioDeTipoDeVector(tipoVector: TipoVector): Int {
        val capacidadDeContagio: Int
        if (tipoVector == TipoVector.HUMANO) {
            capacidadDeContagio = patogeno!!.capacidadDeContagioHumano!!
        } else if (tipoVector == TipoVector.ANIMAL) {
            capacidadDeContagio = patogeno!!.capacidadDeContagioAnimal!!
        } else {
            capacidadDeContagio = patogeno!!.capacidadDeContagioInsecto!!
        }
        return capacidadDeContagio
    }

    fun cumpleLasProbabilidades(vectorAInfectar: Vector):Boolean {
        val pocentajeDeContagioExitoso = Random.nextInt(1, 11) + capacidadDeContagioDeTipoDeVector(vectorAInfectar.tipoVector)
        return pocentajeDeContagioExitoso >= 70
    }

    private fun capacidadDeBiomecanizacion(): Long {
        return patogeno!!.capacidadDeBiomecanizacion!!.toLong()
    }

    fun infectarVector(vectorAInfectar: Vector, vectorAtacante:Vector) {
        if(vectorAInfectar.superaLasPotenciasDeMutacion(this) && cumpleLasProbabilidades(vectorAInfectar)){
            vectorAInfectar.infectar(this)
            intentarMutar(vectorAtacante)
        }
    }

    fun intentarMutar(vectorAMutar:Vector){
        if (this.capacidadDeBiomecanizacion() >= 70 && mutaciones.isNotEmpty()){
            mutarVectorConMutacionAlAzar(vectorAMutar)
        }
    }

    fun mutarVectorConMutacionAlAzar(vector: Vector){
        val mutacion = mutaciones.random()
        if(!vector.tieneMutacion(mutacion)){
            mutacion.mutarAVector(vector)
        }
    }
    fun tieneDefensaMayorV(potencia: Long): Boolean {
        return this.defensaContraMicroOrganismos() > potencia
    }

    fun defensaContraMicroOrganismos(): Long {
        return patogeno!!.defensaContraMicroorganismos!!.toLong()
    }

    fun añadirMutacion(mutacion: MutacionV){
        this.mutaciones.add(mutacion)
    }

}
