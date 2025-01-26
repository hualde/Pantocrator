package com.example.pantocrator.rosary

data class RosaryMystery(
    val title: String,
    val reflection: String,
    val shortMeditations: List<String> // Una meditación corta para cada Avemaría
)

class RosaryReflections {
    // Misterios Gozosos
    val joyfulMysteries = listOf(
        RosaryMystery(
            "La Anunciación",
            "El ángel Gabriel anuncia a María que será la madre del Hijo de Dios.",
            List(10) { "Contemplemos la humildad y obediencia de María al aceptar la voluntad de Dios." }
        ),
        RosaryMystery(
            "La Visitación",
            "María visita a su prima Isabel, llevando a Jesús en su vientre.",
            List(10) { "Meditemos sobre el servicio y amor al prójimo que María nos enseña." }
        ),
        RosaryMystery(
            "El Nacimiento de Jesús",
            "Jesús nace en Belén, trayendo la luz al mundo.",
            List(10) { "Contemplemos la humildad del nacimiento de nuestro Salvador." }
        ),
        RosaryMystery(
            "La Presentación",
            "María y José presentan al niño Jesús en el templo.",
            List(10) { "Reflexionemos sobre la obediencia a la ley de Dios." }
        ),
        RosaryMystery(
            "El Niño Jesús en el Templo",
            "Jesús es encontrado en el templo enseñando a los doctores.",
            List(10) { "Meditemos sobre la sabiduría divina de Jesús." }
        )
    )

    // Misterios Dolorosos
    val sorrowfulMysteries = listOf(
        RosaryMystery(
            "La Agonía en el Huerto",
            "Jesús ora en Getsemaní, aceptando la voluntad del Padre.",
            List(10) { "Contemplemos la angustia de Jesús y su total entrega a la voluntad del Padre." }
        ),
        RosaryMystery(
            "La Flagelación",
            "Jesús es azotado por nuestros pecados.",
            List(10) { "Meditemos sobre el sufrimiento que nuestros pecados causan a Jesús." }
        ),
        RosaryMystery(
            "La Coronación de Espinas",
            "Jesús es coronado con espinas y burlado.",
            List(10) { "Reflexionemos sobre la humillación que Jesús soportó por nosotros." }
        ),
        RosaryMystery(
            "Jesús con la Cruz a Cuestas",
            "Jesús carga su cruz hacia el Calvario.",
            List(10) { "Contemplemos cómo Jesús abraza su cruz por amor a nosotros." }
        ),
        RosaryMystery(
            "La Crucifixión",
            "Jesús muere en la cruz por nuestra salvación.",
            List(10) { "Meditemos sobre el amor infinito de Jesús que da su vida por nosotros." }
        )
    )

    // Misterios Gloriosos
    val gloriousMysteries = listOf(
        RosaryMystery(
            "La Resurrección",
            "Jesús resucita victorioso sobre la muerte.",
            List(10) { "Contemplemos la victoria de Cristo sobre la muerte y el pecado." }
        ),
        RosaryMystery(
            "La Ascensión",
            "Jesús asciende al cielo en presencia de sus discípulos.",
            List(10) { "Meditemos sobre nuestra esperanza de vida eterna." }
        ),
        RosaryMystery(
            "La Venida del Espíritu Santo",
            "El Espíritu Santo desciende sobre los apóstoles.",
            List(10) { "Reflexionemos sobre los dones del Espíritu Santo en nuestras vidas." }
        ),
        RosaryMystery(
            "La Asunción de María",
            "María es llevada al cielo en cuerpo y alma.",
            List(10) { "Contemplemos la glorificación de María, nuestra madre." }
        ),
        RosaryMystery(
            "La Coronación de María",
            "María es coronada como Reina del cielo y la tierra.",
            List(10) { "Meditemos sobre la intercesión de María por nosotros." }
        )
    )

    // Misterios Luminosos
    val luminousMysteries = listOf(
        RosaryMystery(
            "El Bautismo de Jesús",
            "Jesús es bautizado por Juan en el Jordán.",
            List(10) { "Contemplemos nuestro propio bautismo y renovación espiritual." }
        ),
        RosaryMystery(
            "Las Bodas de Caná",
            "Jesús realiza su primer milagro por intercesión de María.",
            List(10) { "Meditemos sobre la intercesión de María y el poder de Jesús." }
        ),
        RosaryMystery(
            "El Anuncio del Reino",
            "Jesús predica el Reino de Dios y llama a la conversión.",
            List(10) { "Reflexionemos sobre nuestra llamada a la conversión." }
        ),
        RosaryMystery(
            "La Transfiguración",
            "Jesús revela su gloria divina en el monte.",
            List(10) { "Contemplemos la gloria divina de Cristo." }
        ),
        RosaryMystery(
            "La Institución de la Eucaristía",
            "Jesús instituye la Eucaristía en la Última Cena.",
            List(10) { "Meditemos sobre el don de la Eucaristía en nuestras vidas." }
        )
    )

    fun getMysteryForDay(dayOfWeek: Int, mysteryIndex: Int): RosaryMystery {
        return when (dayOfWeek) {
            1, 6 -> joyfulMysteries[mysteryIndex] // Lunes y Sábado
            2, 5 -> sorrowfulMysteries[mysteryIndex] // Martes y Viernes
            3, 7 -> gloriousMysteries[mysteryIndex] // Miércoles y Domingo
            4 -> luminousMysteries[mysteryIndex] // Jueves
            else -> joyfulMysteries[mysteryIndex]
        }
    }
} 