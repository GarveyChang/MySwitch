package com.itheima.myswitch78;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.itheima.myswitch78.MySwitch.OnCheckChangeListener;

public class MainActivity extends Activity {

	private MySwitch mSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSwitch = (MySwitch) findViewById(R.id.myswitch);

		// CheckBox check = new CheckBox(this);
		// check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		//
		// }
		// });

		// 4. 设置回调监听,实现监听逻辑
		mSwitch.setOnCheckChangeListener(new OnCheckChangeListener() {

			@Override
			public void onCheckChanged(View view, boolean isOpen) {
				Toast.makeText(getApplicationContext(), "当前状态:" + isOpen,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

}
