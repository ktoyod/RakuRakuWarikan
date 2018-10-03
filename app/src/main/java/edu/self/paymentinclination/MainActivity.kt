package edu.self.paymentinclination

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.Gravity
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "ラクラク割り勘"

        val data = mutableListOf("Layer1: 0 人", "Layer2: 0 人", "+ 階層を追加")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        val listView = findViewById(R.id.myListView) as ListView
        listView.adapter = adapter

        listView.setOnItemClickListener {parent, view, position, id ->

            // 一番下の項目をタップしたら新しい項目をその項目の上に追加
            if (position == adapter.count - 1) {
                adapter.insert("Layer" + (adapter.count).toString() + ": 0 人", adapter.count - 1)
                adapter.notifyDataSetChanged()
            } else {
                val dialog = AlertDialog.Builder(this)
                // タイトル
                dialog.setTitle("Layer" + (position + 1).toString() + "の人数を入力")

                // 入力部分
                val myedit = EditText(this)
                val listItem = adapter.getItem(position)
                val defaultNum = Integer.parseInt(listItem.split(" ")[1])
                myedit.setInputType(InputType.TYPE_CLASS_NUMBER)
                myedit.setGravity(Gravity.RIGHT)
                myedit.requestFocus()
                myedit.setText(defaultNum.toString())
                myedit.setSelection(myedit.text.length)

                dialog.setView(myedit)

                // ボタン設定
                dialog.setPositiveButton("OK", DialogInterface.OnClickListener{_, _ ->
                    val userNumber = myedit.getText().toString()
                    adapter.remove(adapter.getItem(position))
                    adapter.insert("Layer" + (position + 1).toString() + ": " + userNumber + " 人", position)
                    adapter.notifyDataSetChanged()
                })
                if (position > 1) {
                    dialog.setNeutralButton("この階層を削除", DialogInterface.OnClickListener{_, _ ->
                        adapter.remove(adapter.getItem(position))
                        val layerNum = adapter.count - 1
                        for (i in position..layerNum - 1) {
                            val item = adapter.getItem(i)
                            val num = Integer.parseInt(item.split(" ")[1])
                            adapter.remove(adapter.getItem(i))
                            adapter.insert("Layer" + (i + 1).toString() + ": " + num.toString() + " 人", i)
                        }
                        adapter.notifyDataSetChanged()
                    })
                }
                dialog.setNegativeButton("Cancel", null)
                dialog.show()
            }
        }

        var alpha = 3;

        // seekbar 初期値
        seekBar.setProgress(50)
        // seekbar 最大値
        seekBar.setMax(100)

        seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    //ツマミがドラッグされると呼ばれる
                    override fun onProgressChanged(
                            seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        // 68 % のようにフォーマト、
                        // この場合、Locale.USが汎用的に推奨される
                        if (progress < 20) {
                            ratioText.text = "傾斜：超ゆるめ"
                            alpha = 1
                        } else if (progress < 40) {
                            ratioText.text = "傾斜：ゆるめ"
                            alpha = 2
                        } else if (progress < 60) {
                            ratioText.text = "傾斜：普通"
                            alpha = 3
                        } else if (progress < 80) {
                            ratioText.text = "傾斜：きつめ"
                            alpha = 4
                        } else {
                            ratioText.text = "傾斜：超きつめ"
                            alpha = 5
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        // ツマミがタッチされた時に呼ばれる
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        // ツマミがリリースされた時に呼ばれる
                    }
                }
        )

        calcButton.setOnClickListener {
            val layerNum = adapter.count - 1 // 層の数
            val numList: MutableList<Int> = mutableListOf() // 各層の人数
            val ratioList: MutableList<Double> = mutableListOf() // 各層の払う金額の比率
            val numRatioList: MutableList<Double> = mutableListOf()
            for (i in 0..layerNum - 1) {
                val item = adapter.getItem(i)
                val num = Integer.parseInt(item.split(" ")[1])
                numList.add(num)
                ratioList.add(1.0 + 1.0 * alpha * (layerNum - i - 1) / (layerNum - 1))
                numRatioList.add(numList[i] * ratioList[i])
            }
            if (0 in numList) {
            } else {
                val total = Integer.parseInt(totalText.getText().toString()) // トータルの金額
                val sumNum = numRatioList.sum() // 合計

                val basePay = total / sumNum // ベースとなる金額

                val payList: MutableList<Int> = mutableListOf()
                for (i in 0..layerNum - 1) {
                    var pay: Int = 0
                    if (i >= layerNum / 2) {
                        pay = (ceil(basePay * ratioList[i] / 100) * 100).toInt()
                    } else {
                        pay = (floor(basePay * ratioList[i] / 100) * 100).toInt()
                    }
                    payList.add(pay)
                }
                val payArray = ArrayList<Int>(payList)

                var payTotal = 0
                for ( i in 0.. layerNum - 1) {
                    payTotal += numList[i] * payList[i]
                }
                val payDiff = payTotal - total

                val intent: Intent = Intent(this, SecondActivity::class.java)
                intent.putExtra("PAYLIST", payArray)
                intent.putExtra("PAYTOTAL", payTotal)
                intent.putExtra("PAYDIFF", payDiff)
                startActivity(intent)
            }
        }


//
//        seekBar.setOnSeekBarChangeListener {
//            int seekValue = seekBar.get
//        }

//        calcButton.setOnClickListener {
//            val totalValue = totalText.getText()
//            adapter.insert("今のTOTALは" + totalValue + "円だよ", adapter.count - 1)
//            adapter.notifyDataSetChanged()
//        }
    }
}
