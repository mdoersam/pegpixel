package org.md.pegpixel

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TableLayout
import android.widget.Toast
import kotlin.concurrent.thread


class BoardView : AppCompatActivity() {

    private val showShortToast: (String) -> Unit = { errorMessage ->
        Looper.prepare()
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private val bluetoothDeviceName = "DSD TECH HC-05"

    private var bluetoothConnectionToBoard: BluetoothConnectionToBoard =  PendingBluetoothConnectionToBoard(bluetoothDeviceName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_view)

        initiateBluetoothConnection()

        val allPegs = initiateGrid(findViewById(R.id.pegTableLayout))
        initiateSendAllButton(findViewById(R.id.sendAllButton), allPegs)
    }

    private fun initiateSendAllButton(sendAllButton: Button, allPegs: List<PegView>) {
        sendAllButton.setOnClickListener{
            thread{
                allPegs.forEach{pegView ->
                    val json = PegGridToJson.createJsonFor(pegView)
                    sendViaBt(json)
                    // receiving side cannot handle the speed of the transmission -> throttle
                    Thread.sleep(50)
                }
            }
        }
    }

    private val sendViaBt: (String) -> Unit = {data ->
        thread {
            bluetoothConnectionToBoard.sendData(data, showShortToast)
        }
    }

    private fun initiateGrid(rootTable: TableLayout): List<PegView> {
        val allPegsWithButtons = PegGrid.addGridTo(
                columnCount = 4,
                rowCount = 4,
                tableLayout = rootTable
        )

        allPegsWithButtons.forEach {pegViewWithCheckbox ->
            pegViewWithCheckbox.updateColor(Color.RED)

            pegViewWithCheckbox.checkBox.setOnClickListener{
                pegViewWithCheckbox.pegView.toggleSelect()
                val json = PegGridToJson.createJsonFor(pegViewWithCheckbox.pegView)
                sendViaBt(json)
            }
            pegViewWithCheckbox.checkBox.setOnLongClickListener{
                val pickColorFragment = PickColorFragment()
                pickColorFragment.handleSelectedColor = { selectedColor ->
                    allPegsWithButtons
                        .filter { !it.checkBox.isChecked }
                        .forEach{
                            it.updateColor(selectedColor)
                        }
                    pegViewWithCheckbox.selectWithColor(selectedColor)
                    val json = PegGridToJson.createJsonFor(pegViewWithCheckbox.pegView)
                    sendViaBt(json)
                }
                pickColorFragment.show(fragmentManager, "PickColorDialogFragment")
                true
            }
        }

        return allPegsWithButtons.map { it.pegView }
    }

    private fun initiateBluetoothConnection() {
        thread {
            bluetoothConnectionToBoard = BluetoothConnectionToBoard.initiate(bluetoothDeviceName) { errorMessage ->

                Looper.prepare()
                showShortToast(errorMessage)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothConnectionToBoard.close()
    }
}
