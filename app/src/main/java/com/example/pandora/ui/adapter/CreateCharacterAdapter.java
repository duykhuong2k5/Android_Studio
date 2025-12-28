package com.example.pandora.ui.adapter;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pandora.R;
import com.example.pandora.data.entity.CharacterRequest;
import java.util.List;

public class CreateCharacterAdapter extends RecyclerView.Adapter<CreateCharacterAdapter.ViewHolder> {
    private List<CharacterRequest> list;
    public CreateCharacterAdapter(List<CharacterRequest> list) { this.list = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharacterRequest item = list.get(position);
        holder.tvName.setText(item.name);
        // Load avatar dựa trên giới tính
        holder.imgHero.setImageResource(item.gender.equals("Boy") ? R.drawable.ic_boy : R.drawable.ic_girl);
    }

    @Override public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName; ImageView imgHero;
        public ViewHolder(View v) { super(v);
            tvName = v.findViewById(R.id.tvHeroName);
            imgHero = v.findViewById(R.id.imgHeroSilhouette);
        }
    }
}