package james.learning.whatsappclone.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import james.learning.whatsappclone.R
import james.learning.whatsappclone.data.Constants.USER_EMAIL
import james.learning.whatsappclone.data.Constants.USER_ID
import james.learning.whatsappclone.adapter.ContactsAdapter
import james.learning.whatsappclone.data.Constants
import james.learning.whatsappclone.data.UserProfile
import james.learning.whatsappclone.databinding.ActivityContactsBinding
import java.util.*

class ContactsActivity : BaseActivity() {
    private lateinit var view: ActivityContactsBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var adapter : ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(view.root)

        setSupportActionBar(view.toolbar)
        firebaseFirestore = FirebaseFirestore.getInstance()
        view.contactsRV.layoutManager = LinearLayoutManager(this)

        showListOfUsers("")

        view.searchView.setOnCloseListener {
            onBackPressed()
            return@setOnCloseListener true
        }

        view.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    showListOfUsers(newText.trim().lowercase())
                }
                return true
            }
        })
    }

    fun showListOfUsers(text: String) {
        val query = firebaseFirestore.collection(Constants.USERS).whereNotEqualTo(
            USER_EMAIL, getCurrentUser()?.email)
        val options = FirestoreRecyclerOptions.Builder<UserProfile>()
            .setLifecycleOwner(this)
            .setQuery(query.orderBy(USER_EMAIL).startAt(text).endAt("$text\uf8ff"), UserProfile::class.java)
            .build()
        adapter = ContactsAdapter(this, options)
        view.contactsRV.adapter = adapter
        adapter.setOnClick(object : ContactsAdapter.OnClickListener{
            override fun onClick(clickedUserId: String) {
                startActivity(Intent(this@ContactsActivity, ChatPageActivity::class.java).apply {
                    putExtra(USER_ID, clickedUserId)
                })
                finish()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (view.searchView.visibility == View.GONE){
            view.searchView.visibility = View.VISIBLE
            view.toolbar.visibility = View.GONE
        } else {
            view.searchView.visibility = View.GONE
            view.toolbar.visibility = View.VISIBLE
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }
}