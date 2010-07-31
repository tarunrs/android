package in.jammm.bangalore.movies;

import java.io.IOException;
import java.net.MalformedURLException;
import android.content.Context;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class BangaloreMovies extends ListActivity {
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  try {
		populateArray();
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, GlobalData.MOVIES));

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	  lv.setOnItemClickListener(new OnItemClickListener() {
		  	@Override
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		  		handleButtonClick(position);
		      // When clicked, show a toast with the TextView text
		      //Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
		        //  Toast.LENGTH_SHORT).show();
		    }
		  });
	  
	}
	private void handleButtonClick(int pos)
    {
		Intent i = new Intent(this, TheatreListings.class);
		String tts = GlobalData.tts[pos].toString();
		i.putExtra("theaters", tts);
    	startActivity(i);
    }
	
	private void populateArray() throws MalformedURLException, IOException, JSONException 
	{
		WebFile file   = new WebFile( "http://jammm.in/tarunrs/movies" );
		Object content = file.getContent();
		String res = (String) content;
		JSONArray movies;
		JSONObject response ;
		try {
			response = new JSONObject(res);
			movies = response.getJSONArray("listing");
		}
		catch(JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if(movies.length() > 0)
		{
			GlobalData.tts = new JSONArray[movies.length()] ;
			GlobalData.MOVIES = new String[movies.length()];
			
			for(int i = 0; i < movies.length(); i++){
				JSONObject movie = movies.getJSONObject(i);
				JSONArray theaters = movie.getJSONArray("theaters");
				GlobalData.tts[i] = theaters;
				GlobalData.MOVIES[i] = movie.getString("name");
			}
		}
	}
}