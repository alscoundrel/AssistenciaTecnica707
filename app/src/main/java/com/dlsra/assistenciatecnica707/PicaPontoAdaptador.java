package com.dlsra.assistenciatecnica707;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dlsra.assistenciatecnica707.PicaPontoFragmento.OnListFragmentInteractionListener;

import java.util.ArrayList;

/**
 *
 */
public class PicaPontoAdaptador extends RecyclerView.Adapter<PicaPontoAdaptador.ViewHolder> {

    private final ArrayList<ClassePicaPonto> mValores;
    private final OnListFragmentInteractionListener mLista;

    public PicaPontoAdaptador(ArrayList<ClassePicaPonto> items) {
        mValores = items;
        mLista   = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pica_ponto_fragmento_elemento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String data = mValores.get(position).getData();
        String hora = mValores.get(position).getHora();
        int enviado = mValores.get(position).getEnviado();
        String stEnviado = (enviado==1?"":"não ")+"enviado";
        holder.mItem = mValores.get(position);
        holder.mTVHora.setText(hora);
        holder.mTVData.setText(data);
        holder.mTVEnviado.setText(stEnviado);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mLista) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mLista.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTVData;
        public final TextView mTVHora;
        public final TextView mTVEnviado;
        public ClassePicaPonto mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTVHora = (TextView) view.findViewById(R.id.tvHoraPicaPonto);
            mTVData = (TextView) view.findViewById(R.id.tvDataPicaPonto);
            mTVEnviado = (TextView) view.findViewById(R.id.tvEnviadoPicaPonto);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTVData.getText() + "', '"+mTVHora.getText()+ "', '"+mTVEnviado.getText();
        }
    }
}
