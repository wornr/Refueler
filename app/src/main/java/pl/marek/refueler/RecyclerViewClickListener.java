package pl.marek.refueler;

import android.view.View;

public interface RecyclerViewClickListener {
    void onRowClicked(int position);
    void onEditClicked(View v, int position);
    void onDeleteClicked(View v, int position);
}
