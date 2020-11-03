package by.radiant_ronin.tempigilo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_player.view.*


class StartActivity : AppCompatActivity() {

    companion object {
        lateinit var context: StartActivity
    }

    private lateinit var rvPlayers: RecyclerView
//    private lateinit var fabAddPlayer: FloatingActionButton
//    private lateinit var fabBegin: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        context = this

        val player1 = Player(MainActivity.startTime, MainActivity.colors[0], getString(R.string.red))
        val player2 = Player(MainActivity.startTime, MainActivity.colors[1], getString(R.string.blue))
        val player3 = Player(MainActivity.startTime, MainActivity.colors[2], getString(R.string.green))
        val player4 = Player(MainActivity.startTime, MainActivity.colors[3], getString(R.string.yellow))
        MainActivity.players = arrayListOf(player1, player2, player3, player4)
        MainActivity.reservedColors = mutableSetOf(MainActivity.colors[0], MainActivity.colors[1],
            MainActivity.colors[2], MainActivity.colors[3])

        rvPlayers = findViewById(R.id.rv_players)
//        fabAddPlayer = findViewById(R.id.fab_add_player)
//        fabBegin = findViewById(R.id.fab_begin)

//        fabAddPlayer.setOnClickListener {
//            addPlayer()
//        }
//        fabBegin.setOnClickListener {
//            val chooseTimeDialogFragment = ChooseTimeDialogFragment()
//            val manager = supportFragmentManager
//            chooseTimeDialogFragment.show(manager, "chooseTimeDialog")
//        }

        val adapter = PlayerListAdapter(MainActivity.players)
        adapter.setOnItemClickListener {
            removePlayer(it)
        }
        rvPlayers.adapter = adapter

        val manager = LinearLayoutManager(this)
        rvPlayers.layoutManager = manager

        rvPlayers.addItemDecoration(DividerItemDecoration(this,
            manager.orientation))

        val callback = PlayerListAdapter.DragManageAdapter(
            adapter, ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)
        )
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(rvPlayers)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_add_player -> {
                addPlayer()
                true
            }
            R.id.menu_item_new_game -> {
                val chooseTimeDialogFragment = ChooseTimeDialogFragment()
                val manager = supportFragmentManager
                chooseTimeDialogFragment.show(manager, "chooseTimeDialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        rvPlayers.invalidate()
    }

    override fun onPause() {
        super.onPause()

        rvPlayers
    }

    private fun addPlayer() {
        if (MainActivity.players.size == 6) {
            // if Maximum players
            Toast.makeText(this, getString(R.string.maximum_players), Toast.LENGTH_SHORT).show()
            return
        }

        var color = 0
        for (i in MainActivity.colors) {
            if (!MainActivity.reservedColors.contains(i)) {
                color = i
                MainActivity.reservedColors.add(color)
                break
            }
        }
        val name = when (color) {
            MainActivity.colors[0] -> getString(R.string.red)
            MainActivity.colors[1] -> getString(R.string.blue)
            MainActivity.colors[2] -> getString(R.string.green)
            MainActivity.colors[3] -> getString(R.string.yellow)
            MainActivity.colors[4] -> getString(R.string.cyan)
            MainActivity.colors[5] -> getString(R.string.magenta)
            else -> "Gray"
        }
        val position = MainActivity.players.size
        // adding new player
        val newPlayer = Player(MainActivity.startTime, color, name)
        MainActivity.players.add(newPlayer)
        // notifying the adapter
        rvPlayers.adapter!!.notifyItemInserted(position)
    }

    private fun removePlayer(player: Player) {
        if (MainActivity.players.size <= 2) {
            Toast.makeText(this, getString(R.string.minimum_players), Toast.LENGTH_SHORT).show()
            return
        }

        MainActivity.reservedColors.remove(player.color)
        val position = MainActivity.players.indexOf(player)
        // removing selected player
        MainActivity.players.remove(player)
        // notifying the adapter
        rvPlayers.adapter!!.notifyItemRemoved(position)
    }

    // List Adapter
    class PlayerListAdapter(private val mPlayers: ArrayList<Player>) : RecyclerView.Adapter<PlayerListAdapter.ViewHolder>() {

        private var listener: ((item: Player) -> Unit)? = null

        fun setOnItemClickListener(listener: (item: Player) -> Unit) {
            this.listener = listener
        }

        inner class ViewHolder(listItemView: View, val cetl: CustomEditTextListener) : RecyclerView.ViewHolder(listItemView) {
            init {
                listItemView.btn_player_remove.setOnClickListener {
                    listener?.invoke(mPlayers[adapterPosition])
                }
                listItemView.edt_player_name.addTextChangedListener(cetl)
            }
            // views
            val edtPlayerName = itemView.findViewById<EditText>(R.id.edt_player_name)!!
            val imgPlayerColor = itemView.findViewById<ImageView>(R.id.img_player_color)!!
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            // item player
            val playerView = inflater.inflate(R.layout.item_player, parent, false)
            return ViewHolder(playerView, CustomEditTextListener())
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val player: Player = mPlayers[position]
            // player name
            val edtPlayerName = holder.edtPlayerName
            holder.cetl.viewHolder = holder
            edtPlayerName.setText(player.name)
            // player color
            val imgPlayerColor = holder.imgPlayerColor
            imgPlayerColor.setBackgroundColor(player.color)
        }

        override fun getItemCount() = mPlayers.size

        fun swapItems(from: Int, to: Int) {
            if (from < to)
                for (i in from until to)
                    mPlayers[i] = mPlayers.set(i+1, mPlayers[i])
            else
                for (i in from..to + 1)
                    mPlayers[i] = mPlayers.set(i-1, mPlayers[i])

            notifyItemMoved(from, to)
        }

        class DragManageAdapter(adapter: PlayerListAdapter, dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
            private val playerListAdapter = adapter
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                playerListAdapter.swapItems(viewHolder.adapterPosition,
                    target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // nope
            }

            // to disable swiping
            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                if (viewHolder is PlayerListAdapter.ViewHolder) return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }
        }

        inner class CustomEditTextListener(var viewHolder: RecyclerView.ViewHolder? = null) : TextWatcher {
            //var position: Int = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nope
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPlayers[viewHolder!!.adapterPosition].name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // nope
            }
        }
    }
}