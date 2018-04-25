package com.pq.networkdemoapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pq.networkdemoapp.R;
import com.pq.networkdemoapp.normal.NormalActivity;

/**
 * created by panqian on 2018/4/24
 * description:
 */

public class CategoryFragment extends Fragment implements View.OnClickListener {

    Button normalButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_category, null);
        normalButton = (Button) rootView.findViewById(R.id.btn_normal);


        normalButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_normal:
                openActivity(NormalActivity.class);
                break;
            default:
                break;
        }
    }

    private void openActivity(Class klass){
        Intent intent=new Intent(getActivity(),klass);
        startActivity(intent);
    }
}
