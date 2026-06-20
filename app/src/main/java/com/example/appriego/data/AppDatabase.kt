package com.example.appriego.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appriego.model.PlantaModelo

class AppDatabase(context: Context) : SQLiteOpenHelper(context, "pachatech.db", null, 5) { // Subimos a versión 5

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, apellidos TEXT, correo TEXT, contrasena TEXT)")

        // CORRECCIÓN CLAVE: Añadimos 'huerto_nombre' para separar los huertos de verdad
        db.execSQL("CREATE TABLE huerto (" +
                "id INTEGER, " + // ID de la maceta (1 al 6)
                "huerto_nombre TEXT, " + // Nombre del huerto contenedor (Ej: Tomates Tech)
                "nombre TEXT, " + // Nombre de la planta (Ej: Tomate o Vacío)
                "nivelAgua INTEGER, " +
                "PRIMARY KEY (id, huerto_nombre))") // Llave primaria compuesta para evitar duplicados
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS huerto")
        onCreate(db)
    }

    fun registrarUsuario(nombre: String, apellidos: String, correo: String, pass: String): Boolean {
        val db = this.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellidos", apellidos)
            put("correo", correo)
            put("contrasena", pass)
        }
        val resultado = db.insert("usuarios", null, valores)
        db.close()
        return resultado != -1L
    }

    fun verificarLogin(correo: String, pass: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE correo = ? AND contrasena = ?", arrayOf(correo, pass))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    // Guarda o Reemplaza las posiciones de un huerto específico sin amontonar
    fun guardarHuertoCompleto(huertoNombre: String, lista: List<PlantaModelo>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            lista.forEachIndexed { index, planta ->
                val values = ContentValues().apply {
                    put("id", index + 1) // Guardamos la posición física real (1, 2, 3...)
                    put("huerto_nombre", huertoNombre)
                    put("nombre", planta.nombre)
                    put("nivelAgua", planta.nivelAgua)
                }
                // Si la maceta ya existe en ese huerto, la reemplaza (UPDATE automático), si no, la inserta
                db.insertWithOnConflict("huerto", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // Trae únicamente las macetas que pertenecen al huerto seleccionado
    fun obtenerMacetasPorHuerto(huertoNombre: String): List<PlantaModelo> {
        val lista = mutableListOf<PlantaModelo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM huerto WHERE huerto_nombre = ? ORDER BY id ASC", arrayOf(huertoNombre))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val nivelAgua = cursor.getInt(cursor.getColumnIndexOrThrow("nivelAgua"))
                lista.add(PlantaModelo(id, nombre, nivelAgua))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Obtiene la lista de todos los nombres de huertos creados (Sin repetir)
    fun obtenerListaDeHuertosUnicos(): List<String> {
        val lista = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT huerto_nombre FROM huerto", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    fun obtenerNombreUsuario(correo: String): String {
        val db = this.readableDatabase
        var nombre = "Agricultor" // Valor por defecto si pasa algo raro
        val cursor = db.rawQuery("SELECT nombre FROM usuarios WHERE correo = ?", arrayOf(correo))
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0) // Jala la columna 'nombre'
        }
        cursor.close()
        db.close()
        return nombre
    }
    fun eliminarHuertoPorNombre(huertoNombre: String): Boolean {
        val db = this.writableDatabase
        // Borra todas las filas que coincidan con el nombre del huerto
        val filasAfectadas = db.delete("huerto", "huerto_nombre = ?", arrayOf(huertoNombre))
        db.close()
        return filasAfectadas > 0
    }
}