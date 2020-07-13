package com.vincent.flutter_fileloader.activity

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.rmondjone.locktableview.LockTableView
import com.vincent.flutter_fileloader.R
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File


class LoadExcelActivity : AppCompatActivity() {

    companion object {

        const val PATH: String = "path"

        fun start(context: Context, path: String) {
            val i = Intent(context, LoadExcelActivity::class.java)
            i.putExtra(PATH, path)
            i.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }

    }

    private lateinit var mPath: String

    private lateinit var mTab: TabLayout

    private lateinit var mVp: MyViewPager

    private var tables: ArrayList<LockTableView> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_excel)

        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0);

        initViews()
        initData()

        val bar = supportActionBar;
        bar?.setHomeButtonEnabled(true)
        bar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        mTab = findViewById(R.id.mTab)
        mVp = findViewById(R.id.mVp)
    }

    private fun initData() {
        mPath = intent.getStringExtra("path")

        val str = parseXml(File(mPath))
        print(str)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 读取xlsx文件
     */
    fun parseXml(file: File?): String? {

        var fragments = arrayListOf<MyFragment>()


        var result = ""
        try {
//            val fis = FileInputStream(file)
            val sb = StringBuilder()
//            val xwb = XSSFWorkbook(fis)
            val xwb = WorkbookFactory.create(file)

            for (i in 0 until xwb.numberOfSheets) {   //解析 页

                var dataList = arrayListOf<ArrayList<String>>()

                val sheet: Sheet = xwb.getSheetAt(i)

                val startRow = sheet.firstRowNum

                val endRow = sheet.lastRowNum

                sb.append(" ${sheet.sheetName}\n")


                title = file?.name

                for (row in startRow until endRow) {   //解析 行

                    val r = sheet.getRow(row)

                    if (r.firstCellNum < 0) continue

                    var rowList = arrayListOf<String>()

                    for (cell: Int in r.firstCellNum until r.lastCellNum) {

                        val c = if (r.getCell(cell) == null) "" else r.getCell(cell).toString();
                        sb.append("$c")
                        rowList.add(c)
                    }
                    dataList.add(rowList)

                    sb.append("\n");
                }

                val rootLayout = LinearLayout(this, null)


                if (dataList.size > 1000){
                    var list = arrayListOf("数据过多，无法加载！")
                    dataList = arrayListOf<ArrayList<String>>(list)
                }

                val table = LockTableView(this, rootLayout, dataList)

                table.setLockFristColumn(false) //是否锁定第一列
                        .setLockFristRow(false) //是否锁定第一行
                        .setMaxColumnWidth(100) //列最大宽度
                        .setMinColumnWidth(60) //列最小宽度
                        .setColumnWidth(0, 120) //设置指定列文本宽度(从0开始计算,宽度单位dp)
                        .setMinRowHeight(50)//行最小高度
                    .setMaxRowHeight(100)//行最大高度
                        .setTextViewSize(15) //单元格字体大小
                        .setCellPadding(15)//设置单元格内边距(dp)
                        .setFristRowBackGroudColor(R.color.table_head)//表头背景色
                        .setTableHeadTextColor(R.color.beijin)//表头字体颜色
                        .setTableContentTextColor(R.color.border_color)//单元格字体颜色
//                    .setNullableString("N/A") //空值替换值
                        .setNullableString("") //空值替换值


                tables.add(table)

                table.setOnItemLongClickListenter { view, i ->

                    Log.i("点击行数:", "$i")

//                    showToast(dataList[i].toString())
                    showDialog(dataList[i].toString())
                }

                table.show()

                table.tableScrollView.setLoadingMoreEnabled(false)
                table.tableScrollView.setPullRefreshEnabled(false)


                val fragment = MyFragment(rootLayout, sheet.sheetName)

                fragments.add(fragment)
            }


            mVp.adapter = MyPagerAdapter(supportFragmentManager, fragments)

            mTab.setupWithViewPager(mVp)

//            fis.close()

            result += sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (result in grantResults) {
            if (result == -1) {
                showToast("缺少需要的读写权限，请打开!")
                finish()
            }
        }

    }

    fun showDialog(str : String){
        val dialog  = AlertDialog.Builder(this)

        val et = EditText(this)
        et.setText(str)
        dialog.setView(et)
        dialog.setPositiveButton("确定") { dialog, which -> }
        dialog.show()
    }


    fun showToast(tips: String) {
        Toast.makeText(this, tips, Toast.LENGTH_SHORT).show()
    }


    class MyPagerAdapter(fm: FragmentManager, private var fragments: List<MyFragment>) : FragmentPagerAdapter(fm) {


        override fun getItem(position: Int): Fragment {
            return fragments[position];
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragments[position].sheetName
        }
    }

    /**
     *  禁止左右滑动的 ViewPager
     */
    class MyViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
        private val noScroll = true

        override fun onTouchEvent(arg0: MotionEvent?): Boolean {
            return if (noScroll) false else super.onTouchEvent(arg0)
        }

        override fun onInterceptTouchEvent(arg0: MotionEvent?): Boolean {
            return if (noScroll) false else super.onInterceptTouchEvent(arg0)
        }

    }

    class MyFragment(private var contentView: View, var sheetName: String) : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return contentView
        }
    }

}