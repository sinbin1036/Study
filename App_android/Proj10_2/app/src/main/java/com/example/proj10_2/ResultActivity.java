package com.example.proj10_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class ResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        setTitle("투표 결과");

        // 인텐트에서 데이터 수신
        Intent intent = getIntent();
        int[] voteResult = intent.getIntArrayExtra("VoteCount");
        String[] imageName = intent.getStringArrayExtra("ImageName");

        // 텍스트뷰와 레이팅바 배열 선언
        TextView tv[] = new TextView[imageName.length];
        RatingBar rbar[] = new RatingBar[imageName.length];

        // 각 뷰의 ID 배열
        Integer tvID[] = {
                R.id.tv1, R.id.tv2, R.id.tv3,
                R.id.tv4, R.id.tv5, R.id.tv6,
                R.id.tv7, R.id.tv8, R.id.tv9
        };
        Integer rbarID[] = {
                R.id.rbar1, R.id.rbar2, R.id.rbar3,
                R.id.rbar4, R.id.rbar5, R.id.rbar6,
                R.id.rbar7, R.id.rbar8, R.id.rbar9
        };

        // findViewById로 연결
        for (int i = 0; i < voteResult.length; i++) {
            tv[i] = findViewById(tvID[i]);
            rbar[i] = findViewById(rbarID[i]);
        }

        // 이름 텍스트 설정 + 별점 투표 결과 반영
        for (int i = 0; i < voteResult.length; i++) {
            tv[i].setText(imageName[i]);
            rbar[i].setRating((float) voteResult[i]);
        }

        // 돌아가기 버튼
        Button btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> finish());  // 현재 액티비티 종료 → MainActivity로 돌아감
    }
}
