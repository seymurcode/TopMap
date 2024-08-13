package com.artileriya.uicomponents.login

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.artileriya.uicomponents.MainActivity
import com.artileriya.uicomponents.R
import com.artileriya.uicomponents.databinding.ActivityLoginBinding
import com.artileriya.uicomponents.model.artileriya.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.prefs.Preferences

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    lateinit var prefs : SharedPreferences;

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        prefs = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
        if(prefs.getBoolean("login",false))
            toMain()
        else{
            //val userUsername=binding.editTextUsername.text.toString().trim()
            //val userPassword=binding.editTextPassword.text.toString().trim()

            //val userUsername=findViewById<EditText>(R.id.editTextUsername).text.toString()
            //val userPassword=findViewById<EditText>(R.id.editTextPassword).text.toString()

            val reference = database.getReference("users")

            binding.buttonLogin.setOnClickListener{
                val userUsername=binding.editTextUsername.text.toString().trim()
                val userPassword=binding.editTextPassword.text.toString().trim()
                Log.v("-->editTextUsername",userUsername)
                Log.v("-->userPassword",userPassword)
                //Log.v("-->editTextUsername2",findViewById<EditText>(R.id.editTextUsername).text.toString())
                if(!userUsername.isEmpty() && !userPassword.isEmpty()){
                    val query: Query = reference.orderByChild("username").equalTo(userUsername)

                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // dataSnapshot is the "issue" node with all children with id 0

                                for (user in dataSnapshot.children) {
                                    // do something with the individual "issues"
                                    val usersBean: User? = user.getValue(User::class.java)


                                    if (usersBean?.password.equals(userPassword)) {
                                        Toast.makeText(this@LoginActivity, "Giriş uğurlu oldu", Toast.LENGTH_SHORT).show()
                                        val editor = prefs.edit()
                                        editor.putBoolean("login", true)
                                        editor.putString("usertype", usersBean?.usertype)
                                        editor.putString("parent_user", usersBean?.parent_user)
                                        editor.putString("usertype", usersBean?.usertype)

                                        editor.apply()

                                        toMain()
                                    } else {
                                        Toast.makeText(this@LoginActivity, "Giriş məlumatları səhvdir", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(this@LoginActivity, "Error: "+databaseError.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                else
                    Toast.makeText(this@LoginActivity, "Məlumatları tam daxil edin", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun toMain(){




        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        //intent.putExtra("key", value)
        startActivity(intent)
        finish(); // if the activity running has it's own context

    }
}