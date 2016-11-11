package pl.marek.refueler.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.marek.refueler.R;
import pl.marek.refueler.RecyclerViewClickListener;
import pl.marek.refueler.activities.AddCarActivity;
import pl.marek.refueler.adapters.CarsAdapter;
import pl.marek.refueler.activities.CarActivity;
import pl.marek.refueler.database.Car;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;

public class CarsFragment extends Fragment implements RecyclerViewClickListener {
    private List<Car> cars;

    @Bind(R.id.cars_list)
    RecyclerView recyclerView;

    @Bind(R.id.empty_message)
    TextView message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cars_fragment, container, false);
        ButterKnife.bind(this, v);
        initRecyclerView();
        return v;
    }

    private void initRecyclerView() {
        CarsAdapter carsAdapter = new CarsAdapter(this);
        cars = carsAdapter.getCars();

        if (cars.isEmpty())
            message.setVisibility(View.VISIBLE);
        else
            message.setVisibility(View.GONE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        carsAdapter.setCars(cars);
        recyclerView.setAdapter(carsAdapter);
    }

    @Override
    public void onRowClicked(int position) {
        Intent intent = new Intent(getActivity(), CarActivity.class);
        intent.putExtra("car", cars.get(position));
        startActivity(intent);
    }

    @Override
    public void onEditClicked(View v, int position) {
        if (v.getId() == R.id.edit_icon) {
            Intent intent = new Intent(getActivity(), AddCarActivity.class);
            intent.putExtra("car", cars.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteClicked(View v, final int position) {
        if (v.getId() == R.id.delete_icon) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Do you really want to delete this car?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            List<Refuel> refuels = Query.many(Refuel.class, "SELECT * FROM Refuels WHERE carId = ?", cars.get(position).getId()).get().asList();
                            for(Refuel refuel : refuels) {
                                refuel.delete();
                            }
                            cars.get(position).delete();
                            initRecyclerView();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        startActivity(new Intent(getActivity(), AddCarActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }
}