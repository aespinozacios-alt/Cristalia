package com.example.domain.model

import androidx.compose.ui.graphics.Color
import com.example.R

object GameCatalog {

    val characters: List<CharacterHero> = listOf(
        CharacterHero(
            id = "lyra",
            name = "Lyra",
            title = "Tejedora de Estrellas",
            lore = "Custodia de los antiguos manuscritos astrales, Lyra escucha la música silenciosa que une cada constelación.",
            affinityJewel = JewelType.AMETHYST,
            passiveTitle = "Armonía Estelar",
            passiveDesc = "+15% de puntuación en todas las líneas y aparición frecuente de Amatistas.",
            activePowerTitle = "Luz Astral",
            activePowerDesc = "Disuelve los bloques del color más abundante en el tablero.",
            portraitResId = R.drawable.img_char_lyra,
            accentColor = Color(0xFFB689FF)
        ),
        CharacterHero(
            id = "kael",
            name = "Kael",
            title = "Guardián del Bosque",
            lore = "Erudito de las raíces milenarias, Kael calma las tormentas del mundo con paciencia inquebrantable.",
            affinityJewel = JewelType.EMERALD,
            passiveTitle = "Paso Sereno",
            passiveDesc = "La caída de las piezas es un 12% más pausada y obtienes Esmeraldas con mayor frecuencia.",
            activePowerTitle = "Brisa Temporal",
            activePowerDesc = "Activa 12s de cámara lenta y despeja la fila inferior del tablero.",
            portraitResId = R.drawable.img_char_kael,
            accentColor = Color(0xFF5CE1B8)
        ),
        CharacterHero(
            id = "sora",
            name = "Sora",
            title = "Alquimista de Mareas",
            lore = "Navegante de mares luminiscentes, Sora destila el reflejo de la luna en cristales de agua pura.",
            affinityJewel = JewelType.SAPPHIRE,
            passiveTitle = "Marea Próspera",
            passiveDesc = "Aumenta la probabilidad de obtener piezas con joyas en cada ronda.",
            activePowerTitle = "Ola Purificadora",
            activePowerDesc = "Barre al instante las dos filas inferiores ocupadas.",
            portraitResId = R.drawable.img_char_sora,
            accentColor = Color(0xFF58C4F6)
        ),
        CharacterHero(
            id = "ignis",
            name = "Ignis",
            title = "Forjador Solar",
            lore = "Maestro artesano del corazón volcánico, Ignis transforma la piedra ancestral en gemas radiantes.",
            affinityJewel = JewelType.RUBY,
            passiveTitle = "Fuego Maestro",
            passiveDesc = "Duplica los puntos adicionales al activar joyas Rubí y al lograr Tetris de 4 líneas.",
            activePowerTitle = "Estallido Solar",
            activePowerDesc = "Detona una onda 3x3 en el centro de los bloques acumulados.",
            portraitResId = R.drawable.img_char_ignis,
            accentColor = Color(0xFFFF6B7A)
        )
    )

