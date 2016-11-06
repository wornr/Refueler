package pl.marek.refueler.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.marek.refueler.R;
import pl.marek.refueler.RecyclerViewClickListener;
import pl.marek.refueler.database.Car;
import se.emilsjolander.sprinkles.Query;


public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.CarsHolder> {
    private List<Car> carsFromDb;
    private RecyclerViewClickListener listener;

    public CarsAdapter(RecyclerViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public CarsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_car, parent, false);
        return new CarsHolder(view);
    }

    @Override
    public void onBindViewHolder(CarsHolder holder, final int position) {
        /*holder.taskName.setText(carsFromDb.get(position).getEventName());
        String[] latLng = carsFromDb.get(position).getLocation().split(",");
        String formatLocation = latLng[0].substring(0, 5) + " " + latLng[1].substring(0, 5);
        holder.taskLocation.setText(formatLocation);

        if (carsFromDb.get(position).getDate() != null)
            holder.taskDeadline.setText(carsFromDb.get(position).getDate());
        else
            holder.taskDeadline.setText(R.string.unknown_deadline);

        int status = carsFromDb.get(position).getStatus();
        holder.taskStatus.setText(getTaskStatus(status));*/

        holder.carBrand.setText(carsFromDb.get(position).getBrand());
        holder.carModel.setText(carsFromDb.get(position).getModel());
        holder.carRegistrationNumber.setText(carsFromDb.get(position).getRegistrationNumber());
        //holder.carTotalDistance.setText(carsFromDb.get(position).getTotalDistance());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        if (carsFromDb != null)
            return carsFromDb.size();

        return 0;
    }

    public void setCars(List<Car> data) {
        carsFromDb = data;
        notifyDataSetChanged();
    }

    public List<Car> getCars() {
        return Query.all(Car.class).get().asList();
    }

    class CarsHolder extends RecyclerView.ViewHolder {
        final TextView carBrand;
        final TextView carModel;
        final TextView carRegistrationNumber;
        final TextView carTotalDistance;
        final ImageView editIcon;
        final ImageView deleteIcon;

        CarsHolder(View view) {
            super(view);
            carBrand = (TextView) view.findViewById(R.id.car_brand);
            carModel = (TextView) view.findViewById(R.id.car_model);
            carRegistrationNumber = (TextView) view.findViewById(R.id.car_registrationNumber);
            carTotalDistance = (TextView) view.findViewById(R.id.car_totalDistance);
            editIcon = (ImageView) view.findViewById(R.id.edit_icon);
            deleteIcon = (ImageView) view.findViewById(R.id.delete_icon);
        }
    }
}
