package com.example.loginapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.loginapp.Retrofit.Client
import com.example.loginapp.Retrofit.IService
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.rengwuxian.materialedittext.MaterialEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    lateinit var iService: IService;
    internal var compositeDisposable = CompositeDisposable();

    override fun onStop() {
        compositeDisposable.clear();
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        // Initialize API
        val retrofit = Client.getInstance();
        iService = retrofit.create(IService::class.java);

        val button = findViewById<Button>(R.id.btn_login);
        val email = findViewById<com.rengwuxian.materialedittext.MaterialEditText>(R.id.edit_email);
        val password = findViewById<com.rengwuxian.materialedittext.MaterialEditText>(R.id.edit_password);
        val create = findViewById<TextView>(R.id.txt_create_account);

        button.setOnClickListener() {
            loginUser(email.text.toString(), password.text.toString());
        }

        create.setOnClickListener() {
            val itemView = LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.register, null);

            MaterialStyledDialog.Builder(this@MainActivity)
                .setTitle("REGISTRATION")
                .setDescription("Please fill in all the fields!")
                .setCustomView(itemView)
                .setNegativeText("CANCEL")
                .onNegative{dialog, _ -> dialog.dismiss()}
                .setPositiveText("REGISTER")
                .onPositive{_, _ ->
                    val edit_email = itemView.findViewById<View>(R.id.edit_email) as MaterialEditText
                    val edit_name = itemView.findViewById<View>(R.id.edit_name) as MaterialEditText
                    val edit_password = itemView.findViewById<View>(R.id.edit_password) as MaterialEditText

                    registerUser(edit_email.text.toString(), edit_name.text.toString(), edit_password.text.toString());
                }
        }



    }

    private fun registerUser(email: String, name: String, password: String) {
        compositeDisposable.add(iService.registerUser(email, name, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                    result -> Toast.makeText(this@MainActivity, "" + result, Toast.LENGTH_SHORT).show()
            });
    }

    private fun loginUser(email: String, password: String) {
        compositeDisposable.add(iService.loginUser(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                result -> Toast.makeText(this@MainActivity, "" + result, Toast.LENGTH_SHORT).show()
            });
    }
}