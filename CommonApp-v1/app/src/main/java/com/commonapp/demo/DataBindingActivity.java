package com.commonapp.demo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by prashant.patel on 6/1/2017.
 */

public class DataBindingActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* DataBindingActivity binding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding);
        DataBindingModel user = new DataBindingModel("Test", "User");
        binding.setUser(user);*/

      /* final  DataBindingActivity activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding);
        activityMainBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityMainBinding.tvTitle.setText(R.string.app_name);
            }
        });*/

      //  setContentView(R.layout.activity_data_binding);
    }
}
