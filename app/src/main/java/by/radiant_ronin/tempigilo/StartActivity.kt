package by.radiant_ronin.tempigilo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.item_player.view.*

class StartActivity : AppCompatActivity() {

    companion object {
        lateinit var context: StartActivity
    }

    lateinit var rvPlayers: RecyclerView
    lateinit var fabAddPlayer: FloatingActionButton
    lateinit var fabBegin: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        context = this

        val player1 = Player(0, MainActivity.startTime, MainActivity.colors[0], "Red")
        val player2 = Player(1, MainActivity.startTime, MainActivity.colors[1], "Blue")
        val player3 = Player(2, MainActivity.startTime, MainActivity.colors[2], "Green")
        val player4 = Player(3, MainActivity.startTime, MainActivity.colors[3], "Yellow")
        MainActivity.players = arrayListOf(player1, player2, player3, player4)
        MainActivity.reservedColors = mutableSetOf(MainActivity.colors[0], MainActivity.colors[1],
            MainActivity.colors[2], MainActivity.colors[3])

        rvPlayers = findViewById(R.id.rv_players)
        fabAddPlayer = findViewById(R.id.fab_add_player)
        fabBegin = findViewById(R.id.fab_begin)

        val adapter = PlayerListAdapter(MainActivity.players)
        fabAddPlayer.setOnClickListener {
            addPlayer()
        }
        adapter.setOnItemClickListener {
            removePlayer(it.id)
        }
        fabBegin.setOnClickListener {
            val chooseTimeDialogFragment = ChooseTimeDialogFragment()
            val manager = supportFragmentManager
            chooseTimeDialogFragment.show(manager, "chooseTimeDialog")


        }

        rvPlayers.adapter = adapter

        rvPlayers.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()

        rvPlayers.invalidate()
    }

    override fun onPause() {
        super.onPause()

        rvPlayers
    }

    fun addPlayer() {
        if (MainActivity.players.size == 6) {
            // if Maximum players
            Toast.makeText(this, "Maximum players reached!", Toast.LENGTH_SHORT).show()
            return
        }
        val position = MainActivity.players.size

        var color = 0
        for (i in MainActivity.colors) {
            if (!MainActivity.reservedColors.contains(i)) {
                color = i
                MainActivity.reservedColors.add(color)
                break
            }
        }
        val name = when (color) {
            MainActivity.colors[0] -> "Red"
            MainActivity.colors[1] -> "Blue"
            MainActivity.colors[2] -> "Green"
            MainActivity.colors[3] -> "Yellow"
            MainActivity.colors[4] -> "Cyan"
            MainActivity.colors[5] -> "Magenta"
            else -> "Gray"
        }
        // adding new player
        val newPlayer = Player(position, MainActivity.startTime, color, name)
        MainActivity.players.add(newPlayer)
        // notifying the adapter
        rvPlayers.adapter!!.notifyItemInserted(position)
    }

    fun removePlayer(position: Int) {
        if (MainActivity.players.size <= 2) {
            Toast.makeText(this, "Minimum players reached!", Toast.LENGTH_SHORT).show()
            return
        }

        MainActivity.reservedColors.remove(MainActivity.players[position].color)
        MainActivity.players.removeAt(position)
        resetIds()
        rvPlayers.adapter!!.notifyItemRemoved(position)
        rvPlayers.invalidate()
    }

    fun resetIds() {
        for (i in 0 until MainActivity.players.size)
            MainActivity.players[i].id = i
    }

    // List Adapter
    class PlayerListAdapter(private val mPlayers: ArrayList<Player>)
        : RecyclerView.Adapter<PlayerListAdapter.ViewHolder>() {

        private var listener: ((item: Player) -> Unit)? = null

        fun setOnItemClickListener(listener: (item: Player) -> Unit) {
            this.listener = listener
        }

        inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) { // , val cetl: CustomEditTextListener
            init {
                listItemView.btn_player_remove.setOnClickListener {
                    listener?.invoke(mPlayers[adapterPosition])
                }
                //
//                listItemView.edt_player_name.addTextChangedListener(cetl)
            }
            // views
            val edtPlayerName = itemView.findViewById<EditText>(R.id.edt_player_name)
            val imgPlayerColor = itemView.findViewById<ImageView>(R.id.img_player_color)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            // item player
            val playerView = inflater.inflate(R.layout.item_player, parent, false)
            return ViewHolder(playerView) //, CustomEditTextListener())
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val player: Player = mPlayers[position]
            // player name
            val edtPlayerName = holder.edtPlayerName
            edtPlayerName.setText(player.name)
//            holder.cetl.position = position
            // player color
            val imgPlayerColor = holder.imgPlayerColor
            imgPlayerColor.setBackgroundColor(player.color)
        }

        override fun getItemCount() = mPlayers.size

//        inner class CustomEditTextListener : TextWatcher {
//            var position: Int = 0
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // nope
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                mPlayers[position].name = s.toString()
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                // nope
//            }
//        }
    }
}