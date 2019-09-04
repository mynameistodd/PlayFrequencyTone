package com.example.playfrequency;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btnDecrease;
    private Button btnIncrease;
    private TextView txtLevel;

    private int level = 1;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        txtLevel = findViewById(R.id.txtLevel);

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (level > 1) {
                    level--;
                    txtLevel.setText(String.valueOf(level));
                }
                playSound(level);
            }
        });

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (level < 15) {
                    level++;
                    txtLevel.setText(String.valueOf(level));
                }
                playSound(level);
            }
        });
    }

    void playSound(final int level) {

//      Adapted from: https://riptutorial.com/android/example/28432/generate-tone-of-a-specific-frequency
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        final double duration = 0.5; // duration of sound
                        final int sampleRate = 44100; // Hz (maximum frequency is 7902.13Hz (B8))
                        final double numSamples = duration * sampleRate;

                        final double samples[] = new double[(int) numSamples];
                        final short buffer[] = new short[(int) numSamples];

                        final double freqOfTone = level * 125; // hz

                        for (int i = 0; i < numSamples; ++i) {
                            samples[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone)); // Sine wave
                            buffer[i] = (short) (samples[i] * Short.MAX_VALUE);  // Higher amplitude increases volume
                        }

                        AudioTrack audioTrack = new AudioTrack.Builder()
                                .setAudioFormat(new AudioFormat.Builder()
                                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                        .setSampleRate(sampleRate)
                                        .build())
                                .setAudioAttributes(new AudioAttributes.Builder()
                                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                        .build())
                                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                                .setBufferSizeInBytes(buffer.length)
                                .build();

                        audioTrack.write(buffer, 0, buffer.length);
                        audioTrack.play();
                    }
                });
            }
        });
        thread.start();
    }
}
