package cz.tomas_franke.sevendisptest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    SevenDisp disp1;
    SeekBar bar1;
    CheckBox check1;
    RadioGroup gr1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        disp1 = (SevenDisp) findViewById(R.id.disp1);
        bar1 = (SeekBar) findViewById(R.id.bar1);
        check1 = (CheckBox) findViewById(R.id.check1);
        gr1 = (RadioGroup) findViewById(R.id.gr1);

        bar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                disp1.Disp(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        check1.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                disp1.setDP(isChecked);
            }
        });

        gr1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {

                    case R.id.btnL:
                        check1.setClickable(true);
                        disp1.setPointLeft(true);
                        disp1.setPointVisible(true);
                        break;

                    case R.id.btnN:
                        check1.setClickable(false);
                        disp1.setPointVisible(false);
                        break;

                    case R.id.btnR:
                        check1.setClickable(true);
                        disp1.setPointLeft(false);
                        disp1.setPointVisible(true);
                        break;
                }
            }
        });

        gr1.check(R.id.btnR);
    }

}
