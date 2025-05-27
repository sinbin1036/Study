package com.example.proj10_2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setTitle("명화 선호도 투표");

        final int voteCount[] = new int[9];
        for (int i = 0; i < 9; i++) {
            voteCount[i] = 0;
        }

        ImageView image[] = new ImageView[9];
        Integer imageId[] = {
                R.id.iv1, R.id.iv2, R.id.iv3,
                R.id.iv4, R.id.iv5, R.id.iv6,
                R.id.iv7, R.id.iv8, R.id.iv9
        };

        final String imgName[] = {
                "아를의 침실",
                "프리다 칼로 자화상",
                "회색 양복의 남자",
                "무고함",
                "해바라기",
                "키스",
                "팔 괸 여인",
                "꽃을 든 소녀",
                "모나리자"
        };

        // 이미지 클릭 이벤트
        for (int i = 0; i < image.length; i++) {
            final int index = i;

            image[index] = findViewById(imageId[index]);
            image[index].setOnClickListener(v -> {
                voteCount[index]++;
                Toast.makeText(getApplicationContext(),
                        imgName[index] + ": 총 " + voteCount[index] + " 표",
                        Toast.LENGTH_SHORT).show();
            });
        }

        // 버튼 클릭 시 ResultActivity로 이동
        Button btnFinish = findViewById(R.id.btnResult);
        btnFinish.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("VoteCount", voteCount);     // int[] 전달
            intent.putExtra("ImageName", imgName);       // String[] 전달
            startActivity(intent);
        });
    }
}
