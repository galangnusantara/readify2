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

public class NewReleaseFragment extends Fragment {

    public NewReleaseFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_release, container, false);

        ImageButton theHobbitButton = rootView.findViewById(R.id.thehobbit);
        ImageButton lastTrainButton = rootView.findViewById(R.id.lasttraintoistanbul);
        ImageButton utopiaButton = rootView.findViewById(R.id.utopia);
        ImageButton nightSpinnerButton = rootView.findViewById(R.id.nightspinner);
        ImageButton wilderGirlButton = rootView.findViewById(R.id.wildergirl);

        theHobbitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku.class);
                startActivity(intent);
            }
        });

        lastTrainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku1.class);
                startActivity(intent);
            }
        });

        utopiaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku2.class);
                startActivity(intent);
            }
        });

        nightSpinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku3.class);
                startActivity(intent);
            }
        });

        wilderGirlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Detailbuku4.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
