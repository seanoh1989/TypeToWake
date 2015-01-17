package com.wecredible.typetowake;

/**
 * Created by Sean on 1/16/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Button;

public class AlarmDetailsActivity extends Activity {

    private AlarmDBHelper dbHelper = new AlarmDBHelper(this);

    private AlarmModel alarmDetails;

    private TimePicker timePicker;
    private EditText edtName;

    private CustomSwitch btnMonday;
    private CustomSwitch btnTuesday;
    private CustomSwitch btnWednesday;
    private CustomSwitch btnThursday;
    private CustomSwitch btnFriday;
    private CustomSwitch btnSaturday;
    private CustomSwitch btnSunday;

    private CustomSwitch chkWeekly;

    private TextView txtToneSelection;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_details);

        getActionBar().setTitle("Create A New Alarm");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
        edtName = (EditText) findViewById(R.id.alarm_details_name);
        chkWeekly = (CustomSwitch) findViewById(R.id.alarm_details_repeat_weekly);

        btnSunday = (CustomSwitch) findViewById(R.id.sun_button);
        btnMonday = (CustomSwitch) findViewById(R.id.mon_button);
        btnTuesday = (CustomSwitch) findViewById(R.id.tue_button);
        btnWednesday = (CustomSwitch) findViewById(R.id.wed_button);
        btnThursday = (CustomSwitch) findViewById(R.id.thu_button);
        btnFriday = (CustomSwitch) findViewById(R.id.fri_button);
        btnSaturday = (CustomSwitch) findViewById(R.id.sat_button);

        txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);

        saveButton = (Button) findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            }
        });

        long id = getIntent().getExtras().getLong("id");

        if (id == -1) {
            alarmDetails = new AlarmModel();
        } else {
            alarmDetails = dbHelper.getAlarm(id);

            timePicker.setCurrentMinute(alarmDetails.timeMinute);
            timePicker.setCurrentHour(alarmDetails.timeHour);

            edtName.setText(alarmDetails.name);

            chkWeekly.setChecked(alarmDetails.repeatWeekly);

            btnSunday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
            btnMonday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
            btnTuesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
            btnWednesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
            btnThursday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
            btnFriday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.FRIDAY));
            btnSaturday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));

            txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
        }

        final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent , 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_save_alarm_details: {
                updateModelFromLayout();

                AlarmManagerHelper.cancelAlarms(this);

                if (alarmDetails.id < 0) {
                    dbHelper.createAlarm(alarmDetails);
                } else {
                    dbHelper.updateAlarm(alarmDetails);
                }

                AlarmManagerHelper.setAlarms(this);

                setResult(RESULT_OK);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateModelFromLayout() {
        alarmDetails.timeMinute = timePicker.getCurrentMinute().intValue();
        alarmDetails.timeHour = timePicker.getCurrentHour().intValue();
        alarmDetails.name = edtName.getText().toString();
        alarmDetails.repeatWeekly = chkWeekly.isChecked();

        alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, btnSunday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.MONDAY, btnMonday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, btnTuesday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, btnWednesday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, btnThursday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.FRIDAY, btnFriday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, btnSaturday.isChecked());

        alarmDetails.isEnabled = true;
    }

}
