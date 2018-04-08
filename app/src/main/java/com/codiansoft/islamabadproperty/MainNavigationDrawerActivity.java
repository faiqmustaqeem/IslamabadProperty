package com.codiansoft.islamabadproperty;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codiansoft.islamabadproperty.Fragments.AddPropertyFragment;
import com.codiansoft.islamabadproperty.Fragments.DashboardFragment;
import com.codiansoft.islamabadproperty.Fragments.MyPropertiesFragment;

public class MainNavigationDrawerActivity extends AppCompatActivity
      {

    public static int navItemIndex = 0;

    private NavigationView navigationView;
    DrawerLayout drawer;
    // tags used to attach the fragments
    private static final String TAG_DASHBOARD = "dashboard";
    private static final String TAG_ADD_PROPERTY = "add_property";
          private static final String TAG_MY_PROPERTY = "my_property";
    private static final String TAG_LOGOUT = "logout";

    public static String CURRENT_TAG = TAG_DASHBOARD;
    private boolean shouldLoadDashboardFragOnBackPress = true;
    private Handler mHandler;
    Activity activity;

        public static ImageView calendar;
    // toolbar titles respected to selected nav menu item
    //private String[] activityTitles={"Properties" , "Add Property" };
    public static Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation_drawer);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        calendar=(ImageView) findViewById(R.id.calendar);
        mHandler = new Handler();


        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_DASHBOARD;
            loadHomeFragment();
        }
    }
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }


        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {


                    Fragment fragment = getHomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                    fragmentTransaction.commitAllowingStateLoss();


            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers();

        invalidateOptionsMenu();
    }
    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // dashboard
                DashboardFragment dashboardFragment = new DashboardFragment();
                return dashboardFragment;

            case 1:
                AddPropertyFragment addPropertyFragment=new AddPropertyFragment();
                return addPropertyFragment;

            case 2:
                MyPropertiesFragment myPropertiesFragment=new MyPropertiesFragment();
                return myPropertiesFragment;

            default:
                return new DashboardFragment();

        }

    }

//    private void setToolbarTitle() {
//        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
//    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_dashboard:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_DASHBOARD;
                        calendar.setVisibility(View.VISIBLE);
                        break;

                    case R.id.nav_add_property:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ADD_PROPERTY;
                        calendar.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.nav_my_properties:
                        navItemIndex=2;
                        CURRENT_TAG=TAG_MY_PROPERTY;
                        calendar.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.nav_logout:

                        MaterialDialog dialog = new MaterialDialog.Builder(MainNavigationDrawerActivity.this)
                                .title("Logout")
                                .content("are you sure you want to logout ?")
                                .positiveText("Yes")
                                .negativeText("No")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        logout();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    }
                                })
                                .show();
                        break;

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadDashboardFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_DASHBOARD;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.activity_main_navigation_drawer_drawer, menu);
        return false;
    }

    public void logout()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        Intent intent=new Intent(MainNavigationDrawerActivity.this , RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);



    }


}
