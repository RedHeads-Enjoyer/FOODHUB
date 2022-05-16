package com.example.foodhub.Search;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhub.R;
import com.example.foodhub.Recipe;
import com.example.foodhub.Step;
import com.example.foodhub.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OpenRecipeStepsAdapter extends RecyclerView.Adapter<OpenRecipeStepsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Step> steps;
    Step step;
    CountDownTimer countDownTimer;
    boolean isTimerRunning = false;
    long START_TIME;
    long timerTimeLeft;
    MediaPlayer mediaPlayer;

    public OpenRecipeStepsAdapter(Context context, List<Step> steps) {
        this.inflater = LayoutInflater.from(context);
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.open_recipe_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        step = steps.get(position);
        holder.stepDesc.setText(step.getDesc());
        holder.stepPosition.setText("Этап " + Integer.toString(holder.getAdapterPosition() + 1));

        holder.stepDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(inflater.getContext(), step.getSec().toString(), Toast.LENGTH_LONG).show();
            }
        });

        if (step.getHour() != 0 || step.getMin() !=0 || step.getSec() !=0) {
            START_TIME = step.getHour() * 3600 * 1000 + step.getMin() * 60 * 1000 + step.getSec() * 1000;
            timerTimeLeft = START_TIME;

            holder.timeLeft.setVisibility(View.VISIBLE);
            holder.timerStartStop.setVisibility(View.VISIBLE);
            holder.timerReset.setVisibility(View.INVISIBLE);

            holder.timerStartStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isTimerRunning) {
                        countDownTimer.cancel();
                        isTimerRunning = false;
                        holder.timerStartStop.setText("начать");
                        holder.timerReset.setVisibility(View.VISIBLE);
                    }else {
                        countDownTimer = new CountDownTimer(timerTimeLeft, 1000) {
                            @Override
                            public void onTick(long l) {
                                timerTimeLeft = l;
                                int hours = (int) timerTimeLeft / 1000 / 3600;
                                int minitues = (int) timerTimeLeft / 1000 / 60 % 60;
                                int sec = (int) timerTimeLeft / 1000 % 60;
                                String timeLeftString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours,minitues, sec);
                                holder.timeLeft.setText(timeLeftString);
                            }

                            @Override
                            public void onFinish() {
                                isTimerRunning = false;
                                holder.timerStartStop.setText("начать");
                                holder.timerStartStop.setVisibility(View.INVISIBLE);
                                holder.timerReset.setVisibility(View.VISIBLE);
                                mediaPlayer = MediaPlayer.create(inflater.getContext(), R.raw.timer);
                                mediaPlayer.start();


                            }
                        }.start();
                        isTimerRunning = true;
                        holder.timerStartStop.setText("пауза");
                        holder.timerReset.setVisibility(View.INVISIBLE);
                    }
                }
            });

            holder.timerReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timerTimeLeft = START_TIME;
                    int hours = (int) timerTimeLeft / 1000 / 3600;
                    int minitues = (int) timerTimeLeft / 1000 / 60 % 60;
                    int sec = (int) timerTimeLeft / 1000 % 60;
                    String timeLeftString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours,minitues, sec);
                    holder.timeLeft.setText(timeLeftString);
                    holder.timerReset.setVisibility(View.INVISIBLE);
                    holder.timerStartStop.setVisibility(View.VISIBLE);
                }
            });
            int hours = (int) timerTimeLeft / 1000 / 3600;
            int minitues = (int) timerTimeLeft / 1000 / 60 % 60;
            int sec = (int) timerTimeLeft / 1000 % 60;
            String timeLeftString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours,minitues, sec);
            holder.timeLeft.setText(timeLeftString);
        }
    }

    @Override
    public int getItemCount() {
        if (steps == null) return 0;
        return steps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepDesc, timeLeft, stepPosition;
        Button timerStartStop, timerReset;
        ViewHolder(View view){
            super(view);
            stepPosition = view.findViewById(R.id.openRecipeStepPosition);
            stepDesc = view.findViewById(R.id.openRecipeStepDesc);
            timeLeft = view.findViewById(R.id.timerTimeLeft);
            timerReset = view.findViewById(R.id.timerResetTimer);
            timerStartStop = view.findViewById(R.id.timerStartTimer);
        }
    }

}
