package cegepst.example.octo.views.connexion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import cegepst.example.octo.R
import cegepst.example.octo.models.helpers.*
import cegepst.example.octo.models.stored.User
import cegepst.example.octo.viewModels.ConnexionViewModel
import cegepst.example.octo.views.cards.MainActivity



class MoreInformationActivity : ConnexionActivity() {

    private lateinit var guildSpinner: Spinner
    private lateinit var currentChosenColor: String
    private lateinit var currentChosenGuild: String
    private lateinit var moreInformationViewModel: ConnexionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = {}
        super.getUser(callback)
        this.currentChosenColor = COLORLESS
        this.moreInformationViewModel = ViewModelProvider(this).get(ConnexionViewModel::class.java)
        this.moreInformationViewModel.initialize(this)
        setContentView(R.layout.activity_more_information)
        removeActionBar()
        populateSpinner()
    }

    private fun populateSpinner() {
        val guilds = ArrayList<String>()
        guilds.addAll(resources.getStringArray(R.array.guilds))
        guildSpinner = findViewById(R.id.guildSpinner)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, guilds
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        guildSpinner.adapter = adapter
    }

    fun onRadioButtonClicked(view: View) {
        when (view.id) {
            R.id.colorless_mana -> currentChosenColor = COLORLESS
            R.id.red_mana -> currentChosenColor = RED
            R.id.blue_mana -> currentChosenColor = BLUE
            R.id.black_mana -> currentChosenColor = BLACK
            R.id.white_mana -> currentChosenColor = WHITE
            R.id.green_mana -> currentChosenColor = GREEN
        }
    }

    fun completeRegister(view: View) {
        this.currentChosenGuild = guildSpinner.selectedItem.toString()
        user = User(
            user.id,
            user.firstname,
            user.lastname,
            user.username,
            user.email,
            user.password,
            currentChosenColor,
            currentChosenGuild
        )
        val lambda = { proceed() }
        this.moreInformationViewModel.update(user, lambda)
    }

    private fun proceed() {
        saveUserLogin(user)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
