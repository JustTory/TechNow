package com.example.projectlogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainUI extends AppCompatActivity {
    //TODO upload to database
    private ArrayList<String> imageURLList;
    private CarouselView carouselView;
    private FrameLayout frameLayout;

    protected LinearLayout linearLayout;
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected static TextView tv_username;
    private AlertDialog.Builder confirmSignOutBuilder;
    protected Toolbar toolbar;
    private TextView toolbar_title;
    protected TextView noOfItemInCart;
    protected int noOfItem;
    public static final String SHARED_PREFS = "rememberMe";
    private SharedPreferences sharedPreferences;
    protected static String username;
    protected ImageButton cart_btn;

    private RecyclerView recyclerView;
    private ArrayList<Product> productList;
    private ArrayList<Product> topSellerProductList;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_u_i);

        onCreateDrawerLayout();
        loadData();
        loadCarouselView();
        loadTopSeller();
    }

    private void loadData() {
        View newview = navigationView.getHeaderView(0);
        tv_username = newview.findViewById(R.id.username);
        DatabaseRef.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.getKey();
                tv_username.setText(username);

                noOfItem = (int) snapshot.child("Cart").getChildrenCount();
                if (noOfItem == 0) {
                    noOfItemInCart.setVisibility(View.GONE);
                } else {
                    noOfItemInCart.setVisibility(View.VISIBLE);
                    noOfItemInCart.setText(String.valueOf(noOfItem));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onCreateDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_lo);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        linearLayout = findViewById(R.id.lnlo);
        toolbar_title = findViewById(R.id.toolbar_title);

        noOfItemInCart = findViewById(R.id.number_of_item_in_cart);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        confirmSignOutBuilder = new AlertDialog.Builder(this);
        frameLayout = findViewById(R.id.Frame_layout);

        cart_btn = findViewById(R.id.cart_btn);
        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case (R.id.nav_profile):
                        intent = new Intent(MainUI.this, ReenterPassword.class);
                        startActivity(intent);
                        break;
                    case (R.id.order_history):
                        /*intent = new Intent(MainUI.this, OrderHistoryActivity.class);
                        startActivity(intent);*/
                    case (R.id.keyboard):
                        changeFragment("Keyboard");
                        break;
                    case (R.id.mouse):
                        changeFragment("Mouse");
                        break;
                    case (R.id.screen):
                        changeFragment("Monitor");
                        break;
                    case (R.id.laptop):
                        changeFragment("Laptop");
                        break;
                    case (R.id.nav_home):
                        intent = new Intent(MainUI.this, MainUI.class);
                        startActivity(intent);
                        break;
                    case (R.id.about):
                        intent = new Intent(MainUI.this, AboutActivity.class);
                        startActivity(intent);
                        break;
                    case (R.id.feedback):
                        intent = new Intent(MainUI.this, FeedbackActivity.class);
                        startActivity(intent);
                        break;
                    case (R.id.Logout):
                        confirmSignOutBuilder.setTitle("Confirmation");
                        confirmSignOutBuilder.setMessage("Do you want to sign out?");
                        confirmSignOutBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(username, false);
                                editor.commit();
                                Intent intent1 = new Intent(getApplicationContext(), UserLogin.class);
                                startActivity(intent1);
                            }
                        });
                        confirmSignOutBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        confirmSignOutBuilder.create().show();
                }
                return true;
            }
        });
    }

    private void loadCarouselView() {
        carouselView = findViewById(R.id.carouselView);
        imageURLList = new ArrayList<>();
        imageURLList.add("https://firebasestorage.googleapis.com/v0/b/technow-4b3ab.appspot.com/o/UI%2Fcarousel_image_0.jpg?alt=media&token=2c3a248a-4cbf-4a0a-a129-4fc6b3039b79");
        imageURLList.add("https://firebasestorage.googleapis.com/v0/b/technow-4b3ab.appspot.com/o/UI%2Fcarousel_image_1.jpg?alt=media&token=5796c932-9872-4c4a-9c2b-ec4cb5d845ca");
        imageURLList.add("https://firebasestorage.googleapis.com/v0/b/technow-4b3ab.appspot.com/o/UI%2Fcarousel_image_2.jpg?alt=media&token=3f944783-08b2-455d-86af-1b1286bc8b53");
        imageURLList.add("https://firebasestorage.googleapis.com/v0/b/technow-4b3ab.appspot.com/o/UI%2Fcarousel_image_3.jpg?alt=media&token=3b452493-3932-4604-b815-c691587678db");
        imageURLList.add("https://firebasestorage.googleapis.com/v0/b/technow-4b3ab.appspot.com/o/UI%2Fcarousel_image_4.jpg?alt=media&token=ff4d7758-27af-414e-87f0-c1270112d73a");

        carouselView.setPageCount(imageURLList.size());
        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Glide.with(getApplicationContext()).load(imageURLList.get(position)).into(imageView);
            }
        };

        carouselView.setImageListener(imageListener);


        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        changeFragment("Mouse");
                        break;
                    case 1:
                        changeFragment("Keyboard");
                        break;
                    case 2:
                        changeFragment("Monitor");
                        break;
                    case 3:
                        changeFragment("Laptop");
                        break;
                }
            }
        });
    }

    private void loadTopSeller() {
        productList = new ArrayList<>();
        topSellerProductList = new ArrayList<>();
        reff = FirebaseDatabase.getInstance().getReference("Products");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot snapshotKeyboard = snapshot.child("Keyboard");
                for (DataSnapshot dataSnapshot : snapshotKeyboard.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }

                DataSnapshot snapshotLaptop = snapshot.child("Laptop");
                for (DataSnapshot dataSnapshot : snapshotLaptop.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }

                DataSnapshot snapshotMonitor = snapshot.child("Monitor");
                for (DataSnapshot dataSnapshot : snapshotMonitor.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }

                DataSnapshot snapshotMouse = snapshot.child("Mouse");
                for (DataSnapshot dataSnapshot : snapshotMouse.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    productList.add(product);
                }

                Collections.sort(productList, new Comparator<Product>() {
                    public int compare(Product p1, Product p2) {
                        if (p1.getSold() > p2.getSold()) return -1;
                        else if (p1.getSold() < p2.getSold()) return 1;
                        else return 0;
                    }
                });

                for (int i = 0; i < 10; i++) {
                    topSellerProductList.add(productList.get(i));
                }

                TopSellerAdapter adapter = new TopSellerAdapter(MainUI.this, topSellerProductList);
                recyclerView = findViewById(R.id.recycler_view);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void Card_onClick(View view) {
        switch (view.getId()) {
            case (R.id.keyboard_cv):
                changeFragment("Keyboard");
                break;
            case (R.id.mouse_cv):
                changeFragment("Mouse");
                break;
            case (R.id.screen_cv):
                changeFragment("Monitor");
                break;
            case (R.id.laptop_cv):
                changeFragment("Laptop");
                break;
        }

    }

    private void close_FrameLayout() {
        if (frameLayout.getVisibility() != View.GONE) {
            toolbar_title.setText("TechNow");
            frameLayout.setVisibility(View.GONE);
        }
    }

    private void open_FrameLayout() {
        if (frameLayout.getVisibility() != View.VISIBLE)
            frameLayout.setVisibility(View.VISIBLE);
    }

    private void changeFragment(String Catalog) {
        open_FrameLayout();
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (Catalog) {
            case ("Keyboard"):
                toolbar_title.setText("Keyboard");
                navigationView.setCheckedItem(R.id.keyboard);
                getSupportFragmentManager().beginTransaction().replace(R.id.Frame_layout, new Keyboard_Catalog()).commit();
                break;
            case ("Mouse"):
                toolbar_title.setText("Mouse");
                navigationView.setCheckedItem(R.id.mouse);
                getSupportFragmentManager().beginTransaction().replace(R.id.Frame_layout, new Mouse_Catalog()).commit();
                break;
            case ("Monitor"):
                toolbar_title.setText("Monitor");
                navigationView.setCheckedItem(R.id.screen);
                getSupportFragmentManager().beginTransaction().replace(R.id.Frame_layout, new Monitor_Catalog()).commit();
                break;
            case ("Laptop"):
                toolbar_title.setText("Laptop");
                navigationView.setCheckedItem(R.id.laptop);
                getSupportFragmentManager().beginTransaction().replace(R.id.Frame_layout, new Laptop_Catalog()).commit();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawer(GravityCompat.START);
        close_FrameLayout();
    }
}