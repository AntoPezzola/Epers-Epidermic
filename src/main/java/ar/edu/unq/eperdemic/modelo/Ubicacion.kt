package ar.edu.unq.eperdemic.modelo


import javax.persistence.*

@Entity

class Ubicacion() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(nullable = false, unique = true, length = 100)
    lateinit var nombre: String

    constructor(nombre: String) : this() {
        this.nombre = nombre
    }

    constructor(unaUbicacionNeo: UbicacionNeo): this(){
        this.nombre =unaUbicacionNeo.nombre!!
        this.id = unaUbicacionNeo.id
    }
}