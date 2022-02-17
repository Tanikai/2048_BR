package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dhbw.br2048.R
import com.dhbw.br2048.api.GameSocket
import com.dhbw.br2048.data.Coordinates
import com.dhbw.br2048.data.Direction
import com.dhbw.br2048.data.GameManager
import com.dhbw.br2048.databinding.ActivityGameBinding
import com.google.android.material.snackbar.Snackbar
import io.socket.emitter.Emitter


class GameActivity : AppCompatActivity() {
    private lateinit var b: ActivityGameBinding
    private val gridFragment = GridFragment()
    private lateinit var manager: GameManager
    private lateinit var gameSocket: GameSocket

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme from shared preferences
        // Author: Kai
        val sp = getSharedPreferences("theme", MODE_PRIVATE)
        setTheme(sp.getInt("currentTheme", R.style.Theme_Original))
        // End Kai

        super.onCreate(savedInstanceState)

        b = ActivityGameBinding.inflate(layoutInflater)
        setContentView(b.root)
        setCurrentFragment(gridFragment)

        // Author: Caspar
        b.clGame.setOnTouchListener(object : OnSwipeTouchListener(this@GameActivity) {
            override fun onSwipeLeft() {
                manager.move(Direction.LEFT)
            }

            override fun onSwipeRight() {
                manager.move(Direction.RIGHT)
            }

            override fun onSwipeTop() {
                manager.move(Direction.UP)
            }

            override fun onSwipeBottom() {
                manager.move(Direction.DOWN)
            }
        })
        // End Caspar
    }

    private val onNewMessage = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            Log.d("onNewMessage", "onNewMessage")
        })
    }

    // Author: Maxi
    override fun onResume() {
        super.onResume()
        gameSocket = GameSocket("my-game-id") {
            runOnUiThread {
                b.scoreboard.text = it
            }
        }

        //gameSocket.socket.on("score", onNewMessage)

        manager = GameManager(
            b.root.context,
            gridFragment.getGrid(),
            Coordinates(4, 4),
            2,
        )
        manager.addStartTiles()

        manager.scoreCallback = { score: Int ->
            b.score.text = score.toString()
            gameSocket.score(score)
        }
        manager.overCallback = { score: Int ->
            Snackbar.make(b.score, "Game Over!", Snackbar.LENGTH_LONG).show()
            gameSocket.over(score)
        }
        manager.wonCallback = { score: Int ->
            Snackbar.make(b.score, "Wow good Job... Nerd!", Snackbar.LENGTH_LONG).show()
            gameSocket.won(score)
        }
    }

    override fun onStop() {
        super.onStop()
        gameSocket.close()
        Log.d("GameActivity", "onStop")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                manager.move(Direction.UP)
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                manager.move(Direction.LEFT)
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                manager.move(Direction.DOWN)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                manager.move(Direction.RIGHT)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            setReorderingAllowed(true)
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}