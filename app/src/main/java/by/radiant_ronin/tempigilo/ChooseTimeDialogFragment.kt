package by.radiant_ronin.tempigilo
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ChooseTimeDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val catNames = arrayOf("10", "20", "30", "40", "50", "60")

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.choose_time)
                .setItems(catNames) { _, which ->
                    MainActivity.startTime = catNames[which].toLong() * 1000

                    val intentStartGame = Intent(StartActivity.context, MainActivity::class.java)
                    startActivity(intentStartGame)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}