package com.example.wheelpaper

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.wheelpaper.databinding.ActivityMainBinding
import java.io.IOException
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.wheelpaper.databinding.ActivityMainBinding
import com.flask.colorpicker.OnColorChangedListener // Import the new listener interface

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedColor: Int = Color.BLACK // Default color

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupColorPicker()
        setupButtons()
    }

    private fun setupColorPicker() {
        // Set a listener for color changes on the new ColorPickerView
        binding.colorPickerView.setOnColorChangedListener(object : OnColorChangedListener {
            override fun onColorChanged(selectedColor: Int) {
                // Update the selected color variable and the preview
                this@MainActivity.selectedColor = selectedColor
                binding.colorPreview.setBackgroundColor(selectedColor)
            }
        })

        // Set initial preview color
        binding.colorPreview.setBackgroundColor(selectedColor)
    }

    private fun setupButtons() {
        binding.setWallpaperButton.setOnClickListener {
            setDeviceWallpaper(selectedColor, WallpaperManager.FLAG_SYSTEM)
        }

        binding.setLockscreenButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setDeviceWallpaper(selectedColor, WallpaperManager.FLAG_LOCK)
            } else {
                Toast.makeText(this, "Setting lock screen wallpaper requires Android 7.0+", Toast.LENGTH_SHORT).show()
            }
        }

        binding.setBothButton.setOnClickListener {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                 setDeviceWallpaper(selectedColor, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
             } else {
                 // Fallback for older versions: just set system wallpaper
                 setDeviceWallpaper(selectedColor, WallpaperManager.FLAG_SYSTEM)
                 Toast.makeText(this, "Setting lock screen wallpaper requires Android 7.0+. Set system wallpaper only.", Toast.LENGTH_LONG).show()
             }
        }
    }

    private fun setDeviceWallpaper(color: Int, which: Int) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        try {
            // Create a 1x1 pixel bitmap with the selected color
            val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(color)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap, null, true, which)
            } else {
                // For older versions, only FLAG_SYSTEM is implicitly supported by setBitmap
                if (which == WallpaperManager.FLAG_SYSTEM || which == (WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)) {
                     wallpaperManager.setBitmap(bitmap)
                } else {
                    // Cannot set only lock screen on older versions this way
                     Toast.makeText(this, "Setting lock screen wallpaper requires Android 7.0+", Toast.LENGTH_SHORT).show()
                     return // Exit if trying to set only lock screen on older API
                }
            }

            Toast.makeText(this, R.string.wallpaper_set_success, Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, R.string.wallpaper_set_error, Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
             e.printStackTrace()
             Toast.makeText(this, "Permission denied. Check SET_WALLPAPER permission.", Toast.LENGTH_LONG).show()
        }
    }
}