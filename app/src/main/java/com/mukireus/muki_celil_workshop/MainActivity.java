package com.mukireus.muki_celil_workshop;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  private FirebaseDatabase db;
  private DatabaseReference dbRef;
  private FirebaseUser fUser;
  private ArrayList<Message> chatLists = new ArrayList<>();
  private CustomAdapter customAdapter;
  private String subject;
  private ListView listView;
  private EditText inputChat;
  private ImageButton btnSend;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //listView = (ListView)findViewById(R.id.chatListView);

    inputChat = (EditText) findViewById(R.id.inputChat);
    btnSend = (ImageButton) findViewById(R.id.btnSend);
    db = FirebaseDatabase.getInstance();
    fUser = FirebaseAuth.getInstance().getCurrentUser();

    customAdapter = new CustomAdapter(getApplicationContext(), chatLists, fUser);
    listView.setAdapter(customAdapter);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      subject = bundle.getString("subject");
      dbRef = db.getReference("ChatSubjects/" + subject + "/mesaj");
      setTitle(subject);
    }
    dbRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        chatLists.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
          Message message = ds.getValue(Message.class);
          chatLists.add(message);
          //Log.d("VALUE",ds.getValue(Message.class).getMesajText());
        }
        customAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    btnSend.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if(inputChat.getText().length()>=6){
          Message message = new Message(inputChat.getText().toString(),fUser.getEmail());
          dbRef.push().setValue(message);
          inputChat.setText("");
        }else{
          Toast.makeText(getApplicationContext(),"Gönderilecek mesaj uzunluğu en az 6 karakter olmalıdır!",Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
}
