package com.example.mybraintest.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mybraintest.R;
import com.example.mybraintest.model.Database;
import com.example.mybraintest.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuestionActivity extends AppCompatActivity {
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCategory;
    private TextView textViewCountDown;

    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button buttonConfirmNext;

    private CountDownTimer countDownTimer;
    private ArrayList<Question> questionList;
    private long timeLeftInMillis;
    private int questionCounter;
    private int questionSize;

    private int Score;
    private boolean answered;
    private Question currentQuestion;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        getID();

        Intent intent = getIntent();
        int categoryID = intent.getIntExtra("idcategories",0);
        String categoryName = intent.getStringExtra("catgoriesname");

        textViewCategory.setText("Chủ đề : "+categoryName);

        Database database = new Database(this);

        questionList = database.getQuestions(categoryID);

        questionSize = questionList.size();

        Collections.shuffle(questionList);

        showNextQuestion();

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!answered){
                    //nếu chọn 1 trong 4 đáp án
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {

                        checkAnswer();
                    }
                    else {
                        Toast.makeText(QuestionActivity.this,"Hãy chọn đáp án !",Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    showNextQuestion();
                }
            }
        });
    }

    private void showNextQuestion() {

        rb1.setTextColor(Color.BLACK);
        rb2.setTextColor(Color.BLACK);
        rb3.setTextColor(Color.BLACK);
        rb4.setTextColor(Color.BLACK);

        rbGroup.clearCheck();

        if (questionCounter < questionSize){

            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());

            questionCounter++; // count question

            textViewQuestionCount.setText("Câu hỏi : "+questionCounter+" / "+questionSize); // set question

            answered = false;

            //gán tên cho button
            buttonConfirmNext.setText("Xác Nhận");
            //time 45s
            timeLeftInMillis = 45000;

            startCountDown();

        }
        else {
            finishQuestion();
        }

    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;

                //update time
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                //full time
                timeLeftInMillis = 0;
                updateCountDownText();

                checkAnswer();

            }
        }.start();

    }

    private void checkAnswer() {

        answered = true;
        //trả về radiobutton trong rbGroup
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        //vị trí của câu đã chọn
        int answer = rbGroup.indexOfChild(rbSelected) + 1;
        //nếu trả lời đúng đáp án
        if (answer == currentQuestion.getAnswer()){

            Score = Score + 100;

            textViewScore.setText("Điểm : "+Score);
        }

        showSolution();
    }


    private void showSolution() {
        //set color radiobutton
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);


        switch (currentQuestion.getAnswer()){
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Đáp án là A");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Đáp án là B");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Đáp án là C");
                break;
            case 4:
                rb4.setTextColor(Color.GREEN);
                textViewQuestion.setText("Đáp án là D");
                break;
        }

        if (questionCounter < questionSize){
            buttonConfirmNext.setText("Câu tiếp theo");
        }
        //completed
        else {
            buttonConfirmNext.setText("Hoàn thành!");
        }
        //time stop
        countDownTimer.cancel();
    }

    private void updateCountDownText() {

        int minutes = (int) ((timeLeftInMillis/1000)/60);

        int seconds = (int) ((timeLeftInMillis/1000)%60);

        String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);

        textViewCountDown.setText(timeFormatted);

        if (timeLeftInMillis <10000){
            textViewCountDown.setTextColor(Color.RED);
        } else {
            textViewCountDown.setTextColor(Color.BLACK);
        }
    }

    private void finishQuestion() {

        Intent resultIntent = new Intent();

        resultIntent.putExtra("Score",Score);

        setResult(RESULT_OK,resultIntent);

        finish();
    }

    @Override
    public void onBackPressed() {
        count++;
        if (count>=1){
            finishQuestion();
        }
        count=0;
    }

    private void getID(){
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCategory = findViewById(R.id.text_view_category);

        textViewCountDown = findViewById(R.id.text_view_countdown);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);

        buttonConfirmNext = findViewById(R.id.button_confirm_next);
    }
}
