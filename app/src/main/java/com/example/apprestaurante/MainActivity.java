package com.example.apprestaurante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance(); //instancia de firestore
    String idCustomer; //variable que contendra el id de cada cliente


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Instanciar y refereniar los elementos ids del archivo xml
        EditText ident = findViewById(R.id.etident);
        EditText fullname = findViewById(R.id.etfullname);
        EditText email = findViewById(R.id.etemail);
        EditText password = findViewById(R.id.etpassword);
        Button btnsave = findViewById(R.id.save);
        Button btnsearch = findViewById(R.id.search);
        Button btnedit = findViewById(R.id.update);
        Button btndelete = findViewById(R.id.delete);

        btndelete.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             //confirmacion del borrado
                                             AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                             alertDialogBuilder.setMessage("Está seguro de eliminar el cliente con id " + ident.getText().toString() + "?");
                                             alertDialogBuilder.setPositiveButton("Sí",
                                                     new DialogInterface.OnClickListener() {

                                                         @Override
                                                         public void onClick(DialogInterface arg0, int arg1) {
                                                             //seeliminara el cliente con el idCustomer respectivo
                                                             db.collection("customer").document(idCustomer)
                                                                     .delete()
                                                                     .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                         @Override
                                                                         public void onSuccess(Void aVoid) {
                                                                             //Log.d("cliente", "DocumentSnapshot successfully deleted!");
                                                                             Toast.makeText(MainActivity.this, "Cliente borrado correctamente...", Toast.LENGTH_SHORT).show();
                                                                         }
                                                                     })
                                                                     .addOnFailureListener(new OnFailureListener() {
                                                                         @Override
                                                                         public void onFailure(@NonNull Exception e) {
                                                                             Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                         }
                                                                     });
                                                         }


                                                     });

                                             alertDialogBuilder.setNegativeButton("No",
                                                     new DialogInterface.OnClickListener() {

                                                         @Override
                                                         public void onClick(DialogInterface arg0, int arg1) {

                                                         }
                                                     });

                                             AlertDialog alertDialog = alertDialogBuilder.create();
                                             alertDialog.show();
                                         }
                                     });


        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> uCustomer = new HashMap<>();
                uCustomer.put("fullname", fullname.getText().toString());
                uCustomer.put("email", email.getText().toString());
                uCustomer.put("password", password.getText().toString());
                uCustomer.put("ident", ident.getText().toString());

                db.collection("customer").document(idCustomer)
                        .set(uCustomer)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d("cliente", "DocumentSnapshot successfully written!");
                                Toast.makeText(MainActivity.this,"Cliente actualizado correctmente...",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("customer")
                        .whereEqualTo("ident", ident.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) { //si encontró el documento
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            idCustomer = document.getId();
                                            fullname.setText(document.getString("fullname"));
                                            email.setText(document.getString("email"));
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"El ID del cliente no existe",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                saveCustomer(ident.getText().toString(), fullname.getText().toString(), email.getText().toString(), password.getText().toString());
            }
        });

    }


    private void saveCustomer(String sident, String sfullname, String semail, String spassword) {

        //buscar la identificacion del cliente nuevo
        db.collection("customer")
                .whereEqualTo("ident", sident)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) { //no encontró el documento, se puede guardar

                                //guardar los datos del cliente (customer)
                                Map<String, Object> customer = new HashMap<>();
                                customer.put("ident", sident);
                                customer.put("fullname", sfullname);
                                customer.put("email", semail);
                                customer.put("password", spassword);//esta guardando los datos en customer

                                db.collection("customer")
                                        .add(customer)//guarda los datos en la db
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                // Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                Toast.makeText(getApplicationContext(), "Cliente agregado correctamente", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error ... cliente no se guardo", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"El ID del cliente ya existe",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


    }

}
