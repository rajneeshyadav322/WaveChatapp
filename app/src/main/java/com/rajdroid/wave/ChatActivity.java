package com.rajdroid.wave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rajdroid.wave.Class.firebaseModel;
import com.rajdroid.wave.databinding.ActivityChatBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import static android.graphics.Color.GREEN;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    LinearLayoutManager linearLayoutManager;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    ImageView imageViewOfUser;

    FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder> chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        //Query query = firebaseFirestore.collection("Users");
        Query query = firebaseFirestore.collection("Users").whereNotEqualTo("Uid", firebaseAuth.getUid());


        FirestoreRecyclerOptions<firebaseModel> allUserName = new FirestoreRecyclerOptions.Builder<firebaseModel>().setQuery(query, firebaseModel.class).build();

        chatAdapter = new FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>(allUserName) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull NoteViewHolder holder, int position, @NonNull @NotNull firebaseModel model) {

                holder.nameOfUser.setText(model.getName());
                String uri = model.getImage();

                Picasso.get().load(uri).into(imageViewOfUser);

                if (model.getStatus().equals("Online")) {
                    holder.statusOfUser.setText(model.getStatus());
                    holder.statusOfUser.setTextColor(Color.GREEN);
                }
                else {
                    holder.statusOfUser.setText(model.getStatus());
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Item is clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SpecificChatActivity.class);
                        intent.putExtra("name", model.getName());
                        intent.putExtra("receiverUid", model.getUid());
                        intent.putExtra("imageUrl", model.getImage());
                        startActivity(intent);
                    }
                });

            }


            @NotNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view, parent, false);
                return new NoteViewHolder(view);
            }
        };

        binding.recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getBaseContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(chatAdapter);


    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView nameOfUser;
        private TextView statusOfUser;

        public NoteViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameOfUser = itemView.findViewById(R.id.nameOfUser);
            statusOfUser = itemView.findViewById(R.id.statusOfUser);
            imageViewOfUser = itemView.findViewById(R.id.userImage);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.profile:
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.settings:
                Toast.makeText(getApplicationContext(), "settings clicked", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }



    @Override
    protected void onStart() {
        super.onStart();
        chatAdapter.startListening();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("Status","Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(chatAdapter != null) {
            chatAdapter.stopListening();
        }
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("Status","Offline");
    }

}