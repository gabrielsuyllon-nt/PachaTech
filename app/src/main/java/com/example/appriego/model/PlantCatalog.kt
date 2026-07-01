package com.example.appriego.model

data class TipoPlanta(val nombre: String, val nivelAguaIdeal: Int)

val CATALOGO_PLANTAS = listOf(
    TipoPlanta("Tomate (Mucha Agua)", 80),
    TipoPlanta("Lechuga (Agua Media)", 50),
    TipoPlanta("Espinaca (Agua Media)", 45),
    TipoPlanta("Cactus (Poca Agua)", 15),
    TipoPlanta("Romero (Poca Agua)", 20),
    TipoPlanta("Pimiento (Agua Media)", 55),
    TipoPlanta("Fresa (Agua Media)", 50),
    TipoPlanta("Albahaca (Agua Media)", 40),
    TipoPlanta("Zanahoria (Agua Media)", 45),
    TipoPlanta("Papa (Agua Media)", 60),
    TipoPlanta("Cebolla (Agua Media)", 45),
    TipoPlanta("Ajo (Poca Agua)", 30),
    TipoPlanta("Menta (Mucha Agua)", 70),
    TipoPlanta("Cilantro (Agua Media)", 50),
    TipoPlanta("Sandía (Mucha Agua)", 85),
    TipoPlanta("Pepino (Mucha Agua)", 80),
    TipoPlanta("Orégano (Poca Agua)", 25),
    TipoPlanta("Tomillo (Poca Agua)", 20),
    TipoPlanta("Calabaza (Mucha Agua)", 75),
    TipoPlanta("Girasol (Agua Media)", 50)
)