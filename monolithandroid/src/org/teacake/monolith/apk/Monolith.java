package org.teacake.monolith.apk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;

public class Monolith extends Activity
{
	
	private static final int ID_PLAY_GAME = Menu.FIRST;
	private static final int ID_OPTIONS = Menu.FIRST + 1;
	private static final int ID_EXIT = Menu.FIRST+2;
    

    private GameSurfaceView gsf;
    private GameOverlay overlay;
    
    private HighScoreTable hsTable;
    private Options options;
    private Sound soundManager;
    private Game game;
    private android.widget.CheckBox checkboxAcceptLicense;
    private android.widget.Button buttonOK;
    private android.widget.Button buttonCancel;
    private android.widget.TextView textviewLicense;
    public SharedPreferences.Editor prefsEditor;
    public SharedPreferences prefs;
    public android.view.View licenseView;
    private boolean soundInitialized;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        soundInitialized = false;
        
        prefs = this.getPreferences(android.content.Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
        
        //prefsEditor.putBoolean("LicenseAccepted", false);
        //prefsEditor.commit();
        if(false/*!prefs.getBoolean("LicenseAccepted", false)*/)
        {
        	//this.licenseView = this.findViewById(R.layout.licenseagreement);
        	this.licenseView = View.inflate(this, R.layout.licenseagreement, null);
        	this.setContentView(licenseView);
        	this.checkboxAcceptLicense = (android.widget.CheckBox)this.findViewById(R.id.checkLicenseAgreement);
        	this.textviewLicense = (android.widget.TextView)this.findViewById(R.id.textviewLicenseAgreement);
        	this.buttonOK= (android.widget.Button)this.findViewById(R.id.buttonOK);
        	this.buttonCancel = (android.widget.Button)this.findViewById(R.id.buttonCancel);
        	this.buttonCancel.setOnClickListener(
        											new View.OnClickListener()
        											{
        												public void onClick(View view)
        												{
        													exitNotAcceptedApplication();
        												}
        											}
        										);
        	this.buttonOK.setOnClickListener(
					new View.OnClickListener()
					{
						public void onClick(View view)
						{
							if(checkboxAcceptLicense.isChecked())
							{
								startAcceptedApplication();
							}
						}
					}
				);
        }
        else
        {
        	initActivity();
        }
    }
    public void startAcceptedApplication()
    {
    	prefsEditor.putBoolean("LicenseAccepted", true);
		//prefsEditor.commit();
		licenseView.setVisibility(View.INVISIBLE);
		licenseView = null;		
		initActivity();
    }
    public void initActivity()
    {
        this.soundManager = new SoundPoolManager(this);
        this.soundInitialized = true;
        hsTable = new HighScoreTable(this,10);
        if(prefs.getBoolean("gamesaved", false))
        {
        	switch(prefs.getInt("gametype", Game.GAME_MONOLITH))
        	{
        	case Game.GAME_CLASSIC:
        		game = new SimpleGameData();
        		game.loadGame(prefs);
        		break;
        	case Game.GAME_MONOLITH:
        		game= new MonolithGameData();
        		game.loadGame(prefs);
        		break;
        	}
        }
        else
        {
        	game = new MonolithGameData();
            
        		
        	
       
        	
        }
        options = new Options(game,prefs);
        overlay = new GameOverlay(this,hsTable,options);
        overlay.setVisibility(View.VISIBLE);
        overlay.setOverlayType(GameOverlay.OVERLAY_TYPE_INTRO);
        
        
        gsf = new GameSurfaceView(this,overlay,this.soundManager);

        setContentView(gsf);
        gsf.setVisibility(View.VISIBLE);   
        
        this.addContentView(overlay,new android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT));
		
		
		soundManager.addSound(R.raw.explosion2, false);
		soundManager.addSound(R.raw.place, false);
		soundManager.addSound(R.raw.rotate,false);
		soundManager.addSound(R.raw.pluck, false);
		soundManager.addSound(R.raw.pluck2, false);
		soundManager.addSound(R.raw.speech, false);
		soundManager.addSound(R.raw.evolving, false );
		soundManager.addSound(R.raw.gameover, false);
		soundManager.startSound();
		soundManager.addSound(R.raw.monolith, true);
		//try
		//{
		//	Thread.currentThread().sleep(10000);
		//}
		//catch(Exception e)
		//{
		//	
		//}
	