    val boardThemes: List<BoardTheme> = listOf(
        BoardTheme(
            id = "santuario",
            name = "Santuario Nocturno",
            subtitle = "Obsidiana serena con destellos dorados",
            unlockCostCrystals = 0,
            unlockLevelNumber = 1,
            bgTop = Color(0xFF0E1324),
            bgBottom = Color(0xFF171E36),
            gridLineColor = Color(0x26F2C97D),
            frameBorderColor = Color(0xFFF2C97D),
            stoneColor = Color(0xFF47506E),
            motif = BoardMotif.SANCTUARY_STARS,
            pieceColors = mapOf(
                TetrominoType.I to Color(0xFF58C4F6),
                TetrominoType.O to Color(0xFFF2C97D),
                TetrominoType.T to Color(0xFFB689FF),
                TetrominoType.S to Color(0xFF5CE1B8),
                TetrominoType.Z to Color(0xFFFF6B7A),
                TetrominoType.J to Color(0xFF6B8CFF),
                TetrominoType.L to Color(0xFFFFA95E)
            )
        ),
        BoardTheme(
            id = "cerezo",
            name = "Jardín de Cerezo",
            subtitle = "Crepúsculo malva y pétalos suaves",
            unlockCostCrystals = 250,
            unlockLevelNumber = 2,
            bgTop = Color(0xFF1D1326),
            bgBottom = Color(0xFF2D1934),
            gridLineColor = Color(0x28FFB3D1),
            frameBorderColor = Color(0xFFFF9EC4),
            stoneColor = Color(0xFF60496B),
            motif = BoardMotif.SAKURA_PETALS,
            pieceColors = mapOf(
                TetrominoType.I to Color(0xFFFFB5D8),
                TetrominoType.O to Color(0xFFFFDF9E),
                TetrominoType.T to Color(0xFFD69BFF),
                TetrominoType.S to Color(0xFF8BE8CB),
                TetrominoType.Z to Color(0xFFFF7A96),
                TetrominoType.J to Color(0xFF96B8FF),
                TetrominoType.L to Color(0xFFFFB48A)
            )
        ),
        BoardTheme(
            id = "bosque",
            name = "Bosque de Jade",
            subtitle = "Musgo profundo y luciérnagas tranquilas",
            unlockCostCrystals = 450,
            unlockLevelNumber = 3,
            bgTop = Color(0xFF0C1D1A),
            bgBottom = Color(0xFF132D27),
            gridLineColor = Color(0x265CE1B8),
            frameBorderColor = Color(0xFF5CE1B8),
            stoneColor = Color(0xFF3E5A52),
            motif = BoardMotif.JADE_BAMBOO,
            pieceColors = mapOf(
                TetrominoType.I to Color(0xFF5EEAD4),
                TetrominoType.O to Color(0xFFE9D87A),
                TetrominoType.T to Color(0xFFA7F3D0),
                TetrominoType.S to Color(0xFF4ADE80),
                TetrominoType.Z to Color(0xFFF59E7B),
                TetrominoType.J to Color(0xFF58B9E8),
                TetrominoType.L to Color(0xFFFBBF68)
            )
        ),
        BoardTheme(
            id = "abismo",
            name = "Abismo Coral",
            subtitle = "Aguas bioluminiscentes del océano calmo",
            unlockCostCrystals = 700,
            unlockLevelNumber = 4,
            bgTop = Color(0xFF091829),
            bgBottom = Color(0xFF0E2942),
            gridLineColor = Color(0x2858C4F6),
            frameBorderColor = Color(0xFF58C4F6),
            stoneColor = Color(0xFF385370),
            motif = BoardMotif.CORAL_WAVES,
            pieceColors = mapOf(
                TetrominoType.I to Color(0xFF42E2F5),
                TetrominoType.O to Color(0xFFFFD178),
                TetrominoType.T to Color(0xFFA194FF),
                TetrominoType.S to Color(0xFF45E3B8),
                TetrominoType.Z to Color(0xFFFF7B7B),
                TetrominoType.J to Color(0xFF4B9EFF),
                TetrominoType.L to Color(0xFFFF9E73)
            )
        ),
        BoardTheme(
            id = "astral",
            name = "Observatorio Astral",
            subtitle = "Constelaciones doradas sobre cielo cósmico",
            unlockCostCrystals = 1000,
            unlockLevelNumber = 5,
            bgTop = Color(0xFF110D24),
            bgBottom = Color(0xFF211842),
            gridLineColor = Color(0x2CF6D58E),
            frameBorderColor = Color(0xFFE7B8FF),
            stoneColor = Color(0xFF544B7A),
            motif = BoardMotif.ASTRAL_CONSTELLATION,
            pieceColors = mapOf(
                TetrominoType.I to Color(0xFF7CE8FF),
                TetrominoType.O to Color(0xFFFFDC73),
                TetrominoType.T to Color(0xFFD194FF),
                TetrominoType.S to Color(0xFF6CF2C7),
                TetrominoType.Z to Color(0xFFFF7597),
                TetrominoType.J to Color(0xFF8AA8FF),
                TetrominoType.L to Color(0xFFFFB56B)
            )
        )
    )

