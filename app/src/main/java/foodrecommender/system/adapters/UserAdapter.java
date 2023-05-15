package foodrecommender.system.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import foodrecommender.system.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<String> usernameList;
    private ArrayList<String> passwordList;

    public UserAdapter(ArrayList<String> usernameList, ArrayList<String> passwordList) {
        this.usernameList = usernameList;
        this.passwordList = passwordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username = usernameList.get(position);
        String password = passwordList.get(position);

        // Bind the username and password to the ViewHolder
        holder.usernameTextView.setText(username);
        holder.passwordTextView.setText(password);
        // Bind other data as needed
    }

    @Override
    public int getItemCount() {
        return usernameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView passwordTextView;
        // Add other views as needed

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.profile_card_title);
            passwordTextView = itemView.findViewById(R.id.profile_card_value);
            // Initialize other views as needed
        }
    }
}