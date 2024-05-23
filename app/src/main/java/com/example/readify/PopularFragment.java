package com.example.readify;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PopularFragment extends Fragment {

    public PopularFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_popular, container, false);

        ImageButton kimiNoNawaButton = rootView.findViewById(R.id.Kiminonawa);
        ImageButton kafkaOnTheShoreButton = rootView.findViewById(R.id.KafkaonTheShore);
        ImageButton aboutYouButton = rootView.findViewById(R.id.Aboutyou);
        ImageButton humanActButton = rootView.findViewById(R.id.HumanAct);
        ImageButton matahariButton = rootView.findViewById(R.id.Matahari);

        kimiNoNawaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku5.class);
                startActivity(intent);
            }
        });

        kafkaOnTheShoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku6.class);
                startActivity(intent);
            }
        });

        aboutYouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku7.class);
                startActivity(intent);
            }
        });

        humanActButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku8.class);
                startActivity(intent);
            }
        });

        matahariButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku9.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}