    val storyLevels: List<StoryLevel> = listOf(
        StoryLevel(
            levelNumber = 1,
            title = "El Despertar del Santuario",
            location = "Atrio de Cristal",
            difficultyLabel = "Sereno",
            targetLines = 10,
            baseTickMs = 760L,
            minTickMs = 480L,
            initialStoneRows = 0,
            jewelEveryNPieces = 5,
            rewardCrystals = 180,
            unlocksThemeId = "cerezo",
            generalPrologue = "Las lámparas del Atrio antiguo vuelven a encenderse. Alinea las piezas luminosas para despertar la primera reliquia del reino.",
            characterVoices = mapOf(
                "lyra" to "Lyra contempla las estrellas reflejadas en el suelo de obsidiana: «Cada línea completa devuelve una nota olvidada al firmamento».",
                "kael" to "Kael apoya su bastón entre el musgo brillante: «Sin prisa. La armonía del santuario se construye piedra a piedra».",
                "sora" to "Sora recoge gotas de rocío astral en un frasco: «El agua tranquila siempre encuentra su nivel perfecto».",
                "ignis" to "Ignis aviva la primera antorcha dorada: «Incluso el fuego más noble empieza con una sola chispa»."
            ),
            epilogue = "El Atrio resplandece con un fulgor cálido. Los senderos hacia el Jardín de Cerezo quedan abiertos."
        ),
        StoryLevel(
            levelNumber = 2,
            title = "Sendero de los Pétalos",
            location = "Jardín de Cerezo",
            difficultyLabel = "Suave",
            targetLines = 14,
            baseTickMs = 660L,
            minTickMs = 390L,
            initialStoneRows = 1,
            jewelEveryNPieces = 5,
            rewardCrystals = 240,
            unlocksThemeId = "bosque",
            generalPrologue = "La brisa de primavera arrastra pétalos cristalizados entre las losas antiguas. Usa las joyas para despejar las piedras dormidas.",
            characterVoices = mapOf(
                "lyra" to "Lyra atrapa un pétalo flotante: «Las amatistas aquí brillan con recuerdos de estaciones pasadas».",
                "kael" to "Kael sonríe al oír el viento entre las ramas: «Estas raíces antiguas nos protegerán si mantenemos la calma».",
                "sora" to "Sora observa el estanque rosado: «Un pulso de zafiro bastará para limpiar el lecho de piedra».",
                "ignis" to "Ignis admira la luz rosada sobre su escudo: «Ninguna roca resistirá el calor de un buen rubí»."
            ),
            epilogue = "Los cerezos florecen al unísono, revelando un sendero cubierto de esmeraldas hacia el bosque profundo."
        ),
        StoryLevel(
            levelNumber = 3,
            title = "Raíces de Esmeralda",
            location = "Bosque de Jade",
            difficultyLabel = "Equilibrado",
            targetLines = 18,
            baseTickMs = 560L,
            minTickMs = 320L,
            initialStoneRows = 2,
            jewelEveryNPieces = 5,
            rewardCrystals = 300,
            unlocksThemeId = "abismo",
            generalPrologue = "En el corazón del Bosque de Jade, las piezas descienden al ritmo del agua. Aprovecha tu poder de personaje cuando el tablero se eleve.",
            characterVoices = mapOf(
                "lyra" to "Lyra traza runas en la niebla verde: «La claridad mental vale más que la velocidad».",
                "kael" to "Kael respira hondo entre las luciérnagas: «Este es mi hogar. Que la esmeralda guíe tus manos».",
                "sora" to "Sora escucha el murmullo de los arroyos: «Las corrientes aquí son vivas; acompáñalas sin resistir».",
                "ignis" to "Ignis ilumina la espesura con chispas doradas: «¡Abramos paso entre la maleza milenaria!»."
            ),
            epilogue = "Las luciérnagas forman un puente luminoso sobre el lago subterráneo que conduce al Abismo Coral."
        ),
        StoryLevel(
            levelNumber = 4,
            title = "Cantos del Abismo",
            location = "Abismo Coral",
            difficultyLabel = "Desafiante",
            targetLines = 22,
            baseTickMs = 470L,
            minTickMs = 260L,
            initialStoneRows = 3,
            jewelEveryNPieces = 4,
            rewardCrystals = 380,
            unlocksThemeId = "astral",
            generalPrologue = "Bajo las aguas serenas descansan ruinas de coral sólido. Las joyas aparecen con más frecuencia para ayudarte a abrir camino.",
            characterVoices = mapOf(
                "lyra" to "Lyra contempla las burbujas ascendentes: «Incluso en lo profundo, las estrellas encuentran cómo brillar».",
                "kael" to "Kael mantiene el equilibrio frente a la corriente: «Domina el tiempo con la esmeralda y el abismo cederá».",
                "sora" to "Sora extiende sus manos sobre el agua azul: «Las mareas obedecen a quienes escuchan su ritmo».",
                "ignis" to "Ignis forja burbujas incandescentes: «El contraste entre el agua fría y el rubí creará magia pura»."
            ),
            epilogue = "Las puertas sumergidas se abren suavemente, elevando una columna de luz hacia la cúpula celeste."
        ),
        StoryLevel(
            levelNumber = 5,
            title = "Cúpula de Constelaciones",
            location = "Observatorio Astral",
            difficultyLabel = "Intenso",
            targetLines = 26,
            baseTickMs = 390L,
            minTickMs = 210L,
            initialStoneRows = 3,
            jewelEveryNPieces = 4,
            rewardCrystals = 480,
            unlocksThemeId = null,
            generalPrologue = "Las estrellas giran más deprisa en lo alto del Observatorio. Encadena combinaciones múltiples y desata reacciones de joyas.",
            characterVoices = mapOf(
                "lyra" to "Lyra alza su varita estelar hacia la bóveda: «Este observatorio aguarda la última sinfonía».",
                "kael" to "Kael observa los astros con serenidad: «Aunque el cielo se acelere, tu pulso debe seguir firme».",
                "sora" to "Sora refleja la Vía Láctea en su amuleto: «Cada joya alineada es una ola de luz pura».",
                "ignis" to "Ignis sonríe ante el reto cósmico: «¡Hagamos que todo el firmamento brille como una fragua!»."
            ),
            epilogue = "El gran astrolabio se alinea por completo, revelando el pináculo legendario de Cristalia."
        ),
        StoryLevel(
            levelNumber = 6,
            title = "Pináculo de Cristalia",
            location = "Corazón del Reino",
            difficultyLabel = "Maestro",
            targetLines = 32,
            baseTickMs = 320L,
            minTickMs = 165L,
            initialStoneRows = 4,
            jewelEveryNPieces = 4,
            rewardCrystals = 650,
            unlocksThemeId = null,
            generalPrologue = "La prueba suprema del reino. Cuatro hileras ancestrales aguardan en el fondo mientras el ritmo celestial alcanza su plenitud.",
            characterVoices = mapOf(
                "lyra" to "Lyra une todas las constelaciones: «Demostremos que la belleza y la precisión son una misma fuerza».",
                "kael" to "Kael sella el pacto del bosque: «En el centro de la tormenta habita la paz absoluta».",
                "sora" to "Sora invoca la marea eterna: «Nada puede detener un océano en perfecta armonía».",
                "ignis" to "Ignis enciende el sol interior: «¡Forjemos una leyenda digna de la tabla de honor!»."
            ),
            epilogue = "¡Has restaurado el Corazón de Cristalia! Tu nombre brillará para siempre entre los guardianes del santuario."
        )
    )