		soundManager.startMusic(R.raw.monolith);
		if(!options.isMusicEnabled())
		{
			soundManager.pauseMusic(R.raw.monolith);
		}
		
    }
    @Override
    public void onPause()
    {
    	super.onPause();
    	if(soundInitialized)
    	{
    		this.soundManager.stopSound();
    	}
    	
    	this.game.saveGame(this.prefs);
    	//gsf.stopMusic();
    }
    @Override
    public void onStop()
    {
    	super.onStop();
    	if(soundInitialized)
    	{
    		this.soundManager.stopSound();
    	}
    }

    
    public void playGame()
    {
    	
		gsf.setGameType(overlay.getOptions().getGameType());
		
		gsf.initGame(GLThread.VIEW_GAME);
		
    	
    }
    public void showOptions()
    {
    	gsf.setGameType(game.getGameType());
    	
    	gsf.initGame(GLThread.VIEW_OPTIONS);
    	
    	
    }

    

    public void exitApplication()
    {
    	prefsEditor.putBoolean("gamesaved", false);
    	prefsEditor.commit();
    		this.soundManager.stopSound();
    	
			finish();
    }
    public void exitNotAcceptedApplication()
    {
    	
		finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case ID_PLAY_GAME:
            playGame();
            return true;
        case ID_OPTIONS:
        	showOptions();
        	return true;
        case ID_EXIT:
        	exitApplication();
        	return true;
        
        }
       
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, ID_PLAY_GAME,0 ,R.string.s_play);
        menu.add(0, ID_OPTIONS,0,R.string.s_options);
        menu.add(0, ID_EXIT,0,R.string.s_exit);
        return true;
    }   
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        boolean handled = false;
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN||keyCode==KeyEvent.KEYCODE_Z||keyCode==KeyEvent.KEYCODE_X||keyCode==KeyEvent.KEYCODE_C)
        {
        	try
        	{
        		android.os.Message message = android.os.Message.obtain(gsf.getHandler(), GLThread.MSG_MOVE_DOWN);
        		message.sendToTarget();
        	}
        	catch(Exception e)
        	{
        		
        	}
        	handled = true;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT||keyCode== KeyEvent.KEYCODE_A||keyCode== KeyEvent.KEYCODE_S)
        {
        	try
        	{
        		android.os.Message message = android.os.Message.obtain(gsf.getHandler(), GLThread.MSG_MOVE_LEFT);
        		message.sendToTarget();
        		
        	}
        	catch(Exception e)
        	{
        		
        	}
        	handled = true;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT||keyCode== KeyEvent.KEYCODE_D||keyCode== KeyEvent.KEYCODE_F)
        {
        	try
        	{
        		android.os.Message message = android.os.Message.obtain(gsf.getHandler(), GLThread.MSG_MOVE_RIGHT);
        		message.sendToTarget();
        	}
        	catch(Exception e)
        	{
        		
        	}
        	handled = true;
        }
        if(keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_L)
        {
        	try
        	{
        		android.os.Message message = android.os.Message.obtain(gsf.getHandler(), GLThread.MSG_ROTATE);
        		message.sendToTarget();
        	}
        	catch(Exception e)
        	{
        		
        	}
        	handled = true;
        }
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
        	this.exitApplication();
        }
        

        
        return handled;
    }

    /**
     * Standard override for key-up. We actually care about these,
     * so we can turn off the engine or stop rotating.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        boolean handled = false;
        return handled;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    	int action = event.getAction();
    	boolean handled = false;
    	if(action==MotionEvent.ACTION_DOWN)
    	{
    		xval=(int)event.getX();
    		yval=(int)event.getY();

    		handled = true;
    	}
    	
    	if(action==MotionEvent.ACTION_UP)
    	{
    		int xnow = (int)event.getX();
    		int ynow = (int)event.getY();
    		if(xnow<20 && ynow<20)
    		{
    			zx =0 ;
    			zy =0 ;
        		try
        		{
        			android.os.Message message = android.os.Message.obtain(gsf.getHandler(), GLThread.MSG_ROTATE_PLAYFIELD);
        			message.arg1 = zx;
        			message.arg2 = zy;
        			message.sendToTarget();
        		}
        		catch(Exception e)
        		{
        			
        		}
    		}
    		handled=true;
    	}
    	if(action==MotionEvent.ACTION_MOVE)
    	{
            zx = zx+((int)event.getX()-xval);
            zy = zy+((int)event.getY()-yval);
      	  	xval=(int)event.getX();
      	  	yval=(int)event.getY();
    		try
    		{
    			android.os.Message message = android.os.Message.obtain(gsf.getHandler(), GLThread.MSG_ROTATE_PLAYFIELD);
    			message.arg1 = zx;
    			message.arg2 = zy;
    			message.sendToTarget();
    		}
    		catch(Exception e)
    		{
    			
    		}      	  	
      	  	handled = true;
    	}

        return handled;
    } 

    private int xval;
    private int yval;
    private int zx;
    private int zy;
    
     
}