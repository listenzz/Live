package com.shundaojia.live.sample;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BlankFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blank, container, false);
        Button button = root.findViewById(R.id.button);
        button.setOnClickListener(it -> hide());
        return root;
    }

    private void hide() {
        FragmentManager manager =  getActivity().getSupportFragmentManager();
        manager.beginTransaction().remove(this).commit();
    }

}
