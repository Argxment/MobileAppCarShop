package com.example.pastuhovvprojectmobile

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "cars_shop.db", factory, 15) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users (login TEXT PRIMARY KEY, password TEXT, email TEXT, balance INTEGER DEFAULT 50000)")
        db.execSQL("CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, carTitle TEXT, price INTEGER)")
        db.execSQL("CREATE TABLE cars (title TEXT PRIMARY KEY, image TEXT, info TEXT, price INTEGER, qty INTEGER)")
        db.execSQL("CREATE TABLE debts (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, title TEXT, remaining INTEGER)")
        
        // Стартовый автопарк
        insertCar(db, "УАЗ Хантер", "_1", "UAZ Hunter", 500000, 3)
        insertCar(db, "Лада Веста (Синяя)", "_2", "Lada Vesta синяя", 1000000, 2)
        insertCar(db, "Лада Нива Легенд", "_3", "Lada Niva Legend", 900000, 5)
        insertCar(db, "Лада Веста (Серая)", "_4", "Lada Vesta серая", 1200000, 5)
    }

    private fun insertCar(db: SQLiteDatabase, t: String, img: String, i: String, p: Int, q: Int) {
        val v = ContentValues().apply { put("title", t); put("image", img); put("info", i); put("price", p); put("qty", q) }
        db.insert("cars", null, v)
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS users"); db.execSQL("DROP TABLE IF EXISTS history")
        db.execSQL("DROP TABLE IF EXISTS cars"); db.execSQL("DROP TABLE IF EXISTS debts")
        onCreate(db)
    }

    // Пользователи
    fun addUser(u: User) {
        val v = ContentValues().apply { put("login", u.login); put("password", u.password); put("email", u.mail) }
        writableDatabase.insert("users", null, v)
    }

    fun checkUser(l: String, p: String) = readableDatabase.rawQuery("SELECT * FROM users WHERE login=? AND password=?", arrayOf(l, p)).use { it.moveToFirst() }

    fun isTaken(l: String, p: String): String? {
        val db = readableDatabase
        if (db.rawQuery("SELECT * FROM users WHERE login=?", arrayOf(l)).use { it.moveToFirst() }) return "Логин занят"
        if (db.rawQuery("SELECT * FROM users WHERE password=?", arrayOf(p)).use { it.moveToFirst() }) return "Пароль занят"
        return null
    }

    fun getBalance(l: String) = readableDatabase.rawQuery("SELECT balance FROM users WHERE login=?", arrayOf(l)).use { if (it.moveToFirst()) it.getInt(0) else 0 }
    fun updateBalance(l: String, b: Int) = writableDatabase.update("users", ContentValues().apply { put("balance", b) }, "login=?", arrayOf(l))

    // Товары
    fun getAllCars(): List<Pair<Item, Int>> {
        val list = mutableListOf<Pair<Item, Int>>()
        readableDatabase.rawQuery("SELECT * FROM cars", null).use {
            while (it.moveToNext()) {
                val car = Item(0, it.getString(1), it.getString(0), it.getString(2), "", it.getInt(3))
                list.add(car to it.getInt(4))
            }
        }
        return list
    }

    fun deleteCar(t: String) = writableDatabase.delete("cars", "title=?", arrayOf(t))
    fun addOrUpdateCar(t: String, img: String, p: Int, q: Int) {
        val v = ContentValues().apply { put("title", t); put("image", img); put("info", "Авто"); put("price", p); put("qty", q) }
        writableDatabase.insertWithOnConflict("cars", null, v, SQLiteDatabase.CONFLICT_REPLACE)
    }
    fun updateQty(t: String, q: Int) = writableDatabase.update("cars", ContentValues().apply { put("qty", q) }, "title=?", arrayOf(t))
    fun getQty(t: String) = readableDatabase.rawQuery("SELECT qty FROM cars WHERE title=?", arrayOf(t)).use { if (it.moveToFirst()) it.getInt(0) else 0 }

    // Покупки и Долги
    fun hasDebt(l: String) = readableDatabase.rawQuery("SELECT * FROM debts WHERE login=? AND remaining > 0", arrayOf(l)).use { it.moveToFirst() }

    fun buy(l: String, t: String, p: Int) {
        writableDatabase.insert("history", null, ContentValues().apply { put("login", l); put("carTitle", t); put("price", p) })
        writableDatabase.execSQL("UPDATE cars SET qty = qty - 1 WHERE title = ?", arrayOf(t))
    }

    fun startCredit(l: String, t: String, p: Int) {
        writableDatabase.insert("debts", null, ContentValues().apply { put("login", l); put("title", t); put("remaining", p) })
        writableDatabase.execSQL("UPDATE cars SET qty = qty - 1 WHERE title = ?", arrayOf(t))
    }

    fun getDebts(l: String): List<Triple<Int, String, Int>> {
        val list = mutableListOf<Triple<Int, String, Int>>()
        readableDatabase.rawQuery("SELECT id, title, remaining FROM debts WHERE login=? AND remaining > 0", arrayOf(l)).use {
            while (it.moveToNext()) list.add(Triple(it.getInt(0), it.getString(1), it.getInt(2)))
        }
        return list
    }

    fun payDebt(id: Int, amt: Int) = writableDatabase.execSQL("UPDATE debts SET remaining = remaining - $amt WHERE id = $id")

    fun getHistory(l: String): List<String> {
        val list = mutableListOf<String>()
        readableDatabase.rawQuery("SELECT carTitle FROM history WHERE login=?", arrayOf(l)).use {
            while (it.moveToNext()) list.add(it.getString(0))
        }
        return list
    }
}
