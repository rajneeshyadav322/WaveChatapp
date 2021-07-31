package com.rajdroid.wave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rajdroid.wave.Class.Messages;
import com.rajdroid.wave.Class.MessagesAdapter;
import com.rajdroid.wave.databinding.ActivitySpecificChatBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class SpecificChatActivity extends AppCompatActivity {

    private String enteredMessage;
    String receiverName, receiverUid, senderUid;
    String senderRoom;
    String receiverRoom;

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    String currentTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    Intent intent;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;

    ActivitySpecificChatBinding binding;


    private String encryptedMessage, decryptedMessage;
    private byte encryptionKey[] = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;



    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpecificChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();


        messagesArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.specificChatRecyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(SpecificChatActivity.this, messagesArrayList);
        binding.specificChatRecyclerView.setAdapter(messagesAdapter);

        messagesAdapter.notifyDataSetChanged();

        intent = getIntent();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");



        senderUid = firebaseAuth.getUid();
        receiverUid = getIntent().getStringExtra("receiverUid");
        receiverName = getIntent().getStringExtra("name");

        //Creating room
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;


        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");

        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }


        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Messages chat = snapshot1.getValue(Messages.class);
                    try {
                        decryptedMessage = AESDecryptionMethod(chat.getMessage());
                        chat.setMessage(decryptedMessage);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    messagesArrayList.add(chat);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });



        binding.specificChatBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        receiverName = getIntent().getStringExtra("name");
        binding.specificUserName.setText(receiverName);


        String uri = intent.getStringExtra("imageUrl");
        if (uri.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Unable to load image", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.get().load(uri).into(binding.viewUserImageInImageView);
        }


        binding.sendMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enteredMessage = binding.getMessage.getText().toString();
                if (enteredMessage.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Enter Message First", Toast.LENGTH_SHORT).show();
                }
                else {

                    Date date = new Date();
                    currentTime = simpleDateFormat.format(calendar.getTime());

                    try {
                        encryptedMessage = AESEncryptionMethod(binding.getMessage.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Messages messages = new Messages(encryptedMessage, firebaseAuth.getUid(), date.getTime(), currentTime);
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push()
                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {


                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            firebaseDatabase = FirebaseDatabase.getInstance();
                            firebaseDatabase.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {

                                }
                            });
                        }
                    });

                    binding.getMessage.setText(null);
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(messagesAdapter != null) {
            messagesAdapter.notifyDataSetChanged();
        }
    }


    private String AESEncryptionMethod(String string){

        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String returnString = null;

        try {
            returnString = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    private String AESDecryptionMethod(String string) throws UnsupportedEncodingException {
        byte[] EncryptedByte = string.getBytes("ISO-8859-1");
        String decryptedString = string;

        byte[] decryption;

        try {
            decipher.init(cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }


}