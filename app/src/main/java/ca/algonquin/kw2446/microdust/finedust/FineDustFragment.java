package ca.algonquin.kw2446.microdust.finedust;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ca.algonquin.kw2446.microdust.MainActivity;
import ca.algonquin.kw2446.microdust.R;
import ca.algonquin.kw2446.microdust.data.FineDustRepository;
import ca.algonquin.kw2446.microdust.data.LocationFineDustRepository;
import ca.algonquin.kw2446.microdust.model.FineDust;
import ca.algonquin.kw2446.microdust.model.dust_material.Dust;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FineDustFragment extends Fragment implements FineDustContract.View {

    private TextView mLocationTextView;
    private TextView mTimeTextView;
    private TextView mDustTextView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FineDustRepository mRepository;
    private FineDustPresenter mPresenter;

    public static FineDustFragment newInstance(double lat, double lng) {
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);

        FineDustFragment fragment = new FineDustFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FineDustFragment() {
        // 반드시 필요함
        //매개변수를 받지 못해서 newInstance 이용
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fine_dust, container, false);

        mLocationTextView = (TextView) view.findViewById(R.id.tvLocation);
        mTimeTextView = (TextView) view.findViewById(R.id.tvTime);
        mDustTextView = (TextView) view.findViewById(R.id.tvDust);
        if (savedInstanceState != null) {
            mLocationTextView.setText(savedInstanceState.getString("location"));
            mTimeTextView.setText(savedInstanceState.getString("time"));
            mDustTextView.setText(savedInstanceState.getString("dust"));
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srlDust);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadFineDustData();
               // loadFineDust();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            double lat = getArguments().getDouble("lat");
            double lng = getArguments().getDouble("lng");
//            mRepository = new LocationFineDustRepository(lat, lng);
            this.newLoad(lat,lng);
        } else {
            mRepository = new LocationFineDustRepository(0, 0);
            ((MainActivity) getActivity()).getLastKnownLocation();
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frag_fine_dust, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
           mPresenter.loadFineDustData();
            //loadFineDust();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("location", mLocationTextView.getText().toString());
        outState.putString("time", mTimeTextView.getText().toString());
        outState.putString("dust", mDustTextView.getText().toString());
    }

    @Override
    public void showFineDustResult(FineDust fineDust) {
        try {
            if(fineDust.getWeather().getDust().size()>0){
                Dust dust=fineDust.getWeather().getDust().get(0);
                mLocationTextView.setText(String.format("%s (%s, %s)",
                        dust.getStation().getName(),dust.getStation().getLatitude(),dust.getStation().getLongitude()));
                mTimeTextView.setText(dust.getTimeObservation());
                mDustTextView.setText(dust.getPm10().getValue() + " ㎍/㎥, "
                        + dust.getPm10().getGrade());
            }else{

                mLocationTextView.setText("Your current location is not supporting");
                mTimeTextView.setText("Your current location is not supporting");
                mDustTextView.setText("Your current location is not supporting");
            }

        } catch (Exception e) {
            mLocationTextView.setText("The current is not working");
            mTimeTextView.setText("The current is not working");
            mDustTextView.setText("The current is not working");
        }
    }

    @Override
    public void showLoadError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadingStart() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void loadingEnd() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void newLoad(double lat, double lng) {
        mRepository = new LocationFineDustRepository(lat, lng);
        mPresenter = new FineDustPresenter(mRepository, this);
        mPresenter.loadFineDustData();
       // loadFineDust();
    }

    @Override
    public void loadFineDust() {
        // 데이터 제공이 가능하면
        if (mRepository.isAvailable()) {
            // 로딩 시작
            this.loadingStart();

            // 데이터 가져오기
            mRepository.getFineDustData(new Callback<FineDust>() {
                @Override
                public void onResponse(Call<FineDust> call, Response<FineDust> response) {
                    // 데이터 표시하기
                    showFineDustResult(response.body());
                    // 로딩 끝
                    loadingEnd();
                }

                @Override
                public void onFailure(Call<FineDust> call, Throwable t) {
                    // 에러 표시하기
                    showLoadError(t.getLocalizedMessage());
                    // 로딩 끝
                    loadingEnd();
                }
            });
        }
    }

}

