package com.example.f6feizbakhsh.QR_Login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class login extends AppCompatActivity implements View.OnClickListener{

    Button btLogin;
    EditText etUsername;
    UserLocalStore userLocalStore;
    CheckBox checkBox;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate( R.menu.main_menu_login,menu );
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int y=0;
        switch(item.getItemId())
        {
            case R.id.logout_button_x:
                if(((checkBox)).isChecked()){
                    User user = new User(etUsername.getText().toString(),1,99," " );
                    userLocalStore.storeUserData(user);
                } else {
                    userLocalStore.clearUserData();
                }
                int p = android.os.Process.myPid();
                android.os.Process.killProcess(p);
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Entered onCreate","");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        etUsername = (EditText) findViewById(R.id.etUsername);
        btLogin    = (Button) findViewById(R.id.btLogin);
        btLogin.setOnClickListener(this);
        checkBox   = (CheckBox) findViewById(R.id.remember_me );
        userLocalStore = new UserLocalStore(this);
        int  xy=userLocalStore.getLoggedInUser().check;
        if (xy==1) {
            etUsername.setText(userLocalStore.getLoggedInUser().username);
            checkBox.setChecked( true );
        }
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        Log.d("onSupport", "");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btLogin:
                /*
                Put the result of the QR Code here.
                 */
                String username = etUsername.getText().toString();
                int check=-1;
                if(((checkBox)).isChecked())
                {
                    check = 1;
                }
                User user = new User( username, check,0,"");
                authenticate(user);
                break;
        }
    }


    private void authenticate(User user){
        Log.d("Entered authenticate","");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser){
                if (returnedUser == null) {
                    showErrorMessage();
                }else {
                    loginUserIn(returnedUser);
                }
            }
        });
    }
    private void showErrorMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(login.this);
        dialogBuilder.setMessage("Incorrect user Detail");
        dialogBuilder.setPositiveButton("Ok",null);
        dialogBuilder.show();
    }

    private void loginUserIn(User returnedUser){
        if(((checkBox)).isChecked()){
            String x="hello";
        }
        //startActivity(new Intent(this,MainActivity.class));
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("id", returnedUser.id);
        startActivity(intent);
        //startActivity(new Intent(this,MapsActivity.class));
    }
}