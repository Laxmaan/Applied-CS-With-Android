package ai.laxmaan.scarnesdice;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnScoreChangeListener {
    Scores scores = new Scores(this);
    int turn_score=0,roll,no_rolls=0;
    boolean gameOver=false;
    Random random = new Random();
    ImageView diceView;
    Drawable transparent;
    TextView userScore,compScore,turnScore;
    Button roll_button,hold_button,reset_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        diceView=findViewById(R.id.dieFace);
        transparent = diceView.getDrawable();
        userScore = findViewById(R.id.yourScore);
        compScore = findViewById(R.id.computerScore);
        turnScore = findViewById(R.id.turn_Score);
        roll_button = findViewById(R.id.button);
        hold_button = findViewById(R.id.button2);
        reset_button = findViewById(R.id.button3);
    }

    public void rollButton(View view){
        roll=rollDie();
        if(roll==1) {
            turn_score=0;
            //TURN OVER
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerTurn();
                }
            },1000);

        }

        else{
            turn_score+=roll;
        }
        turnScore.setText("Turn Score = "+turn_score);


    }

    public void holdButton(View view){
        scores.incrementUserScore(turn_score);
        turn_score=0;
        turnScore.setText("");
        userScore.setText("Your Score : "+scores.getUser_score());
        if(!gameOver)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerTurn();
                }
            },1000);
    }

    public void resetButton(View view){
        scores=new Scores(this);
        userScore.setText(R.string.user_score_initial);
        compScore.setText(R.string.comp_score_initial);
        turnScore.setText("");
        roll_button.setEnabled(true);
        hold_button.setEnabled(true);
        reset_button.setEnabled(true);
        gameOver=false;
    }
    void computerTurn(){
        roll_button.setEnabled(false);
        hold_button.setEnabled(false);
        reset_button.setEnabled(false);
        no_rolls=0;


        int times_to_roll = new Random().nextInt(7)+1;      // It rolls randomly between 1-7 turns and then holds.
        Log.d("COMPROLL","rolling for turns: "+times_to_roll);  //UNCOMMENT to see how many times it rolls
        final Handler handler = new Handler();
        final int finalTimes_to_roll = times_to_roll;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                roll=rollDie();
                Log.d("COMPROLL","rolled a "+roll);
                turn_score+=roll!=1?roll:0;
                no_rolls++;
                if(roll!=1&&no_rolls< finalTimes_to_roll)
                    handler.postDelayed(this,500);
                else {
                    handler.removeCallbacks(this);
                    turn_score=(roll==1)?0:turn_score;
                    scores.incrementCompScore(turn_score);
                    compScore.setText("Computer Score : "+scores.getComp_score());
                    String msg = (roll==1)?"Computer rolled a 1":"Computer decided to hold. Turn score: "
                            +turn_score;

                    if(!gameOver) {
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                        roll_button.setEnabled(true);
                        hold_button.setEnabled(true);
                        reset_button.setEnabled(true);
                        turn_score = 0;


                    }
                    else
                        reset_button.setEnabled(true);
                }


            }
        };





        handler.postDelayed(runnable,0);



        Log.d("COMPROLL", String.valueOf(turn_score));





    }
    public int rollDie(){
        int roll=random.nextInt(6)+1;

        int id;
        switch (roll){
            case 1: id=R.drawable.dice1;
                break;
            case 2: id=R.drawable.dice2;
                break;
            case 3: id=R.drawable.dice3;
                break;
            case 4: id=R.drawable.dice4;
                break;
            case 5: id=R.drawable.dice5;
                break;
            default:
                id=R.drawable.dice6;
        }
        Animation animation = new TranslateAnimation(0f,20f,0f,0.4f);
        animation.setInterpolator(new LinearInterpolator());
        diceView.startAnimation(animation);
        diceView.setImageDrawable(getResources().getDrawable(id,null));

        return roll;
    }

    @Override
    public void onScoreChange(int user_score, int comp_score) {

        if(user_score>=100) {
            Toast.makeText(this, "YOU WIN", Toast.LENGTH_SHORT).show();// TRigger User Win
            gameOver=true;
        }
        if(comp_score>=100) {
            Toast.makeText(this, "COMP WINS", Toast.LENGTH_SHORT).show();//Trigger Comp Win
            gameOver=true;
        }

        if(gameOver){
            roll_button.setEnabled(false);

            hold_button.setEnabled(false);
        }
    }

    class Scores{
        int user_score, comp_score;
        OnScoreChangeListener listener=null;
        Scores(OnScoreChangeListener listener){
            user_score=comp_score=0;
            if(this.listener==null)
                this.listener=listener;
        }

        public void incrementUserScore(int offset){
            user_score+=offset;
            listener.onScoreChange(user_score,comp_score);
        }
        public void incrementCompScore(int offset){
            comp_score+=offset;
            listener.onScoreChange(user_score,comp_score);
        }

        public int getUser_score() {
            return user_score;
        }

        public int getComp_score() {
            return comp_score;
        }
    }


}