    val ambientTracks: List<AmbientTrack> = listOf(
        AmbientTrack(
            id = "brisa_bosque",
            name = "Brisa del Bosque",
            description = "Acordes cálidos en Re menor con susurro suave de viento",
            chordFrequencies = listOf(146.83f, 220.00f, 261.63f, 349.23f),
            shimmerRateSeconds = 4.5f
        ),
        AmbientTrack(
            id = "lluvia_estelar",
            name = "Lluvia Estelar",
            description = "Campanillas pentatónicas cristalinas sobre un manto nocturno",
            chordFrequencies = listOf(174.61f, 261.63f, 329.63f, 392.00f),
            shimmerRateSeconds = 3.2f
        ),
        AmbientTrack(
            id = "templo_jade",
            name = "Templo de Jade",
            description = "Resonancia profunda de cuenco tibetano y armonía zen",
            chordFrequencies = listOf(130.81f, 196.00f, 293.66f, 329.63f),
            shimmerRateSeconds = 5.8f
        )
    )

    fun characterById(id: String): CharacterHero =
        characters.find { it.id == id } ?: characters.first()

    fun themeById(id: String): BoardTheme =
        boardThemes.find { it.id == id } ?: boardThemes.first()

    fun levelByNumber(levelNumber: Int): StoryLevel =
        storyLevels.find { it.levelNumber == levelNumber } ?: storyLevels.first()

    fun trackById(id: String): AmbientTrack =
        ambientTracks.find { it.id == id } ?: ambientTracks.first()
}
