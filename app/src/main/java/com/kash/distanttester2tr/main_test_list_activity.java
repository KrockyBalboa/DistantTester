package com.kash.distanttester2tr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class main_test_list_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseFirestore db;
    TableLayout tl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test_list_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db= FirebaseFirestore.getInstance();

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //перейти к окну создания теста

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent test_create = new Intent(getApplicationContext(),Test_creating.class);
                startActivity(test_create);
            }
        });

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tl = findViewById(R.id.test_list_table_layout);

        db.collection("Tests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Test[] tests=(Test[]) queryDocumentSnapshots.getDocuments().toArray();
                int testNum=queryDocumentSnapshots.getDocuments().size();
                List<DocumentSnapshot>docs=queryDocumentSnapshots.getDocuments();
                for(int i=0;i<testNum;i++){
                    final String authorname, testname;
                    authorname=String.valueOf(docs.get(i).get("testAuthor"));
                    testname=String.valueOf(docs.get(i).get("title"));
                    final String documentReference = queryDocumentSnapshots.getDocuments().get(i).getReference().getId();
                    TableRow tr = new TableRow(getApplicationContext());
                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    TextView testName = new TextView(getApplicationContext());
                    testName.setTextSize(20);
                    testName.setPadding(10,10,10,10);

                    testName.setText(testname +" made by "+authorname);
                    tr.addView(testName);

                    tr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent testActivity = new Intent(getApplicationContext(),TestActivity.class);
                            testActivity.putExtra("DocumentReference",documentReference);
                            startActivity(testActivity);
                            Log.d("TableRow debug","Leaving to "+testname+" from table");
                        }
                    });
                    Log.d("TableRow debug",testname+" added to table rows");
                    tr.setVisibility(View.VISIBLE);

                    tl.addView(tr,i);
                }
            }
        });

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_test_list_activity, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //перейти на страницы профиля, пользователей, главную страницу тестов и в настройки
        switch (id){
            case R.id.nav_profile:{}
            case R.id.nav_users:{}
            case R.id.nav_tests:{}
            case R.id.nav_settings:{}
            default:{}
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
