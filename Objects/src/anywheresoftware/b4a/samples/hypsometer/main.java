package anywheresoftware.b4a.samples.hypsometer;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "anywheresoftware.b4a.samples.hypsometer", "anywheresoftware.b4a.samples.hypsometer.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "anywheresoftware.b4a.samples.hypsometer", "anywheresoftware.b4a.samples.hypsometer.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "anywheresoftware.b4a.samples.hypsometer.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static float _pi = 0f;
public static anywheresoftware.b4a.phone.Phone.PhoneSensors _sensor = null;
public static float _pitch = 0f;
public static float _thetabase = 0f;
public static float _thetatop = 0f;
public static float _distance = 0f;
public static float _observerheight = 0f;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmeasurebase = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmeasuretop = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtdistance = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtobserverheight = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txttopangle = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncalculate = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblobjectheight = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbldistance = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblobserverheight = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltopangle = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 35;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 36;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 38;BA.debugLine="txtDistance.InputType = Bit.Or(txtDistance.InputT";
mostCurrent._txtdistance.setInputType(anywheresoftware.b4a.keywords.Common.Bit.Or(mostCurrent._txtdistance.getInputType(),(int) (4096)));
 //BA.debugLineNum = 39;BA.debugLine="txtObserverHeight.InputType = Bit.Or(txtObserverH";
mostCurrent._txtobserverheight.setInputType(anywheresoftware.b4a.keywords.Common.Bit.Or(mostCurrent._txtobserverheight.getInputType(),(int) (4096)));
 //BA.debugLineNum = 40;BA.debugLine="txtTopAngle.InputType = Bit.Or(txtTopAngle.InputT";
mostCurrent._txttopangle.setInputType(anywheresoftware.b4a.keywords.Common.Bit.Or(mostCurrent._txttopangle.getInputType(),anywheresoftware.b4a.keywords.Common.Bit.Or((int) (8192),(int) (4096))));
 //BA.debugLineNum = 43;BA.debugLine="sensor.Initialize(sensor.TYPE_ACCELEROMETER)";
_sensor.Initialize(_sensor.TYPE_ACCELEROMETER);
 //BA.debugLineNum = 44;BA.debugLine="thetaBase = 0 ' Postavi kut baze na 0 po defaultu";
