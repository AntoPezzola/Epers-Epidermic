package ar.edu.unq.eperdemic.modelo

enum class TipoVector {

    HUMANO {
        override fun puedeAtravesar(tipoCamino: TipoCamino): Boolean {
            return when (tipoCamino) {
                TipoCamino.CAMINOTERRESTRE, TipoCamino.CAMINOMARITIMO -> true
                TipoCamino.CAMINOAEREO -> false
            }
        }

        override fun caminosPermitidos(): List<TipoCamino> {
            var tiposDeCaminos: MutableList<TipoCamino> = mutableListOf()

            tiposDeCaminos.add(TipoCamino.CAMINOTERRESTRE)
            tiposDeCaminos.add(TipoCamino.CAMINOMARITIMO)

            return tiposDeCaminos
        }
    },
    ANIMAL {
        override fun puedeAtravesar(tipoCamino: TipoCamino): Boolean {
            return when (tipoCamino) {
                TipoCamino.CAMINOTERRESTRE, TipoCamino.CAMINOMARITIMO -> true
                TipoCamino.CAMINOAEREO -> false
            }
        }

        override fun caminosPermitidos(): List<TipoCamino> {
            var tiposDeCaminos: MutableList<TipoCamino> = mutableListOf()

            tiposDeCaminos.add(TipoCamino.CAMINOTERRESTRE)
            tiposDeCaminos.add(TipoCamino.CAMINOAEREO)
            tiposDeCaminos.add(TipoCamino.CAMINOMARITIMO)

            return tiposDeCaminos
        }
    },
    INSECTO {
        override fun puedeAtravesar(tipoCamino: TipoCamino): Boolean {
            return when (tipoCamino) {
                TipoCamino.CAMINOTERRESTRE -> true
                TipoCamino.CAMINOMARITIMO, TipoCamino.CAMINOAEREO -> false
            }
        }
        override fun caminosPermitidos(): List<TipoCamino> {
            var tiposDeCaminos: MutableList<TipoCamino> = mutableListOf()

            tiposDeCaminos.add(TipoCamino.CAMINOTERRESTRE)
            tiposDeCaminos.add(TipoCamino.CAMINOAEREO)

            return tiposDeCaminos
        }


    };

    abstract fun puedeAtravesar(tipoCamino: TipoCamino): Boolean
    abstract fun caminosPermitidos(): List<TipoCamino>
}