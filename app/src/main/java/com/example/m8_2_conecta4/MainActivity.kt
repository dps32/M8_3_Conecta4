package com.example.m8_2_conecta4

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val ROWS = 6
    private val COLS = 7

    // 0 = vacía, 1 = rojo, 2 = amarillo
    private val board = Array(ROWS) { IntArray(COLS) { 0 } }
    private val cells = Array(ROWS) { arrayOfNulls<ImageView>(COLS) }

    private var currentPlayer = 1 // 1 rojo, 2 amarillo

    private lateinit var table: TableLayout
    private lateinit var colButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        table = findViewById(R.id.board_table)

        // los 7 botones de las columnas
        colButtons = listOf(
            findViewById(R.id.btn_col_0),
            findViewById(R.id.btn_col_1),
            findViewById(R.id.btn_col_2),
            findViewById(R.id.btn_col_3),
            findViewById(R.id.btn_col_4),
            findViewById(R.id.btn_col_5),
            findViewById(R.id.btn_col_6)
        )

        createBoard()
        attachButtonListeners()
    }

    private fun createBoard() {
        val density = resources.displayMetrics.density
        val sizePx = (48 * density).toInt()
        val marginPx = (4 * density).toInt()

        // itaramos cada row
        for (r in 0 until ROWS) {
            val row = TableRow(this) // creamos una fila
            row.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            row.gravity = Gravity.CENTER

            // añadimos las posiciones en la fila
            for (c in 0 until COLS) {
                val img = ImageView(this) // creamos la imagen
                val lp = TableRow.LayoutParams(sizePx, sizePx) // tamaño
                lp.setMargins(marginPx, marginPx, marginPx, marginPx)
                img.layoutParams = lp
                img.setImageResource(R.drawable.circle_empty) // le ponemos el circulo vacio
                img.scaleType = ImageView.ScaleType.CENTER_INSIDE
                row.addView(img)

                // guardamos la posicion y el estado
                cells[r][c] = img
                board[r][c] = 0
            }

            table.addView(row)
        }
    }


    private fun attachButtonListeners() {
        for (c in 0 until COLS) {
            colButtons[c].setOnClickListener { dropChip(c) }
        }
    }

    private fun dropChip(col: Int) {
        // Buscar la fila más baja vacía
        var placedRow = -1

        for (r in ROWS - 1 downTo 0) {
            if (board[r][col] == 0) {
                board[r][col] = currentPlayer
                placedRow = r
                break
            }
        }

        if (placedRow == -1) {
            Toast.makeText(this, "Columna llena", Toast.LENGTH_SHORT).show()
            return
        }

        // Poner la ficha en la UI
        val img = cells[placedRow][col] ?: return
        img.setImageResource(if (currentPlayer == 1) R.drawable.circle_red else R.drawable.circle_yellow)

        // Comprobar si hay ganador
        if (checkWin(placedRow, col)) {
            val winner = if (currentPlayer == 1) "Rojo" else "Amarillo"
            Toast.makeText(this, "$winner ha ganado", Toast.LENGTH_LONG).show()
            disableAllButtons()
            return
        }

        // Cambiar turno
        if (currentPlayer == 1)
            currentPlayer = 1
        else
            currentPlayer = 2

        val turnText: TextView = findViewById(R.id.turnText)

        turnText.text = "Turno del jugador " + currentPlayer
    }

    private fun disableAllButtons() {
        colButtons.forEach { it.isEnabled = false }
    }

    private fun checkWin(r0: Int, c0: Int): Boolean {
        val player = board[r0][c0]

        val directions = arrayOf(
            intArrayOf(0,1),
            intArrayOf(1,0),
            intArrayOf(1,1),
            intArrayOf(1,-1)
        )

        for (d in directions) {
            var count = 1
            // hacia adelante
            var r = r0 + d[0]
            var c = c0 + d[1]
            while (r in 0 until ROWS && c in 0 until COLS && board[r][c] == player) {
                count++
                r += d[0]
                c += d[1]
            }


            // hacia atrás
            r = r0 - d[0]
            c = c0 - d[1]
            while (r in 0 until ROWS && c in 0 until COLS && board[r][c] == player) {
                count++
                r -= d[0]
                c -= d[1]
            }
            if (count >= 4) return true
        }
        
        return false
    }
}