_thetabase = (float) (0);
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _activity_permissionresult(String _permission,boolean _result) throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub Activity_PermissionResult (Permission As Strin";
 //BA.debugLineNum = 48;BA.debugLine="If Permission = \"android.permission.BODY_SENSORS\"";
if ((_permission).equals("android.permission.BODY_SENSORS")) { 
 //BA.debugLineNum = 49;BA.debugLine="If Result = False Then";
if (_result==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 50;BA.debugLine="ToastMessageShow(\"Sensors permission DENIED! \",";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Sensors permission DENIED! "),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 51;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 54;BA.debugLine="sensor.Initialize(sensor.TYPE_ACCELEROMETER)";
_sensor.Initialize(_sensor.TYPE_ACCELEROMETER);
 };
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _btncalculate_click() throws Exception{
float _thetabaserad = 0f;
float _thetatoprad = 0f;
float _objectheight = 0f;
 //BA.debugLineNum = 83;BA.debugLine="Sub btnCalculate_Click";
 //BA.debugLineNum = 85;BA.debugLine="If txtDistance.Text = \"\" Or txtObserverHeight.Tex";
if ((mostCurrent._txtdistance.getText()).equals("") || (mostCurrent._txtobserverheight.getText()).equals("")) { 
 //BA.debugLineNum = 86;BA.debugLine="ToastMessageShow(\"Enter the observer's distance";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Enter the observer's distance and height"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 87;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 91;BA.debugLine="distance = txtDistance.Text";
_distance = (float)(Double.parseDouble(mostCurrent._txtdistance.getText()));
 //BA.debugLineNum = 92;BA.debugLine="observerHeight = txtObserverHeight.Text";
_observerheight = (float)(Double.parseDouble(mostCurrent._txtobserverheight.getText()));
 //BA.debugLineNum = 93;BA.debugLine="If txtTopAngle.Text <> \"\" Then";
if ((mostCurrent._txttopangle.getText()).equals("") == false) { 
 //BA.debugLineNum = 95;BA.debugLine="thetaTop = txtTopAngle.Text";
_thetatop = (float)(Double.parseDouble(mostCurrent._txttopangle.getText()));
 }else {
 //BA.debugLineNum = 98;BA.debugLine="thetaTop = pitch";
_thetatop = _pitch;
 };
 //BA.debugLineNum = 102;BA.debugLine="Dim thetaBaseRad As Float = thetaBase * Pi / 180";
_thetabaserad = (float) (_thetabase*_pi/(double)180);
 //BA.debugLineNum = 103;BA.debugLine="Dim thetaTopRad As Float = thetaTop * Pi / 180";
_thetatoprad = (float) (_thetatop*_pi/(double)180);
 //BA.debugLineNum = 106;BA.debugLine="Dim objectHeight As Float = distance * (Tan(theta";
_objectheight = (float) (_distance*(anywheresoftware.b4a.keywords.Common.Tan(_thetatoprad)-anywheresoftware.b4a.keywords.Common.Tan(_thetabaserad))+_observerheight);
 //BA.debugLineNum = 109;BA.debugLine="lblObjectHeight.Text = \"Height of the object: \" &";
mostCurrent._lblobjectheight.setText(BA.ObjectToCharSequence("Height of the object: "+anywheresoftware.b4a.keywords.Common.NumberFormat(_objectheight,(int) (1),(int) (2))+" m"));
 //BA.debugLineNum = 110;BA.debugLine="End Sub";
return "";
}
public static void  _btnmeasurebase_click() throws Exception{
ResumableSub_btnMeasureBase_Click rsub = new ResumableSub_btnMeasureBase_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnMeasureBase_Click extends BA.ResumableSub {
public ResumableSub_btnMeasureBase_Click(anywheresoftware.b4a.samples.hypsometer.main parent) {
this.parent = parent;
}
anywheresoftware.b4a.samples.hypsometer.main parent;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 59;BA.debugLine="sensor.StartListening(\"sensorChanged\") ' Pokreni";
parent._sensor.StartListening(processBA,"sensorChanged");
 //BA.debugLineNum = 61;BA.debugLine="Sleep(500)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (500));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
 //BA.debugLineNum = 62;BA.debugLine="thetaBase = pitch";
parent._thetabase = parent._pitch;
 //BA.debugLineNum = 63;BA.debugLine="ToastMessageShow(\"Angle to base of the object: \"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Angle to base of the object: "+anywheresoftware.b4a.keywords.Common.NumberFormat(parent._thetabase,(int) (1),(int) (2))+"°"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 64;BA.debugLine="sensor.StopListening ' Zaustavi slušanje senzora";
parent._sensor.StopListening(processBA);
 //BA.debugLineNum = 65;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _btnmeasuretop_click() throws Exception{
ResumableSub_btnMeasureTop_Click rsub = new ResumableSub_btnMeasureTop_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnMeasureTop_Click extends BA.ResumableSub {
public ResumableSub_btnMeasureTop_Click(anywheresoftware.b4a.samples.hypsometer.main parent) {
this.parent = parent;
}
anywheresoftware.b4a.samples.hypsometer.main parent;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 68;BA.debugLine="sensor.StartListening(\"sensorChanged\") ' Pokreni";
parent._sensor.StartListening(processBA,"sensorChanged");
 //BA.debugLineNum = 70;BA.debugLine="Sleep(500)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (500));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
 //BA.debugLineNum = 71;BA.debugLine="thetaTop = pitch";
parent._thetatop = parent._pitch;
 //BA.debugLineNum = 72;BA.debugLine="ToastMessageShow(\"Angle to top of the object: \" &";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Angle to top of the object: "+anywheresoftware.b4a.keywords.Common.NumberFormat(parent._thetatop,(int) (1),(int) (2))+"°"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 73;BA.debugLine="sensor.StopListening ' Zaustavi slušanje senzora";
parent._sensor.StopListening(processBA);
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Private btnMeasureBase As Button";
mostCurrent._btnmeasurebase = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private btnMeasureTop As Button";
mostCurrent._btnmeasuretop = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private txtDistance As EditText";
mostCurrent._txtdistance = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private txtObserverHeight As EditText";
mostCurrent._txtobserverheight = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private txtTopAngle As EditText ' Novi EditText z";
mostCurrent._txttopangle = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private btnCalculate As Button";
mostCurrent._btncalculate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private lblObjectHeight As Label";
mostCurrent._lblobjectheight = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private lblDistance As Label";
mostCurrent._lbldistance = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private lblObserverHeight As Label";
mostCurrent._lblobserverheight = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private lblTopAngle As Label";
mostCurrent._lbltopangle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 11;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 13;BA.debugLine="Private const Pi As Float = 3.1415927";
_pi = (float) (3.1415927);
 //BA.debugLineNum = 15;BA.debugLine="Dim sensor As PhoneSensors ' Senzor za akcelerome";
_sensor = new anywheresoftware.b4a.phone.Phone.PhoneSensors();
 //BA.debugLineNum = 16;BA.debugLine="Dim pitch As Float ' Vrijednost nagiba iz akceler";
_pitch = 0f;
 //BA.debugLineNum = 17;BA.debugLine="Dim thetaBase, thetaTop As Float ' Kutevi prema b";
_thetabase = 0f;
_thetatop = 0f;
 //BA.debugLineNum = 18;BA.debugLine="Dim distance, observerHeight As Float ' Udaljenos";
_distance = 0f;
_observerheight = 0f;
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _sensorchanged(float[] _values) throws Exception{
 //BA.debugLineNum = 77;BA.debugLine="Sub sensorChanged (Values() As Float)";
 //BA.debugLineNum = 79;BA.debugLine="pitch = Values(1) * 180 / Pi ' konvertiraj iz rad";
_pitch = (float) (_values[(int) (1)]*180/(double)_pi);
 //BA.debugLineNum = 80;BA.debugLine="Log(\"Pitch: \" & pitch) ' Dodaj log da pratiš vrij";
anywheresoftware.b4a.keywords.Common.LogImpl("4393219","Pitch: "+BA.NumberToString(_pitch),0);
 //BA.debugLineNum = 81;BA.debugLine="End Sub";
return "";
}
}
