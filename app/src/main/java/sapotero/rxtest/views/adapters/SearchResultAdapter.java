package sapotero.rxtest.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import sapotero.rxtest.R;
import sapotero.rxtest.application.EsdApplication;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.utils.Settings;
import sapotero.rxtest.views.activities.InfoActivity;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
  private final Context context;
  private final List<RDocumentEntity> mDataset;

  @Inject Settings settings;

  public SearchResultAdapter(Context context, List<RDocumentEntity> mDataset) {
    this.context = context;
    this.mDataset = mDataset;

    EsdApplication.getDataComponent().inject(this);
  }

  @Override
  public SearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_query_item, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  @Override
  public void onBindViewHolder(SearchResultAdapter.ViewHolder holder, int position) {
    RDocumentEntity doc = mDataset.get(position);
    holder.mNumber.setText( doc.getRegistrationNumber() );
    holder.mTitle.setText(  doc.getShortDescription() );

    holder.mCard.setOnClickListener(v -> {
      settings.setUid( doc.getUid() );
      settings.setRegNumber( doc.getRegistrationNumber() );
      settings.setStatusCode( doc.getFilter() );
      settings.setRegDate( doc.getRegistrationDate() );
      settings.setLoadFromSearch( true );

      Intent intent = new Intent(context, InfoActivity.class);
      context.startActivity(intent);
    });
  }

  @Override
  public int getItemCount() {
    return mDataset.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView mNumber;
    private final TextView mTitle;
    private final CardView mCard;

    ViewHolder(View v) {
      super(v);
      mCard   = (CardView) v.findViewById(R.id.query_card);
      mNumber = (TextView) v.findViewById(R.id.query_number);
      mTitle  = (TextView) v.findViewById(R.id.query_title);
    }
  }
}
