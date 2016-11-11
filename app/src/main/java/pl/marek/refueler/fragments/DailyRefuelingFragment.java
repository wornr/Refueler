package pl.marek.refueler.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import pl.marek.refueler.activities.AddRefuelingActivity;
import pl.marek.refueler.adapters.CarsAdapter;
import pl.marek.refueler.adapters.RefuelsAdapter;
import pl.marek.refueler.database.Car;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;

public class DailyRefuelingFragment extends Fragment implements RecyclerViewClickListener {
    private Car car = null;
    private List<Car> cars;
    private List<Refuel> refuels;

    @Bind(R.id.refuels_list)
    RecyclerView recyclerView;

    @Bind(R.id.empty_message)
    TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.daily_refueling_fragment, container, false);
        ButterKnife.bind(this, v);
        initRecyclerView();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void initRecyclerView() {
        if (getActivity().getIntent().getExtras() != null)
            if (getActivity().getIntent().getExtras().get("car") != null)
                car = (Car) getActivity().getIntent().getExtras().get("car");

        CarsAdapter carsAdapter = new CarsAdapter(this);
        cars = carsAdapter.getCars();

        RefuelsAdapter refuelsAdapter = new RefuelsAdapter(this);
        if(car != null)
            refuels = Query.many(Refuel.class, "SELECT * FROM Refuels WHERE carId = ?", car.getId()).get().asList();
        else
            refuels = refuelsAdapter.getRefuels();

        if(cars.isEmpty()) {
            message.setText(R.string.no_cars);
            message.setVisibility(View.VISIBLE);
        } else if (refuels.isEmpty()) {
            message.setText(R.string.no_refuels);
            message.setVisibility(View.VISIBLE);
        } else
            message.setVisibility(View.GONE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        refuelsAdapter.setRefuels(refuels);
        recyclerView.setAdapter(refuelsAdapter);
    }

    @Override
    public void onRowClicked(int position) {
        // TODO add action
        Snackbar.make(getView(), "Refuel clicked!", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClicked(View v, int position) {
        if (v.getId() == R.id.edit_icon) {
            Intent intent = new Intent(getActivity(), AddRefuelingActivity.class);
            if(car != null)
                intent.putExtra("car", car);
            intent.putExtra("refuel", refuels.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteClicked(View v, final int position) {
        if (v.getId() == R.id.delete_icon) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.refuel_delete))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            refuels.get(position).delete();
                            initRecyclerView();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        if (cars.isEmpty())
            startActivity(new Intent(getActivity(), AddCarActivity.class));
        else {
            Intent intent = new Intent(getActivity(), AddRefuelingActivity.class);
            if (car != null)
                intent.putExtra("car", car);
            startActivity(intent);
        }
    }
}
