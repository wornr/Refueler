package pl.marek.refueler.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import pl.marek.refueler.Services;
import pl.marek.refueler.fragments.CarsFragment;
import pl.marek.refueler.fragments.ChartsFragment;
import pl.marek.refueler.fragments.DailyRefuelingFragment;
import pl.marek.refueler.InformationDialog;
import pl.marek.refueler.R;
import pl.marek.refueler.fragments.StatisticsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PREF_LAST_DRAWER_FRAGMENT = "last_selected_main_drawer_fragment";
    private static final DrawerFragmentItem[] DRAWER_FRAGMENTS = new DrawerFragmentItem[]{
            new DrawerFragmentItem(R.id.nav_cars, "crs", CarsFragment.class),
            new DrawerFragmentItem(R.id.nav_daily_refueling, "drf", DailyRefuelingFragment.class),
            new DrawerFragmentItem(R.id.nav_statistics, "sts", StatisticsFragment.class),
            new DrawerFragmentItem(R.id.nav_charts, "chr", ChartsFragment.class),
    };
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Choose and open fragment
        if (savedInstanceState == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            // Choose last selected fragment
            DrawerFragmentItem item = findDrawerItemFragmentWithName(prefs.getString(PREF_LAST_DRAWER_FRAGMENT, null));

            // If nothing above chosen anything, use default fragment
            if (item == null) {
                item = DRAWER_FRAGMENTS[0];
            }

            // Open resolved fragment
            openFragment(item);
            navigationView.setCheckedItem(item.id);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        DrawerFragmentItem item = findDrawerItemFragmentWithName(prefs.getString(PREF_LAST_DRAWER_FRAGMENT, null));
        if (item != null)
            navigationView.setCheckedItem(item.id);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_settings);

        if (menuItem != null) {
            Services.getInstance().tintMenuIcon(this, menuItem, android.R.color.white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_info) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            InformationDialog informationDialog = new InformationDialog();
            informationDialog.show(fragmentManager, "Information");
        } else {
            DrawerFragmentItem drawerFragmentItem = findDrawerItemFragmentWithId(item.getItemId());
            if (drawerFragmentItem != null) {
                openFragment(drawerFragmentItem);
                rememberSelectedItem(drawerFragmentItem);
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private static DrawerFragmentItem findDrawerItemFragmentWithId(int id) {
        for (DrawerFragmentItem item : DRAWER_FRAGMENTS) {
            if (item.id == id) {
                return item;
            }
        }

        return null;
    }

    private static DrawerFragmentItem findDrawerItemFragmentWithName(String name) {
        for (DrawerFragmentItem item : DRAWER_FRAGMENTS) {
            if (item.name.equals(name)) {
                return item;
            }
        }

        return null;
    }

    private void openFragment(DrawerFragmentItem item) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, item.createFragmentInstance())
                .commit();
    }

    private void rememberSelectedItem(DrawerFragmentItem item) {
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putString(PREF_LAST_DRAWER_FRAGMENT, item.name)
                .apply();
    }

    /**
     * See {@link #DRAWER_FRAGMENTS}
     */
    private static class DrawerFragmentItem {
        final int id;
        final String name;
        final Class<? extends Fragment> fragmentClass;
        final Bundle fragmentArguments;

        DrawerFragmentItem(int id, String name, Class<? extends Fragment> fragmentClass, Bundle fragmentArguments) {
            this.id = id;
            this.name = name;
            this.fragmentClass = fragmentClass;
            this.fragmentArguments = fragmentArguments;
        }

        DrawerFragmentItem(int id, String name, Class<? extends Fragment> fragmentClass) {
            this(id, name, fragmentClass, null);
        }

        Fragment createFragmentInstance() {
            try {
                Fragment fragment = fragmentClass.newInstance();
                fragment.setArguments(fragmentArguments);
                return fragment;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}