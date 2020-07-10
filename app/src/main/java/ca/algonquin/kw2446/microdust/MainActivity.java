package ca.algonquin.kw2446.microdust;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;

import ca.algonquin.kw2446.microdust.common.AddCoodicationDialogFragment;
import ca.algonquin.kw2446.microdust.common.AddLocationDialogFragment;
import ca.algonquin.kw2446.microdust.db.LocationRealmObject;
import ca.algonquin.kw2446.microdust.finedust.FineDustContract;
import ca.algonquin.kw2446.microdust.finedust.FineDustFragment;
import ca.algonquin.kw2446.microdust.util.GeoUtil;
import ca.algonquin.kw2446.microdust.util.IntentUtil;
import io.realm.Realm;
import io.realm.RealmResults;

import android.Manifest;

import android.content.pm.PackageManager;
import android.location.Location;

import android.widget.Toast;



import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_FINE_COARSE_PERMISSION = 1000;
    private FusedLocationProviderClient mFusedLocationClient;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ArrayList<Pair<Fragment, String>> mFragmentList;

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

       mRealm = Realm.getDefaultInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddLocationDialogFragment.newInstance(new AddLocationDialogFragment.OnClickListener() {
                    @Override
                    public void onOkClicked(final String city) {
                        GeoUtil.getLocationFromName(MainActivity.this,
                                city, new GeoUtil.GeoUtilListener() {
                                    @Override
                                    public void onSuccess(double lat, double lng, String name) {
                                        saveNewCity(lat, lng, city);
                                        addNewFragment(lat, lng, city);
                                        mViewPager.setCurrentItem(mFragmentList.size()-1);
                                    }

                                    @Override
                                    public void onError(String message) {
                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).show(getSupportFragmentManager(), "dialog");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpViewPager();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void saveNewCity(double lat, double lng, String city) {
        mRealm.beginTransaction();
        LocationRealmObject newLocationRealmObject = mRealm.createObject(LocationRealmObject.class);
        newLocationRealmObject.setName(city);
        newLocationRealmObject.setLat(lat);
        newLocationRealmObject.setLng(lng);
        mRealm.commitTransaction();
    }

    private void addNewFragment(double lat, double lng, String city) {
        mFragmentList.add(new Pair<Fragment, String>(FineDustFragment.newInstance(lat, lng), city));
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    private void setUpViewPager() {
        mTabLayout = (TabLayout) findViewById(R.id.tabDust);
        mViewPager = (ViewPager) findViewById(R.id.vpDust);

        loadDbData();

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void loadDbData() {
        RealmResults<LocationRealmObject> realmResults = mRealm.where(LocationRealmObject.class).findAll();

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new Pair<Fragment, String>(new FineDustFragment(), "Current"));
        for (LocationRealmObject realmObject : realmResults) {
            mFragmentList.add(new Pair<Fragment, String>(FineDustFragment.newInstance(realmObject.getLat(), realmObject.getLng()), realmObject.getName()));
        }
    }

    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_FINE_COARSE_PERMISSION);
            return;
        }

        //device location
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            FineDustContract.View view = (FineDustContract.View) mFragmentList.get(0).first;
                            view.newLoad(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_FINE_COARSE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()){
            case R.id.action_all_delete:
                mRealm.beginTransaction();
                mRealm.where(LocationRealmObject.class).findAll().deleteAllFromRealm();
                mRealm.commitTransaction();
                setUpViewPager();
                return true;
            case R.id.action_delete:
                if (mTabLayout.getSelectedTabPosition() == 0) {
                    Toast.makeText(this, "This Tab can't delete.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mRealm.where(LocationRealmObject.class).findAll().get(mTabLayout.getSelectedTabPosition() - 1).deleteFromRealm();
                    }
                });
//            mRealm.beginTransaction();
//            mRealm.where(LocationRealmObject.class).findAll().get(mTabLayout.getSelectedTabPosition() - 1).deleteFromRealm();
//            mRealm.commitTransaction();
                setUpViewPager();
                return true;
            case R.id.action_add:
                AddCoodicationDialogFragment.newInstance(new AddCoodicationDialogFragment.OnClickListener() {
                    @Override
                    public void onOkClicked(final double lat, double lng) {
                        GeoUtil.getFromLocation(MainActivity.this,
                                lat, lng, new GeoUtil.GeoUtilListener() {
                                    @Override
                                    public void onSuccess(double lat, double lng,String name) {
                                        saveNewCity(lat, lng, name);
                                        addNewFragment(lat, lng, name);
                                        mViewPager.setCurrentItem(mFragmentList.size()-1);
                                    }

                                    @Override
                                    public void onError(String message) {
                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).show(getSupportFragmentManager(), "dialog");
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    private static class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Pair<Fragment, String>> mFragmentList;

        public MyPagerAdapter(FragmentManager fm, List<Pair<Fragment, String>> fragmentList) {
            super(fm);
            mFragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position).first;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentList.get(position).second;
        }
    }
}