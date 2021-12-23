package james.learning.whatsappclone.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import james.learning.whatsappclone.fragment.CallsFragment
import james.learning.whatsappclone.fragment.ChatsFragment
import james.learning.whatsappclone.R
import james.learning.whatsappclone.fragment.StatusFragment
import james.learning.whatsappclone.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    lateinit var view: ActivityMainBinding

    class MainViewPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getPageTitle(position: Int): CharSequence {

            return when(position) {
                1 -> context.getString(R.string.status)
                2 -> context.getString(R.string.calls)
                else -> context.getString(R.string.chats)
            }
        }

        override fun getCount() = 3

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> ChatsFragment()
                1 -> StatusFragment()
                else -> CallsFragment()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        setSupportActionBar(view.mainToolbar)
        val viewpager = view.viewpager
        viewpager.adapter = MainViewPagerAdapter(this,supportFragmentManager)
        view.tabsLayout.setupWithViewPager(viewpager)

        view.searchView.setOnCloseListener {
            view.searchBar.visibility = GONE
            view.mainBar.visibility = VISIBLE
            return@setOnCloseListener true
        }


//        view.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//
//            }
//
//            override fun onQueryTextChange(text: String?): Boolean {
//                if (text != null && text.isNotEmpty()) {
//
//                }
//            }
//
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (view.searchBar.visibility == GONE){
            view.searchBar.visibility = VISIBLE
            view.mainBar.visibility = GONE
        } else {
            view.searchBar.visibility = GONE
            view.mainBar.visibility = VISIBLE
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        onlineStatus(false)
    }
}