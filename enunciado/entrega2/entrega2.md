## Entrega 2 - ORM HIBERNATE

## De vuelta con los científicos del laboratorio

Triunfantes y llenos de algarabía, luego de haber cumplido todos y cada uno de los minuciosos requerimientos que los científicos les habían impuesto, vuelven al laboratorio donde se encuentran con una situación tanto particular.

Caos y desorden por todos lados, científicos corriendo de un lado a otro llevando torres de papelerio de aquí allá; Profesionales en batas blancas desplomados sobre sus escritorios por el cansancio; Vasitos de plástico tirados en el piso, aun mojados con café.

Un científico con los ojos algo cansados y con un guante negro que tapa todo su brazo derecho se les acerca para bienvenirlos y se presenta como el lider cientifico a cargo del proyecto en el laboratorio. Sin mucha pausa, pasa a  comentarles que desde la última entrega, han estado trabajando sin parar con la simulación y realizando descubrimientos realmente importantes.

Sin dejarlos ni siquiera contestar y con un movimiento brusco, los empuja hacia la sala de proyecciones, donde el lider científico si bien cansado, comienza a compartir su conocimiento de forma muy entusiasmada. Esto para ustedes solo significa una cosa.... más requerimientos.

## Cambios desde el TP anterior

Se identificaron una serie de cambios necesarios a hacerse sobre la prueba de concepto anterior:
La capa de persistencia deberá cambiarse para utilizar Hibernate/JPA en lugar de JDBC.
**Nota:** No es necesario que mantengan los test y funcionalidad utilizando JDBC.

## Funcionalidad

<p align="center">
  <img src="virus.png" />
</p>

Una vez en la sala de proyectores, los cientificos profundizan contándonos más sobre esta suerte de patogenos biomecanicos, las cuales nos interesan por los atributos que los caracterizan.

### Atributos

- Capacidad de contagio, que puede ser por persona, animales o insectos.
- Defensa contra otros micro-organismos
- Capacidad de biomecanizacion, cual es el grado con el que convierte la carne y materia biologica del infectado en componentes mecanizados.
Una capacidad baja puede resultar en el remplazo del cabello por hilos de cobre. Una capacidad alta puede resultar en todo el cuerpo convirtiendose en una masa de cables, tubos y y compnentes mecanizados sin similutd alguna a un humano. 

Cada uno de estas atributos representa con un valor numérico del 1 al 100.

### Vectores

<p align="center">
  <img src="contagio.png" />
</p>

Les enseñan que un patógeno se esparce a través de vectores, que son los agentes que transportan y transmiten un patógeno a otro organismo vivo. 

Estos pueden ser Humanos, Animales, o Insectos.

### Ubicación

Los vectores pueden moverse de ubicación en ubicación.
Toda ubicación tendrá un nombre que deberá ser único.

### Contagio

Un vector podrá poner en riesgo de contagio a otro vector respetando las siguientes normas:

- Un humano puede ser contagiado por otro humano, un insecto o un animal.
- Un animal solo puede ser contagiado por un insecto.
- Un insecto solo puede ser contagiado por un humano o un animal.

La probabilidad que un contagio de un vector a otro sea exitoso se resolverá de la siguiente forma.
Tiene como base un número entre el 1 y el 10
A Este número se le suma el atributo de contagio de la especie relacionado al vector que se está intentando infectar  
Esto se traduce a:

`porcentajeDeContagioExitoso = (random(1, 10)) + capacidadDeContagio`

Con este porcentaje, deberá determinarse si el contagio fue exitoso o no. Si lo fue, el nuevo vector pasa de estar Sano a estar Infectado.

**Nota:** Un vector puede estar infectado por varias especies de patógenos.


## Servicios

Se pide que implementen los siguientes servicios los cuales serán consumidos por el frontend de la aplicación.


### VectorService

- `Crear, Updatear, Recuperar y Recuperar todos`

- `infectar(vectorId: Long, especieId: Long)`  Se infecta al vector con la especie

- `enfermedades(vectorId: Long): List<Especie> `  Dado un vector retorna todas las especies que esta padeciendo.


### UbicacionService

- `Crear, Updatear, Recuperar y Recuperar todos`

- `mover(vectorId: Long, ubicacionId: Long)` Mueve un vector de la ubicación en la que se encontraba a una nueva. Si el vector está infectado, intentara contagiar a todos los vectores presentes en la nueva locación. **Nota:** Leer VectorService.

- `expandir( ubicacionId: Long)` Dada una ubicación, deberá tomar un vector contagiado **elegido al azar**. Ese vector debe intentar contagiar a todos los otros vectores presentes en el mismo lugar. De no haber ningún vector contagiado en el lugar, no hace nada. **Nota:** Leer VectorService


### PatogenoService

- `Crear, Updatear, Recuperar y Recuperar todos`

- `agregarEspecie(patogenoid: Long, nombreEspecie: String, ubicacionId: Long) : Especie` - Deberá lograr que se genere una nueva Especie del Patogeno, e infectar a un vector al azar en la ubicacion dada. Si no hay ningun vector en dicha ubicacion, lanzar una excepcion.

- `especiesDePatogeno(patogenoId: Long ): List<Especie>` devuelve las especies del patogeno

