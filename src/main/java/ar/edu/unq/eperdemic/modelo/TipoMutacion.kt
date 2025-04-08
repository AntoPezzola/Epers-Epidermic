package ar.edu.unq.eperdemic.modelo

enum class TipoMutacion {

            BIOALTGENETICA {
                override fun caminosPermitidos(): List<TipoCamino> {
                    var tiposDeCaminos: MutableList<TipoCamino> = mutableListOf()

                    return tiposDeCaminos
                }
            },
            SUPBIOMECANICA {
                override fun caminosPermitidos(): List<TipoCamino> {
                    var tiposDeCaminos: MutableList<TipoCamino> = mutableListOf()

                    return tiposDeCaminos
                }
            },
            ELECTROBRANQUEAS {
                override fun caminosPermitidos(): List<TipoCamino> {
                    var tiposDeCaminos: MutableList<TipoCamino> = mutableListOf()

                    tiposDeCaminos.add(TipoCamino.CAMINOMARITIMO)

                    return tiposDeCaminos
                }
            },
            PROPULSIONMOTORA {
                override fun caminosPermitidos(): List<TipoCamino> {
                    var tiposDeCaminos: MutableList<TipoCamino> = mutableListOf()

                    tiposDeCaminos.add(TipoCamino.CAMINOAEREO)

                    return tiposDeCaminos
                }
            };


    abstract fun caminosPermitidos(): List<TipoCamino>


}
