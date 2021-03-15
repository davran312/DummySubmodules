package namba.wallet.nambaone.uikit.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(
    manager: FragmentManager,
    private var items: List<PagerTab> = ArrayList()
) :
    FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return items[position].getFragment()
    }

    override fun getCount(): Int = items.size

    override fun getItemPosition(objects: Any): Int {
        return items.indexOf(objects)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return items[position].getTitle()
    }
}

interface PagerTab {
    fun getFragment(): Fragment
    fun getTitle(): String
}
