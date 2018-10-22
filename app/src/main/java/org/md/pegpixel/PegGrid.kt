package org.md.pegpixel

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.*

class PegGrid {

    companion object {
        private val tableParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT)
        private val rowParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)

        fun initialize(columnCount: Int, rowCount: Int, tableLayout: TableLayout): List<PegView> {
            return createPegViews(columnCount, rowCount)
                    .flatMap{addToTable(it, tableLayout)}


        }

        private fun createPegViews(columnCount: Int, rowCount: Int): List<List<Peg>> {
            return(rowCount downTo 0).map{currentRow ->
                    (0 .. columnCount).map{currentColumn ->
                    Log.i("STUFF", "creating pegview column: $currentColumn row: $currentRow")
                    Peg(currentColumn, currentRow, false)
                }
            }
        }

        private fun addToTable(pegs:List<Peg>, tableLayout: TableLayout): List<PegView>{
            val context = tableLayout.context
            val tableRow = TableRow(context)
            tableRow.layoutParams = tableParams

            val allPegsInRow = pegs.map { pegView ->
                rowParams.column = pegView.columnIndex
                val checkbox = createCheckBox(context, pegView)

                tableRow.addView(checkbox)
                PegView(pegView, checkbox)
            }
            tableLayout.addView(tableRow)

            return allPegsInRow
        }

        private fun createCheckBox(context: Context?, peg: Peg): CompoundButton {
            val checkbox = RadioButton(context)
            checkbox.layoutParams = rowParams
            // add for better debugging
            //checkbox.text = "${peg.columnIndex}-${peg.rowIndex}"
            checkbox.id = (peg.columnIndex * 10) + peg.rowIndex
            return checkbox
        }
    }
}