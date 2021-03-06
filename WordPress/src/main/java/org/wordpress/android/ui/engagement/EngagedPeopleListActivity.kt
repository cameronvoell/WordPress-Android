package org.wordpress.android.ui.engagement

import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.toolbar_main.*
import org.wordpress.android.R
import org.wordpress.android.WordPress
import org.wordpress.android.ui.LocaleAwareActivity
import org.wordpress.android.util.analytics.AnalyticsUtilsWrapper
import java.lang.IllegalArgumentException
import javax.inject.Inject

class EngagedPeopleListActivity : LocaleAwareActivity() {
    @Inject lateinit var analyticsUtilsWrapper: AnalyticsUtilsWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as WordPress).component().inject(this)

        setContentView(R.layout.engaged_people_list_activity)

        val listScenario = intent.getParcelableExtra<ListScenario>(KEY_LIST_SCENARIO)
                ?: throw IllegalArgumentException(
                        "List Scenario cannot be null. Make sure to pass a valid List Scenario in the intent"
                )

        analyticsUtilsWrapper.trackLikeListOpened(
                EngagementNavigationSource.getSourceDescription(listScenario.source),
                ListScenarioType.getSourceDescription(listScenario.type)
        )

        setSupportActionBar(toolbar_main)
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)

            it.title = if (listScenario.headerData.numLikes == 1) {
                getString(R.string.like_title_singular)
            } else {
                getString(R.string.like_title_plural, listScenario.headerData.numLikes)
            }
        }

        val fm = supportFragmentManager
        var likeListFragment = fm.findFragmentByTag(TAG_LIKE_LIST_FRAGMENT) as? EngagedPeopleListFragment

        if (likeListFragment == null) {
            likeListFragment = EngagedPeopleListFragment.newInstance(listScenario)
            fm.beginTransaction()
                    .add(R.id.fragment_container, likeListFragment, TAG_LIKE_LIST_FRAGMENT)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val KEY_LIST_SCENARIO = "list_scenario"
        private const val TAG_LIKE_LIST_FRAGMENT = "tag_like_list_fragment"
    }
}
