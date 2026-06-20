package com.example.appriego.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appriego.model.PlantaModelo

class AppDatabase(context: Context) : SQLiteOpenHelper(context, "pachatech.db", null, 4) { // Subimos la versión a 4 para forzar actualización

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, apellidos TEXT, correo TEXT, contrasena TEXT)")
        db.execSQL("CREATE TABLE huerto (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, nivelAgua INTEGER)")
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

    fun guardarHuerto(lista: List<PlantaModelo>) {
        val db = this.writableDatabase

        for (planta in lista) {
            val values = ContentValues().apply {
                // Dejamos que el ID se autoincremente solo en la tabla 'huerto'
                put("nombre", planta.nombre)
                put("nivelAgua", planta.nivelAgua)
            }
            // CORREGIDO: Ahora apunta correctamente a la tabla 'huerto'
            db.insert("huerto", null, values)
        }
        db.close()
    }

    fun obtenerHuerto(): List<PlantaModelo> {
        val lista = mutableListOf<PlantaModelo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM huerto", null)
        if (cursor.moveToFirst()) {
            var idCounter = 1
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val nivelAgua = cursor.getInt(cursor.getColumnIndexOrThrow("nivelAgua"))
                lista.add(PlantaModelo(idCounter++, nombre, nivelAgua))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun tieneHuerto(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM huerto", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count > 0
    }
}