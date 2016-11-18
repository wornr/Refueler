package pl.marek.refueler.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import pl.marek.refueler.R;
import pl.marek.refueler.RecyclerViewClickListener;
import pl.marek.refueler.Services;
import pl.marek.refueler.database.Refuel;
import se.emilsjolander.sprinkles.Query;


public class RefuelsAdapter extends RecyclerView.Adapter<RefuelsAdapter.RefuelsHolder> {
    private List<Refuel> refuelFromDb;
    private final RecyclerViewClickListener listener;

    public RefuelsAdapter(RecyclerViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RefuelsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_refuel, parent, false);
        return new RefuelsHolder(view);
    }

    @Override
    public void onBindViewHolder(RefuelsHolder holder, final int position) {
        holder.refuelDate.setText(
                Services.getInstance().getFormatDate(refuelFromDb.get(position).getRefuelDate(), "dd.MM.yyyy"));
        holder.refuelFuelPrice.setText(Services.getInstance().addCurrencyByVolumeUnit(refuelFromDb.get(position).getPrice()));
        holder.refuelFuelVolume.setText("+ " + Services.getInstance().addVolumeUnit(refuelFromDb.get(position).getVolume()));
        holder.refuelDistance.setText("+ " + Services.getInstance().addDistanceUnit(String.valueOf(refuelFromDb.get(position).getDistance())));
        holder.refuelTotalPrice.setText("- " + Services.getInstance().addCurrencyUnit(Services.getInstance().multiplyString(refuelFromDb.get(position).getPrice(), refuelFromDb.get(position).getVolume())));

        holder.singleRefuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onRowClicked(position);
            }
        });

        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onEditClicked(v, position);
            }
        });

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onDeleteClicked(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (refuelFromDb != null)
            return refuelFromDb.size();

        return 0;
    }

    public void setRefuels(List<Refuel> data) {
        refuelFromDb = data;
        notifyDataSetChanged();
    }

    public List<Refuel> getRefuels() {
        return Query.all(Refuel.class).get().asList();
    }


    class RefuelsHolder extends RecyclerView.ViewHolder {
        final LinearLayout singleRefuel;
        final TextView refuelDate;
        final TextView refuelFuelPrice;
        final TextView refuelTotalPrice;
        final TextView refuelFuelVolume;
        final TextView refuelDistance;
        final ImageView editIcon;
        final ImageView deleteIcon;

        RefuelsHolder(View view) {
            super(view);
            singleRefuel = (LinearLayout) view.findViewById(R.id.single_refuel);
            refuelDate = (TextView) view.findViewById(R.id.refuel_date);
            refuelFuelPrice = (TextView) view.findViewById(R.id.refuel_fuel_price);
            refuelTotalPrice = (TextView) view.findViewById(R.id.refuel_total_price);
            refuelFuelVolume = (TextView) view.findViewById(R.id.refuel_fuel_volume);
            refuelDistance = (TextView) view.findViewById(R.id.refuel_distance);
            editIcon = (ImageView) view.findViewById(R.id.edit_icon);
            deleteIcon = (ImageView) view.findViewById(R.id.delete_icon);
        }
    }
}
