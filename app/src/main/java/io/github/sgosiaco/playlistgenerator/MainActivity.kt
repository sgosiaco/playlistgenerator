package io.github.sgosiaco.playlistgenerator

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.dsl.extension.requestPermissions

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            requestCode = 1
            resultCallback = {
                handlePerm(this@MainActivity, this)
            }
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_fragment, MainActivityFragment(), "songList")
            .commit()

        fab.setOnClickListener {
            val frag = supportFragmentManager.findFragmentById(R.id.main_fragment) as MainActivityFragment?
            frag?.export()
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_date -> {
                val cal = Calendar.getInstance()
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
                val datePref = sharedPref.getString("date", "${cal.get(Calendar.MONTH).toString().padStart(2, '0')}/${cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}/${cal.get(Calendar.YEAR)}") ?: "${cal.get(Calendar.MONTH).toString().padStart(2, '0')}/${cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}/${cal.get(Calendar.YEAR)}"
                val monthPref = datePref.split("/")[0].toInt()
                val dayPref = datePref.split("/")[1].toInt()
                val yearPref = datePref.split("/")[2].toInt()
                cal.set(yearPref, monthPref, dayPref)
                val dateListener =
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        sharedPref.edit().putString("date", "${month.toString().padStart(2, '0')}/${dayOfMonth.toString().padStart(2, '0')}/$year").apply()
                        cal.set(Calendar.MONTH, month)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        cal.set(Calendar.YEAR, year)
                        val frag = supportFragmentManager.findFragmentById(R.id.main_fragment) as MainActivityFragment?
                        frag?.setDate(cal.time.toInstant().toEpochMilli())
                        Toast.makeText(this, "${cal.time}", Toast.LENGTH_LONG).show()
                    }
                DatePickerDialog(this,
                    dateListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handlePerm(activity: Activity, permissionResult: PermissionResult) {
        when(permissionResult) {
            is PermissionResult.PermissionGranted -> {

            }
            is PermissionResult.PermissionDenied -> {
                finishAffinity()
            }
            is PermissionResult.PermissionDeniedPermanently -> {
                finishAffinity()
            }
            is PermissionResult.ShowRational -> {
                AlertDialog.Builder(activity)
                    .setTitle("Permissions Required")
                    .setMessage("Read External Storage\nWrite External Storage")
                    .setNegativeButton("Deny") { dialog, _ ->
                        dialog.dismiss()
                        finishAffinity()
                    }
                    .setPositiveButton("Accept") { _, _ ->
                        requestPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                            requestCode = permissionResult.requestCode
                            resultCallback = {
                                handlePerm(activity, this)
                            }
                        }
                    }
                    .create()
                    .show()
            }
        }
    }

}
