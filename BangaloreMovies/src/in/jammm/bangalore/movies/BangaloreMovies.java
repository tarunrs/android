package in.jammm.bangalore.movies;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.content.Context;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BangaloreMovies extends ListActivity {
    
	private ProgressDialog m_ProgressDialog = null; 
    private ArrayAdapter<String> m_adapter;
    private Runnable loadMovies;
    GlobalData globalData = null;
    String err_msg = "asd";
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  globalData = new GlobalData();
	  //GlobalData.MOVIES = new String[];
	  //m_orders = new ArrayList<Order>();
	  globalData.MOVIES = new ArrayList<String>() ;
	  globalData.tts = new ArrayList<JSONArray>() ;
	  m_adapter = new ArrayAdapter<String>(this, R.layout.list_item, globalData.MOVIES);
	  setListAdapter(m_adapter);
	  loadMovies = new Runnable(){
          public void run() {
              getMovies();
          };
	  };
		Thread thread =  new Thread(null, loadMovies, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data ...", true);
        Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT).show();
	}
    private Runnable returnRes = new Runnable() {

        public void run() {
        	Log.d("len",String.valueOf(globalData.MOVIES.size()));
            if( globalData.MOVIES != null &&  globalData.MOVIES.size()> 0){   
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<globalData.MOVIES.size();i++)
                {
                	m_adapter.add(globalData.MOVIES.get(i));
                }
          	  ListView lv = getListView();
        	  lv.setTextFilterEnabled(true);
        	  lv.setOnItemClickListener(new OnItemClickListener() {
        		  	public void onItemClick(AdapterView<?> parent, View view,
        		        int position, long id) {
        		  		handleButtonClick(position);
        		    }
        		  });
        	  err_msg = String.valueOf(globalData.MOVIES.size());
            }
            //Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT).show();
            m_ProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT).show();
            m_adapter.notifyDataSetChanged();
        }
    };
	
	private void getMovies() {
			// TODO Auto-generated method stub
		 try{
			 populateArray();
		 } catch (Exception e) {
             Log.e("BACKGROUND_PROC", e.getMessage());
           }
           runOnUiThread(returnRes);
       }
			
	private void handleButtonClick(int pos)
    {
		Intent i = new Intent(this, TheatreListings.class);
		String tts = globalData.tts.get(pos).toString();
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
			Log.d("BM","saving to file");
			saveToFile(res);
		}
		catch(JSONException e) {
			e.printStackTrace();
			populateFromFile(res);
			response = new JSONObject(res);
			movies = response.getJSONArray("listing");
		}
		
		if(movies.length() > 0)
		{
			globalData.tts = new ArrayList<JSONArray>(); 
			globalData.MOVIES = new ArrayList<String>();
			
			for(int i = 0; i < movies.length(); i++){
				JSONObject movie = movies.getJSONObject(i);
				JSONArray theaters = movie.getJSONArray("theaters");
				globalData.tts.add(theaters);
				globalData.MOVIES.add(movie.getString("name")) ;
			}
		}
	}
	
	public void populateFromFile(String content){
		String FILENAME = "listing_file";
		StringBuffer strContent = new StringBuffer("");
		int ch;
		FileInputStream fin = null;
		err_msg ="populate from file";
		try {
			fin = openFileInput(FILENAME);
			while( (ch = fin.read()) != -1)
				strContent.append((char)ch);
			content = strContent.toString();
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	return;	
	}
	
	public void saveToFile(String content) {
		String FILENAME = "listing_file";
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//Toast.makeText(getApplicationContext(), "File not found!!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		try {
			fos.write(content.getBytes());
			fos.close();
		} catch (IOException e) {
			//Toast.makeText(getApplicationContext(), "i/o Error!", Toast.LENGTH_SHORT).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d("BM","Written to file");
		//Toast.makeText(getApplicationContext(), "Cached..", Toast.LENGTH_SHORT).show();
	}
}