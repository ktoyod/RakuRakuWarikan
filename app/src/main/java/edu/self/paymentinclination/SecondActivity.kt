package edu.self.paymentinclination

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_second.*
import kotlin.math.abs

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Result"

        // MainActivityから受け取った結果を表示する
        val intent = getIntent()
        val resultsArray = intent.getParcelableArrayListExtra<Parcelable>("PAYLIST")
        val resultsStrData: MutableList<String> = mutableListOf()
        for (i in 1..resultsArray.size) {
            resultsStrData.add("Layer" + i.toString() + ": " + resultsArray[i - 1].toString() + " 円")
        }


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resultsStrData)
        val listView = findViewById(R.id.myListView) as ListView
        listView.adapter = adapter

        // 集める合計金額
        val payTotal = intent.getIntExtra("PAYTOTAL", -1)
        totalPay.setText("合計：" + payTotal.toString() + "円")

        // 集める金額と実際の金額の差
        val payDiff = intent.getIntExtra("PAYDIFF", -1)
        if (payDiff > 0) {
            payComment.setText(payDiff.toString() + "円余ります(^ o ^)")
        } else if (payDiff == 0) {
            payComment.setText("ぴったり集まりました！")
        } else {
            payComment.setText(abs(payDiff).toString() + "円足りません(T o T)")
        }


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