- `esPandemia(especieId: Long): Boolean` Devuelve true si la especie se encuentra presente más de la mitad de todas las locaciones disponibles


### EspecieService

- `Updatear, Recuperar y Recuperar todos`

- `cantidadDeInfectados(especieId: Long ): Int` devuelve la cantidad de vectores infectados por la especie


### EstadisticaService

- `especieLider(): Especie` retorna la especie que haya infectado a más humanos

- `lideres(): List<Especie>` retorna las especies que hayan infectado la mayor cantidad total de vectores humanos y animales combinados en orden descendente.

- `reporteDeContagios(nombreDeLaUbicacion: String): ReporteDeContagios` Dada una ubicacion, debera retornar un objeto reporte que contenga
    - La cantidad de vectores presentes.
    - Cantidad de vectores infectados.
    - Nombre de la especie que esta infectando a mas vectores.

### Se pide:

- Que provean implementaciónes para las interfaces descriptas anteriormente.

- Que modifiquen el mecanismo de persistencia de Patógeno de forma de que todo el modelo persistente utilice Hibernate.

- Asignen propiamente las responsabilidades a todos los objetos intervinientes, discriminando entre servicios, DAOs y objetos de negocio.

- Creen test que prueben todas las funcionalidades pedidas, con casos favorables y desfavorables.

- Que los tests sean determinísticos. Hay mucha lógica que depende del resultado de un valor aleatorio. Se aconseja no utilizar directamente generadores de valores aleatorios (random) sino introducir una interfaz en el medio para la cual puedan proveer una implementación mock determinística en los tests.

### Recuerden que:
- Pueden agregar nuevos métodos y atributos a los objetos ya provistos

### Consejos útiles:

- Finalicen los métodos de los services de uno en uno. Que quiere decir esto? Elijan un service, tomen el método más sencillo que vean en ese service, y encárguense de desarrollar la capa de modelo, de servicios y persistencia solo para ese único método. Una vez finalizado (esto también significa testeado), pasen al próximo método y repitan.

- Cuando tengan que persistir con hibernate, analicen:
  Qué objetos deben ser persistentes y cuáles no?
  Cuál es la cardinalidad de cada una de las relaciones? Como mapearlas?

## Bonus: Paginación

Se disponen a retirarse del laboratorio después de lo que parecieron horas de perorata y sinsentido científico, cuando ya a pocos metros de la puerta, antes de cruzar el umbral, chocan violentamente con un joven científico cargando con una torre de papeles que oscurece aún más su ya escasa visión.

Contrastando con las robustas espaldas de los programadores —una característica mundialmente asociada al oficio- al chocar, el joven científico de lentes grandes y redondos es catapultado hacia atrás, dando media vuelta carnero en el aire antes de caer de traste contra el suelo y lanzando por los aires su carga en el proceso.

En un gesto de amabilidad, mueven la compasión en dirección a la humanidad y se detienen, ofreciendo un momento de cortesía para recolectar los fragmentos de la burocracia científica. Comentario va, comentario viene, aprenden que es el asistente y protegido del líder científico, y que sus compañeros le han asignado la ingrata tarea de ser el cartero del laboratorio; un poco a modo de broma, otro poco simplemente para aprovecharse de su timidez, pero principalmente para sortear una complicación técnica del sistema del laboratorio.

Con didáctica y dejando entrever un pequeño tinte de orgullo por su conocimiento, les explica que las computadoras están tan sobrecargadas con los datos que tienen que procesar para realizar las simulaciones que para aliviar a estos titanes electrónicos de última generación se transcriben resultados a papel, que luego deben ser archivados y analizados de manera manual.

El joven ayudante suspira, pero ya con su pesada carga nuevamente a cuestas, procede a marcharse no sin disculparse nuevamente por las molestias. Ustedes se miran y una pregunta flota en el aire, suspendida en el espacio que deja la torpe y pequeña silueta que se aleja: ¿habrá alguna forma de ayudarlo? Tal vez, si las computadoras no estuvieran tan sobrecargadas de datos, no haría falta tanto análisis manual.

## Implementacion bonus

Se nos pide agregar paginacion al metodo especiesDePatogeno del PatogenoService y al metodo lideres de EstadisticaService.

Se agregara a la firma de estos metodos una página, y una dirección que puede ser : ASCENDENTE o DESCENDENTE

- `especiesDePatogeno(patogenoId: Long, direccion:Direccion, pagina:Int, cantidadPorPagina:Int ): List<Especie>` devuelve las especies del patogeno, respetando la direccion, la pagina y cantidad declaradas en la firma del metodo.

- `lideres(direccion:Direccion, pagina:Int, cantidadPorPagina:Int): List<Espcie>` - Retorna las especies que hayan infectado la mayor cantidad total de vectores humanos y animales combinados, respetando la direccion, la pagina y cantidad declaradas en la firma del metodo.

Un ejemplo: lideres(ASCENDENTE, 0, 5): Retorna las primeras 5 especies lideres. 

## Consideraciones:
El bonus no es necesario para aprobar, pero de ser implementado correctamente sumará nota. Una mala implementación NO restará nota, aún así, recuerden no invertir esfuerzos en el bonus a costa de la implementación principal del TP que es donde las correcciones si afectan la nota final.
