package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

class FraisHfAdapter extends BaseAdapter {

	private final ArrayList<FraisHf> lesFrais ; // liste des frais du mois*
	private final Context context;
	private final Integer key;
	private final LayoutInflater inflater ;

    /**
	 * Constructeur de l'adapter pour valoriser les propriétés
     * @param context Accès au contexte de l'application
     * @param lesFrais Liste des frais hors forfait
     */
	public FraisHfAdapter(Context context, ArrayList<FraisHf> lesFrais, Integer key) {
		inflater = LayoutInflater.from(context) ;
		this.lesFrais = lesFrais ;
		this.key = key;
		this.context = context;
    }
	
	/**
	 * retourne le nombre d'éléments de la listview
	 */
	@Override
	public int getCount() {
		return lesFrais.size() ;
	}

	/**
	 * retourne l'item de la listview à un index précis
	 */
	@Override
	public Object getItem(int index) {
		return lesFrais.get(index) ;
	}

	/**
	 * retourne l'index de l'élément actuel
	 */
	@Override
	public long getItemId(int index) {
		return index;
	}

	/**
	 * structure contenant les éléments d'une ligne
	 */
	private class ViewHolder {
		TextView txtListJour ;
		TextView txtListMontant ;
		TextView txtListMotif ;
		ImageButton btnSuppHf;
	}
	
	/**
	 * Affichage dans la liste
	 */
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder() ;
			convertView = inflater.inflate(R.layout.layout_liste, parent, false) ;
			holder.txtListJour = convertView.findViewById(R.id.txtListJour);
			holder.txtListMontant = convertView.findViewById(R.id.txtListMontant);
			holder.txtListMotif = convertView.findViewById(R.id.txtListMotif);
			holder.btnSuppHf = convertView.findViewById(R.id.cmdSuppHf);
			convertView.setTag(holder) ;
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.txtListJour.setText(String.format(Locale.FRANCE, "%d", lesFrais.get(index).getJour()));
		holder.txtListMontant.setText(String.format(Locale.FRANCE, "%.2f", lesFrais.get(index).getMontant())) ;
		holder.txtListMotif.setText(lesFrais.get(index).getMotif()) ;

		holder.btnSuppHf.setTag(index);

		holder.btnSuppHf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//Récupération de l'index
				Integer index = (Integer)view.getTag();
				//Suppression du frais dans la liste
				lesFrais.remove(getItem(index));
				//Suppression du frais dans la globale
				Global.listFraisMois.get(key).supprFraisHf(index);
				//Serialization
				Serializer.serialize(Global.listFraisMois, context);
				//Refresh la vue
				notifyDataSetChanged();
			}
		});
		return convertView ;
	}
	
}
