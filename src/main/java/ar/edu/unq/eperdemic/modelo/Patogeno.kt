package ar.edu.unq.eperdemic.modelo

import org.hibernate.annotations.Check
import java.io.Serializable
import javax.persistence.*

@Entity
class Patogeno() : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Check(constraints = "capacidadDeContagioHumano <= 100")
    var capacidadDeContagioHumano: Int? = null
    @Check(constraints = "capacidadDeContagioInsecto <= 100")
    var capacidadDeContagioInsecto: Int? = null
    @Check(constraints = "capacidadDeContagioAnimal <= 100")
    var capacidadDeContagioAnimal: Int? = null
    @Check(constraints = "cantidadDeEspecies <= 100")
    var cantidadDeEspecies: Int = 0
    @Check(constraints = "defensaContraMicroorganismos <= 100")
    var defensaContraMicroorganismos: Int? = null
    @Check(constraints = "capacidadDeBiomecanizacion <= 100")
    var capacidadDeBiomecanizacion: Int? = null

    @OneToMany(mappedBy = "patogeno", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var especies: MutableList<Especie> = mutableListOf()

    @Column(unique = true, length = 100, nullable = false)
    var tipo: String? = null


    constructor(
        tipo: String, capacidadDeContagioHumano: Int,
        capacidadDeContagioAnimal: Int,
        capacidadDeContagioInsecto: Int,
        defensaContraMicroorganismos: Int,
        capacidadDeBiomecanizacion: Int
    ) : this() {
        this.tipo = tipo
        this.capacidadDeContagioHumano = capacidadDeContagioHumano
        this.capacidadDeContagioInsecto = capacidadDeContagioInsecto
        this.capacidadDeContagioAnimal = capacidadDeContagioAnimal
        this.capacidadDeBiomecanizacion = capacidadDeBiomecanizacion
        this.defensaContraMicroorganismos = defensaContraMicroorganismos

    }

    override fun toString(): String {
        return tipo!!
    }

    fun crearEspecie(nombreEspecie: String, ubicacion: Ubicacion): Especie {
        val unaEspecie = Especie(this, nombreEspecie, ubicacion)
        cantidadDeEspecies++
        especies.add(unaEspecie)
        return unaEspecie
    }
}